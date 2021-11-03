package ru.wert.datapik.utils.entities.material_groups.commands;

import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.entity.models.MaterialGroup;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.entities.material_groups.MaterialGroup_TreeView;
import ru.wert.datapik.winform.warnings.Warning1;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_MATERIAL_GROUPS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

@Slf4j
public class MaterialGroup_AddCommand implements ICommand {

    private final MaterialGroup newItem;
    private final MaterialGroup_TreeView<MaterialGroup> treeView;

    /**
     * @param treeView MaterialGroupTableView
     */
    public MaterialGroup_AddCommand(MaterialGroup newItem, MaterialGroup_TreeView<MaterialGroup> treeView) {
        this.newItem = newItem;
        this.treeView = treeView;

    }

    @Override
    public void execute() {
        TreeItem<MaterialGroup> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if(selectedItem != null) //Условие когда добавляют в корень
            selectedItem.setExpanded(true);
        try {
            CH_MATERIAL_GROUPS.save(newItem);
            log.info("Добавлена группа изделий {}", newItem.getName());
        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_ADDING_ITEM, $SERVER_IS_NOT_AVAILABLE_MAYBE);
            log.error("При добавлении группы изделий {} произошла ошибка {} по причине {}",
                    newItem.getName(), e.getMessage(), e.getCause());
        }

        //Обновляем каталог
        treeView.updateView();
        int row = treeView.findTreeItemRow(newItem);
        treeView.getSelectionModel().select(row);
        treeView.scrollTo(row);
        //Обновляем Таблицу
//        treeView.getConnectedForm().updateCatalogView(selectedItem, true);


    }
}
