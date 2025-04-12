package ru.wert.tubus.chogori.entities.drafts.commands;

import javafx.concurrent.Task;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.api_interfaces.DraftApiInterface;
import ru.wert.tubus.client.retrofit.RetrofitClient;
import ru.wert.tubus.winform.enums.EDraftType;
import ru.wert.tubus.winform.warnings.Warning1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class Draft_QuickAddCommand extends Task<Void> implements ICommand {

    @Getter
    private final Draft newItem;
    private final Draft_TableView tableView;
    private final DraftApiInterface apiInterface;
    private Draft savedDraft;

    public Draft_QuickAddCommand(Draft newItem, Draft_TableView tableView) {
        this.newItem = newItem;
        this.tableView = tableView;
        this.apiInterface = RetrofitClient.getInstance().getRetrofit().create(DraftApiInterface.class);
    }

    public Draft executeWithReturn() {
        execute();
        log.debug("executeWithReturn() - saved draft with id = {}", savedDraft != null ? savedDraft.getId() : "null");
        return savedDraft;
    }

    @Override
    public void execute() {
        try {
            // 1. Сначала создаем/получаем паспорт
            Passport passport = createPassport();
            if (passport == null) {
                handleError("Не удалось создать паспорт");
                return;
            }

            // 2. Устанавливаем паспорт для чертежа
            newItem.setPassport(passport);

            // 3. Подготовка файла для загрузки
            File uploadingFile = tableView.getAccController().getCurrentFilePath();
            byte[] fileBytes = Files.readAllBytes(uploadingFile.toPath());

            // 4. Формируем запросы (без указания имени файла)
            RequestBody passportPart = RequestBody.create(
                    MediaType.parse("application/json"),
                    RetrofitClient.getGson().toJson(passport));

            RequestBody draftPart = RequestBody.create(
                    MediaType.parse("application/json"),
                    RetrofitClient.getGson().toJson(newItem));

            MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                    "file",
                    uploadingFile.getName(), // Используем оригинальное имя файла
                    RequestBody.create(MediaType.parse("application/octet-stream"), fileBytes));

            // 5. Отправляем запрос
            Call<Draft> call = apiInterface.quickCreateDraft(passportPart, draftPart, filePart);
            Response<Draft> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                savedDraft = response.body();
                handleSuccess(savedDraft);
            } else {
                handleError("Ошибка сервера при сохранении чертежа");
            }
        } catch (IOException e) {
            log.error("Ошибка при добавлении чертежа", e);
            handleError("Ошибка ввода-вывода: " + e.getMessage());
        }
    }

    /**
     * Логика создания паспорта из Draft_AddCommand
     */
    private Passport createPassport() {
        Passport newPassport = newItem.getPassport();
        Passport foundPassport = ChogoriServices.CH_QUICK_PASSPORTS.findByPrefixIdAndNumber(
                newPassport.getPrefix(),
                newPassport.getNumber());

        if(foundPassport == null) {
            Passport savedPassport = ChogoriServices.CH_QUICK_PASSPORTS.save(newPassport);
            if (savedPassport != null) {
                log.info("Добавлен пасспорт: {}", savedPassport.toUsefulString());
                return savedPassport;
            } else {
                Warning1.create($ATTENTION,
                        String.format("Не удалось создать пасспорт \n%s", newPassport.toUsefulString()),
                        $SERVER_IS_NOT_AVAILABLE_MAYBE);
                return null;
            }
        } else {
            return foundPassport;
        }
    }

    protected Draft getResult() {
        return savedDraft;
    }

    private void handleSuccess(Draft savedDraft) {
        log.info("Добавлен чертеж {}", savedDraft.getPassport().toUsefulString());
        AppStatic.createLog(false, String.format("Добавил чертеж '%s' (%s) в комплект '%s'",
                savedDraft.getPassport().toUsefulString(),
                EDraftType.getDraftTypeById(savedDraft.getDraftType()).getShortName() + "-" + savedDraft.getPageNumber(),
                savedDraft.getFolder().toUsefulString()));

        tableView.updateTableView();
    }

    private void handleError(String message) {
        Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, message);
        log.error("При добавлении чертежа {} произошла ошибка: {}",
                newItem.getPassport().toUsefulString(), message);
    }

    @Override
    protected Void call() throws Exception {
        execute();
        return null;
    }
}
