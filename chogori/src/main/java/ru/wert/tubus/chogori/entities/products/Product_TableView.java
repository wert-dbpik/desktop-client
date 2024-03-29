package ru.wert.tubus.chogori.entities.products;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import ru.wert.tubus.client.entity.models.Product;
import ru.wert.tubus.client.entity.models.ProductGroup;
import ru.wert.tubus.client.interfaces.CatalogGroup;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.tableView.CatalogTableView;
import ru.wert.tubus.chogori.entities.products.commands._Product_Commands;
import ru.wert.tubus.chogori.common.treeView.Item_TreeView;
import ru.wert.tubus.chogori.application.services.ChogoriServices;

import java.util.List;

public class Product_TableView extends CatalogTableView<Product, ProductGroup> {

    private static final String accWindowRes = "/chogori-fxml/products/productACC.fxml";
    private final _Product_Commands commands;
    private String searchedText = "";
    private List<Product> currentItemList;
    private Product_ACCController accController;

    public Product_TableView(Item_TreeView<Product, ProductGroup> catalogTree, String itemName, boolean useContextMenu) {
        super(ChogoriServices.CH_QUICK_PRODUCTS, catalogTree, itemName);

        commands = new _Product_Commands(this);

        if (useContextMenu)
            createContextMenu();
    }

    @Override
    public void setTableColumns() {
        TableColumn<Product, String> tcProduct = Product_Columns.createTcPartOneRow();

        getColumns().add(tcProduct);
    }

    @Override
    public void createContextMenu() {
        contextMenu = new Product_ContextMenu(this, commands, accWindowRes);
        setContextMenu(contextMenu);
    }

    @Override
    public List<Product> prepareList() {
        return ChogoriServices.CH_QUICK_PRODUCTS.findAll();
    }

    @Override
    public void setSearchedText(String searchedText) {
        this.searchedText = searchedText;
    }

    @Override
    public String getSearchedText() {
        return searchedText;
    }

    @Override
    public ItemCommands<Product> getCommands() {
        return commands;
    }

    @Override
    public String getAccWindowRes() {
        return accWindowRes;
    }

    @Override //Searchable
    public List<Product> getCurrentItemSearchedList() {
        return currentItemList;
    }

    @Override //Searchable
    public void setCurrentItemSearchedList(List<Product> currentItemList) {
        this.currentItemList = currentItemList;
    }

    @Override
    public void setAccController(FormView_ACCController<Product> accController) {
        this.accController = (Product_ACCController) accController;
    }

    @Override
    public FormView_ACCController<Product> getAccController() {
        return accController;
    }

    @Override
    public TreeItem<ProductGroup> getUpwardRow() {
        return null;
    }

    @Override
    public void setUpwardRow(TreeItem<ProductGroup> item) {

    }

    /**
     * Обновляет таблицу независимо от выделения в TreeView
     *
     * @param selectedCatalogGroup
     */
    @Override
    public void updateVisibleLeafOfTableView(CatalogGroup selectedCatalogGroup) {

    }


}
