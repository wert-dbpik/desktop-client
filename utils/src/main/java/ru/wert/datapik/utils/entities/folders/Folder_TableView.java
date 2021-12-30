package ru.wert.datapik.utils.entities.folders;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import lombok.Getter;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.FormViewControlIml;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.tableView.CatalogableTable;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_ContextMenu;
import ru.wert.datapik.utils.entities.product_groups.ProductGroup_TreeView;
import ru.wert.datapik.utils.entities.product_groups.commands._ProductGroup_Commands;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.images.AppImages.TREE_NODE_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;

public class Folder_TableView extends ItemTableView<Item> implements IFormView<Item>, CatalogableTable<ProductGroup> {

    private String accWindowRes = "/utils-fxml/folders/folderACC.fxml";
    private final _Folder_Commands commands;

    private List<Folder> currentItemList = new ArrayList<>();
    private FormView_ACCController<Item> accController;
    private TreeItem<ProductGroup> selectedItem;
    @Getter private ProductGroup_TreeView<ProductGroup> catalogTree;

    private Folder_ContextMenu contextMenu;

    public Folder_TableView(ProductGroup_TreeView<ProductGroup> catalogTree, String prompt) {
        super(prompt);
        this.catalogTree = catalogTree;

        new FormViewControlIml(this, catalogTree);

        commands = new _Folder_Commands(this);
        createContextMenu();

        //При двойном клике на верхнюю строку, поднимаемся по списку выше
        //При двойном клике на папку открываем папку
        //При клике правой кнопку по пустой строке снимаем всякое выделение
        setRowFactory( tv -> {
            TableRow<Item> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                Item rowData = row.getItem();
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    if(rowData instanceof ProductGroup){
                        if(rowData == selectedItem.getValue()){
                            selectedItem = catalogTree.findTreeItemById(((ProductGroup) rowData).getParentId());
                        } else {
                            selectedItem = catalogTree.findTreeItemById(rowData.getId());
                        }
                        catalogTree.getSelectionModel().select(selectedItem);
                    }
                }
                if (row.isEmpty()) {
                    getSelectionModel().clearSelection();
                }
            });

            return row ;
        });

    }

    @Override
    public void updateTableView() {

        List<Item> items = new ArrayList<>();

        selectedItem = catalogTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null) selectedItem = catalogTree.getRoot();
        ProductGroup selectedGroup = selectedItem.getValue();
        //Добавим верхнюю строку в список, потом она превратится в троеточие
        //Корневой элемент в список не добавляем
        if(selectedItem != catalogTree.getRoot())
            items.add(selectedItem.getValue());
        List<TreeItem<ProductGroup>> children = selectedItem.getChildren();
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
                if(item.equals(selectedItem.getValue())){
                    label.setText("< . . . >");
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


}
