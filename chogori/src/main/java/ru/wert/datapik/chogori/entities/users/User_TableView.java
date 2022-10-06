package ru.wert.datapik.chogori.entities.users;

import javafx.scene.control.TableColumn;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.chogori.common.interfaces.Sorting;
import ru.wert.datapik.chogori.common.tableView.RoutineTableView;
import ru.wert.datapik.chogori.entities.users.commands._UserCommands;
import ru.wert.datapik.chogori.statics.Comparators;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.datapik.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class User_TableView extends RoutineTableView<User> implements Sorting<User> {

    private static final String accWindowRes = "/chogori-fxml/users/usersACC.fxml";
    private final _UserCommands commands;
    private User_ContextMenu contextMenu;
    private List<User> currentItemList = new ArrayList<>();
    @Getter@Setter private Object modifyingItem;
    private User_ACCController accController;

    private String searchedText = "";

    public User_TableView(String promptText, boolean useContextMenu) {
        super(promptText);

        commands = new _UserCommands(this);

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) CH_SEARCH_FIELD.changeSearchedTableView(this, "ПОЛЬЗОВАТЕЛЬ");
        });

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

    @Override //Searchable
    public List<User> getCurrentItemSearchedList() {
        return currentItemList;
    }

    @Override //Searchable
    public void setCurrentItemSearchedList(List<User> currentItemList) {
        this.currentItemList = currentItemList;
    }

    @Override
    public void setAccController(FormView_ACCController<User> accController) {
        this.accController = (User_ACCController) accController;
    }

    @Override
    public FormView_ACCController<User> getAccController() {
        return accController;
    }

    /**
     * Метод сортирует предложенный лист
     *
     * @param list List<P>
     */
    @Override
    public void sortItemList(List<User> list) {
        list.sort(Comparators.usefulStringComparator());
    }
}
