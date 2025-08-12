package ru.wert.tubus.chogori.crashReport;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.tableView.ReadOnlyTableView;
import ru.wert.tubus.client.entity.models.CrashReport;
import ru.wert.tubus.winform.warnings.Warning2;

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
        this.setEditable(false);

        if (useContextMenu)
            createContextMenu();

        //По двойному клику открывается вкладка с отчетом
        this.setRowFactory(tv -> {
            TableRow<CrashReport> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    CrashReport clickedReport = row.getItem();
                    openReportInTab(clickedReport);
                }
            });

            return row;
        });
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

        MenuItem openItem = new MenuItem("Открыть отчет");
        openItem.setOnAction(this::openReports);

        MenuItem deleteItem = new MenuItem("Удалить");
        deleteItem.setOnAction(this::deleteReport);

        contextMenu.getItems().addAll(openItem, deleteItem);
        setContextMenu(contextMenu);
    }

    private void deleteReport(ActionEvent actionEvent) {
        // Получаем все выделенные отчеты
        ObservableList<CrashReport> selectedReports = getSelectionModel().getSelectedItems();

        if (selectedReports.isEmpty()) {
            return;
        }

        // Запрашиваем подтверждение удаления
        String message = selectedReports.size() > 1 ?
                "Вы уверены, что хотите удалить " + selectedReports.size() + " выделенных отчетов?" :
                "Вы уверены, что хотите удалить выделенный отчет?";

        if (!Warning2.create("ВНИМАНИЕ!",
                message,
                "Отменить будет невозможно.")) {
            return;
        }

        // Удаляем все выделенные отчеты

        for (CrashReport report : selectedReports){
            CH_CRASH_REPORTS.delete(report);
        }

        updateTableView();
    }

    /**
     * Открывает группу выделенных в таблице отчетов в отдельных вкладках
     * @param actionEvent, ActionEvent
     */
    private void openReports(ActionEvent actionEvent) {
        // Получаем список всех выделенных элементов
        ObservableList<CrashReport> selectedItems = getSelectionModel().getSelectedItems();

        // Если ничего не выделено, выходим
        if (selectedItems.isEmpty()) {
            return;
        }

        // Для каждого выделенного элемента создаем вкладку
        for (CrashReport item : selectedItems) {
            openReportInTab(item);
        }
    }

    /**
     * Открывает отчет в новой вкладке
     * @param item, CrashReport
     */
    private void openReportInTab(CrashReport item) {
        try {
            String date = String.valueOf(item.getDate());
            String report = item.getReport();

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
