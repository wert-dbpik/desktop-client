package ru.wert.datapik.chogori.password;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import ru.wert.datapik.chogori.common.components.BtnDoubleEye;
import ru.wert.datapik.winform.warnings.Warning1;

import java.util.Arrays;
import java.util.List;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_USERS;
import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.datapik.winform.statics.WinformStatic.closeWindow;

public class ChangePassword {


    @FXML
    private PasswordField ptfOldPass;

    @FXML
    private PasswordField ptfNewPass1;

    @FXML
    private PasswordField ptfNewPass2;

    @FXML
    private HBox hbEye;

    @FXML
    private HBox hbForOldPassField;

    @FXML
    private HBox hbForNewPassField1;

    @FXML
    private HBox hbForNewPassField2;

    @FXML
    public void initialize() {
        //Инициализация КНОПКИ с глазом
        BtnDoubleEye btnDoubleEye = new BtnDoubleEye(false);
        Button btnEye = btnDoubleEye.create();
        hbEye.getChildren().add(btnEye);

        final TextField tfOldPass = new TextField();
        final TextField tfNewPass1 = new TextField();
        final TextField tfNewPass2 = new TextField();

        List<TextField> tfs = Arrays.asList(tfOldPass, tfNewPass1, tfNewPass2);
        for(TextField tf: tfs){
            tf.setFont(ptfOldPass.getFont());
            tf.setPrefSize(ptfOldPass.getPrefWidth(), ptfOldPass.getPrefHeight());
            HBox.setHgrow(tf, Priority.ALWAYS);
            tf.setManaged(false);
            tf.setVisible(false);
            tf.managedProperty().bind(btnDoubleEye.getStateProperty());
            tf.visibleProperty().bind(btnDoubleEye.getStateProperty());
        }

        List<PasswordField> pfs = Arrays.asList(ptfOldPass, ptfNewPass1, ptfNewPass2);
        for(PasswordField pf : pfs){
            pf.managedProperty().bind(btnDoubleEye.getStateProperty().not());
            pf.visibleProperty().bind(btnDoubleEye.getStateProperty().not());
        }

        tfOldPass.textProperty().bindBidirectional(ptfOldPass.textProperty());
        tfNewPass1.textProperty().bindBidirectional(ptfNewPass1.textProperty());
        tfNewPass2.textProperty().bindBidirectional(ptfNewPass2.textProperty());

        hbForOldPassField.getChildren().add(tfOldPass);
        hbForNewPassField1.getChildren().add(tfNewPass1);
        hbForNewPassField2.getChildren().add(tfNewPass2);

    }


    @FXML
    void cancel(ActionEvent event) {
        closeWindow(event);
    }

    @FXML
    void ok(ActionEvent event) {
        String oldPass = ptfOldPass.getText();
        String newPass1 = ptfNewPass1.getText();
        String newPass2 = ptfNewPass2.getText();

        if (!oldPass.equals(CH_CURRENT_USER.getPassword())) {
            Warning1.create("ОШИБКА!", "Старый пароль не верен!", "Повторите ввод");
        } else if (newPass1.equals(newPass2)) {
            CH_CURRENT_USER.setPassword(newPass1);
            CH_USERS.update(CH_CURRENT_USER);
            closeWindow(event);
        } else
            Warning1.create("ОШИБКА!", "Введенные пароли не совпадают!", "Повторите ввод");

    }

}

//class PasswordFieldSkin extends TextFieldSkin {
//    public static final char BULLET = '\u2022';
//
//    public PasswordFieldSkin(PasswordField passwordField) {
//        super(passwordField, new PasswordFieldBehavior(passwordField));
//    }
//
//    @Override
//    protected String maskText(String txt) {
//        TextField textField = getSkinnable();
//
//        int n = textField.getLength();
//        StringBuilder passwordBuilder = new StringBuilder(n);
//        for (int i = 0; i < n; i++) {
//            passwordBuilder.append(BULLET);
//        }
//
//        return passwordBuilder.toString();
//    }
//}
