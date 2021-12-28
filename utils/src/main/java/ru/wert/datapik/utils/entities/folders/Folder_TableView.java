package ru.wert.datapik.utils.entities.folders;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.tableView.ItemTableView;
import ru.wert.datapik.utils.entities.drafts.Draft_ContextMenu;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.images.AppImages.TREE_NODE_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;

public class Folder_TableView extends ItemTableView<Item> implements IFormView<Item> {

    private static final String accWindowRes = "/utils-fxml/folders/folderACC.fxml";
    private final _Folder_Commands commands;

    private List<Folder> currentItemList = new ArrayList<>();
    private Folder_ACCController accController;
    private TreeItem<ProductGroup> selectedItem;
    private Item_TreeView<Folder, ProductGroup> catalogTree;


    public Folder_TableView(Item_TreeView<Folder, ProductGroup> catalogTree, String prompt) {
        super(prompt);
        this.catalogTree = catalogTree;

        commands = new _Folder_Commands(this);

        setRowFactory( tv -> {
            TableRow<Item> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Item rowData = row.getItem();
                    if(rowData instanceof ProductGroup){
                        if(rowData == selectedItem.getValue()){
                            selectedItem = catalogTree.findTreeItemById(((ProductGroup) rowData).getParentId());
                        } else {
                            selectedItem = catalogTree.findTreeItemById(rowData.getId());
                        }
                        catalogTree.getSelectionModel().select(selectedItem);
                    }
                }
            });
            return row ;
        });


        createContextMenu();
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
        Folder_ContextMenu contextMenu = new Folder_ContextMenu(this, commands, accWindowRes);
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

    }

    @Override
    public List<Item> getAllSelectedItems() {
        return getSelectionModel().getSelectedItems();
    }

    @Override
    public void setAccController(FormView_ACCController<Item> accController) {

    }

    @Override
    public FormView_ACCController<Item> getAccController() {
        return accController;
    }
}
