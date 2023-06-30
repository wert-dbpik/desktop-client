package ru.wert.tubus.chogori.entities.materials.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.Material;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.materials.Material_TableView;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.warnings.Warning1;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class Material_ChangeCommand implements ICommand {

    private Material item;
    private Material_TableView tableView;

    /**
     *
     * @param tableView Material_TableView
     */
    public Material_ChangeCommand(Material item, Material_TableView tableView) {
        this.item = item;
        this.tableView = tableView;

    }

    @Override
    public void execute() {

        try {
            ChogoriServices.CH_MATERIALS.update(item);

            Platform.runLater(()->{
                tableView.easyUpdate(ChogoriServices.CH_MATERIALS);
                tableView.scrollTo(item);
                tableView.getSelectionModel().select(item);

                log.info("Обновлен материал {}", item.getName());
                AppStatic.createLog(false, String.format("Изменил материал '%s'", item.getName()));
            });

        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При обновлении материала {} произошла ошибка", item.getName());
        }

    }
}
