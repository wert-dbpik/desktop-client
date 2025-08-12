package ru.wert.tubus.chogori.crashReport;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.tableView.ReadOnlyTableView;
import ru.wert.tubus.client.entity.models.CrashReport;

import java.io.IOException;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_CRASH_REPORTS;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;

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
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Удалить");

        MenuItem openItem = new MenuItem("Открыть отчет");
        openItem.setOnAction(this::openReport);
        contextMenu.getItems().addAll(openItem, deleteItem);
        setContextMenu(contextMenu);
    }

    private void openReport(ActionEvent actionEvent) {
        try {
            String date = String.valueOf(getSelectionModel().getSelectedItem().getDate());
            String report = getSelectionModel().getSelectedItem().getReport();
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/chogori-fxml/crashReports/report.fxml"));
            Parent parent = loader.load();
            ((ReportController)loader.getController()).init(report);
            parent.getStylesheets().add(this.getClass().getResource("/chogori-css/details-dark.css").toString());
            String tabName = "crash: " + date;
            String tabId = tabName;
            CH_TAB_PANE.createNewTab(tabId, tabName, parent, true, loader.getController());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
