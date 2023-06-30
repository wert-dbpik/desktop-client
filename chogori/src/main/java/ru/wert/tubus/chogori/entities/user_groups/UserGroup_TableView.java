package ru.wert.tubus.chogori.entities.user_groups;

import javafx.scene.control.TableColumn;
import lombok.Getter;
import lombok.Setter;
import ru.wert.tubus.client.entity.models.UserGroup;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.interfaces.Sorting;
import ru.wert.tubus.chogori.common.tableView.RoutineTableView;
import ru.wert.tubus.chogori.entities.user_groups.commands._UserGroupCommands;
import ru.wert.tubus.chogori.statics.Comparators;
import ru.wert.tubus.chogori.application.services.ChogoriServices;


import java.util.ArrayList;
import java.util.List;

public class UserGroup_TableView extends RoutineTableView<UserGroup> implements Sorting<UserGroup> {

    private static final String accWindowRes = "/chogori-fxml/user_group/userGroupACC.fxml";
    private final _UserGroupCommands commands;
    private UserGroup_ContextMenu contextMenu;
    private List<UserGroup> currentItemList = new ArrayList<>();
    @Getter@Setter private Object modifyingItem;

    private String searchedText = "";

    public UserGroup_TableView(String itemName, boolean useContextMenu) {
        super(itemName);

        commands = new _UserGroupCommands(this);

        if (useContextMenu)
            createContextMenu();
    }

    @Override
    public void setTableColumns() {

        TableColumn<UserGroup, String> tcName = UserGroup_Columns.createTcName();

        getColumns().addAll(tcName);
    }

    @Override
    public ItemCommands<UserGroup> getCommands() {
        return null;
    }

    @Override
    public String getAccWindowRes() {
        return null;
    }

    @Override
    public void createContextMenu() {
        contextMenu = new UserGroup_ContextMenu(this, commands, accWindowRes);
        setContextMenu(contextMenu);
    }

    @Override
    public List<UserGroup> prepareList() {
        return ChogoriServices.CH_USER_GROUPS.findAll();
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
    public List<UserGroup> getCurrentItemSearchedList() {
        return currentItemList;
    }

    @Override //IFormView
    public void setCurrentItemSearchedList(List<UserGroup> currentItemList) {
        this.currentItemList = currentItemList;
    }

    @Override
    public void setAccController(FormView_ACCController<UserGroup> accController) {

    }

    @Override
    public FormView_ACCController<UserGroup> getAccController() {
        return null;
    }

    /**
     * Метод сортирует предложенный лист
     *
     * @param list List<P>
     */
    @Override
    public void sortItemList(List<UserGroup> list) {
        list.sort(Comparators.usefulStringComparator());
    }
}
