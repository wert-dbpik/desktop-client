package ru.wert.tubus.chogori.entities.materials.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.tubus.client.entity.models.AnyPart;
import ru.wert.tubus.client.entity.models.Material;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.materials.Material_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.warnings.Warning1;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class Material_AddCommand implements ICommand {

    private Material newItem;
    private Material_TableView tableView;

    /**
     *
     * @param tableView Material_TableView
     */
    public Material_AddCommand(Material newItem, Material_TableView tableView) {
        this.newItem = newItem;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        @NotNull AnyPart finalPart = createAnyPart();

        newItem.setAnyPart(finalPart);

        //Сохраняем новое изделие
        Material savedMaterial = ChogoriServices.CH_MATERIALS.save(newItem);

        if(savedMaterial != null){ //Если сохранение произошло
            log.info("Добавлен материал '{}' ", savedMaterial.getName());
            AppStatic.createLog(false, String.format("Добавил материал '%s'", savedMaterial.getName()));
        } else{//Если сохранение НЕ произошло
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении материала '{}' произошла ошибка",
                    newItem.toUsefulString());
        };
        Platform.runLater(()->{
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
        AnyPart foundPart = ChogoriServices.CH_QUICK_ANY_PARTS.findByName(newPart.getName());
        if(foundPart == null) { //Если newPart в базе отсутствует
            AnyPart savedPart = ChogoriServices.CH_QUICK_ANY_PARTS.save(newPart); //Сохраняем в базе
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
}
