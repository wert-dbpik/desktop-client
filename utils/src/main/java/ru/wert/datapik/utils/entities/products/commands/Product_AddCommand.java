package ru.wert.datapik.utils.entities.products.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.client.entity.models.AnyPart;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.entities.products.Product_TableView;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class Product_AddCommand implements ICommand {

    private Product newItem;
    private Product_TableView tableView;

    /**
     *
     * @param tableView Product_TableView
     */
    public Product_AddCommand(Product newItem, Product_TableView tableView) {
        this.newItem = newItem;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        @NotNull Passport finalPassport = createPassport();
        @NotNull AnyPart finalPart = createAnyPart();

        newItem.setPassport(finalPassport);
        newItem.setAnyPart(finalPart);

        //Сохраняем новое изделие
        Product savedProduct = CH_QUICK_PRODUCTS.save(newItem);

        if (savedProduct != null) { //Если сохранение произошло
            log.info("Добавлено изделие {} исп. {}", savedProduct.getPassport().toUsefulString(), savedProduct.getVariant());
            AppStatic.createLog(false, String.format("Добавил изделие '%s' исп.'%s'", savedProduct.getPassport().toUsefulString(), savedProduct.getVariant()));
        } else {//Если сохранение НЕ произошло
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении изделия {} произошла ошибка",
                    newItem.toUsefulString());
        }

        Platform.runLater(() -> {
            tableView.updateCatalogView(tableView.getChosenCatalogItem(), true);
            tableView.scrollTo(newItem);
            tableView.getSelectionModel().select(newItem);
        });


    }

    /**
     * Если имеющегося пасспорта еще нет в БД, то пасспорт будет сохранен
     */
    private AnyPart createAnyPart() {
        //Окончательный AnyPart
        AnyPart finalPart = null;
        //AnyPart пришедший с изделием
        AnyPart newPart = newItem.getAnyPart();
        //Проверяем наличие newPart в БД
        AnyPart foundPart = CH_QUICK_ANY_PARTS.findByName(newPart.getName());
        if(foundPart == null) { //Если newPart в базе отсутствует
            AnyPart savedPart = CH_QUICK_ANY_PARTS.save(newPart); //Сохраняем в базе
            if (savedPart != null) { //Если сохранение произошло
                finalPart = savedPart;
                log.info("Добавлен элемент {}, {}", savedPart.getName(), savedPart.getSecondName());
            } else { //Если сохранение не произошло
                Warning1.create($ATTENTION, String.format("Не удалось создать AnyPart \n%s, %s", newPart.getName(), newPart.getSecondName()),
                        $SERVER_IS_NOT_AVAILABLE_MAYBE);
            }
        }else { //Если newPart уже есть в базе
            finalPart = foundPart;
        }
        return finalPart;
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
        Passport foundPassport = CH_QUICK_PASSPORTS.findByPrefixIdAndNumber(newPassport.getPrefix(), newPassport.getNumber());
        if(foundPassport == null) { //Если newPassport в базе отсутствует
            Passport savedPassport = CH_QUICK_PASSPORTS.save(newPassport); //Сохраняем в базе
            if (savedPassport != null) {//Если сохранение произошло
                finalPassport = savedPassport;
                log.info("Добавлен пасспорт : {}", savedPassport.toUsefulString());
            } else {//Если сохранение не произошло
                Warning1.create($ATTENTION, String.format("Не удалось создать пасспорт \n%s", newPassport.toUsefulString()),
                        $SERVER_IS_NOT_AVAILABLE_MAYBE);
            }
        } else {
            finalPassport = foundPassport;
        }
        return finalPassport;
    }


}
