package ru.wert.tubus.chogori.application.numJournal;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.tableView.ReadOnlyTableView;
import ru.wert.tubus.chogori.logging.Passport_Columns;
import ru.wert.tubus.chogori.statics.Comparators;
import ru.wert.tubus.client.entity.models.Passport;

import java.util.List;

import static java.util.Collections.reverse;
import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_LOGS;

public class NumJournal_TableView extends ReadOnlyTableView<Passport> {

    private List<Passport> currentItemList;
    private boolean adminOnly;

    private String searchedText = "";

    public NumJournal_TableView(String itemName, boolean adminOnly, boolean useContextMenu) {
        super(itemName);
        this.adminOnly = adminOnly;
        this.setEditable(true);

        if (useContextMenu)
            createContextMenu();
    }

    @Override
    public void setTableColumns() {

        TableColumn<Passport, String> tcNumber = NumJournal_Columns.createTcPassportNumber();
        TableColumn<Passport, String> tcName = NumJournal_Columns.createTcPassportName();
        TableColumn<Passport, String> tcSource = NumJournal_Columns.createTcSource();

        getColumns().addAll(tcNumber, tcName, tcSource);
    }

    @Override
    public void createContextMenu() {

    }

    @Override
    public List<Passport> prepareList() {
        List<Passport> logs;
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
    public List<Passport> getCurrentItemSearchedList() {
        return currentItemList;
    }

    @Override //IFormView
    public void setCurrentItemSearchedList(List<Passport> currentItemList) {
        this.currentItemList = currentItemList;
    }


    /**
     * Связь с контроллером окна добавления/изменения
     *
     * @param accController FormView_ACCController<P>
     */
    @Override
    public void setAccController(FormView_ACCController<Passport> accController) {

    }

    /**
     * Связь с контроллером окна добавления/изменения
     */
    @Override
    public FormView_ACCController<Passport> getAccController() {
        return null;
    }
}
