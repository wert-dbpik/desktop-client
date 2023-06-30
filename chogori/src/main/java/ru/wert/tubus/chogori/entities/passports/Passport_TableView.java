package ru.wert.tubus.chogori.entities.passports;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.chogori.common.commands.ItemCommands;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ACCController;
import ru.wert.tubus.chogori.common.interfaces.Sorting;
import ru.wert.tubus.chogori.common.tableView.RoutineTableView;
import ru.wert.tubus.chogori.entities.passports.commands._Passport_Commands;
import ru.wert.tubus.chogori.previewer.PreviewerPatchController;
import ru.wert.tubus.chogori.application.services.ChogoriServices;

import java.util.*;

public class Passport_TableView extends RoutineTableView<Passport> implements Sorting<Passport> {

    private static final String accWindowRes = "/chogori-fxml/drafts/draftACC.fxml";
    private final _Passport_Commands commands;
    @Getter private PreviewerPatchController previewerController;

    private Object modifyingItem; //Product или Folder
    @Getter private Passport_ACCController accController;
    @Setter private Object modifyingClass;

    private List<Passport> shownList = new ArrayList<>(); //Лист карточек, отображаемых в таблице сейчас
    @Getter@Setter private String searchedText = "";

    @Getter@Setter private List<Folder> selectedFolders;


    private TableColumn<Passport, String> tcId;
    private TableColumn<Passport, VBox> tcPassport; //Колонка Идентификатор
    private TableColumn<Passport, Label> tcDraftNumber; //Номер чертежа
    private TableColumn<Passport, Label> tcDraftName; //Наименование чертежа

    //Показывать колонки
    @Getter@Setter private boolean showId; //Идентификатор
    @Getter@Setter private boolean showIdentity ; //Идентификатор
    @Getter@Setter private boolean showNumber; //Дец номер
    @Getter@Setter private boolean showName; //Наименование

    //Фильтр
//    @Getter@Setter private boolean showLegal = true; //ДЕЙСТВУЮЩИЕ - по умолчанию
//    @Getter@Setter private boolean showChanged; //ЗАМЕНЕНННЫЕ
//    @Getter@Setter private boolean showAnnulled; //АННУЛИРОВАННЫЕ


    /**
     * Конструктор для таблицы, связанной с предпросмотром чертежей
     * @param promptText String, текст, добавляемый в поисковую строку
     * @param previewerController PreviewerNoTBController контроллер окна предпросмотра
     */
    public Passport_TableView(String promptText, PreviewerPatchController previewerController, boolean useContextMenu) {
        this(promptText);
        this.previewerController = previewerController;

        new Passport_Manipulator(this);

        //Создаем изначальное контекстное меню, чтобы оно могло открыться при клике в пустом месте
        if(useContextMenu) createContextMenu();

    }



    /**
     * Конструктор для таблицы без предпросмотра
     * @param promptText String, текст, добавляемый в поисковую строку
     */
    public Passport_TableView(String promptText) {
        super(promptText);

        commands = new _Passport_Commands(this);

    }

    @Override
    public void setTableColumns() {

        tcId = Passport_Columns.createTcId(); //id пасспорта
        tcPassport = Passport_Columns.createTcPassport(); //Колонка пасспорта
        tcDraftNumber = Passport_Columns.createTcPassportNumber(); //Колонка Дец номер
        tcDraftName = Passport_Columns.createTcPassportName(); //Колонка Наименование

        getColumns().addAll(tcId, tcPassport, tcDraftNumber, tcDraftName);

    }

    /**
     * Метод выключает ненужные столбцы
     */
    public void showTableColumns(boolean useTcId, boolean useTcPassport){
        tcId.setVisible(useTcId);
        showId = useTcId;

        tcPassport.setVisible(useTcPassport);
        showIdentity = useTcPassport;

        tcDraftNumber.setVisible(!useTcPassport);
        showNumber = !useTcPassport;

        tcDraftName.setVisible(!useTcPassport);
        showName = !useTcPassport;

    }

    @Override
    public void createContextMenu() {
        //Контекстного меню не предусмотрено
    }

    @Override
    public List<Passport> prepareList() {

        List<Passport> list = new ArrayList<>();
        if(modifyingClass instanceof Folder){
            if(selectedFolders == null || selectedFolders.isEmpty()) {
                if (modifyingItem == null)
                    list = ChogoriServices.CH_QUICK_PASSPORTS.findAll();
                else {
                    list = new ArrayList<>(findPassportsInFolder((Folder)modifyingItem));
                }
            } else {
                for(Folder folder: selectedFolders){
                    list.addAll(findPassportsInFolder(folder));
                }
                selectedFolders = null;
            }
        }

        return list;
    }

    /**
     * Метод находит пасспорта в нужной папке, пасспорта не должны повторяться, поэтому используется Set<Passport>
     * @param folder Folder
     * @return Set<Passport>
     */
    private Set<Passport> findPassportsInFolder(Folder folder){
        Set<Passport> foundPassports = new HashSet<>();
        List<Draft> listOfDrafts = ChogoriServices.CH_QUICK_DRAFTS.findAllByFolder(folder);
        for(Draft d : listOfDrafts){
            foundPassports.add(d.getPassport());
        }
        return foundPassports;
    }

    @Override
    public ItemCommands<Passport> getCommands() {
        return commands;
    }

    @Override
    public String getAccWindowRes() {
        return accWindowRes;
    }

    /**
     * Устанавливает выделенный на данный момент элемент
     * @param modifyingItem Product или Folder
     */
    public void setModifyingItem(Object modifyingItem) {
        this.modifyingItem = modifyingItem;
    }

    /**
     * Возвращает выделенный на данный момент элемент Product или Folder
     */
    public Object getModifyingItem() {
        return modifyingItem;
    }

    @Override
    public void sortItemList(List<Passport> list) {
        list.sort(passportsComparator());
    }

    /**
     * Компаратор сравнивает чертеж по НОМЕРУ -> ТИПУ -> СТРАНИЦЕ
     */
    public static Comparator<Passport> passportsComparator() {
        return (o1, o2) -> {

            int result = o1.toUsefulString()
                    .compareTo(o2.toUsefulString());
            return result;
        };
    }


    @Override //Searchable
    public List<Passport> getCurrentItemSearchedList() {
        return shownList;
    }

    @Override //Searchable
    public void setCurrentItemSearchedList(List<Passport> currentItemList) {
        this.shownList = currentItemList;
    }

    public void setAccController(FormView_ACCController<Passport> accController){
        this.accController = (Passport_ACCController) accController;
    }

}
