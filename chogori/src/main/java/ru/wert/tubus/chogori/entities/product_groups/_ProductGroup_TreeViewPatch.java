package ru.wert.tubus.chogori.entities.product_groups;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Setter;
import ru.wert.tubus.client.entity.models.ProductGroup;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.chogori.common.tableView.ItemTableView;
import ru.wert.tubus.chogori.application.services.ChogoriServices;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;

public class _ProductGroup_TreeViewPatch<P extends Item> {

    @Setter private ItemService<P> dependedItemService;

    public _ProductGroup_TreeViewPatch() {

    }

    public ProductGroup_TreeView<P> createProductTreeView(ItemTableView<Item> dependedTableView){

        ProductGroup rootProductGroup = new ProductGroup("Изделие", 0L);
        rootProductGroup.setId(1L);
        boolean useContextMenu = ChogoriSettings.CH_CURRENT_USER.getUserGroup().isEditProductStructures();

        ProductGroup_TreeView<P> catalogTreeView = new ProductGroup_TreeView<>(ChogoriServices.CH_PRODUCT_GROUPS, dependedItemService, dependedTableView, rootProductGroup, useContextMenu);
        catalogTreeView.buildTree();

        VBox.setVgrow(catalogTreeView, Priority.ALWAYS);

        return catalogTreeView;
    }




}
