package ru.wert.datapik.utils.entities.drafts;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import ru.wert.datapik.utils.entities.drafts.commands.Draft_MultipleAddCommand;
import ru.wert.datapik.utils.entities.folders.Folder_Chooser;
import ru.wert.datapik.utils.previewer.PreviewerPatchController;
import ru.wert.datapik.winform.enums.EDraftStatus;
import ru.wert.datapik.winform.enums.EDraftType;
import ru.wert.datapik.winform.enums.EOperation;
import ru.wert.datapik.utils.popups.HintPopup;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.warnings.Warning1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_DRAFTS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_QUICK_PREFIXES;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.CH_PDF_VIEWER;
import static ru.wert.datapik.utils.statics.AppStatic.closeWindow;
import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;
import static ru.wert.datapik.winform.warnings.WarningMessages.$ATTENTION;

/**
 * Класс описывает форму добавления и замены чертежей
 * Вилка : можно просто добавить или добавить сразу папку.
 * Добавит : делится на добавить один чертеж и несколько, зависит от того,
 * сколько выбрал в папке пользователь
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
    private TextArea txtAreaNote;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnCancel;

    private static File lastFile = new File("C:/test");

    private Draft_TableView tableView;
    private PreviewerPatchController previewerController;
    private List<DraftFileAndId> draftsList; //Список чертежей <Файлб>
    private File currentFilePath;
    private IntegerProperty currentFile;
    private Folder currentFolder;
    private HintPopup hintPopup;
    private String currentFileName;
    private ObjectProperty<EOperation> operationProperty ;

    private StackPane spIndicator;//Панель с расположенным на ней индикатором прогресса, опявляется при длительных процессах
    private ICommand currentCommand; //Команда исполняемая в текущее время.
    private Task<Draft> manipulation;//Текущая выполняемая задача



    @FXML
    void initialize(){
        btnCancel.setOnAction(event -> {

            // НЕ РАБОТААЕТ!!!!
            System.out.println("cancel pressed");
            if (manipulation.isRunning()) {
                manipulation.cancel();
            } else {
                super.cancelPressed(event);
            }
        });


    }

    @FXML
    void ok(ActionEvent event) {
        if(notNullFieldEmpty()) {
            Warning1.create($ATTENTION, "Некоторые поля не заполнены!", "Заполните все поля");
            return;
        }
        //draftsList == null при изменении
        if (draftsList != null && draftsList.size() > 1) {
            if (operationProperty.get().equals(EOperation.ADD) || operationProperty.get().equals(EOperation.ADD_FOLDER)) {
                //При сохранении чертежа, нам нужен id сохраненного чертежа
                currentCommand = new Draft_MultipleAddCommand(getNewItem(), tableView);
                btnOk.setDisable(true);
                spIndicator.setVisible(true);
                manipulation = addDraftTask();

                new Thread(manipulation).start();

            } else {
                //при изменении чертежа нам не нужен его Id
                super.okPressed(event, spIndicator, btnOk);
            }


        } else if (operationProperty.get().equals(EOperation.REPLACE)) {
            replaceDraft(event);
        } else {
            //Если операция была для множественного добавления, а в папке оказался всего один
            //то самое время спенить название опреации
            operation = EOperation.ADD;
            super.okPressed(event, spIndicator, btnOk);
        }

    }

    @NotNull
    private Task<Draft> addDraftTask() {
        return new Task<Draft>() {
            @Override
            protected Draft call() throws Exception {
                return ((Draft_MultipleAddCommand)currentCommand).addDraft();
            }

            @Override
            protected void cancelled() {
                btnOk.setDisable(false);
                spIndicator.setVisible(false);
                showNextDraft();
                super.cancelled();
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
                Long saveDraftId = savedDraft.getId();
                draftsList.get(currentFile.get()).setDraftId(saveDraftId);
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
            fillForm(currentFile.get());
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
                currentCommand = new Draft_ChangeCommand(oldDraft, tableView);
                currentCommand.execute();
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
     * Предварительно происходит выбор чертежа или папки с чертежами
     */
    @Override
    public void init(EOperation operation, IFormView<Draft> formView, ItemCommands<Draft> commands) {
        super.initSuper(operation, formView, commands, CH_QUICK_DRAFTS);
        this.tableView = (Draft_TableView) formView;

        initOperationProperty(operation);

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

        if(currentFile != null) //currentFile = null только при ИЗМЕНЕНИИ
        currentFile.addListener((observable, oldValue, newValue) -> {
//            if(draftsList.size() > 1)
                lblNumFile.setText(String.format("Файл %d из %d", newValue.intValue()+1, draftsList.size()));
        });

        //Показываем изначальное число файлов
        lblNumFile.setText("Файлов: " + draftsList.size());

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
        operationProperty.addListener((observable, oldValue, newValue) -> {
            if(newValue.equals(EOperation.ADD) || newValue.equals(EOperation.ADD_FOLDER)) {
                btnOk.setText("ДОБАВИТЬ");
                btnOk.setStyle("-fx-background-color: #8bc8ff;");
            }else if(newValue.equals(EOperation.REPLACE)){
                btnOk.setText("ЗАМЕНИТЬ");
                btnOk.setStyle("-fx-background-color: #70DB55;");
            }else {
                btnOk.setText("ИЗМЕНИТЬ");
                btnOk.setStyle("-fx-background-color: #ffd4a3;");
            }
        });
        operationProperty.set(operation);
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
     * Метод преобразовавает наименование файла в строки дец номера и наименования
     */
    private void setDecNumberAndName() {
        //Обрезаем расширение файла
        String numberAndName = currentFileName.substring(0, currentFileName.lastIndexOf("."));
        //Определяем наличие префикса
        Prefix prefix = null;
        int index1 = numberAndName.indexOf(".");
        if(index1 > 0)
            prefix = CH_QUICK_PREFIXES.findByName(numberAndName.substring(0, index1));
        //Если префикс присутствует, отрезаем его от строки
        if(prefix != null) {
            numberAndName = numberAndName.substring(index1 + 1);
            bxPrefix.setValue(prefix);
        }

        //Если пробел разделяет номер группами по три
        if(numberAndName.indexOf(" ") == 3) numberAndName = numberAndName.replaceFirst(" ", "");

        //Имеем обрезок из номера и наименования
        //В оставшейся части ищем первый пробел -
        int index = numberAndName.indexOf(" ");

        if(index > 0){
            //Строка с номером - подстрока до первого пробела
            String strWithNumber = numberAndName.substring(0, index);

            Pattern pattern = Pattern.compile("\\d{3}.?\\d{3}\\.\\d{3}");
            //В строке с номером находим сам номер
            Matcher matcher = pattern.matcher(strWithNumber);
            while (matcher.find()) {
                txtNumber.setText(numberAndName.substring(matcher.start(), matcher.end()));
            }
            txtName.setText(numberAndName.substring(index + 1));
        } else {
            txtName.setText(numberAndName);
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
        currentFile = new SimpleIntegerProperty(0);

        btnPrevious.disableProperty().bind(currentFile.lessThanOrEqualTo(0));
        btnPrevious.setOnAction(event -> {
            int prev = currentFile.get() - 1;
            if (prev >= 0) {
                currentFile.set(prev);
                fillForm(prev);
            }
        });

        btnNext.disableProperty().bind(currentFile.greaterThanOrEqualTo(draftsList.size() - 1));
        btnNext.setOnAction(event -> {
            int next = currentFile.get() + 1;
            if (next < draftsList.size()) {
                currentFile.set(next);
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
            currentFilePath = draftsList.get(currentFile.get()).getDraftFile(); //File
            previewerController.showDraft(null, currentFilePath);
            currentFileName = draftsList.get(currentFile.get()).getDraftFile().getName(); //String
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
            operationProperty.set(EOperation.CHANGE);

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

}
