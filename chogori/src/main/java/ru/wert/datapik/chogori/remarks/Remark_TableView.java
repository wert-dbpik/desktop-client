package ru.wert.datapik.chogori.remarks;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Remark;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.chogori.common.tableView.RoutineTableView;
import ru.wert.datapik.chogori.remarks.commands._RemarkCommands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.wert.datapik.chogori.services.ChogoriServices.CH_REMARKS;
import static ru.wert.datapik.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class Remark_TableView extends RoutineTableView<Remark>{

    private static final String accWindowRes = "/chogori-fxml/remarks/remarkACC.fxml";
    private final _RemarkCommands commands;
    private Remark_ContextMenu contextMenu;
    private List<Remark> currentItemList = new ArrayList<>();
    @Getter@Setter private Object modifyingItem;
    private Remark_ACCController accController;
    @Getter private Passport passport;

    private String searchedText = "";

    public Remark_TableView(String promptText, boolean useContextMenu, Passport passport) {
        super(promptText);
        this.passport = passport;

        commands = new _RemarkCommands(this);

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) CH_SEARCH_FIELD.changeSearchedTableView(this, "КОММЕНТАРИИ");
        });

        if (useContextMenu)
            createContextMenu();
    }

    @Override
    public void setTableColumns() {

        TableColumn<Remark, Parent> tcRemark = new TableColumn<>("Комментарий");
        tcRemark.setCellValueFactory(cd ->{
            Parent parent = null;
            try {
                Remark remark = cd.getValue();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/remarks/remarkEntry.fxml"));
                parent = loader.load();
                ((RemarkEntryController)loader.getController()).init(remark);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ReadOnlyObjectWrapper<>(parent);
        });


        getColumns().addAll(tcRemark);
    }

    @Override
    public ItemCommands<Remark> getCommands() {
        return null;
    }

    @Override
    public String getAccWindowRes() {
        return null;
    }

    @Override
    public void createContextMenu() {
        contextMenu = new Remark_ContextMenu(this, commands, accWindowRes);
        setContextMenu(contextMenu);
    }

    @Override
    public List<Remark> prepareList() {
        List<Remark> foundRemarks = CH_REMARKS.findAllByPassport(passport);
        if (foundRemarks.size() > 1) {
            foundRemarks =
                    new ArrayList<>(foundRemarks.stream()
                            .sorted((o1, o2) -> o2.getCreationTime().compareTo(o1.getCreationTime()))
                            .collect(Collectors.toList()));
        }

        return foundRemarks;
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
    public List<Remark> getCurrentItemSearchedList() {
        return currentItemList;
    }

    @Override //Searchable
    public void setCurrentItemSearchedList(List<Remark> currentItemList) {
        this.currentItemList = currentItemList;
    }

    @Override
    public void setAccController(FormView_ACCController<Remark> accController) {
        this.accController = (Remark_ACCController) accController;
    }

    @Override
    public FormView_ACCController<Remark> getAccController() {
        return accController;
    }


}
