package ru.wert.datapik.chogori.entities.product_groups;

import lombok.Getter;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.chogori.common.tableView.ItemTableView;
import ru.wert.datapik.chogori.entities.product_groups.commands._ProductGroup_Commands;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.treeView.Item_TreeView;

public class ProductGroup_TreeView<P extends Item> extends Item_TreeView<P, ProductGroup> {

    @Getter
    private String accWindowRes = "/chogori-fxml/productGroup/productGroupACC.fxml";
    private _ProductGroup_Commands<P> commands;
    private ProductGroup_ContextMenu contextMenu;
    private ProductGroup_ACCController accController;
    @Getter private ProductGroup_Manipulator manipulator;


    public ProductGroup_TreeView(ItemService<ProductGroup> itemService, ItemService<P> dependedItemService, ItemTableView<Item> dependedTableView, ProductGroup rootItem, boolean useContextMenu) {
        super(itemService, rootItem);

        commands = new _ProductGroup_Commands<>(this, dependedTableView, dependedItemService);

        if(useContextMenu) manipulator = new ProductGroup_Manipulator((Item_TreeView<Item, ProductGroup>) this, dependedTableView);
        if (useContextMenu) createContextMenu();


    }

    @Override
    public void createContextMenu() {
        contextMenu = new ProductGroup_ContextMenu((ProductGroup_TreeView<Item>) this, commands, accWindowRes);
        setContextMenu(contextMenu);
    }

    @Override
    public ItemCommands<ProductGroup> getItemCommands() {
        return commands;
    }

    @Override
    public void setAccController(FormView_ACCController<ProductGroup> accController) {
        this.accController = (ProductGroup_ACCController) accController;
    }

    @Override
    public FormView_ACCController<ProductGroup> getAccController() {
        return accController;
    }
}
