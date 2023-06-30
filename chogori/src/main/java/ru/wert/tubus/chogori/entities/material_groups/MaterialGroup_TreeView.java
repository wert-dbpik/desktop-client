package ru.wert.tubus.chogori.entities.material_groups;

import ru.wert.tubus.client.entity.models.MaterialGroup;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.treeView.Item_TreeView;
import ru.wert.tubus.chogori.entities.material_groups.commands._MaterialGroup_Commands;


import java.util.List;

public class MaterialGroup_TreeView<P extends Item> extends Item_TreeView<P, MaterialGroup> {

    private static final String accWindowRes = "/chogori-fxml/materialGroup/materialGroupACC.fxml";
    private _MaterialGroup_Commands commands;
    private MaterialGroup_ContextMenu contextMenu;
    private List<MaterialGroup> currentItemList;
    private MaterialGroup_ACCController accController;

    public MaterialGroup_TreeView(ItemService<MaterialGroup> itemService, MaterialGroup rootItem, boolean useContextMenu) {
        super(itemService, rootItem);

        commands = new _MaterialGroup_Commands(this);

        if (useContextMenu)
            createContextMenu();

    }

    @Override
    public void createContextMenu() {
        contextMenu = new MaterialGroup_ContextMenu( this, commands, accWindowRes);
        setContextMenu(contextMenu);
    }

    @Override
    public ItemCommands<MaterialGroup> getItemCommands() {
        return commands;
    }

    @Override
    public void setAccController(FormView_ACCController<MaterialGroup> accController) {
        this.accController = (MaterialGroup_ACCController) accController;
    }

    @Override
    public FormView_ACCController<MaterialGroup> getAccController() {
        return accController;
    }
}
