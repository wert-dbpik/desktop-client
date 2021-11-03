package ru.wert.datapik.utils.entities.folders;

import javafx.scene.control.TableColumn;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.entities.folders.commands._Folder_Commands;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_FOLDERS;

public class Folder_TableView extends  CatalogTableView<Folder, ProductGroup>{

    private static final String accWindowRes = "/utils-fxml/folders/folderACC.fxml";
    private final _Folder_Commands commands;
    private String searchedText = "";
    private List<Folder> currentItemList = new ArrayList<>();
    private Folder_ACCController accController;

    public Folder_TableView(Item_TreeView<Folder, ProductGroup> catalogTree, String itemName) {
        super(CH_QUICK_FOLDERS, catalogTree, itemName);

        commands = new _Folder_Commands(this);

        createContextMenu();
    }

    @Override
    public void setTableColumns() {
        TableColumn<Folder, String> tcFolder = Folder_Columns.createTcFullName();

        getColumns().add(tcFolder);

    }

    @Override
    public void createContextMenu() {
        contextMenu = new Folder_ContextMenu(this, commands, accWindowRes);
        setContextMenu(contextMenu);
    }

    @Override
    public List<Folder> prepareList() {
        return CH_QUICK_FOLDERS.findAll();
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
    public ItemCommands<Folder> getCommands() {
        return commands;
    }

    @Override
    public String getAccWindowRes() {
        return accWindowRes;
    }

    @Override //Searchable
    public List<Folder> getCurrentItemList() {
        return currentItemList;
    }

    @Override //Searchable
    public void setCurrentItemSearchedList(List<Folder> currentItemList) {
        this.currentItemList = currentItemList;
    }

    @Override
    public void setAccController(FormView_ACCController<Folder> accController) {
        this.accController = (Folder_ACCController) accController;
    }

    @Override
    public FormView_ACCController<Folder> getAccController() {
        return accController;
    }
}
