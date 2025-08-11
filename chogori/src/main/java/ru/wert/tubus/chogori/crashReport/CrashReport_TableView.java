package ru.wert.tubus.chogori.crashReport;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.tableView.ReadOnlyTableView;
import ru.wert.tubus.client.entity.models.CrashReport;

import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_CRASH_REPORTS;

public class CrashReport_TableView extends ReadOnlyTableView<CrashReport> {

    private List<CrashReport> currentItemList;
    private boolean adminOnly;

    private String searchedText = "";

    public CrashReport_TableView(String itemName, boolean adminOnly, boolean useContextMenu) {
        super(itemName);
        this.adminOnly = adminOnly;
        this.setEditable(true);

        if (useContextMenu)
            createContextMenu();
    }

    @Override
    public void setTableColumns() {

        TableColumn<CrashReport, String> tcDate = CrashReport_Columns.createTcDate();
        TableColumn<CrashReport, String> tcUser = CrashReport_Columns.createTcUser();
        TableColumn<CrashReport, Label> tcGadget = CrashReport_Columns.createTcGadget();
        TableColumn<CrashReport, String> tcVersion = CrashReport_Columns.createTcVersion();
        TableColumn<CrashReport, TextArea> tcStackTrace = CrashReport_Columns.createTcStackTrace();

        getColumns().addAll(tcDate, tcUser, tcGadget, tcVersion, tcStackTrace);

    }

    @Override
    public void createContextMenu() {

    }

    @Override
    public List<CrashReport> prepareList() {
        return CH_CRASH_REPORTS.findAll();
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
    public List<CrashReport> getCurrentItemSearchedList() {
        return currentItemList;
    }

    @Override //IFormView
    public void setCurrentItemSearchedList(List<CrashReport> currentItemList) {
        this.currentItemList = currentItemList;
    }


    /**
     * Связь с контроллером окна добавления/изменения
     *
     * @param accController FormView_ACCController<P>
     */
    @Override
    public void setAccController(FormView_ACCController<CrashReport> accController) {

    }

    /**
     * Связь с контроллером окна добавления/изменения
     */
    @Override
    public FormView_ACCController<CrashReport> getAccController() {
        return null;
    }
}
