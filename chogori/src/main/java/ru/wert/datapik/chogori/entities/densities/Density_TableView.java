package ru.wert.datapik.chogori.entities.densities;

import javafx.scene.control.TableColumn;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.chogori.common.interfaces.Sorting;
import ru.wert.datapik.chogori.common.tableView.RoutineTableView;
import ru.wert.datapik.chogori.entities.densities.commands._DensityCommands;
import ru.wert.datapik.chogori.statics.Comparators;
import ru.wert.datapik.client.entity.models.Density;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_DENSITIES;

public class Density_TableView extends RoutineTableView<Density> implements Sorting<Density> {

    private static final String accWindowRes = "/chogori-fxml/densities/densitiesACC.fxml";
    private final _DensityCommands commands;
    private Density_ContextMenu contextMenu;
    private List<Density> currentItemList = new ArrayList<>();
    @Getter@Setter private Object modifyingItem;

    private String searchedText = "";

    public Density_TableView(String itemName, boolean useContextMenu) {
        super(itemName);

        commands = new _DensityCommands(this);

        if (useContextMenu)
            createContextMenu();
    }

    @Override
    public void setTableColumns() {

        TableColumn<Density, String> tcName = Density_Columns.createTcName();
        TableColumn<Density, String> tcNote = Density_Columns.createTcNote();
        TableColumn<Density, String> tcAmount = Density_Columns.createTcAmount();

        getColumns().addAll(tcName, tcAmount, tcNote);
    }

    @Override
    public ItemCommands<Density> getCommands() {
        return null;
    }

    @Override
    public String getAccWindowRes() {
        return null;
    }

    @Override
    public void createContextMenu() {
        contextMenu = new Density_ContextMenu(this, commands, accWindowRes);
        setContextMenu(contextMenu);
    }

    @Override
    public List<Density> prepareList() {
        return CH_DENSITIES.findAll();
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
    public List<Density> getCurrentItemSearchedList() {
        return currentItemList;
    }

    @Override //IFormView
    public void setCurrentItemSearchedList(List<Density> currentItemList) {
        this.currentItemList = currentItemList;
    }

    @Override
    public void setAccController(FormView_ACCController<Density> accController) {

    }

    @Override
    public FormView_ACCController<Density> getAccController() {
        return null;
    }

    /**
     * Метод сортирует предложенный лист
     *
     * @param list List<P>
     */
    @Override
    public void sortItemList(List<Density> list) {
        list.sort(Comparators.usefulStringComparator());
    }
}
