package ru.wert.datapik.utils.entities.product_groups;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Setter;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.tableView.ItemTableView;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_PRODUCT_GROUPS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class _ProductGroup_TreeViewPatch<P extends Item> {

    @Setter private ItemService<P> dependedItemService;
    @Setter private ItemTableView<Item> dependedTableView;

    public ProductGroup_TreeView<P> create(){

        ProductGroup rootProductGroup = new ProductGroup("Изделие", 0L);
        rootProductGroup.setId(1L);
        boolean useContextMenu = CH_CURRENT_USER.getUserGroup().isEditProductStructures();
        ProductGroup_TreeView<P> catalogTreeView = new ProductGroup_TreeView<>(CH_PRODUCT_GROUPS, dependedItemService, dependedTableView, rootProductGroup, useContextMenu);
        catalogTreeView.buildTree();

        VBox.setVgrow(catalogTreeView, Priority.ALWAYS);

        return catalogTreeView;
    }


}
