package ru.wert.tubus.chogori.entities.drafts;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import ru.wert.tubus.chogori.statics.AppStatic;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.tubus.chogori.common.utils.ClipboardUtils;
import ru.wert.tubus.chogori.entities.drafts.commands._Draft_Commands;
import ru.wert.tubus.chogori.setteings.ChogoriSettings;
import ru.wert.tubus.winform.enums.EDraftStatus;
import ru.wert.tubus.winform.enums.EDraftType;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.tubus.chogori.statics.AppStatic.DXF_DOCKS;

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
    private MenuItem openFolderWithDraft; //Открыть комплект с этим чертежом
    private MenuItem showRemarks; //Открыть комментарии
    private MenuItem showInfo; //Открыть информацию о чертеже
    private MenuItem downloadDrafts; //Скачать документ

    //Условие, при котором список составлен только для одной папки
    private boolean condition;



    public Draft_ContextMenu(Draft_TableView tableView, _Draft_Commands commands, String draftsACCRes) {
        super(tableView, commands, draftsACCRes);
        this.commands = commands;
        this.tableView = tableView;

        editDraftsPermission = ChogoriSettings.CH_CURRENT_USER_GROUP.isEditDrafts();

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
                if(ChogoriSettings.CH_CURRENT_USER_GROUP.isDeleteDrafts())deleteItem = true;
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
        boolean extraShowFolderWithDraft = false;
        boolean extraShowRemarks = false;
        boolean extraShowInfo = false;
        //======================================
        boolean extraDownloadDrafts = false;

        List<Draft> selectedDrafts = tableView.getSelectionModel().getSelectedItems();

        addFolder = new MenuItem("Добавить папку с чертежами");
        cutDrafts = new MenuItem("Перенести");
        pasteDrafts = new MenuItem("Вставить");
        renameDraft = new MenuItem("Переименовать чертеж");
        replaceDraft = new MenuItem("Заменить");
        nullifyDraft = new MenuItem("Аннулировать");
        openInTab = new MenuItem("Открыть в отдельной вкладке" );
        openInOuterApp = new MenuItem("Открыть во внешней программе" );
        openFolderWithDraft = new MenuItem("Перейти в комплект с этим чертежом" );
        showRemarks = new MenuItem("Комментарии");
        showInfo = new MenuItem("Инфо");
        downloadDrafts = new MenuItem("Скачать DXF");

        addFolder.setOnAction(commands::addFromFolder);
        cutDrafts.setOnAction(e-> ClipboardUtils.copyToClipboardText(manipulator.cutItems()));
        pasteDrafts.setOnAction(e->manipulator.pasteItems(ClipboardUtils.getStringFromClipboardOutOfFXThread()));
        renameDraft.setOnAction(commands::renameDraft);
        replaceDraft.setOnAction(commands::replaceDraft);
        nullifyDraft.setOnAction(commands::nullifyDraft);
        openInTab.setOnAction(commands::openInTab);
        openInOuterApp.setOnAction(e->{
            Draft selectedDraft = tableView.getSelectionModel().getSelectedItem();
            AppStatic.openDraftInPreviewer(
                    selectedDraft,
                    tableView.getPreviewerController(),
                    true);
            AppStatic.openInOuterApplication(selectedDraft);
        });
        openFolderWithDraft.setOnAction(commands::goToFolderWithTheDraft);
        showRemarks.setOnAction(commands::showRemarks);
        showInfo.setOnAction(commands::showInfo);
        downloadDrafts.setOnAction(e->manipulator.downloadDrafts(selectedDrafts));

        if(!selectedDrafts.isEmpty() && downloadIsPossible(selectedDrafts))
            extraDownloadDrafts = true;

        if(selectedDrafts.size() == 1) {
            extraOpenInOuterApp = true;//ОТКРЫТЬ ВО ВНЕШНЕМ ПРИЛОЖЕНИИ
            extraCutDrafts = true; //ВЫРЕЗАТЬ ЧЕРТЕЖ ДЛЯ ПЕРЕНОСА
            extraShowRemarks = true;//ОТКРЫТЬ КОММЕНТАРИИ
            extraShowFolderWithDraft = true; //ПЕРЕЙТИ В КОМПЛЕКТ С ЭТИМ ЧЕРТЕЖОМ
            extraShowInfo = true; //ПОКАЗАТЬ ИНФОРМАЦИЮ О ЧЕРТЕЖЕ

        }
        if(selectedDrafts.size() > 0)  extraOpenInTab = true;//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ

        if(editDraftsPermission) {

            if (manipulator.pastePossible(ClipboardUtils.getStringFromClipboardOutOfFXThread()))
                extraPasteDrafts = true; //ВСТАВИТЬ ЧЕРТЕЖИ

            //Если ничего не выделено
            if (selectedDrafts.size() == 0) {
                extraAddFolder = true;//ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ
            } else if (selectedDrafts.size() == 1) {

                //Следующие операции допустимы только с ДЕЙСТВУЮЩИМИ чертежами
                if (selectedDrafts.get(0).getStatus().equals(EDraftStatus.LEGAL.getStatusId()) && editDraftsPermission) {
                    extraRenameDraft = true; //ПЕРЕИМЕНОВАТЬ
                    extraReplaceDraft = true;//ЗАМЕНИТЬ
                    extraNullifyDraft = true;//АННУЛИРОВАТЬ
                }

                if (condition) {
                    extraAddFolder = true;//ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ
                    extraCutDrafts = true;//ВЫРЕЗАТЬ
                }

            } else { //selectedDrafts.size() >1
                extraOpenInTab = true;//ОТКРЫТЬ В ОТДЕЛЬНОЙ ВКЛАДКЕ
                extraCutDrafts = true; //ВЫРЕЗАТЬ чертежи
                if (condition) {
                    extraAddFolder = true; //ДОБАВИТЬ ПАПКУ С ЧЕРТЕЖАМИ
                }
            }
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
        if (extraShowFolderWithDraft) extraItems.add(openFolderWithDraft); //ПЕРЕЙТИ В КОМПЛЕКТ С ЧЕРТЕЖОМ
        if (extraDownloadDrafts) extraItems.add(downloadDrafts); //СКАЧАТЬ ДОКУМЕНТЫ
        if (extraShowRemarks) extraItems.add(showRemarks); //ПОКАЗАТЬ КОММЕНТАРИИ
        if (extraShowInfo) extraItems.add(showInfo); //ПОКАЗАТЬ ИНФОРМАЦИЮ О ЧЕРТЕЖЕ



        return extraItems;
    }

    /**
     * Метод проверяет, есть ли среди выделенных документов такой, который можно скачать
     * Если в списке есть хотя бы один такой документ, то возвращается true
     * @param selectedDrafts - выделенные чертежи
     * @return boolean
     */
    private boolean downloadIsPossible(List<Draft> selectedDrafts){
        for(Draft d : selectedDrafts){
            EDraftType type = EDraftType.getDraftTypeById(d.getDraftType());
            if(DXF_DOCKS.contains(type)) return true;
        }
        return false;
    }


}
