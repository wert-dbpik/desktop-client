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
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER_GROUP;

public class Draft_ContextMenu extends FormView_ContextMenu<Draft> {

    private final _Draft_Commands commands;
    private Draft_TableView tableView;
    private Draft_Manipulator manipulator;
    private final boolean editDraftsPermission;

    private MenuItem cutDrafts; //Вырезать чертеж
    private MenuItem pasteDrafts; //Вставить чертеж

    private MenuItem renameDraft; //Заменить
    private MenuItem replaceDraft; //Заменить
    private MenuItem nullifyDraft; //Аннулировать
    private MenuItem addFolder; //Добавить папку
    private MenuItem openInTab; //Открыть в отдельной вкладке

    //Условие, при котором список не представляет все сразу или пв списке чертежи из более, чем одна папка
    private final boolean condition;



    public Draft_ContextMenu(Draft_TableView tableView, _Draft_Commands commands, String draftsACCRes) {
        super(tableView, commands, draftsACCRes);
        this.commands = commands;
        this.tableView = tableView;

        editDraftsPermission = CH_CURRENT_USER_GROUP.isEditDrafts();

        manipulator = tableView.getManipulator();

        condition = !(tableView.getSelectedFolders() == null || tableView.getSelectedFolders().size() == 1);

        createMainMenuItems();

    }

    @Override
    public void createMainMenuItems() {
        boolean addItem = false;
        boolean copyItem = false;//копирование не применяется
        boolean changeItem = false;
        boolean deleteItem = false;

        if(CH_CURRENT_USER.getUserGroup().isDeleteDrafts()) {
            deleteItem = true;
        }

        List<Draft> selectedDrafts = tableView.getSelectionModel().getSelectedItems();

        if(editDraftsPermission) {
            if(condition) addItem = true;
            if (selectedDrafts.size() != 0) {
                deleteItem = true;
                if (selectedDrafts.size() == 1){
                    Integer itemStatus = selectedDrafts.get(0).getStatus();
                    if (itemStatus.equals(EDraftStatus.LEGAL.getStatusId()))
                        changeItem = true;
                }
            }
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

        addFolder = new MenuItem("Добавить папку с чертежами");
        cutDrafts = new MenuItem("Вырезать");
        pasteDrafts = new MenuItem("Вставить");
        renameDraft = new MenuItem("Переименовать чертеж");
        replaceDraft = new MenuItem("Заменить");
        nullifyDraft = new MenuItem("Аннулировать");
        openInTab = new MenuItem("Открыть в отдельной вкладке" );


        addFolder.setOnAction(commands::addFromFolder);
        cutDrafts.setOnAction(e-> ClipboardUtils.copyToClipboardText(manipulator.cutItems()));
        pasteDrafts.setOnAction(e->manipulator.pasteItems(ClipboardUtils.getStringFromClipboard()));
        renameDraft.setOnAction(commands::renameDraft);
        replaceDraft.setOnAction(commands::replaceDraft);
        nullifyDraft.setOnAction(commands::nullifyDraft);
        openInTab.setOnAction(commands::openInTab);

        List<Draft> selectedDrafts = tableView.getSelectionModel().getSelectedItems();

        if(editDraftsPermission) {
            //Если ничего не выделено
            if (selectedDrafts.size() == 0) {
                extraAddFolder = true;//ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ
                if (manipulator.pastePossible(ClipboardUtils.getStringFromClipboard()))
                    extraPasteDrafts = true;

            } else if (selectedDrafts.size() == 1) {
                extraOpenInTab = true;//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
                //Следующие операции допустимы только с ДЕЙСТВУЮЩИМИ чертежами
                if (selectedDrafts.get(0).getStatus().equals(EDraftStatus.LEGAL.getStatusId()) && editDraftsPermission) {
                    extraRenameDraft = true; //ПЕРЕИМЕНОВАТЬ
                    extraReplaceDraft = true;//ЗАМЕНИТЬ
                    extraNullifyDraft = true;//АННУЛИРОВАТЬ
                }

                if (condition) {
                    extraAddFolder = true;//ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ
                    extraCutDrafts = true;//ВЫРЕЗАТЬ
                    if (manipulator.pastePossible(ClipboardUtils.getStringFromClipboard()))
                        extraPasteDrafts = true;

                }

            } else { //selectedDrafts.size() >1
                extraOpenInTab = true;//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
                if (condition) {
                    extraAddFolder = true; //ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ
                    if (manipulator.pastePossible(ClipboardUtils.getStringFromClipboard()))
                        extraPasteDrafts = true;
                }
            }
        } else{
            if(selectedDrafts.size() > 0)
                extraOpenInTab = true;//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
        }

        List<MenuItem> extraItems = new ArrayList<>();

        if (extraAddFolder) extraItems.add(addFolder); //ДОБАВИТЬ ПАПКУ

        if ((extraCutDrafts || extraPasteDrafts) && extraAddFolder) extraItems.add(new SeparatorMenuItem());//===============
        if (extraCutDrafts) extraItems.add(cutDrafts);//ВЫРЕЗАТЬ ЧЕРТЕЖИ
        if (extraPasteDrafts) extraItems.add(pasteDrafts);//ВСТАВИТЬ ЧЕРТЕЖИ

        if ((extraRenameDraft || extraReplaceDraft || extraNullifyDraft) && (extraCutDrafts || extraPasteDrafts)) extraItems.add(new SeparatorMenuItem());//==================
        if (extraRenameDraft) extraItems.add(renameDraft);//ПЕРЕИМЕНОВАТЬ
        if (extraReplaceDraft) extraItems.add(replaceDraft);//ЗАМЕНИТЬ
        if (extraNullifyDraft) extraItems.add(nullifyDraft);//АННУЛИРОВАТЬ
//
        if (extraOpenInTab && (extraRenameDraft || extraReplaceDraft || extraNullifyDraft)) extraItems.add(new SeparatorMenuItem());//==================
        if (extraOpenInTab) extraItems.add(openInTab);//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ


        return extraItems;
    }


}
