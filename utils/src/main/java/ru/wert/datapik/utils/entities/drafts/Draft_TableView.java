package ru.wert.datapik.utils.entities.drafts;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.Sorting;
import ru.wert.datapik.utils.common.tableView.RoutineTableView;
import ru.wert.datapik.utils.entities.drafts.commands._Draft_Commands;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.statics.Comparators;
import ru.wert.datapik.winform.enums.EDraftStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_DRAFTS;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER_GROUP;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class Draft_TableView extends RoutineTableView<Draft> implements Sorting<Draft> {

    private static final String accWindowRes = "/utils-fxml/drafts/draftACC.fxml";
    private _Draft_Commands commands;
    private PreviewerPatchController previewerController;
    @Setter private Object modifyingClass;
    @Getter@Setter private Object modifyingItem; //Product или Folder
    private List<Draft> currentItemList = new ArrayList<>(); //Лист чертежей, отображаемых в таблице сейчас
    private Draft_ACCController accController;
    @Getter private Draft_Manipulator manipulator;
    @Getter@Setter private String searchedText = "";

    @Getter@Setter private List<Folder> tempSelectedFolders; //Обнуляется после расчетов
    @Getter@Setter private List<Folder> selectedFolders;//ForContextMenu и не только

    @Getter ListProperty<Draft> preparedList = new SimpleListProperty<>();

    //Фильтр
    @Getter@Setter private boolean showValid = true; //ДЕЙСТВУЮЩИЕ - по умолчанию
    @Getter@Setter private boolean showChanged = true; //ЗАМЕНЕНННЫЕ
    @Getter@Setter private boolean showAnnulled; //АННУЛИРОВАННЫЕ

    private TableColumn<Draft, String> tcId;
    private TableColumn<Draft, VBox> tcPassport; //Колонка Идентификатор
    private TableColumn<Draft, Label> tcDraftNumber; //Номер чертежа
    private TableColumn<Draft, Label> tcDraftName; //Наименование чертежа
    private TableColumn<Draft, String> tcDraftType; //Тип чертежа
    private TableColumn<Draft, Label> tcStatus; //Статус
    private TableColumn<Draft, String> tcInitialName; //Наименование файла
    private TableColumn<Draft, String> tcCreationTime; //Дата создания
    private TableColumn<Draft, String> tcNote;   //Колонка Комментарии

    //Показывать колонки
    @Getter@Setter private boolean showId; //Идентификатор
    @Getter@Setter private boolean showIdentity; //Идентификатор
    @Getter@Setter private boolean showNumber; //Дец номер
    @Getter@Setter private boolean showName; //Наименование
    @Getter@Setter private boolean showDraftType  = true; //Тип чертежа
    @Getter@Setter private boolean showStatus  = true; //Статус
    @Getter@Setter private boolean showInitialName; //Изначальное имя файла
    @Getter@Setter private boolean showCreationTime; //Время создания
    @Getter@Setter private boolean showNote  = true; //Примечание


    /**
     * Конструктор для таблицы, связанной с предпросмотром чертежей
     * @param promptText String, текст, добавляемый в поисковую строку
     * @param previewerController PreviewerNoTBController контроллер окна предпросмотра
     */
    public Draft_TableView(String promptText, PreviewerPatchController previewerController) {
        super(promptText);
        this.previewerController = previewerController;

        if(CH_CURRENT_USER_GROUP.isEditDrafts()) manipulator = new Draft_Manipulator(this);

        commands = new _Draft_Commands(this);

        createContextMenu();

        //Слушатель работает с задержкой 0,5 сек
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            new Thread(()->{
                try {
                    Thread.sleep(500);
                    if (newValue == getSelectionModel().getSelectedItem()) {
                        if (newValue == null || newValue.getId() == null)
                            return;
                        Platform.runLater(() -> {
                            if (!getAltOnProperty().get()) {
                                AppStatic.openDraftInPreviewer(newValue, previewerController);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        });

        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.isAltDown()) {
                if (e.getClickCount() == 2)
                    AppStatic.openDraftsInNewTabs(getSelectionModel().getSelectedItems());
                else {
                    if(getAltOnProperty().get()) {
                        Platform.runLater(()->{
                            Draft selectedDraft = getSelectionModel().getSelectedItem();
                            if(selectedDraft != null)
                                AppStatic.openDraftInPreviewer(selectedDraft, previewerController);
                        });
                    }
                }
                e.consume();
            }
        });

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) CH_SEARCH_FIELD.changeSearchedTableView(this, "ЧЕРТЕЖ");
        });

    }

    @Override
    public void setTableColumns() {

        tcId = Draft_Columns.createTcId(); //id Чертежа
        tcPassport = Draft_Columns.createTcPassport(); //Колонка Чертеж
        tcDraftNumber = Draft_Columns.createTcDraftNumber(); //Колонка Дец номер
        tcDraftName = Draft_Columns.createTcDraftName(); //Колонка Наименование
        tcDraftType = Draft_Columns.createTcDraftType(); //Тип чертежа
        tcStatus = Draft_Columns.createTcStatus(); //Статус
        tcInitialName = Draft_Columns.createTcInitialDraftName(); //Наименование файла
        tcCreationTime = Draft_Columns.createTcCreation(); //Дата создания
        tcNote = Draft_Columns.createTcNote();   //Колонка Комментарии

        getColumns().addAll(tcId, tcPassport, tcDraftNumber, tcDraftName, tcDraftType, tcStatus, tcInitialName, tcCreationTime, tcNote);

    }

    /**
     * Метод выключает ненужные столбцы
     */
    public void showTableColumns(boolean useTcId, boolean useTcPassport, boolean useTcDraftType, boolean useTcStatus,
                                 boolean useTcInitialName, boolean useTcCreationTime, boolean useTcNote){
        tcId.setVisible(useTcId);
        showId = useTcId;

        tcPassport.setVisible(useTcPassport);
        showIdentity = useTcPassport;

        tcDraftNumber.setVisible(!useTcPassport);
        showNumber = !useTcPassport;

        tcDraftName.setVisible(!useTcPassport);
        showName = !useTcPassport;

        tcDraftType.setVisible(useTcDraftType);
        showDraftType = useTcDraftType;

        tcStatus.setVisible(useTcStatus);
        showStatus = useTcStatus;

        tcInitialName.setVisible(useTcInitialName);
        showInitialName = useTcInitialName;

        tcCreationTime.setVisible(useTcCreationTime);
        showCreationTime = useTcCreationTime;

        tcNote.setVisible(useTcNote);
        showNote = useTcNote;

    }


    @Override
    public void createContextMenu() {
        setOnContextMenuRequested(event->{
            contextMenu = new Draft_ContextMenu(this, commands, accWindowRes);
            contextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getSceneY());
        });
    }

    @Override
    public List<Draft> prepareList() {
        List<Draft> list = new ArrayList<>();
        if(modifyingClass instanceof Folder){
            if(tempSelectedFolders == null || tempSelectedFolders.isEmpty()) {
                if (modifyingItem == null)
                    list = CH_QUICK_DRAFTS.findAll();
                else {
                    list = CH_QUICK_DRAFTS.findAllByFolder((Folder) modifyingItem);
                }
            } else {
                for(Folder folder: tempSelectedFolders){
                    list.addAll(CH_QUICK_DRAFTS.findAllByFolder(folder));
                }
                selectedFolders = tempSelectedFolders;
                tempSelectedFolders = null;
            }
        }

        else if(modifyingClass instanceof Passport){
            if(modifyingItem == null)
                list = new ArrayList<>();
            else {
                list = CH_QUICK_DRAFTS.findByPassport((Passport)modifyingItem);
            }
        }

        filterList(list);
        preparedList.set(FXCollections.observableArrayList(list));

        return list;
    }

    /**
     * Метод фильтрует переданный список чертежей по статусу
     * @param items List<Draft>
     */
    public void filterList(List<Draft> items) {
        if(items.isEmpty()) return;
        Iterator<Draft> i = items.iterator();
        while (i.hasNext()) {
            Draft d = i.next();
            EDraftStatus status = EDraftStatus.getStatusById(d.getStatus());
            if (status != null) {
                if ((status.equals(EDraftStatus.LEGAL) && !isShowValid()) ||
                        (status.equals(EDraftStatus.CHANGED) && !isShowChanged()) ||
                        (status.equals(EDraftStatus.ANNULLED) && !isShowAnnulled()))
                    i.remove();
            }

        }
    }

    /**
     * Обновляет таблицу из других мест
     * @param item
     */
    public void updateDraftTableView(Draft item) {
        Platform.runLater(() -> {
            updateView();
            scrollTo(item);
            getSelectionModel().select(item);
        });
    }
    
    @Override
    public ItemCommands<Draft> getCommands() {
        return commands;
    }

    @Override
    public String getAccWindowRes() {
        return accWindowRes;
    }


    public PreviewerPatchController getPreviewerController(){
        return previewerController;
    }

    @Override
    public void sortItemList(List<Draft> list) {
        list.sort(Comparators.draftsComparator());
    }


    @Override //Searchable
    public List<Draft> getCurrentItemSearchedList() {
        return currentItemList;
    }

    @Override //Searchable
    public void setCurrentItemSearchedList(List<Draft> currentItemList) {
        this.currentItemList = currentItemList;
    }


    public void setAccController(FormView_ACCController<Draft> accController){
        this.accController = (Draft_ACCController) accController;
    }

    public Draft_ACCController getAccController(){
        return accController;
    }


}
