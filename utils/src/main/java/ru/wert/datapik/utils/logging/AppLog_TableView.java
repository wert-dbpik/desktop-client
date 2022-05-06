package ru.wert.datapik.utils.logging;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import ru.wert.datapik.client.entity.models.AppLog;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.tableView.ReadOnlyTableView;

import java.util.Comparator;
import java.util.List;

import static java.util.Collections.reverse;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_LOGS;

public class AppLog_TableView extends ReadOnlyTableView<AppLog> {

    private List<AppLog> currentItemList;
    private boolean adminOnly;

    private String searchedText = "";

    public AppLog_TableView(String itemName, boolean adminOnly, boolean useContextMenu) {
        super(itemName);
        this.adminOnly = adminOnly;

        if (useContextMenu)
            createContextMenu();
    }

    @Override
    public void setTableColumns() {

        TableColumn<AppLog, String> tcDate = AppLog_Columns.createTcDate();
        TableColumn<AppLog, String> tcUser = AppLog_Columns.createTcUser();
        TableColumn<AppLog, String> tcText = AppLog_Columns.createTcText();
        TableColumn<AppLog, Label> tcApplication = AppLog_Columns.createTcApplication();
        TableColumn<AppLog, String> tcVersion = AppLog_Columns.createTcVersion();

        if(adminOnly)
            getColumns().addAll(tcDate, tcUser, tcText);
        else
            getColumns().addAll(tcDate, tcUser, tcText, tcApplication, tcVersion);
    }

    @Override
    public void createContextMenu() {

    }

    @Override
    public List<AppLog> prepareList() {
        List<AppLog> logs;
        if(adminOnly) logs = CH_LOGS.findAllByAdminOnlyFalse();
        else logs = CH_LOGS.findAll();
        reverse(logs);
        return logs;
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
    public List<AppLog> getCurrentItemSearchedList() {
        return currentItemList;
    }

    @Override //IFormView
    public void setCurrentItemSearchedList(List<AppLog> currentItemList) {
        this.currentItemList = currentItemList;
    }


    /**
     * Связь с контроллером окна добавления/изменения
     *
     * @param accController FormView_ACCController<P>
     */
    @Override
    public void setAccController(FormView_ACCController<AppLog> accController) {

    }

    /**
     * Связь с контроллером окна добавления/изменения
     */
    @Override
    public FormView_ACCController<AppLog> getAccController() {
        return null;
    }
}
