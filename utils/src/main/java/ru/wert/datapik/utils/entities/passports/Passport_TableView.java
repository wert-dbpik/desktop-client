package ru.wert.datapik.utils.entities.passports;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
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
import ru.wert.datapik.utils.entities.passports.commands._Passport_Commands;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;

import java.util.*;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_DRAFTS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_PASSPORTS;

public class Passport_TableView extends RoutineTableView<Passport> implements Sorting<Passport> {

    private static final String accWindowRes = "/utils-fxml/drafts/draftACC.fxml";
    private final _Passport_Commands commands;
    private PreviewerPatchController previewerController;
    private String searchedText = "";
    private Object modifyingItem; //Product или Folder
    private List<Passport> currentItemList = new ArrayList<>();
    private Passport_ACCController accController;
    @Setter private Object modifyingClass;


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
//        contextMenu = new Passport_ContextMenu(this, commands, accWindowRes);
//        setContextMenu(contextMenu);
    }

    @Override
    public List<Passport> prepareList() {
        List<Passport> list = new ArrayList<>();
        if(modifyingClass instanceof Folder){
            if(modifyingItem == null)
                list = CH_QUICK_PASSPORTS.findAll();
            else {
                List<Draft> listOfDrafts = CH_QUICK_DRAFTS.findAllByFolder((Folder)modifyingItem);
                Set<Passport> passports = new HashSet<>();
                for(Draft d : listOfDrafts){
                    passports.add(d.getPassport());
                }
                list = new ArrayList<>(passports);
            }
        }

        return list;
    }

    @Override
    public void setSearchedText(String searchedText) {
        this.searchedText = searchedText;
    }

    @Override
    public String getSearchedText() {
        return searchedText;
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

    public PreviewerPatchController getPreviewerController(){
        return previewerController;
    }


    @Override
    public void sortItemList(List<Passport> list) {
        list.sort(draftsComparator());
    }

    /**
     * Компаратор сравнивает чертеж по НОМЕРУ -> ТИПУ -> СТРАНИЦЕ
     */
    public static Comparator<Passport> draftsComparator() {
        return (o1, o2) -> {

            //Сравниваем номер чертежа, причем 745 должен быть выше, чем 469
            int result = o2.toUsefulString()
                    .compareTo(o1.toUsefulString());
//            if (result == 0) {
//                //Сравниваем тип чертежа
//                result = o1.getPassportType() - o2.getPassportType();
//                if (result == 0) {
//                    //Сравниваем номер страницы
//                    result = o1.getPageNumber() - o2.getPageNumber();
//                }
//            }
            return result;
        };
    }


    @Override //Searchable
    public List<Passport> getCurrentItemSearchedList() {
        return currentItemList;
    }

    @Override //Searchable
    public void setCurrentItemSearchedList(List<Passport> currentItemList) {
        this.currentItemList = currentItemList;
    }

    public void setAccController(FormView_ACCController<Passport> accController){
        this.accController = (Passport_ACCController) accController;
    }

    public Passport_ACCController getAccController(){
        return accController;
    }
}
