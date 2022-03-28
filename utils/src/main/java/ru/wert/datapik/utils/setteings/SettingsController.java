package ru.wert.datapik.utils.setteings;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import ru.wert.datapik.client.entity.models.AppSettings;
import ru.wert.datapik.client.entity.models.Prefix;
import ru.wert.datapik.client.retrofit.AppProperties;
import ru.wert.datapik.utils.common.components.BXMonitor;
import ru.wert.datapik.utils.common.components.BXPrefix;
import ru.wert.datapik.utils.statics.AppStatic;
import ru.wert.datapik.winform.enums.EPDFViewer;

import java.io.File;
import java.util.List;

import static ru.wert.datapik.utils.images.AppImages.TREE_NODE_IMG;
import static ru.wert.datapik.utils.images.BtnImages.BTN_HOME_IMG;
import static ru.wert.datapik.utils.services.ChogoriServices.CH_SETTINGS;
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
        closeWindow(event);
    }

    @FXML
    void choosePathToNormyMK(Event event) {
        File initialDirectory = new File(tfPathToNormyMK.getText().trim());
        File newDirectory = AppStatic.chooseDirectory(event, initialDirectory);
        tfPathToNormyMK.setText(newDirectory.toString());
    }
}
