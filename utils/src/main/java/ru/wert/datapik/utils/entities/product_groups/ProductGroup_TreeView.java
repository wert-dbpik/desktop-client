package ru.wert.datapik.utils.entities.product_groups;

import lombok.Getter;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.product_groups.commands._ProductGroup_Commands;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;

import java.util.List;

public class ProductGroup_TreeView<P extends Item> extends Item_TreeView<P, ProductGroup> {

    @Getter
    private String accWindowRes = "/utils-fxml/productGroup/productGroupACC.fxml";
    private _ProductGroup_Commands<P> commands;
    private ProductGroup_ContextMenu contextMenu;
    private ProductGroup_ACCController accController;


    public ProductGroup_TreeView(ItemService<ProductGroup> itemService, ItemService<P> dependedItemService, ItemTableView<Item> dependedTableView, ProductGroup rootItem, boolean useContextMenu) {
        super(itemService, rootItem);

        commands = new _ProductGroup_Commands<>(this, null, dependedItemService, dependedTableView);

        if (useContextMenu)
            createContextMenu();

    }

    @Override
    public void createContextMenu() {
        contextMenu = new ProductGroup_ContextMenu( this, commands, accWindowRes);
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
