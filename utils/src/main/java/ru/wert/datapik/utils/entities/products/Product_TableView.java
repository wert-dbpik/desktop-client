package ru.wert.datapik.utils.entities.products;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;
import ru.wert.datapik.utils.entities.products.commands._Product_Commands;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;

import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_PRODUCTS;

public class Product_TableView extends  CatalogTableView<Product, ProductGroup>{

    private static final String accWindowRes = "/utils-fxml/products/productACC.fxml";
    private final _Product_Commands commands;
    private String searchedText = "";
    private List<Product> currentItemList;
    private Product_ACCController accController;

    public Product_TableView(Item_TreeView<Product, ProductGroup> catalogTree, String itemName, boolean useContextMenu) {
        super(CH_QUICK_PRODUCTS, catalogTree, itemName);

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
        return CH_QUICK_PRODUCTS.findAll();
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
    public List<Product> getCurrentItemList() {
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
    public TreeItem<ProductGroup> getSelectedTreeItem() {
        return null;
    }

    @Override
    public void setSelectedTreeItem(TreeItem<ProductGroup> item) {

    }

    /**
     * Обновляет таблицу независимо от выделения в TreeView
     *
     * @param selectedCatalogGroup
     */
    @Override
    public void updateOnlyTableView(CatalogGroup selectedCatalogGroup) {

    }


}
