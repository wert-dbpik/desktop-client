package ru.wert.tubus.chogori.entities.material_groups.commands;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.models.MaterialGroup;
import ru.wert.tubus.chogori.common.commands.ICommand;
import ru.wert.tubus.chogori.entities.material_groups.MaterialGroup_TreeView;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.winform.warnings.Warning1;

import static ru.wert.tubus.winform.warnings.WarningMessages.*;

@Slf4j
public class MaterialGroup_ChangeCommand implements ICommand {

    private MaterialGroup item;
    private MaterialGroup_TreeView<MaterialGroup> treeView;

    /**
     *
     * @param treeView MaterialGroup_TreeView
     */
    public MaterialGroup_ChangeCommand(MaterialGroup item, MaterialGroup_TreeView<MaterialGroup> treeView) {
        this.item = item;
        this.treeView = treeView;

    }

    @Override
    public void execute() {
        int row = treeView.getSelectionModel().getSelectedIndex();

        try {
            ChogoriServices.CH_MATERIAL_GROUPS.update(item);
            Platform.runLater(()->{
            treeView.updateView();
            treeView.getSelectionModel().select(row);
            treeView.scrollTo(row);

            log.info("Изменение группы изделий {}", item.getName());
            });

        } catch (Exception e) {
            Warning1.create($ATTENTION, $ERROR_WHILE_CHANGING_ITEM, $ITEM_IS_NOT_AVAILABLE_MAYBE);
            log.error("При зменении группы изделий {} произошла ошибка {} по причине {}",
                    item.getName(), e.getMessage(), e.getCause());
        }

    }
}
