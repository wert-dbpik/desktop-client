package ru.wert.datapik.utils.entities.drafts;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.utils.common.commands.ICommand;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.components.*;
import ru.wert.datapik.utils.common.contextMenuACC.FormViewACCWindow;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ACCController;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.entities.drafts.commands.Draft_AddCommand;
import ru.wert.datapik.utils.entities.drafts.commands.Draft_ChangeCommand;
import ru.wert.datapik.utils.entities.drafts.commands.Draft_DeleteCommand;
import ru.wert.datapik.utils.entities.drafts.commands.Draft_MultipleAddCommand;
import ru.wert.datapik.utils.entities.folders.Folder_Chooser;
import ru.wert.datapik.utils.popups.HintPopup;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.enums.EDraftStatus;
import ru.wert.datapik.winform.enums.EDraftType;
import ru.wert.datapik.winform.enums.EOperation;
import ru.wert.datapik.winform.warnings.Warning1;
import ru.wert.datapik.winform.warnings.Warning2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_PDF_VIEWER;
import static ru.wert.datapik.utils.statics.AppStatic.closeWindow;
import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;
import static ru.wert.datapik.winform.warnings.WarningMessages.*;

/**
 * Класс описывает форму добавления и замены чертежей
 * Вилка : можно просто добавить или добавить сразу папку.
 * Добавить : делится на добавить один чертеж и несколько, зависит от того,
 * сколько выбрал в папке пользователь
 * List<DraftFileAndId> draftsList - список добавляемых чертежей, DraftFileAndId - Draft + Id
 * если Id = null, значит чертеж еще не добавлен в БД
 */
@Slf4j
public class Draft_ACCController extends FormView_ACCController<Draft> {

    @FXML
    private Label lblNumFile;

    @FXML
    private Label lblFileName;

    @FXML
    private Label lblStatus;

    @FXML
    private Button btnNext;

    @FXML
    private Button btnPrevious;

    @FXML
    private StackPane spPreviewer;

    @FXML
    private ComboBox<Prefix> bxPrefix;

    @FXML
    private TextField txtNumber;

    @FXML
    private TextField txtName;

    @FXML
    private ComboBox<EDraftType> bxType;

    @FXML
    private ComboBox<Integer> bxPage;

    @FXML
    private ComboBox<Folder> bxFolder;

    @FXML
    private Button btnSearchFolder;

    @FXML
    private RadioButton rbAsk;

    @FXML
    private RadioButton rbChange;

    @FXML
    private RadioButton rbDelete;

    @FXML
    private TextArea txtAreaNote;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;

    private static File lastFile = new File("C:/test");

    private Draft_TableView tableView;
    private PreviewerPatchController previewerController;



    private Folder currentFolder;
    private HintPopup hintPopup;
    private ObjectProperty<EOperation> operationProperty ;

    private StackPane spIndicator;//Панель с расположенным на ней индикатором прогресса, опявляется при длительных процессах
    private ICommand currentCommand; //Команда исполняемая в текущее время.
    private Task<?> manipulation;//Текущая выполняемая задача

    private List<DraftFileAndId> draftsList; //Список чертежей <Файл - ID>
    private IntegerProperty currentPosition; //Порядковый номер файла в draftsList
    private File currentFilePath; //Текущий путь к файлу, хранящийся в draftsList
    private String currentFileName;
    private Draft currentDraft; //Текущий чертеж для которого нажали OK - id = null, так как он не сохранен

    private boolean askMe, changeMe, deleteMe;

    @FXML
    void initialize(){

        initButtonCancel();

        initRadioGroup();

    }

    /**
     * Метод инициализирует кнопку Отмена
     */
    private void initButtonCancel() {
        btnCancel.setOnAction(event -> {
            // НЕ РАБОТААЕТ!!!!
            if (manipulation.isRunning()) {
                manipulation.cancel();
            } else {
                super.cancelPressed(event);
            }
        });
    }

    /**
     * Радио группа определяет действие при сопадении добавляемого чертежа с имеющимся
     * По умолчанию - полагается спросить, что делать
     */
    private void initRadioGroup() {
        ToggleGroup radioGroup = new ToggleGroup();
        radioGroup.getToggles().addAll(rbAsk, rbChange, rbDelete);
        radioGroup.selectedToggleProperty().addListener(observable -> {
            askMe = rbAsk.isSelected();
            changeMe = rbChange.isSelected();
            deleteMe = rbDelete.isSelected();
        });
        rbDelete.setSelected(true);
    }

    /**
     * Предварительно происходит выбор чертежа или папки с чертежами
     */
    @Override
    public void init(EOperation operation, IFormView<Draft> formView, ItemCommands<Draft> commands) {
        super.initSuper(operation, formView, commands, CH_QUICK_DRAFTS);
        this.tableView = (Draft_TableView) formView;

        currentPosition = new SimpleIntegerProperty();

        initOperationProperty(operation);

        new BXPage().create(bxPage); //СТРАНИЦЫ
        new BXDraftType().create(bxType); //ТИП ЧЕРТЕЖА
        new BXPrefix().create(bxPrefix); //ПРЕФИКСЫ
        new BXFolder().create(bxFolder); //ИЗДЕЛИЯ
        new BtnSearchProduct().create(btnSearchFolder); //НАЙТИ/ДОБАВИТЬ изделие
        new BtnCancel().create(btnCancel); //ОТМЕНА кнопка

        createLabelFileName();

        initLabelDraftStatus();

        createPreviewer();

        btnSearchFolder.setOnAction(this::findFolder);

        //Устанавливаем начальные значения полей в зависимости от operation
        setInitialValues();

        if(operation.equals(EOperation.ADD_FOLDER)){
            setSettingsForOperationAddFolder();
        } else
            lblNumFile.setText("Файлов: 1");

    }

    /**
     * Метод устанавливает начальное значение кнопки ОК, количество файлов в папке
     * Начальную позицию добавляемых чертежей в папке и вешает слушателя на изменение этой позиции
     */
    private void setSettingsForOperationAddFolder() {
        //Показываем изначальное число файлов
        lblNumFile.setText("Файлов: " + draftsList.size());
        //Ghb последующей итерации
        currentPosition.addListener((observable) -> {
            lblNumFile.setText(String.format("Файл %d из %d", currentPosition.get() +1, draftsList.size()));
            Long id = draftsList.get(currentPosition.get()).draftId;
            if(id == null) {
                btnOk.setText("ДОБАВИТЬ");
                btnOk.setStyle("-fx-background-color: #8bc8ff;");
                manipulation = addDraftTask();
            } else {
                btnOk.setText("ИЗМЕНИТЬ");
                btnOk.setStyle("-fx-background-color: #ffd4a3;");
                manipulation = changeDraftTask();
            }
        });
        //Инициируем
        manipulation = addDraftTask();
    }

    /**
     * Метод инициирует надпись со статусом чертежа - ДЕЙСТВУЕТ, ЗАМЕНЕН, АННУЛИРОВАН
     */
    private void initLabelDraftStatus() {
        if(operationProperty.get().equals(EOperation.ADD) || operationProperty.get().equals(EOperation.ADD_FOLDER))
            setDraftStatus(null);
        else
            setDraftStatus(tableView.getAllSelectedItems() == null ? null : tableView.getAllSelectedItems().get(0));
    }

    /**
     * Метод содержит слушатель для управлением надписью на кнопке btnOk
     * @param operation EOperation
     */
    private void initOperationProperty(EOperation operation) {
        operationProperty = new SimpleObjectProperty<>();
        operationProperty.set(operation);

        switch(operationProperty.get()){
            case REPLACE:
                btnOk.setText("ЗАМЕНИТЬ");
                btnOk.setStyle("-fx-background-color: #70DB55;");
                break;
            case CHANGE:
                btnOk.setText("ИЗМЕНИТЬ");
                btnOk.setStyle("-fx-background-color: #ffd4a3;");
                break;
            default: //ADD, ADD_FOLDER
                btnOk.setText("ДОБАВИТЬ");
                btnOk.setStyle("-fx-background-color: #8bc8ff;");
                break;
        }

        if(operationProperty.get().equals(EOperation.ADD)) loadManyDrafts();
        if(operationProperty.get().equals(EOperation.ADD_FOLDER)) loadFolder();
        if(operationProperty.get().equals(EOperation.REPLACE)) loadOneDraft();

        //Если ничего не выбрано, выходим без создания окна
        if((operationProperty.get().equals(EOperation.ADD) || operationProperty.get().equals(EOperation.ADD_FOLDER)
                || operationProperty.get().equals(EOperation.REPLACE))
                && (draftsList == null || draftsList.isEmpty())){
            FormViewACCWindow.windowCreationAllowed = false;
            return;
        }
    }

    @FXML
    void ok(ActionEvent event) {
        if (notNullFieldEmpty()) {
            Warning1.create($ATTENTION, "Некоторые поля не заполнены!", "Заполните все поля");
            return;
        }
        //draftsList == null при изменении
        if (draftsList != null && !draftsList.isEmpty()) {
            if (operationProperty.get().equals(EOperation.ADD_FOLDER)) {
                //При сохранении чертежа, нам нужен id сохраненного чертежа
                currentDraft = getNewItem();
                currentCommand = new Draft_MultipleAddCommand(currentDraft, tableView);
                btnOk.setDisable(true);
                spIndicator.setVisible(true);
                new Thread(manipulation).start();
            } else if (operationProperty.get().equals(EOperation.REPLACE)) {
                replaceDraft(event);
            } else {
                super.okPressed(event, spIndicator, btnOk);
            }
        } else {
            super.okPressed(event, spIndicator, btnOk);
        }
    }

    /**
     * Проверка чертежа на дубликаты
     * Если новый чертеж повторяет имеющийся ДЕЙСТВУЮЩИЙ или АННУЛИРОВАННЫЙ, то пользователю поступит предложение его заменить
     * Предполагается, что метод действует в потоке отличном от главного, поэтому окно с сообщением выводится принудительно
     * в главном потоке. Ожидание ответа от пользователя происходит с помощью класса CountDownLatch и связывания BooleanProperty
     */
    public boolean draftIsDuplicated(Draft newDraft){
        //Так как пасспорт нового чертежа еще фактически не существует, то ищем такой же пасспорт в базе по косвенным признакам
        Passport passport = CH_QUICK_PASSPORTS.findByPrefixIdAndNumber(newDraft.getPassport().getPrefix(), newDraft.getPassport().getNumber());
        if (passport == null) {
            log.debug("draftIsDuplicated : пасспорта {} не найдено", newDraft.getPassport().toUsefulString());
            return false; //Если пасспорта в базе нет - чертеж новый
        } else
            log.debug("draftIsDuplicated : найден пасспорт {}", passport.toUsefulString());

        List<Draft> drafts = CH_QUICK_DRAFTS.findByPassport(passport);
        log.debug("draftIsDuplicated : найдено {} чертежей с пасспортом {}", drafts.size(), passport.toUsefulString());
        if (drafts.isEmpty()) return false;

        for (Draft draft : drafts) {
            if (draft.equals(newDraft)) {
                if (draft.getStatus().equals(EDraftStatus.LEGAL.getStatusId())) {
                    if (foundDuplicatedLegalDraft(draft)) return true; //Иначе возвращаем на доработку

                } else if (draft.getStatus().equals(EDraftStatus.ANNULLED.getStatusId())) {
                    if (foundDuplicatedAnnulledDraft(draft)) return true; //Иначе возвращаем на доработку
                }
            }
        }

        return false;
    }

    private boolean foundDuplicatedAnnulledDraft(Draft draft) {
        CountDownLatch latch = new CountDownLatch(1);
        BooleanProperty answer = new SimpleBooleanProperty();
        Platform.runLater(() -> {
            answer.set(Warning2.create($ATTENTION,
                    "Существует АННУЛИРОВАННЫЙ чертеж с номером " + draft.getDecimalNumber(),
                    "Хотите изменить статус чертежа на ЗАМЕННЫЙ?"));

            latch.countDown();
        });
        if (answer.get()) {
            draft.setStatus(EDraftStatus.CHANGED.getStatusId());
            draft.setStatusTime(LocalDateTime.now().toString());
            log.debug("draftIsDuplicated : меняем статус чертежа {} на АННУЛИРОВАННЫЙ", draft.toUsefulString());
            Draft_ChangeCommand updateCommand = new Draft_ChangeCommand(draft, tableView);
            updateCommand.execute();
        } else {
            log.debug("draftIsDuplicated : пользователь отказался менять статус чертежа {} на АННУЛИРОВАННЫЙ", draft.toUsefulString());
            return true;
        }
        return false;
    }

    private boolean foundDuplicatedLegalDraft(Draft draft){
        CountDownLatch latch = new CountDownLatch(1);
        BooleanProperty changeDraft = new SimpleBooleanProperty();
        Platform.runLater(() -> {
            changeDraft.set(Warning2.create($ATTENTION,
                    "Существует ДЕЙСТВУЮЩИЙ чертеж\n "
                            + "\"" + draft.toUsefulString() + "\",\n"
                            + "Статус: " + EDraftStatus.getStatusById(draft.getStatus()).getStatusName() + "-" + draft.getPageNumber(),
                    "Хотите заменить чертеж?"));
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (changeDraft.get()) {
            draft.setStatus(EDraftStatus.CHANGED.getStatusId());
            draft.setStatusTime(LocalDateTime.now().toString());
            log.debug("draftIsDuplicated : меняем статус чертежа {} на ЗАМЕНЕННЫЙ", draft.toUsefulString());
            Draft_ChangeCommand updateCommand = new Draft_ChangeCommand(draft, tableView);
            updateCommand.execute();
//                        CH_QUICK_DRAFTS.update(draft);
        } else {
            log.debug("draftIsDuplicated : пользователь отказался менять статус чертежа {} на ЗАМЕНЕННЫЙ", draft.toUsefulString());
            return true;
        }
        return false;
    }

    @NotNull
    private Task<Draft> addDraftTask() {
        return new Task<Draft>() {
            @Override
            protected Draft call() throws Exception {
                if(draftIsDuplicated(currentDraft)) return null;
                return ((Draft_MultipleAddCommand)currentCommand).addDraft();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                btnOk.setDisable(false);
                spIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                super.failed();
                btnOk.setDisable(false);
                spIndicator.setVisible(false);
                showNextDraft();
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                btnOk.setDisable(false);
                spIndicator.setVisible(false);
                Draft savedDraft = this.getValue();
                if(savedDraft != null) {
                    Long saveDraftId = savedDraft.getId();
                    draftsList.get(currentPosition.get()).setDraftId(saveDraftId);
                    showNextDraft();
                }
            }
        };
    }

    @NotNull
    private Task<Draft> changeDraftTask() {
        return new Task<Draft>() {
            @Override
            protected Draft call() throws Exception {
                if (isDuplicated(getNewItem(), currentDraft)) {
                    if(askMe)
                        Platform.runLater(() -> Warning1.create($ATTENTION, $ITEM_EXISTS, $USE_ORIGINAL_ITEM));
                    return null;
                }
                Draft oldDraft = CH_QUICK_DRAFTS.findById(draftsList.get(currentPosition.get()).draftId);

                changeOldItemFields(oldDraft);
//                ((Draft_ChangeCommand)currentCommand).;
                commands.change(null, oldDraft);
                return oldDraft;

            }

            @Override
            protected void cancelled() {
                super.cancelled();
                btnOk.setDisable(false);
                spIndicator.setVisible(false);
            };

            @Override
            protected void failed() {
                super.failed();
                btnOk.setDisable(false);
                spIndicator.setVisible(false);
                showNextDraft();
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                btnOk.setDisable(false);
                spIndicator.setVisible(false);
                showNextDraft();
            }
        };
    }


    /**
     * Метод показывает следущий в списке чертеж, если активна нопка NEXT
     */
    private void showNextDraft() {
        //Если мы дошли до конца списка (кнопка активна)
        if (!btnNext.isDisable())
            btnNext.fire(); //Эмулируем нажатие кнопки
        else
            //Иначе перезаполняем форму с обновленными данными
            fillForm(currentPosition.get());
    }


    /**
     * ЗАМЕНИТЬ ЧЕРТЕЖ
     */
    private void replaceDraft(ActionEvent event) {
        Draft oldDraft = tableView.getSelectionModel().getSelectedItem();
        oldDraft.setStatus(EDraftStatus.CHANGED.getStatusId());
        oldDraft.setStatusUser(CH_CURRENT_USER);
        oldDraft.setStatusTime(LocalDateTime.now().toString());

        manipulation = replaceDraftTask(event, oldDraft);

        new Thread(manipulation).start();
    }

    @NotNull
    private Task<Draft> replaceDraftTask(ActionEvent event, Draft oldDraft) {
        return new Task<Draft>() {
            @Override
            protected Draft call() throws Exception {
                if(deleteMe){
                    currentCommand = new Draft_DeleteCommand(Arrays.asList(oldDraft), tableView);
                    currentCommand.execute();
                } else{
                    currentCommand = new Draft_ChangeCommand(oldDraft, tableView);
                    currentCommand.execute();
                }

                //Сохраняем новый чертеж
                currentCommand = new Draft_AddCommand(getNewItem(), tableView);
                currentCommand.execute();

                return null;
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                closeWindow(event);
            }

            @Override
            protected void failed() {
                super.failed();
                closeWindow(event);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                closeWindow(event);
            }
        };
    }



    /**
     * Метод описывает действие при нажатии на кнопку Добавить пакет
     */
    void findFolder(ActionEvent event) {

        Folder chosenFolder = Folder_Chooser.create(((Node) event.getSource()).getScene().getWindow());
        if(chosenFolder != null){
            bxFolder.setValue(chosenFolder);
        }
    }

    /**
     * Устанавливается поле статуса согласно БД
     * @param draft Draft
     */
    private void setDraftStatus(Draft draft) {
        EDraftStatus status;
        if(draft == null)
            status = EDraftStatus.UNKNOWN;
        else
            status = EDraftStatus.getStatusById(draft.getStatus());
        lblStatus.setText(status.getStatusName());
        switch (status) {
            case LEGAL:
                lblStatus.setStyle("-fx-background-color: white; -fx-font-weight: bold;-fx-text-fill: green; "); break;//ДЕЙСТВУЕТ
            case CHANGED:
                lblStatus.setStyle("-fx-background-color: white; -fx-font-weight: bold;-fx-text-fill: yellow; "); break;//ЗАМЕНЕН
            case ANNULLED:
                lblStatus.setStyle("-fx-background-color: white; -fx-font-weight: bold;-fx-text-fill: red; "); break;//АННУЛИРОВАН
            case UNKNOWN:
                lblStatus.setStyle("-fx-background-color: white; -fx-font-weight: bold;-fx-text-fill: black; "); break;//НЕОПРЕДЕЛЕН
        }
    }


    /**
     * Загружаем папку с чертежами
     */
    private void loadFolder(){

        try {
            draftsList = new ArrayList<>();

            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setInitialDirectory(lastFile);
            dirChooser.setTitle("Выберите папку...");
            File folder = dirChooser.showDialog(CH_MAIN_STAGE);
            if(folder == null || !folder.isDirectory()) return;

            List<Path> filesInFolder = Files.walk(folder.toPath())
                    .filter(file ->file.toString().toLowerCase().endsWith(".pdf") ||
                            file.toString().toLowerCase().endsWith(".png") ||
                            file.toString().toLowerCase().endsWith(".jpg"))
                    .collect(Collectors.toList());
            for(Path p : filesInFolder)
                draftsList.add(new DraftFileAndId(p.toFile(), null));
            lastFile = folder.getParentFile();

            currentFilePath = draftsList.get(0).getDraftFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Загружаем несколько выбранных чертежей
     */
    private void loadManyDrafts() {
        draftsList = new ArrayList<>();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(lastFile);
        fileChooser.setTitle("Выберите чертежи...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF, PNG, JPEG", "*.pdf", "*.png", "*.jpg"));

        List<File> chosenList = fileChooser.showOpenMultipleDialog(CH_MAIN_STAGE);
        if(chosenList == null) return;
        lastFile = chosenList.get(0).getParentFile();
        //Формируем список файлов из выбранных чертежей
        for(File file: chosenList){
            draftsList.add(new DraftFileAndId(file, null));
        }
        //За текущий файл берем самый первый в списке
        currentFilePath = draftsList.get(0).getDraftFile();


    }

    /**
     * Метод для замены одного чертежа другим
     */
    private void loadOneDraft(){
        draftsList = new ArrayList<>();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(lastFile);
        fileChooser.setTitle("Выберите чертеж...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF, PNG, JPEG", "*.pdf", "*.png", "*.jpg"));

        File chosenFile = fileChooser.showOpenDialog(CH_MAIN_STAGE);
        if(chosenFile == null) return;
        lastFile = chosenFile.getParentFile();
        //Формируем список файлов из выбранных чертежей

        draftsList.add(new DraftFileAndId(chosenFile, null));

        //За текущий файл берем единственный в списке
        currentFilePath = draftsList.get(0).getDraftFile();
    }

    /**
     * Выводит ниже комбобокса подсказку - это же наименование, но мелким шрифтом
     */
    private void createLabelFileName() {
        lblFileName.setStyle("-fx-background-color: white; -fx-text-fill: saddlebrown");
        lblFileName.setOnMouseEntered(event ->{
            String hint = lblFileName.getText();
            hintPopup = new HintPopup(lblFileName, hint, 0.0);
            hintPopup.showHint();
        });

        lblFileName.setOnMouseExited(event ->{
            if(hintPopup != null)
                hintPopup.closeHint();
        });

        lblFileName.setOnMousePressed(event->{
            if(hintPopup != null)
                hintPopup.closeHint();
        });

        if (operationProperty.get().equals(EOperation.ADD) || operationProperty.get().equals(EOperation.ADD_FOLDER))
            //При изменении файла меняется дец номер и наименование
            lblFileName.textProperty().addListener(((observable, oldValue, newValue) -> setDecNumberAndName()));

    }

    /**
     * Метод создает кнопки NEXT и PREVIOUS
     */
    private void createNextAndPreviousButtons() {
        currentPosition = new SimpleIntegerProperty(0);

        btnPrevious.disableProperty().bind(currentPosition.lessThanOrEqualTo(0));
        btnPrevious.setOnAction(event -> {
            int prev = currentPosition.get() - 1;
            if (prev >= 0) {
                currentPosition.set(prev);
                fillForm(prev);
            }
        });

        btnNext.disableProperty().bind(currentPosition.greaterThanOrEqualTo(draftsList.size() - 1));
        btnNext.setOnAction(event -> {
            int next = currentPosition.get() + 1;
            if (next < draftsList.size()) {
                currentPosition.set(next);
                fillForm(next);
            }
        });

    }

    /**
     * Метод заполняет форму в зависимости от того, файл уже сохранен или еще не сохранен в БД
     * @param num int - порядковый номер файла в списке выбранных файлов
     */
    private void fillForm(int num) {
        //Если документ #num еще не сохранен, то форму заполняем из файла
        if (draftsList.get(num).getDraftId() == null) {
            currentFilePath = draftsList.get(currentPosition.get()).getDraftFile(); //File
            previewerController.showDraft(null, currentFilePath);
            currentFileName = draftsList.get(currentPosition.get()).getDraftFile().getName(); //String
            lblFileName.setText(currentFileName);
            bxPage.getSelectionModel().select(0); //Устанавливаем страницу в "1"
            txtAreaNote.setText("");//Комментарий пустой
            //В случае если мы пришли сюда из EOperation.ADD_FOLDER, то мы будем теперь только добавлять
//            operationProperty.set(EOperation.ADD);
            setDraftStatus(null);
        } else {
            //Если документ уже сохранен, то форму заполняем значениями БД
            Draft draft = CH_QUICK_DRAFTS.findById(draftsList.get(num).getDraftId());
            fillFieldsOnTheForm(draft);
            //сохраненный чертеж может быть только изменен
//            operationProperty.set(EOperation.CHANGE);

        }
    }

    /**
     * Метод создает панель предпросмотра, состоящий и двух панелей - собственно предпросмотра
     * и панели с индикатором прогресса. Последняя панель находится в скрытом состоянии и появляется только
     * на время асинхронных операций
     */
    private void createPreviewer(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/previewer/previewerPatch.fxml"));
            Parent previewer = loader.load();
            //Помещаем панель с previewer в шаблонное окно WindowDecoration
            previewerController = loader.getController();
            previewerController.initPreviewer(CH_PDF_VIEWER, CH_MAIN_STAGE.getScene());

            //Создаем прозрачную панель с индикатором
            spIndicator = new StackPane();
            spIndicator.setAlignment(Pos.CENTER);
            spIndicator.setStyle("-fx-background-color: rgb(225, 225,225, 0.5)");
            //создаем сам индикатор
            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setMaxSize(90.0, 90.0);
            spIndicator.getChildren().addAll(progressIndicator);
            spIndicator.setVisible(false);

            //На панели размещаем предпросмотрщик и панель с индикатором
            spPreviewer.getChildren().addAll(previewer, spIndicator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public ArrayList<String> getNotNullFields() {
        ArrayList<String> notNullFields = new ArrayList<>();
        notNullFields.add(txtNumber.getText());
        notNullFields.add(txtName.getText());
        return notNullFields;
    }

    @Override
    public Draft getNewItem() {

        Draft newDraft = new Draft();

        Passport newPassport = new Passport(bxPrefix.getValue(),
                txtNumber.getText().trim(),
                txtName.getText().trim(), new ArrayList<>());
        newDraft.setPassport(newPassport);
        newDraft.setFolder(bxFolder.getValue());
        newDraft.setInitialDraftName(lblFileName.getText());
        if(operationProperty.get().equals(EOperation.ADD) || operationProperty.get().equals(EOperation.ADD_FOLDER))
            newDraft.setExtension(FilenameUtils.getExtension(currentFilePath.toString()).toLowerCase());
        else
            //Получениее расширения при ИЗМЕНЕНИИ полей записи
            newDraft.setExtension(FilenameUtils.getExtension(lblFileName.getText()).toLowerCase());
        //
        newDraft.setDraftType(bxType.getValue().getTypeId());
        newDraft.setPageNumber(bxPage.getValue());
        // СТАТУС
        newDraft.setStatus(EDraftStatus.LEGAL.getStatusId());
        newDraft.setStatusUser(CH_CURRENT_USER);
        newDraft.setStatusTime(LocalDateTime.now().toString());
        //СОЗДАНИЕ
        newDraft.setCreationUser(CH_CURRENT_USER);
        newDraft.setCreationTime(LocalDateTime.now().toString());

        newDraft.setNote(txtAreaNote.getText());

        return newDraft;
    }

    @Override
    public Draft getOldItem() {
        return tableView.getSelectionModel().getSelectedItems().get(0);
    }

    /**
     * Метод заполняет поля формы сохраненного ранее в БД чертежа
     * @param oldItem Draft
     */
    @Override
    public void fillFieldsOnTheForm(Draft oldItem) {

        //При выборе единственного чертежа для изменения
        if (draftsList == null || draftsList.size() == 1) {
            //Кнопки далее и предыдущее нужно загасить
            btnNext.setDisable(true);
            btnPrevious.setDisable(true);
        }
//        AppStatic.openDraftInPreviewer(oldItem, previewerController);
        lblFileName.setText(oldItem.getInitialDraftName());

        bxPrefix.setValue(oldItem.getPassport().getPrefix());
        txtNumber.setText(oldItem.getPassport().getNumber());
        txtName.setText(oldItem.getName());
        bxType.setValue(EDraftType.getDraftTypeById(oldItem.getDraftType()));
        bxPage.setValue(oldItem.getPageNumber());
        bxFolder.setValue(oldItem.getFolder());
        txtAreaNote.setText(oldItem.getNote());
        setDraftStatus(oldItem);

        if(operationProperty.get().equals(EOperation.REPLACE)){
            lblFileName.setText(currentFilePath.getName());
            previewerController.showDraft(currentFilePath);

            bxPrefix.setDisable(true);
            txtNumber.setDisable(true);
            txtName.setDisable(true);
            bxType.setDisable(true);
            bxPage.setDisable(true);
            bxFolder.setDisable(true);
            btnSearchFolder.setDisable(true);

        }
        else {
            AppStatic.openDraftInPreviewer(oldItem, previewerController);
        }
    }

    @Override
    public void changeOldItemFields(Draft oldItem) {
        if (!oldItem.getPassport().getPrefix().equals(bxPrefix.getValue()) ||
                !oldItem.getPassport().getNumber().equals(txtNumber.getText()) ||
                !oldItem.getPassport().getName().equals(txtName.getText()))

            oldItem.setPassport(new Passport(
                    bxPrefix.getValue(),
                    txtNumber.getText().trim(),
                    txtName.getText().trim(),
                    new ArrayList<>())
            );

        oldItem.setDraftType(bxType.getValue().getTypeId());
        oldItem.setPageNumber(bxPage.getValue());
        oldItem.setFolder(bxFolder.getValue());
        //Статус не может меняться
//        oldItem.setStatus(EDraftStatus.findStatusIdByStatusName(lblStatus.getText()));
        oldItem.setNote(txtAreaNote.getText());
    }

    @Override
    public void showEmptyForm() {

        currentFileName = draftsList.get(0).draftFile.getName();
        lblFileName.setText(currentFileName);

        createNextAndPreviousButtons();
        currentFilePath = draftsList.get(0).draftFile;
        previewerController.showDraft(currentFilePath);

        bxFolder.setValue((Folder) tableView.getModifyingItem());

        setDecNumberAndName();

    }

    /**
     * Метод преобразовавает наименование файла в строки дец номера и наименования
     * Вызывается так же при изменении lblFileName в методе createLabelFileName()
     */
    private void setDecNumberAndName() {
        //Обрезаем расширение файла
        String initialFileName = currentFileName.substring(0, currentFileName.lastIndexOf("."));
        log.debug("setDecNumberAndName : initial file name is '{}'", initialFileName);
        //Определяем наличие префикса
        Prefix prefix = null;
        int index1 = initialFileName.indexOf(".");
        if(index1 > 0)
            prefix = CH_QUICK_PREFIXES.findByName(initialFileName.substring(0, index1));
        //Если префикс присутствует, отрезаем его от строки
        if(prefix != null) {
            initialFileName = initialFileName.substring(index1 + 1);
            bxPrefix.setValue(prefix);
        }

        //Если пробел разделяет номер группами по три
        if(initialFileName.indexOf(" ") == 3) initialFileName = initialFileName.replaceFirst(" ", "");

        //Предположительно на этом этапе мы имеем что-то вроде 745222.255 какая-то деталь
        //Вычленим децимальный номер из оставшейся части получим наименование
        //Если децимального номера нет, то это будет только наименованием
        //Маска децимального номера XXXXXX.XXX, где X число

        Pattern p = Pattern.compile("\\d{3}.?\\d{3}\\.\\d{3}"); //Децимальный номер xxxxxx.xxx
        Matcher m = p.matcher(initialFileName);
        String decNumber = "";
        while(m.find()){
            decNumber = initialFileName.substring(m.start(), m.end());
        }
        log.debug("setDecNumberAndName : found decimal number '{}'",  decNumber);

        String partName = initialFileName.replace(decNumber, "").trim();
        log.debug("setDecNumberAndName : part name is '{}'", partName);

        txtNumber.setText(decNumber);
        txtName.setText(partName);

    }


    /**
     * Возвращает путь к сохраняемому в данный момент файлу
     * Используется в методе execute() класса AddCommand
     */
    public File getCurrentFilePath(){
        return currentFilePath;
    }
}

/**
 * Класс для использования в списке загруженных чертежей для сохранения
 * С помощью списка контролируется состояние чертежа, загружен/не загружен
 */
class DraftFileAndId {

    @Getter@Setter File draftFile; //Сохраняемый чертеж (путь)
    @Getter@Setter Long draftId; //Если id есть, то чертеж загружен

    public DraftFileAndId(File draftFile, Long draftId) {
        this.draftFile = draftFile;
        this.draftId = draftId;
    }

    @Override
    public String toString() {
        return "DraftFileAndId{" +
                "draftFile=" + draftFile +
                ", draftId=" + draftId +
                '}';
    }
}
