package ru.wert.datapik.chogori.entities.materials.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Material;
import ru.wert.datapik.chogori.common.commands.ICommand;
import ru.wert.datapik.chogori.entities.materials.Material_TableView;
import ru.wert.datapik.chogori.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_MATERIALS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class Material_DeleteCommand implements ICommand {

    private List<Material> items;
    private Material_TableView tableView;

    /**
     *
     * @param tableView Material_TableView
     */
    public Material_DeleteCommand(List<Material> items, Material_TableView tableView) {
        this.items = items;
        this.tableView = tableView;

    }

    @Override
    public void execute() {
        //После удаления таблица "подтянется вверх" и поэтому нужна позиция первого из удаляемых элементов
        int row = tableView.getItems().lastIndexOf(items.get(0));

        for(Material item : items){
            try {
                CH_MATERIALS.delete(item);
                log.info("Удален материал {}", item.getName());
                AppStatic.createLog(false, String.format("Удалил материал '%s'", item.getName()));
            } catch (Exception e) {
                Warning1.create($ATTENTION, $ERROR_WHILE_DELETING_ITEM, $ITEM_IS_BUSY_MAYBE);
                log.error("При добавлении материала {} произошла ошибка", item.getName());
            }
        }

        tableView.easyUpdate(CH_MATERIALS);

        tableView.getSelectionModel().select(row);

    }
}
