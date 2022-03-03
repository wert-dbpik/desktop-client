package ru.wert.datapik.utils.entities.folders;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
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
import ru.wert.datapik.utils.entities.drafts.Draft_TableView;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.search.Searchable;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.images.AppImages.TREE_NODE_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;
import static ru.wert.datapik.utils.statics.AppStatic.UPWARD;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class Folder_TableView extends ItemTableView<Item> implements IFormView<Item>, CatalogableTable<ProductGroup>, Searchable<Item> {

    @Getter private String accWindowRes = "/utils-fxml/folders/folderACC.fxml";
    @Getter private _Folder_Commands commands;

    private FormView_ACCController<Item> accController;
    @Getter@Setter private TreeItem<ProductGroup> upwardTreeItemRow;//Верхняя строка в таблице
    @Getter private ProductGroup_TreeView<Item> catalogTree;
    @Getter private Folder_Manipulator manipulator;
    @Getter@Setter private Draft_TableView draftTable;

    @Getter@Setter private String searchedText = "";


    private Folder_ContextMenu contextMenu;
    @Getter private boolean useContextMenu;

    public Folder_TableView(String prompt, boolean useContextMenu) {
        super(prompt);
        this.useContextMenu = useContextMenu;

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                CH_SEARCH_FIELD.setText(searchedText);
            }
        });


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
        this.upwardTreeItemRow = catalogTree.getRoot();//Инициализируем upwardRow

        manipulator = new Folder_Manipulator(this, catalogTree);

        commands = new _Folder_Commands(this);

        if(useContextMenu)
            createContextMenu();

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
                        if(rowData.equals(upwardTreeItemRow.getValue())){ //Верхняя строка
                            prevRowData = rowData;
                            upwardTreeItemRow = catalogTree.findTreeItemById(((ProductGroup) rowData).getParentId());
                        } else {
                            upwardTreeItemRow = catalogTree.findTreeItemById(rowData.getId());
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
        searchedText = CH_SEARCH_FIELD.getText();
        if(globalOn) updateWithGlobalOn();
        else {
            //Находим выделенный элемент в дереве каталогов
            upwardTreeItemRow = catalogTree.getSelectionModel().getSelectedItem();
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
     * Обновление без учета выделенного элемента в TREE VIEW
     * @param prevGroupToBeSelected группа TreeView - верхняя строка в таблице, учитывается припереходе назад
     */
    public void updateNow(ProductGroup prevGroupToBeSelected) {
        List<Item> items = new ArrayList<>();
        if (upwardTreeItemRow == null) upwardTreeItemRow = catalogTree.getRoot();
        ProductGroup selectedGroup = upwardTreeItemRow.getValue();
        //Добавим верхнюю строку в список, потом она превратится в троеточие
        //Корневой элемент в список не добавляем
        if(upwardTreeItemRow != catalogTree.getRoot())
            items.add(upwardTreeItemRow.getValue());
        List<TreeItem<ProductGroup>> children = upwardTreeItemRow.getChildren();
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

        upwardTreeItemRow = catalogTree.findTreeItemById(selectedProductGroup.getId());

        List<Item> items = new ArrayList<>();
        ProductGroup selectedGroup = upwardTreeItemRow.getValue();
        //Добавим верхнюю строку в список, потом она превратится в троеточие
        //Корневой элемент в список не добавляем
        if(upwardTreeItemRow != catalogTree.getRoot())
            items.add(upwardTreeItemRow.getValue());
        List<TreeItem<ProductGroup>> children = upwardTreeItemRow.getChildren();
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
        setOnContextMenuRequested(event->{
            contextMenu = new Folder_ContextMenu(this, catalogTree, commands, accWindowRes);
            contextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getSceneY());
        });
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
                if(item.equals(upwardTreeItemRow.getValue())){
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
