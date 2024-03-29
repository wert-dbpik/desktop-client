package ru.wert.tubus.chogori.common.tableView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.tubus.chogori.common.treeView.Item_TreeView;
import ru.wert.tubus.chogori.search.Searchable;
import ru.wert.tubus.client.interfaces.CatalogGroup;
import ru.wert.tubus.client.interfaces.CatalogService;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.winform.enums.EOperation;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;


public abstract class CatalogTableView<P extends Item, T extends CatalogGroup>  extends ItemTableView<P> implements CatalogableTable<T>, Searchable<P> {

    private final Item_TreeView<P, T> catalogTree;
    private final CatalogService<P> itemService;
    protected FormView_ContextMenu<P> contextMenu;

    public abstract void setTableColumns();
    public abstract ItemCommands<P> getCommands();
    public abstract String getAccWindowRes();

    @Override//Searchable
    public abstract void setCurrentItemSearchedList(List<P> currentItemList);
    @Override//Searchable
    public abstract List<P> getCurrentItemSearchedList();
    @Override//Searchable
    public abstract void setSearchedText(String searchedText);
    @Override//Searchable
    public abstract String getSearchedText();


    public CatalogTableView(ItemService<P> itemService, Item_TreeView<P, T> catalogTree, String itemName) {
        super(itemName);
        this.catalogTree = catalogTree;
        this.itemService = (CatalogService<P>) itemService;

    }


    /**
     * Обновляет данные формы
     */
    @Override
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

        return group;
    }

    /**
     * Обновляет данные формы
     */
    @Override //Searchable
    public void updateSearchedView(){
        List<P> list = getCurrentItemSearchedList();
        List<P> foundList = new ArrayList<>();
        String searchedText = CH_SEARCH_FIELD.getText();
        for(P item : list){
            if(item.toUsefulString().contains(searchedText))
                foundList.add(item);
        }
        updateForm(foundList);
    }

    /**
     * Обновляет данные формы
     */
    @Override //Searchable
    public void easyUpdate(ItemService<P> service) {
        getItems().clear();
        refresh();
        String searchedText = getSearchedText();
//        if (searchedText == null || searchedText.equals(""))
        List<P> list = service.findAll();
        setItems(FXCollections.observableArrayList(list));
        setCurrentItemSearchedList(list);
//        else
//            setItems(service.findAllByText(searchedText));
    }

    @Override //IFormView
    public List<P> getAllSelectedItems(){
        return getSelectionModel().getSelectedItems();
    }


}
