package ru.wert.datapik.chogori.entities.drafts;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.chogori.common.utils.ClipboardUtils;
import ru.wert.datapik.chogori.entities.drafts.commands._Draft_Commands;
import ru.wert.datapik.winform.enums.EDraftStatus;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER_GROUP;

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
    private MenuItem openInOuterApp; //Открыть во внешнем приложении
    private MenuItem showRemarks; //Открыть комментарии
    private MenuItem showInfo; //Открыть информацию о чертеже

    //Условие, при котором список составлен только для одной папки
    private boolean condition;



    public Draft_ContextMenu(Draft_TableView tableView, _Draft_Commands commands, String draftsACCRes) {
        super(tableView, commands, draftsACCRes);
        this.commands = commands;
        this.tableView = tableView;

        editDraftsPermission = CH_CURRENT_USER_GROUP.isEditDrafts();

        manipulator = tableView.getManipulator();

        condition = !(tableView.getSelectedFolders() == null
                || tableView.getSelectedFolders().size() != 1);

        createMainMenuItems();

    }

    @Override
    public void createMainMenuItems() {
        boolean addItem = false;
        boolean copyItem = false;//копирование не применяется
        boolean changeItem = false;
        boolean deleteItem = false;

        List<Draft> selectedDrafts = tableView.getSelectionModel().getSelectedItems();

        if(editDraftsPermission) {
            if(condition) addItem = true;
            if (selectedDrafts.size() != 0) {
                if(CH_CURRENT_USER_GROUP.isDeleteDrafts())deleteItem = true;
                if (selectedDrafts.size() == 1){
                    Integer itemStatus = selectedDrafts.get(0).getStatus();
                    if (itemStatus.equals(EDraftStatus.LEGAL.getStatusId()))
                        changeItem = true;
                }
            }
        }


        createMenu(addItem, copyItem, changeItem, deleteItem);

        setAddMenuName("Добавить отдельные чертежи");
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
        boolean extraOpenInOuterApp = false;
        //================================
        boolean extraShowRemarks = false;
        boolean extraShowInfo = false;

        addFolder = new MenuItem("Добавить папку с чертежами");
        cutDrafts = new MenuItem("Вырезать");
        pasteDrafts = new MenuItem("Вставить");
        renameDraft = new MenuItem("Переименовать чертеж");
        replaceDraft = new MenuItem("Заменить");
        nullifyDraft = new MenuItem("Аннулировать");
        openInTab = new MenuItem("Открыть в отдельной вкладке" );
        openInOuterApp = new MenuItem("Открыть во внешней программе" );
        showRemarks = new MenuItem("Комментарии");
        showInfo = new MenuItem("Инфо");

        addFolder.setOnAction(commands::addFromFolder);
        cutDrafts.setOnAction(e-> ClipboardUtils.copyToClipboardText(manipulator.cutItems()));
        pasteDrafts.setOnAction(e->manipulator.pasteItems(ClipboardUtils.getStringFromClipboard()));
        renameDraft.setOnAction(commands::renameDraft);
        replaceDraft.setOnAction(commands::replaceDraft);
        nullifyDraft.setOnAction(commands::nullifyDraft);
        openInTab.setOnAction(commands::openInTab);
        openInOuterApp.setOnAction(commands::openInOuterApp);
        showRemarks.setOnAction(commands::showRemarks);
        showInfo.setOnAction(commands::showInfo);

        List<Draft> selectedDrafts = tableView.getSelectionModel().getSelectedItems();

        if(editDraftsPermission) {
            //Если ничего не выделено
            if (selectedDrafts.size() == 0) {
                extraAddFolder = true;//ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ
                if (manipulator.pastePossible(ClipboardUtils.getStringFromClipboard()))
                    extraPasteDrafts = true;

            } else if (selectedDrafts.size() == 1) {
                extraShowInfo = true;//ПОКАЗАТЬ ИНФОРМАЦИЮ О ЧЕРТЕЖЕ
                extraOpenInTab = true;//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
                extraOpenInOuterApp = true;//ОТКРЫТЬ ВО ВНЕШНЕМ ПРИЛОЖЕНИИ
                extraShowRemarks = true;//ОТКРЫТЬ КОММЕНТАРИИ
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
                extraCutDrafts = true; //ВЫРЕЗАТЬ чертежи
                if (condition) {
                    extraAddFolder = true; //ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ
                    if (manipulator.pastePossible(ClipboardUtils.getStringFromClipboard()))
                        extraPasteDrafts = true;
                }
            }
        } else{
            if(selectedDrafts.size() == 1) extraShowInfo = true; //ПОКАЗАТЬ ИНФОРМАЦИЮ О ЧЕРТЕЖЕ
            if(selectedDrafts.size() > 0)  extraOpenInTab = true;//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
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
        if ((extraOpenInTab || extraOpenInOuterApp) && (extraRenameDraft || extraReplaceDraft || extraNullifyDraft)) extraItems.add(new SeparatorMenuItem());//==================
        if (extraOpenInTab) extraItems.add(openInTab);//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
        if (extraOpenInOuterApp) extraItems.add(openInOuterApp);//ОТКРЫТЬ ВО ВНЕШНЕЙ ПРОГРАММЕ

        if (extraShowInfo) extraItems.add(new SeparatorMenuItem());//==================
        if (extraShowInfo) extraItems.add(showRemarks); //ПОКАЗАТЬ КОММЕНТАРИИ
        if (extraShowInfo) extraItems.add(showInfo); //ПОКАЗАТЬ ИНФОРМАЦИЮ О ЧЕРТЕЖЕ

        return extraItems;
    }


}
