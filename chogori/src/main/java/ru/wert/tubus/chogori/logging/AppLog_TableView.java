package ru.wert.tubus.chogori.logging;

import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import ru.wert.tubus.chogori.statics.Comparators;
import ru.wert.tubus.client.entity.models.AppLog;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.tableView.ReadOnlyTableView;

import java.util.List;

import static java.util.Collections.reverse;
import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_LOGS;

public class AppLog_TableView extends ReadOnlyTableView<AppLog> {

    private List<AppLog> currentItemList;
    private boolean adminOnly;

    private String searchedText = "";

    public AppLog_TableView(String itemName, boolean adminOnly, boolean useContextMenu) {
        super(itemName);
        this.adminOnly = adminOnly;
        this.setEditable(true);

        if (useContextMenu)
            createContextMenu();
    }

    @Override
    public void setTableColumns() {

        TableColumn<AppLog, String> tcDate = AppLog_Columns.createTcDate();
        TableColumn<AppLog, String> tcUser = AppLog_Columns.createTcUser();
        TableColumn<AppLog, TextField> tcText = AppLog_Columns.createTcText();
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
//        if(logs != null) reverse(logs);
        if(logs != null) {
            logs.sort(Comparators.logsComparator());
            reverse(logs);
        }
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
