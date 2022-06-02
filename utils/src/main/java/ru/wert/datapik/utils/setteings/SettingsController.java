package ru.wert.datapik.utils.setteings;

import com.twelvemonkeys.io.FileUtil;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import ru.wert.datapik.client.entity.models.AppSettings;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.entity.models.VersionAndroid;
import ru.wert.datapik.client.entity.models.VersionDesktop;
import ru.wert.datapik.client.entity.serviceREST.VersionAndroidService;
import ru.wert.datapik.client.interfaces.UpdatableTabController;
import ru.wert.datapik.client.retrofit.AppProperties;
import ru.wert.datapik.utils.common.components.BXMonitor;
import ru.wert.datapik.utils.common.components.BXPrefix;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.utils.tabs.AppTab;
import ru.wert.datapik.winform.enums.EPDFViewer;
import ru.wert.datapik.winform.warnings.Warning1;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static ru.wert.datapik.utils.images.AppImages.TREE_NODE_IMG;
import static ru.wert.datapik.utils.images.BtnImages.BTN_HOME_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.*;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;
import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_TAB_PANE;
import static ru.wert.datapik.winform.statics.WinformStatic.closeWindow;

public class SettingsController {

    @FXML
    private Button btnSave;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnCancel;

    @FXML
    private TabPane tabPane;

    // Common Settings ========================================

    @FXML
    private Tab tabCommonSettings;

    @FXML
    private ComboBox<String> cmbMonitorChooser;

    @FXML
    private TextField tfPathToNormyMK;

    @FXML
    private Button btnPathToNormyMK;

    @FXML
    private CheckBox chbShowPrefixes;

    @FXML
    private ComboBox<EPDFViewer> cmbPDFViewerChooser;

    // Drafts Settings =======================================

    @FXML
    private Tab tabDraftsSettings;

    @FXML
    private ComboBox<Prefix> cmbPrefixChooser;

    @FXML
    private CheckBox chbValidateDecNumbersEntering;

    @FXML
    private TextField tfPathToOpenPDFWith;

    @FXML
    private Button btnPathToOpenPDFWith;

    @FXML
    private TextField tfPathToOpenImageWith;

    @FXML
    private Button btnPathToOpenImageWith;

    @FXML
    private TextField tfPathToOpenSolidWith;

    @FXML
    private Button btnPathToOpenSolidWith;

    // ВЕРСИЯ ЕХЕ =======================================

    @FXML
    private Tab tabVersionEXE;

    @FXML
    private TextField tfLastVersion;

    @FXML
    private TextField tfPathToLastVersion;

    @FXML
    private Button btnPathToLastVersion;

    @FXML
    private TextArea taLastVersionNote;

    // ВЕРСИЯ APK  =======================================

    @FXML
    private Tab tabVersionAPK;

    @FXML
    private TextField tfLastVersionAPK;

    @FXML
    private Button btnUploadNewAPKToDB;

    @FXML
    private TextArea taLastVersionNoteAPK;

    public static String USE_SYSTEM_SETTINGS = "СИСТЕМНЫЕ НАСТРОЙКИ";

    @FXML
    void initialize() {

        //Отключаем вкладку с настройками Редактирования чертежей
        if(!CH_CURRENT_USER_GROUP.isEditDrafts()) tabPane.getTabs().remove(tabDraftsSettings);
        else{
            //ПРОВЕРЯТЬ ВВЕДЕННЫЕ ДЕЦИМАЛЬНЫЕ НОМЕРА
            chbValidateDecNumbersEntering.setSelected(CH_CURRENT_USER_SETTINGS.isValidateDecNumbers());
        }
        //КНОПКА СБРОС В ЗНАЧЕНИЯ ПО УМОЛЧАНИЮ
        btnReset.setText("");
        btnReset.setGraphic(new ImageView(BTN_HOME_IMG));
        btnReset.setTooltip(new Tooltip("Загрузить настройки по умолчанию"));
        //МОНИТОР
        List<String> screens = new BXMonitor().create(cmbMonitorChooser);
        cmbMonitorChooser.getSelectionModel().select(AppProperties.getInstance().getMonitor());
        //PDF просмотрщик
        cmbPDFViewerChooser.getItems().addAll(EPDFViewer.values());
        cmbPDFViewerChooser.getSelectionModel().select(EPDFViewer.values()[CH_CURRENT_USER_SETTINGS.getPdfViewer()]);
        cmbPDFViewerChooser.setDisable(true);
        //НОРМЫ МК
        tfPathToNormyMK.setText(CH_CURRENT_USER_SETTINGS.getPathToNormyMK());
        btnPathToNormyMK.setText("");
        btnPathToNormyMK.setGraphic(new ImageView(TREE_NODE_IMG));
        btnPathToNormyMK.setTooltip(new Tooltip("Выберите директорию"));
        //ПОКАЗЫВАТЬ ПРЕФИКСЫ
        chbShowPrefixes.setSelected(CH_CURRENT_USER_SETTINGS.isShowPrefixes());
        //ПРЕФИКС ПО УМОЛЧАНИЮ
        new BXPrefix().create(cmbPrefixChooser);
        cmbPrefixChooser.getSelectionModel().select(CH_CURRENT_USER_SETTINGS.getDefaultPrefix());
        //ПОКАЗЫАТЬ PDF В ПРОГРАММЕ
        String openPDFWith = CH_CURRENT_USER_SETTINGS.getPathToOpenPDFWith();
        tfPathToOpenPDFWith.setText(openPDFWith.equals("") ? USE_SYSTEM_SETTINGS : openPDFWith);
        btnPathToOpenPDFWith.setText("");
        btnPathToOpenPDFWith.setGraphic(new ImageView(TREE_NODE_IMG));
        btnPathToOpenPDFWith.setTooltip(new Tooltip("Выберите исполняемый файл(.ехе)"));
        //ПОКАЗЫАТЬ ФОТО В ПРОГРАММЕ
        String openImageWith = CH_CURRENT_USER_SETTINGS.getPathToOpenImageWith();
        tfPathToOpenImageWith.setText(openImageWith.equals("") ? USE_SYSTEM_SETTINGS : openImageWith);
        btnPathToOpenImageWith.setText("");
        btnPathToOpenImageWith.setGraphic(new ImageView(TREE_NODE_IMG));
        btnPathToOpenImageWith.setTooltip(new Tooltip("Выберите исполняемый файл(.ехе)"));
        //ПОКАЗЫАТЬ 3D ИЗОБРАЖЕНИЯ В ПРОГРАММЕ
        String openSolidWith = CH_CURRENT_USER_SETTINGS.getPathToOpenSolidWith();
        tfPathToOpenSolidWith.setText(openSolidWith.equals("") ? USE_SYSTEM_SETTINGS : openSolidWith);
        btnPathToOpenSolidWith.setText("");
        btnPathToOpenSolidWith.setGraphic(new ImageView(TREE_NODE_IMG));
        btnPathToOpenSolidWith.setTooltip(new Tooltip("Выберите исполняемый файл(.ехе)"));
        //ПОСЛЕДНЯЯ ВЕРСИЯ EXE
        if (!CH_CURRENT_USER_GROUP.isAdministrate()) {
            tabPane.getTabs().removeAll(tabVersionEXE);
        } else {
            VersionDesktop lastVersion = AppStatic.findCurrentLastAppVersion();
            tfLastVersion.setText(lastVersion.getName());
            tfPathToLastVersion.setText(lastVersion.getPath() == null ? "" : lastVersion.getPath());
            taLastVersionNote.setText(lastVersion.getNote() == null ? "" : lastVersion.getNote());
            btnPathToLastVersion.setTooltip(new Tooltip("Выберите исполняемый файл(ехе/jar)"));
        }

        //ПОСЛЕДНЯЯ ВЕРСИЯ APK
        if (!CH_CURRENT_USER_GROUP.isAdministrate()) {
            tabPane.getTabs().removeAll(tabVersionAPK);
        } else {
            List<VersionAndroid> versionsAPK = CH_VERSIONS_ANDROID.findAll();
            VersionAndroid lastVersionAPK = versionsAPK.get(versionsAPK.size() - 1);
            tfLastVersionAPK.setText(lastVersionAPK.getName());
            taLastVersionNoteAPK.setText(lastVersionAPK.getNote() == null ? "" : lastVersionAPK.getNote());

            btnUploadNewAPKToDB.setTooltip(new Tooltip("Выберите файл apk"));
        }
    }

    @FXML
    void uploadNewAPKToDB(Event event) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("C:\\"));
        File file = chooser.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
        if(file == null) return;
        String ext = FileUtil.getExtension(file);
        if(ext.equals("apk")){
            try {
                CH_FILES.upload(file.getName(), "apk", file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Warning1.create("Ошибка!",
                    String.format("Невозможно загрузить файл с расширением %s", ext),
                    "Загрузите файл с расширением .apk");
        }
    }

    @FXML
    void cancel(Event event) {
        closeWindow(event);
    }

    @FXML
    void resetSettings(Event event) {
        AppSettings defSettings = CH_SETTINGS.findByName("default");

        List<String> cmbItems = cmbMonitorChooser.getItems();
        cmbMonitorChooser.getSelectionModel().select(0);
        //PDF просмотрщик
        cmbPDFViewerChooser.getSelectionModel().select(EPDFViewer.values()[defSettings.getPdfViewer()]);
        //НОРМЫ МК
        tfPathToNormyMK.setText(defSettings.getPathToNormyMK());
        //ПОКАЗЫАТЬ PDF В ПРОГРАММЕ
        tfPathToOpenPDFWith.setText(USE_SYSTEM_SETTINGS);
        //ПОКАЗЫАТЬ IMAGE В ПРОГРАММЕ
        tfPathToOpenImageWith.setText(USE_SYSTEM_SETTINGS);
        //ПОКАЗЫАТЬ SOLID  В ПРОГРАММЕ
        tfPathToOpenSolidWith.setText(USE_SYSTEM_SETTINGS);
        //ПОКАЗЫВАТЬ ПРЕФИКСЫ
        chbShowPrefixes.setSelected(defSettings.isShowPrefixes());
        //ПРЕФИКС ПО УМОЛЧАНИЮ
        cmbPrefixChooser.getSelectionModel().select(defSettings.getDefaultPrefix());
        //ПРОВЕРЯТЬ ВВЕДЕННЫЕ ДЕЦИМАЛЬНЫЕ НОМЕРА
        chbValidateDecNumbersEntering.setSelected(defSettings.isValidateDecNumbers());
        //ПОСЛЕДНЯЯ ВЕРСИЯ
        VersionDesktop lastVersion = AppStatic.findCurrentLastAppVersion();
        tfLastVersion.setText(lastVersion.getName());
        tfPathToLastVersion.setText(lastVersion.getPath() == null ? "" : lastVersion.getPath());
        taLastVersionNote.setText(lastVersion.getNote() == null? "" : lastVersion.getNote());

    }

    @FXML
    void saveSettings(Event event) {
        //ПОКАЗЫАТЬ PDF В ПРОГРАММЕ
        String pathToPDFOpener = tfPathToOpenPDFWith.getText().trim();
        if(!pathToPDFOpener.equals(USE_SYSTEM_SETTINGS) && !pathToPDFOpener.equals("") && !checkPath(pathToPDFOpener, "PDF")) return;

        String pathToImageOpener = tfPathToOpenImageWith.getText().trim();
        if(!pathToImageOpener.equals(USE_SYSTEM_SETTINGS) && !pathToImageOpener.equals("") && !checkPath(pathToImageOpener, "PICTURE")) return;

        String pathToSolidOpener = tfPathToOpenSolidWith.getText().trim();
        if(!pathToSolidOpener.equals(USE_SYSTEM_SETTINGS) && !pathToSolidOpener.equals("") && !checkPath(pathToSolidOpener, "SOLID"))  return;

        //ПОКАЗЫВАТЬ PDF В ПРОГРАММЕ
        CH_CURRENT_USER_SETTINGS.setPathToOpenPDFWith(pathToPDFOpener.equals(USE_SYSTEM_SETTINGS) ? "" : pathToPDFOpener);
        //ПОКАЗЫАТЬ IMAGE В ПРОГРАММЕ
        CH_CURRENT_USER_SETTINGS.setPathToOpenImageWith(pathToImageOpener.equals(USE_SYSTEM_SETTINGS) ? "" : pathToImageOpener);
        //ПОКАЗЫАТЬ SOLID В ПРОГРАММЕ
        CH_CURRENT_USER_SETTINGS.setPathToOpenSolidWith(pathToSolidOpener.equals(USE_SYSTEM_SETTINGS) ? "" : pathToSolidOpener);

        //МОНИТОР
        AppProperties.getInstance().setMonitor(cmbMonitorChooser.getSelectionModel().getSelectedIndex());
        //PDF просмотрщик
        CH_CURRENT_USER_SETTINGS.setPdfViewer(cmbPDFViewerChooser.getSelectionModel().getSelectedIndex());
        CH_PDF_VIEWER = cmbPDFViewerChooser.getSelectionModel().getSelectedItem();
        //НОРМЫ МК
        CH_DEFAULT_PATH_TO_NORMY_MK = new File(tfPathToNormyMK.getText().trim());
        CH_CURRENT_USER_SETTINGS.setPathToNormyMK(tfPathToNormyMK.getText().trim());
        //ПОКАЗЫВАТЬ ПРЕФИКСЫ
        CH_CURRENT_USER_SETTINGS.setShowPrefixes(chbShowPrefixes.isSelected());
        CH_SHOW_PREFIX = chbShowPrefixes.isSelected(); //для моментального применения
        //ПРЕФИКС ПО УМОЛЧАНИЮ
        Prefix newPrefix = cmbPrefixChooser.getSelectionModel().getSelectedItem();
        CH_DEFAULT_PREFIX = newPrefix;
        CH_CURRENT_USER_SETTINGS.setDefaultPrefix(newPrefix);
        //ПРОВЕРЯТЬ ВВЕДЕННЫЕ ДЕЦИМАЛЬНЫЕ НОМЕРА
        CH_CURRENT_USER_SETTINGS.setValidateDecNumbers(chbValidateDecNumbersEntering.isSelected());
        CH_VALIDATE_DEC_NUMBERS = chbValidateDecNumbersEntering.isSelected();
        CH_SETTINGS.update(CH_CURRENT_USER_SETTINGS);

        if (CH_CURRENT_USER_GROUP.isAdministrate()) {
            //ПОСЛЕДНЯЯ ВЕРСИЯ ПРОГРАММЫ EXE
            VersionDesktop lastVersion = AppStatic.findCurrentLastAppVersion();
            VersionDesktop versionDesktop = CH_VERSIONS_DESKTOP.findByName(tfLastVersion.getText().trim());
            if (versionDesktop != null) {
                versionDesktop.setPath(tfPathToLastVersion.getText().trim());
                versionDesktop.setNote(taLastVersionNote.getText());
                CH_VERSIONS_DESKTOP.update(versionDesktop);
            } else {
                VersionDesktop version = new VersionDesktop();
                version.setName(tfLastVersion.getText().trim());
                if (version.compareTo(AppStatic.findCurrentLastAppVersion()) < 0) {
                    Warning1.create("Внимание!",
                            "Наименование версии не прошло проверку",
                            "Последняя версия программы " + lastVersion.getName());
                    return;
                } else {
                    version.setData(LocalDateTime.now().toString());
                    version.setPath(tfPathToLastVersion.getText().trim());
                    version.setNote(taLastVersionNote.getText());
                    CH_VERSIONS_DESKTOP.save(version);
                }
            }

            //ПОСЛЕДНЯЯ ВЕРСИЯ ПРОГРАММЫ APK
            List<VersionAndroid> versionsAPK = CH_VERSIONS_ANDROID.findAll();
            VersionAndroid lastVersionAPKinDB = versionsAPK.get(versionsAPK.size() - 1);
            VersionAndroid versionAPK = CH_VERSIONS_ANDROID.findByName(tfLastVersionAPK.getText().trim());
            if (versionAPK != null) {
                versionAPK.setNote(taLastVersionNoteAPK.getText());
                CH_VERSIONS_ANDROID.update(versionAPK);
            } else {
                VersionAndroid versionAndroid = new VersionAndroid();
                versionAndroid.setName(tfLastVersionAPK.getText().trim());
                if (versionAndroid.compareTo(lastVersionAPKinDB) < 0) {
                    Warning1.create("Внимание!",
                            "Наименование версии не прошло проверку",
                            "Последняя версия программы " + lastVersionAPKinDB.getName());
                    return;
                } else {
                    versionAndroid.setData(LocalDateTime.now().toString());
                    versionAndroid.setNote(taLastVersionNoteAPK.getText());
                    CH_VERSIONS_ANDROID.save(versionAndroid);
                }
            }

        }

        //Обновляем внешний вид табов
        for(Tab tab: CH_TAB_PANE.getTabs()){
            if(((AppTab)tab).getTabController() instanceof UpdatableTabController)
                Platform.runLater(()->((UpdatableTabController) ((AppTab)tab).getTabController()).updateTab());
        }
        closeWindow(event);
    }

    /**
     * Проверяется указанный файл
     */
    private boolean checkPath(String pathToApp, String txt) {
        File pdfOpener = new File(pathToApp);
        if (!pdfOpener.exists()){
            Warning1.create("ОШИБКА",
                    String.format("Файл открытия %s не существует", txt),
                    "Укажите другой файл или оставьте пустую строку");
            return false;
        }
        if (!FileUtil.getExtension(pdfOpener.getName()).equals("exe")) {
            Warning1.create("ОШИБКА",
                    String.format("Файл открытия %s не является исполняемым(.exe)", txt),
                    "Укажите другой файл или оставьте пустую строку");
            return false;
        }

        return true;
    }

    @FXML
    void choosePathToNormyMK(Event event) {
        File initialDirectory = new File(tfPathToNormyMK.getText().trim());
        File newDirectory = AppStatic.chooseDirectory(event, initialDirectory);
        if(newDirectory != null && newDirectory.exists())
            tfPathToNormyMK.setText(newDirectory.toString());
    }

    @FXML
    void choosePathToOpenPDFWith(Event event) {
        File initialDirectory = new File("C:\\");
        File newFileOpener = AppStatic.chooseFile(event, initialDirectory);
        if(newFileOpener != null && newFileOpener.exists())
            tfPathToOpenPDFWith.setText(newFileOpener.toString());
        else
            tfPathToOpenPDFWith.setText("");
    }

    @FXML
    void choosePathToOpenImageWith(Event event) {
        File initialDirectory = new File("C:\\");
        File newFileOpener = AppStatic.chooseFile(event, initialDirectory);
        if(newFileOpener != null && newFileOpener.exists())
            tfPathToOpenImageWith.setText(newFileOpener.toString());
        else
            tfPathToOpenImageWith.setText("");
    }

    @FXML
    void choosePathToOpenSolidWith(Event event) {
        File initialDirectory = new File("C:\\");
        File newFileOpener = AppStatic.chooseFile(event, initialDirectory);
        if(newFileOpener != null && newFileOpener.exists())
            tfPathToOpenSolidWith.setText(newFileOpener.toString());
        else
            tfPathToOpenSolidWith.setText("");
    }


    @FXML
    void choosePathToLastVersion(Event event) {
        FileChooser chooser = new FileChooser();
        File initFile = new File("\\\\\\\\serverhp.ntcpik.com\\\\ntcpik\\\\BazaPIK\\\\");
        chooser.setInitialDirectory(initFile.exists()? initFile : new File("C:\\"));
        File newFile = chooser.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
        if(newFile == null) return;
        tfPathToLastVersion.setText(newFile.toString());
    }

}
