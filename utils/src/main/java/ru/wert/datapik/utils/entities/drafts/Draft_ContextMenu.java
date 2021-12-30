package ru.wert.datapik.utils.entities.drafts;

import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.entities.drafts.commands._Draft_Commands;
import ru.wert.datapik.winform.enums.EDraftStatus;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class Draft_ContextMenu extends FormView_ContextMenu<Draft> {

    private final _Draft_Commands commands;
    private TableView<Draft> tableView;

    private MenuItem renameDraft; //Заменить
    private MenuItem replaceDraft; //Заменить
    private MenuItem nullifyDraft; //Аннулировать
    private MenuItem addFolder; //Добавить папку
    private MenuItem openInTab; //Открыть в отдельной вкладке


    public Draft_ContextMenu(Draft_TableView tableView, _Draft_Commands commands, String draftsACCRes) {
        super(tableView, commands, draftsACCRes);
        this.commands = commands;
        this.tableView = tableView;

        createOnShowing();

    }

    @Override
    public void createOnShowing() {
        boolean addItem = true;
        boolean copyItem = false;//копирование не применяется
        boolean changeItem = true;
        boolean deleteItem = false;

        if(CH_CURRENT_USER.getUserGroup().isDeleteDrafts()) {
            deleteItem = true;
        }

        List<Draft> selectedDrafts = tableView.getSelectionModel().getSelectedItems();

        if(selectedDrafts.size() == 0) {
            changeItem = false;
            deleteItem = false;
        }else if(selectedDrafts.size() == 1
                    && (selectedDrafts.get(0).getStatus().equals(EDraftStatus.CHANGED.getStatusId())
                    || selectedDrafts.get(0).getStatus().equals(EDraftStatus.ANNULLED.getStatusId()))){
            changeItem = false;
        } else if(selectedDrafts.size() > 1){
            changeItem = false;
        }


        createMenu(addItem, copyItem, changeItem, deleteItem);

        setAddMenuName("Добавить чертеж");
    }

    @Override
    public List<MenuItem> createExtraItems(){

        List<MenuItem> extraItems = new ArrayList<>();

        renameDraft = new MenuItem("Переименовать чертеж");
        renameDraft.setOnAction(commands::renameDraft);

        replaceDraft = new MenuItem("Заменить");
        replaceDraft.setOnAction(commands::replaceDraft);

        nullifyDraft = new MenuItem("Аннулировать");
        nullifyDraft.setOnAction(commands::nullifyDraft);

        openInTab = new MenuItem("Открыть в отдельной вкладке" );
        openInTab.setOnAction(commands::openInTab);

        addFolder = new MenuItem("Добавить папку с чертежами");
        addFolder.setOnAction(commands::addFromFolder);

        List<Draft> selectedDrafts = tableView.getSelectionModel().getSelectedItems();

        //======================================================
        extraItems.add(addFolder);
        extraItems.add(new SeparatorMenuItem());//===============
        //Заменить можно только один чертеж за раз не являющегося уже ЗАМЕНЕННЫМ или АННУЛИРОВАННЫМ
        if (selectedDrafts.size() == 1
                && selectedDrafts.get(0).getStatus().equals(EDraftStatus.LEGAL.getStatusId())) {//ДЕЙСТВУЮЩИЙ
            extraItems.add(renameDraft); //ПЕРЕИМЕНОВАТЬ
            extraItems.add(replaceDraft);//ЗАМЕНИТЬ
            extraItems.add(nullifyDraft);//АННУЛИРОВАТЬ
        }
        extraItems.add(new SeparatorMenuItem());//==================
        extraItems.add(openInTab);//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
        return extraItems;
    }


}
