package ru.wert.tubus.chogori.entities.drafts.commands;

import javafx.concurrent.Task;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.drafts.Draft_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.enums.EDraftType;
import ru.wert.tubus.winform.statics.WinformStatic;
import ru.wert.tubus.winform.warnings.Warning1;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;
import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class Draft_AddCommand  extends Task<Void>  implements ICommand {

    @Getter
    private final Draft newItem;
    private final Draft_TableView tableView;
    private Draft savedDraft;

    /**
     * Конструктор
     * @param tableView Draft_TableView
     */
    public Draft_AddCommand(Draft newItem, Draft_TableView tableView) {
        this.newItem = newItem;
        this.tableView = tableView;

    }

    public Draft executeWithReturn(){
        execute();
        log.debug(format("executeWithReturn() - saved draft with id = %s", newItem.getId()));
        return newItem;
    }

    @Override
    public void execute() {

        @NotNull Passport finalPassport = createPassport();
        newItem.setPassport(finalPassport);

        //Сохраняем новое изделие
        savedDraft = ChogoriServices.CH_QUICK_DRAFTS.save(newItem);

        if (savedDraft != null) { //Если сохранение произошло
            log.info("Добавлен чертеж {} ", savedDraft.getPassport().toUsefulString());
            AppStatic.createLog(false, format("Добавил чертеж '%s' (%s) в комплект '%s'",
                    savedDraft.getPassport().toUsefulString(),
                            EDraftType.getDraftTypeById(savedDraft.getDraftType()).getShortName() + "-" + savedDraft.getPageNumber(),
                    savedDraft.getFolder().toUsefulString()));
        } else {//Если сохранение НЕ произошло
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении чертежа {} произошла ошибка",
                    newItem.getPassport().toUsefulString());
        }

        File uploadingFile = tableView.getAccController().getCurrentFilePath();
        assert savedDraft != null;
        String fileNewName = savedDraft.getId() + "." + savedDraft.getExtension();

        try {
            ChogoriServices.CH_QUICK_DRAFTS.upload(fileNewName, "drafts", uploadingFile);
        } catch (IOException e) {
            log.error("Не удалось загрузить на сервер файл {}", uploadingFile.getName());
            Warning1.create("Критическая ошибка!", "Не удалось на сервер загрузить файл " + uploadingFile.getName(),
                    "Возможно, сервер не доступен");
            e.printStackTrace();
        }

        log.debug(format("execute() - saved draft with id = %s", newItem.getId()));

    }

    /**
     * Если имеющегося Passport еще нет в БД, то Passport будет сохранен
     */
    private Passport createPassport() {
        //Окончательный Passport
        Passport finalPassport = null;
        //Passport пришедший с изделием
        Passport newPassport = newItem.getPassport();
        //Проверяем наличие newPassport в БД
        Passport foundPassport = ChogoriServices.CH_QUICK_PASSPORTS.findByPrefixIdAndNumber(newPassport.getPrefix(), newPassport.getNumber());
        if(foundPassport == null) { //Если newPassport в базе отсутствует
            Passport savedPassport = ChogoriServices.CH_QUICK_PASSPORTS.save(newPassport); //Сохраняем в базе
            if (savedPassport != null) {//Если сохранение произошло
                finalPassport = savedPassport;
                log.info("Добавлен пасспорт : {}", savedPassport.toUsefulString());
            } else {//Если сохранение не произошло
                Warning1.create($ATTENTION, format("Не удалось создать пасспорт \n%s", newPassport.toUsefulString()),
                        $SERVER_IS_NOT_AVAILABLE_MAYBE);
            }
        } else {
            finalPassport = foundPassport;
        }
        return finalPassport;
    }


    protected Draft getResult() {
        return savedDraft;
    }

    @Override
    protected Void call() throws Exception {
        
        return null;
    }
}
