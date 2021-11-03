package ru.wert.datapik.utils.entities.materials.commands;

import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.Material;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.entities.materials.Material_TableView;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_MATERIALS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

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
            CH_MATERIALS.update(item);

            tableView.easyUpdate(CH_MATERIALS);

//            tableView.updateView();
            tableView.scrollTo(item);
            tableView.getSelectionModel().select(item);

            log.info("Обновлен материал {}", item.getName());
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При обновлении материала {} произошла ошибка", item.getName());
        }

    }
}
