package ru.wert.datapik.utils.entities.folders;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.search.Searchable;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.images.AppImages.TREE_NODE_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;
import static ru.wert.datapik.utils.statics.AppStatic.UPWARD;

public class Folder_TableView extends ItemTableView<Item> implements IFormView<Item>, CatalogableTable<ProductGroup>, Searchable<Item> {

    @Getter private String accWindowRes = "/utils-fxml/folders/folderACC.fxml";
    @Getter private _Folder_Commands commands;

    private FormView_ACCController<Item> accController;
    @Getter private TreeItem<ProductGroup> selectedTreeItem;
    @Getter private ProductGroup_TreeView<Item> catalogTree;
    @Getter private Folder_Manipulator manipulator;

    @Getter@Setter private String searchedText;


    private Folder_ContextMenu contextMenu;
    private final boolean useContextMenu;

    public Folder_TableView(String prompt, boolean useContextMenu) {
        super(prompt);
        this.useContextMenu = useContextMenu;
    }

    public List<ProductGroup> findMultipleProductGroups(ProductGroup productGroup){
        List<ProductGroup> foundProductGroups =
                catalogTree.findAllGroupChildren(catalogTree.findTreeItemById(productGroup.getId()));
        foundProductGroups.add(productGroup);
        return foundProductGroups;

    }

    /**
     * Метод подключает Folder_Manipulator и RowFactory к таблице
     * @param catalogTree ProductGroup_TreeView
     */
    public void plugContextMenuAndFolderManipulators(ProductGroup_TreeView catalogTree){
        this.catalogTree = catalogTree;

        if(useContextMenu) manipulator = new Folder_Manipulator(this, catalogTree);

        commands = new _Folder_Commands(this);

        if(useContextMenu) createContextMenu();

        //При двойном клике на верхнюю строку, поднимаемся по списку выше
        //При двойном клике на папку открываем папку
        //При клике правой кнопку по пустой строке снимаем всякое выделение

        setRowFactory( tv -> {
            TableRow<Item> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                Item prevRowData = null;
                Item rowData = row.getItem();
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    if(rowData instanceof ProductGroup){
                        if(rowData == selectedTreeItem.getValue()){ //Верхняя строка
                            prevRowData = rowData;
                            selectedTreeItem = catalogTree.findTreeItemById(((ProductGroup) rowData).getParentId());
                        } else {
                            selectedTreeItem = catalogTree.findTreeItemById(rowData.getId());
                        }
                        updateNow((ProductGroup) prevRowData);
                    }
                }
                //Снимаем всякое выделение, если клик по пустому месту
                if (row.isEmpty()) {
                    getSelectionModel().clearSelection();
                }
            });

            if(useContextMenu) {
                row.setOnDragDetected(e -> manipulator.createOnDragDetected(row));
                row.setOnDragOver(e -> manipulator.createOnDragOver(row));
                row.setOnDragDropped(e -> manipulator.createOnDragDropped(e, row));
            }

            return row ;
        });
    }


    /**
     * Обновление с учетом выделенного элемента в TREE VIEW
     * Метод вызывается извне этого класса
     */
    @Override
    public void updateTableView() {
        if(globalOn) updateWithGlobalOn();
        else {
            //Находим выделенный элемент в дереве каталогов
            selectedTreeItem = catalogTree.getSelectionModel().getSelectedItem();
            updateNow(null);
        }
    }

    /**
     * Обновление таблицы с учетом нажатой кнопки Global
     */
    private void updateWithGlobalOn(){
        Platform.runLater(()->{
            ObservableList<Folder> folders = FXCollections.observableArrayList(CH_QUICK_FOLDERS.findAll());
            ObservableList<Item> items = FXCollections.observableArrayList();
            for(Folder folder: folders){
                items.add((Item)folder);
            }
            getItems().clear();
            setItems(items);
        });
    }

    /**
     * Обновление с учетом выделенного элемента в TREE VIEW
     * Метод вызывается в этом классе
     * @param prevGroupToBeSelected группа TreeView - верхняя строка в таблице, указывающая на верхний уровень ProductGroup
     */
    private void updateNow(ProductGroup prevGroupToBeSelected) {
        List<Item> items = new ArrayList<>();
        if (selectedTreeItem == null) selectedTreeItem = catalogTree.getRoot();
        ProductGroup selectedGroup = selectedTreeItem.getValue();
        //Добавим верхнюю строку в список, потом она превратится в троеточие
        //Корневой элемент в список не добавляем
        if(selectedTreeItem != catalogTree.getRoot())
            items.add(selectedTreeItem.getValue());
        List<TreeItem<ProductGroup>> children = selectedTreeItem.getChildren();
        for (TreeItem<ProductGroup> ti : children) {
            items.add(ti.getValue());
        }

        List<Folder> folders = CH_QUICK_FOLDERS.findAllByGroupId(selectedGroup.getId());
        items.addAll(folders);

        getItems().clear();
        refresh();
        getItems().addAll(items);

        //TODO:Выделяем родительскую группу
        if(prevGroupToBeSelected != null){
            getSelectionModel().select(prevGroupToBeSelected);
        }
    }

    /**
     * Обновляет таблицу независимо от выделения в TreeView
     */
    @Override
    public void updateOnlyTableView(CatalogGroup selectedProductGroup) {

        selectedTreeItem = catalogTree.findTreeItemById(selectedProductGroup.getId());

        List<Item> items = new ArrayList<>();
        ProductGroup selectedGroup = selectedTreeItem.getValue();
        //Добавим верхнюю строку в список, потом она превратится в троеточие
        //Корневой элемент в список не добавляем
        if(selectedTreeItem != catalogTree.getRoot())
            items.add(selectedTreeItem.getValue());
        List<TreeItem<ProductGroup>> children = selectedTreeItem.getChildren();
        for (TreeItem<ProductGroup> ti : children) {
            items.add(ti.getValue());
        }

        List<Folder> folders = CH_QUICK_FOLDERS.findAllByGroupId(selectedGroup.getId());
        items.addAll(folders);

        getItems().clear();
        refresh();
        getItems().addAll(items);
    }

    @Override
    public void createContextMenu() {
        contextMenu = new Folder_ContextMenu(this, catalogTree, commands, accWindowRes);
        setContextMenu(contextMenu);
    }

    @Override
    public List<Item> prepareList() {
        return null;
    }

    @Override
    public void setTableColumns() {
        TableColumn<Item, Label> tcFolder = new TableColumn<>();
        tcFolder.setCellValueFactory(cd ->{
            Label label = new Label();
            Item item = cd.getValue();
            if(item instanceof ProductGroup) {
                if(item.equals(selectedTreeItem.getValue())){
                    label.setText(UPWARD);
                }else {
                    label.setText(item.toUsefulString());
                    label.setGraphic(new ImageView(TREE_NODE_IMG));
                }
            } else {
                label.setText(item.toUsefulString());
            }
            return new ReadOnlyObjectWrapper<>(label);
        });

        getColumns().add(tcFolder);

    }


    @Override
    public void easyUpdate(ItemService<Item> service) {
        //No use
    }

    @Override
    public List<Item> getAllSelectedItems() {
        return getSelectionModel().getSelectedItems();
    }

    @Override
    public void setAccController(FormView_ACCController<Item> accController) {
        this.accController = accController;
    }

    @Override
    public FormView_ACCController<Item> getAccController() {
        return accController;
    }

    @Override
    public TreeItem<ProductGroup> getChosenCatalogItem() {
        return catalogTree.getSelectionModel().getSelectedItem();
    }

    @Override
    public TreeItem<ProductGroup> getRootItem() {
        return catalogTree.getRoot();
    }

    @Override
    public void setSelectedTreeItem(TreeItem<ProductGroup> item) {

    }

    /**
     * Searchable
     * Метод вызывается для обновления таблицы
     */
    @Override
    public void updateSearchedView() {

    }

    /**
     * Searchable
     * Метод возвращает текущий список таблицы, с этим списком работает поиск
     * Метод необходим для ускорения работы поиска и вообще апдейта таблиц
     */
    @Override
    public List<Item> getCurrentItemSearchedList() {
        return null;
    }

    /**
     * Searchable
     * Метод устанавливает текущий список элементов, отображаемых в таблице
     * Он вызывается, например в Тасках для передачи окончательного списка
     */
    @Override
    public void setCurrentItemSearchedList(List<Item> currentItemList) {

    }
}
