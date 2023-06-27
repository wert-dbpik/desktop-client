package ru.wert.datapik.chogori.entities.prefixes;

import javafx.scene.control.TableColumn;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.chogori.common.interfaces.Sorting;
import ru.wert.datapik.chogori.common.tableView.RoutineTableView;
import ru.wert.datapik.chogori.entities.prefixes.commands._PrefixCommands;
import ru.wert.datapik.chogori.statics.Comparators;
import ru.wert.datapik.client.entity.models.Prefix;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_PREFIXES;

public class Prefix_TableView extends RoutineTableView<Prefix> implements Sorting<Prefix> {

    private static final String accWindowRes = "/chogori-fxml/prefixes/prefixesACC.fxml";
    private final _PrefixCommands commands;
    private Prefix_ContextMenu contextMenu;
    private List<Prefix> currentItemList = new ArrayList<>();
    @Getter@Setter private Object modifyingItem;

    private String searchedText = "";

    public Prefix_TableView(String itemName, boolean useContextMenu) {
        super(itemName);

        commands = new _PrefixCommands(this);

        if (useContextMenu)
            createContextMenu();
    }

    @Override
    public void setTableColumns() {

        TableColumn<Prefix, String> tcName = Prefix_Columns.createTcName();
        TableColumn<Prefix, String> tcNote = Prefix_Columns.createTcNote();

        getColumns().addAll(tcName, tcNote);
    }

    @Override
    public ItemCommands<Prefix> getCommands() {
        return null;
    }

    @Override
    public String getAccWindowRes() {
        return null;
    }

    @Override
    public void createContextMenu() {
        contextMenu = new Prefix_ContextMenu(this, commands, accWindowRes);
        setContextMenu(contextMenu);
    }

    @Override
    public List<Prefix> prepareList() {
        return CH_PREFIXES.findAll();
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
    public List<Prefix> getCurrentItemSearchedList() {
        return currentItemList;
    }

    @Override //IFormView
    public void setCurrentItemSearchedList(List<Prefix> currentItemList) {
        this.currentItemList = currentItemList;
    }

    @Override
    public void setAccController(FormView_ACCController<Prefix> accController) {

    }

    @Override
    public FormView_ACCController<Prefix> getAccController() {
        return null;
    }

    /**
     * Метод сортирует предложенный лист
     *
     * @param list List<P>
     */
    @Override
    public void sortItemList(List<Prefix> list) {
        list.sort(Comparators.usefulStringComparator());
    }
}
