package ru.wert.datapik.utils.entities.drafts;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.common.utils.ClipboardUtils;
import ru.wert.datapik.utils.entities.drafts.commands._Draft_Commands;
import ru.wert.datapik.winform.enums.EDraftStatus;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;

public class Draft_ContextMenu extends FormView_ContextMenu<Draft> {

    private final _Draft_Commands commands;
    private TableView<Draft> tableView;
    private Draft_Manipulator manipulator;

    private MenuItem cutDrafts; //Вырезать чертеж
    private MenuItem pasteDrafts; //Вставить чертеж

    private MenuItem renameDraft; //Заменить
    private MenuItem replaceDraft; //Заменить
    private MenuItem nullifyDraft; //Аннулировать
    private MenuItem addFolder; //Добавить папку
    private MenuItem openInTab; //Открыть в отдельной вкладке



    public Draft_ContextMenu(Draft_TableView tableView, _Draft_Commands commands, String draftsACCRes, Draft_Manipulator manipulator) {
        super(tableView, commands, draftsACCRes);
        this.commands = commands;
        this.tableView = tableView;
        this.manipulator = manipulator;

        createMainMenuItems();

    }

    @Override
    public void createMainMenuItems() {
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
        boolean extraAddFolder = false;
        //=============================
        boolean extraCutDrafts = false;
        boolean extraPasteDrafts = false;
        //=============================
        boolean extraRenameDraft = false;
        boolean extraReplaceDraft = false;
        boolean extraNullifyDraft = false;
        //=============================
        boolean extraOpenInTab = false;

        cutDrafts = new MenuItem("Вырезать");
        cutDrafts.setOnAction(e-> ClipboardUtils.copyToClipboardText(manipulator.cutItems()));

        pasteDrafts = new MenuItem("Вставить");
        pasteDrafts.setOnAction(e->manipulator.pasteItems(ClipboardUtils.getStringFromClipboard()));

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

        //Если ничего не выделено
        if (selectedDrafts.size() == 0) {
            extraAddFolder = true;//ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ
            if (manipulator.pastePossible(ClipboardUtils.getStringFromClipboard())) extraPasteDrafts = true;

        } else if (selectedDrafts.size() == 1) {
            extraAddFolder = true;//ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ
            extraCutDrafts = true;//ВЫРЕЗАТЬ
            extraOpenInTab = true;//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
            if (manipulator.pastePossible(ClipboardUtils.getStringFromClipboard())) extraPasteDrafts = true;
            if (selectedDrafts.get(0).getStatus().equals(EDraftStatus.LEGAL.getStatusId())) {
                extraRenameDraft = true; //ПЕРЕИМЕНОВАТЬ
                extraReplaceDraft = true;//ЗАМЕНИТЬ
                extraNullifyDraft = true;//АННУЛИРОВАТЬ
            }

        } else { //selectedDrafts.size() >1
            extraAddFolder = true; //ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ
            extraOpenInTab = true;//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
            if (manipulator.pastePossible(ClipboardUtils.getStringFromClipboard())) extraPasteDrafts = true;
        }

        List<MenuItem> extraItems = new ArrayList<>();

        if (extraAddFolder) extraItems.add(addFolder); //Добавить папку

        if (extraCutDrafts || extraPasteDrafts) extraItems.add(new SeparatorMenuItem());//===============
        if (extraCutDrafts) extraItems.add(cutDrafts);//ВЫРЕЗАТЬ ЧЕРТЕЖИ
        if (extraPasteDrafts) extraItems.add(pasteDrafts);//ВСТАВИТЬ ЧЕРТЕЖИ

        if (extraRenameDraft || extraReplaceDraft || extraNullifyDraft) extraItems.add(new SeparatorMenuItem());//==================
        if (extraRenameDraft) extraItems.add(renameDraft);//ПЕРЕИМЕНОВАТЬ
        if (extraReplaceDraft) extraItems.add(replaceDraft);//ЗАМЕНИТЬ
        if (extraNullifyDraft) extraItems.add(nullifyDraft);//АННУЛИРОВАТЬ

        if (extraOpenInTab || extraAddFolder) extraItems.add(new SeparatorMenuItem());//==================
        if (extraOpenInTab) extraItems.add(openInTab);//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
        if (extraAddFolder) extraItems.add(addFolder);//ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ

        return extraItems;
    }


}
