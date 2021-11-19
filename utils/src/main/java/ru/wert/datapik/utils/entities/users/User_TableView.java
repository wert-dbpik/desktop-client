package ru.wert.datapik.utils.entities.users;

import javafx.scene.control.TableColumn;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.tableView.RoutineTableView;
import ru.wert.datapik.utils.entities.users.commands._UserCommands;

import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_USERS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class User_TableView extends RoutineTableView<User> {

    private static final String accWindowRes = "/utils-fxml/users/usersACC.fxml";
    private final _UserCommands commands;
    private User_ContextMenu contextMenu;
    private List<User> currentItemList;
    @Getter@Setter private Object modifyingItem;

    private String searchedText = "";

    public User_TableView(String itemName, boolean useContextMenu) {
        super(itemName);

        commands = new _UserCommands(this);

        if (useContextMenu)
            createContextMenu();
    }

    @Override
    public void setTableColumns() {

        TableColumn<User, String> tcName = User_Columns.createTcName();
        TableColumn<User, String> tcPassword = User_Columns.createTcPassword();
        TableColumn<User, String> tcUserGroup = User_Columns.createTcUserGroup();

        getColumns().addAll(tcName, tcPassword, tcUserGroup);
    }

    @Override
    public ItemCommands<User> getCommands() {
        return null;
    }

    @Override
    public String getAccWindowRes() {
        return null;
    }

    @Override
    public void createContextMenu() {
        contextMenu = new User_ContextMenu(this, commands, accWindowRes);
        setContextMenu(contextMenu);
    }

    @Override
    public List<User> prepareList() {
        return CH_USERS.findAll();
    }

    @Override
    public void setSearchedText(String searchedText) {
        this.searchedText = searchedText;
    }

    @Override
    public String getSearchedText() {
        return searchedText;
    }

    @Override //IFormView
    public List<User> getCurrentItemList() {
        return currentItemList;
    }

    @Override //IFormView
    public void setCurrentItemSearchedList(List<User> currentItemList) {
        this.currentItemList = currentItemList;
    }

    @Override
    public void setAccController(FormView_ACCController<User> accController) {

    }

    @Override
    public FormView_ACCController<User> getAccController() {
        return null;
    }
}
