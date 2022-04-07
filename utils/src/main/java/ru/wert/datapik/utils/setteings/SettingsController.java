package ru.wert.datapik.utils.setteings;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import ru.wert.datapik.client.entity.models.AppSettings;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.entity.models.VersionDesktop;
import ru.wert.datapik.client.retrofit.AppProperties;
import ru.wert.datapik.utils.common.components.BXMonitor;
import ru.wert.datapik.utils.common.components.BXPrefix;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.enums.EPDFViewer;
import ru.wert.datapik.winform.warnings.Warning1;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static ru.wert.datapik.utils.images.AppImages.TREE_NODE_IMG;
import static ru.wert.datapik.utils.images.BtnImages.BTN_HOME_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_SETTINGS;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_VERSIONS_DESKTOP;
import static ru.wert.datapik.utils.setteings.ChogoriSettings.*;
import static ru.wert.datapik.winform.statics.WinformStatic.closeWindow;

public class SettingsController {

    @FXML
    private Button btnSave;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnCancel;

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

    // Admin Settings =======================================

    @FXML
    private Tab tabAdminSettings;

    @FXML
    private TextField tfLastVersion;

    @FXML
    private TextField tfPathToLastVersion;

    @FXML
    private Button btnPathToLastVersion;

    @FXML
    private TextArea taLastVersionNote;


    @FXML
    void initialize() {

        //Отключаем вкладку с настройками Редактирования чертежей
        if(!CH_CURRENT_USER_GROUP.isEditDrafts()) tabDraftsSettings.setDisable(true);
        //КНОПКА СБРОС В ЗНАЧЕНИЯ ПО УМОЛЧАНИЮ
        btnReset.setText("");
        btnReset.setGraphic(new ImageView(BTN_HOME_IMG));
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
        //ПОКАЗЫВАТЬ ПРЕФИКСЫ
        chbShowPrefixes.setSelected(CH_CURRENT_USER_SETTINGS.isShowPrefixes());
        //ПРЕФИКС ПО УМОЛЧАНИЮ
        new BXPrefix().create(cmbPrefixChooser);
        cmbPrefixChooser.getSelectionModel().select(CH_CURRENT_USER_SETTINGS.getDefaultPrefix());
        //ПРОВЕРЯТЬ ВВЕДЕННЫЕ ДЕЦИМАЛЬНЫЕ НОМЕРА
        chbValidateDecNumbersEntering.setSelected(CH_CURRENT_USER_SETTINGS.isValidateDecNumbers());
        //ПОСЛЕДНЯЯ ВЕРСИЯ
        VersionDesktop lastVersion = AppStatic.findCurrentLastAppVersion();
        tfLastVersion.setText(lastVersion.getName());
        tfPathToLastVersion.setText(lastVersion.getPath() == null ? "" : lastVersion.getPath());
        taLastVersionNote.setText(lastVersion.getNote() == null ? "" : lastVersion.getNote());
        if (!CH_CURRENT_USER_GROUP.isAdministrate()) {
            tfLastVersion.setDisable(true);
            tfPathToLastVersion.setDisable(true);
            taLastVersionNote.setDisable(true);
            btnPathToLastVersion.setDisable(true);
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
        //ПОСЛЕДНЯЯ ВЕРСИЯ ПРОГРАММЫ
        VersionDesktop lastVersion = AppStatic.findCurrentLastAppVersion();
        VersionDesktop versionDesktop = CH_VERSIONS_DESKTOP.findByName(tfLastVersion.getText().trim());
        if(versionDesktop != null){
            versionDesktop.setPath(tfPathToLastVersion.getText().trim());
            versionDesktop.setNote(taLastVersionNote.getText());
            CH_VERSIONS_DESKTOP.update(versionDesktop);
        } else {
            VersionDesktop version = new VersionDesktop();
            version.setName(tfLastVersion.getText().trim());
            if(version.compareTo(AppStatic.findCurrentLastAppVersion()) < 0){
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

        closeWindow(event);
    }

    @FXML
    void choosePathToNormyMK(Event event) {
        File initialDirectory = new File(tfPathToNormyMK.getText().trim());
        File newDirectory = AppStatic.chooseDirectory(event, initialDirectory);
        if(newDirectory != null && newDirectory.exists())
            tfPathToNormyMK.setText(newDirectory.toString());
    }

    @FXML
    void choosePathToLastVersion(Event event) {
        FileChooser chooser = new FileChooser();
        File initFile = new File("\"\\\\\\\\serverhp.ntcpik.com\\\\ntcpik\\\\BazaPIK\\\\\"");
        chooser.setInitialDirectory(initFile.exists()? initFile : new File("C:\\"));
        File newFile = chooser.showOpenDialog(((Node)event.getSource()).getScene().getWindow());
        if(newFile == null) return;
        tfPathToLastVersion.setText(newFile.toString());
    }

}
