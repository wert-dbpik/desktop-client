package ru.wert.datapik.utils.common.tableView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.*;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;
import ru.wert.datapik.utils.entities.products.Product_TableView;
import ru.wert.datapik.winform.enums.EOperation;


public abstract class CatalogTableView<P extends Item, T extends CatalogGroup>  extends ItemTableView<P> implements CatalogableTable<T> {

    private final Item_TreeView<P, T> catalogTree;
    private final CatalogService<P> itemService;
    protected FormView_ContextMenu<P> contextMenu;

    public abstract void setTableColumns();
    public abstract ItemCommands<P> getCommands();
    public abstract String getAccWindowRes();


    public CatalogTableView(ItemService<P> itemService, Item_TreeView<P, T> catalogTree, String itemName) {
        super(itemName);
        this.catalogTree = catalogTree;
        this.itemService = (CatalogService<P>) itemService;

    }


    /**
     * Обновляет данные формы
     */
    public void updateTableView() {

        TreeItem<T> treeItem = catalogTree.getSelectionModel().getSelectedItem();
        updateCatalogView(treeItem, false);

    }



    /**
     * Обновляет данные формы после выбора treeItem в дереве каталога
     * При необходимости, после обновления устанавливает фокус на строку
     * @param treeItem TreeItem<T>, узел дерева каталога
     * @param setFocus boolean тербуется/нет установка фокуса после обновления формы
     */
    public void updateCatalogView(TreeItem<T> treeItem, boolean setFocus) {
        if(treeItem == null) treeItem = catalogTree.getRoot();
        TaskUpdateItemsInCatalogTableView<P, T> taskUpdate = new TaskUpdateItemsInCatalogTableView<>(
                treeItem,
                catalogTree, this,
                setFocus,
                itemService);
        Thread t = new Thread(taskUpdate);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public TreeItem<T> getChosenCatalogItem() {
        return catalogTree.getSelectionModel().getSelectedItem();
    }

    @Override
    public TreeItem<T> getRootItem() {
        return catalogTree.getRoot();
    }

    /**
     * Метод используется классом BtnGlobe
     * Обновление таблицы с учетом выделенной позиции в дереве каталога
     */
    public void updateFuck(){
        Platform.runLater(()->{
//            updateCatalogView(catalogTree.getRoot(), true);
//            catalogTree.getSelectionModel().select(catalogTree.getRoot());
//            getSelectionModel().select(0);

            ObservableList<P> items = FXCollections.observableArrayList(itemService.findAll());
            getItems().clear();
            setItems(items);
        });
    }

    public T findChosenGroup(EOperation operation, CatalogTableView<P, T> tableView, T defaultGroup) {

        T group = null;
        //Определяем нулевую группу в корне дерева, т.к. она нам нужна при нажатии на кнопку GLOBE
        T rootGroup = tableView.getRootItem().getValue();

        //Потом определяем текущую выделенную группу, если она выбрана
        T chosenGroup = null;
        TreeItem<T> groupTreeItem = tableView.getChosenCatalogItem();
        if(groupTreeItem != null)
            chosenGroup = groupTreeItem.getValue();

        P chosenItem = tableView.getAllSelectedItems().get(0);
        //Depending on an operation we switch bxGroup to proper group
        if (operation.equals(EOperation.ADD)) { //When operation is ADD
            if (chosenGroup == null || chosenGroup.equals(rootGroup)) {
                group = defaultGroup;
            } else
                group = chosenGroup;
        }
//        if(operation.equals(EOperation.COPY)){
//            group = chosenItem.getProductGroup();
//        }
        return group;
    }










}
