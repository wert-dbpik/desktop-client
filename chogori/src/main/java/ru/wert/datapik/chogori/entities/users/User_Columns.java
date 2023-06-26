package ru.wert.datapik.chogori.entities.users;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.wert.datapik.chogori.popups.HintPopup;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.User;
import ru.wert.datapik.winform.enums.EDraftStatus;

import static ru.wert.datapik.winform.statics.WinformStatic.parseLDTtoNormalDate;

public class User_Columns {

    /**
     * ID
     */
    public static TableColumn<User, String> createTcId(){
        TableColumn<User, String> tcId = new TableColumn<>("ID");
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcId.setStyle("-fx-alignment: CENTER;");
        return tcId;
    };

    /**
     * ПОЛЬЗОВАТЕЛЬ
     */
    public static TableColumn<User, String> createTcName(){
        TableColumn<User, String> tcName = new TableColumn<>("Пользователь");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcName.setMinWidth(200);
        return tcName;
    };

    /**
     * ГРУППА
     */
    public static TableColumn<User, String> createTcUserGroup(){
        TableColumn<User, String> tcUserGroup = new TableColumn<>("Группа\nпользователей");
        tcUserGroup.setCellValueFactory(new PropertyValueFactory<>("userGroup"));
        tcUserGroup.setMinWidth(200);
        return tcUserGroup;
    };

    /**
     * ПАРОЛЬ
     */
    public static TableColumn<User, String> createTcPassword(){
        TableColumn<User, String> tcUserPassword = new TableColumn<>("Пароль");
        tcUserPassword.setCellValueFactory(cd ->{
            String pass = "******";
            return new ReadOnlyStringWrapper(pass);
        });
        tcUserPassword.setMinWidth(100);
        tcUserPassword.setStyle("-fx-alignment: CENTER;");
        tcUserPassword.setSortable(false);
        return tcUserPassword;
    };

    /**
     * АКТИВНОСТЬ
     */
    public static TableColumn<User, Label> createTcActivity(){
        TableColumn<User, Label> tcStatus = new TableColumn<>("Статус");
        tcStatus.setCellValueFactory(cd->{
            HintPopup hint;
            User user = cd.getValue();
            boolean status = user.isActive();
            String str = "";
            Label lblStatus = new Label();
            lblStatus.setMouseTransparent(true); //Подсказка не работает
            if(user.isActive()) {
                lblStatus.setText("АКТИВЕН");
                lblStatus.setStyle("-fx-text-fill: darkgreen");
            } else{
                lblStatus.setText("ЗАБЛОКИРОВАН");
                lblStatus.setStyle("-fx-text-fill: darkred");
                }
            return new ReadOnlyObjectWrapper<>(lblStatus);
        });
        tcStatus.setMinWidth(150);//100
        tcStatus.setPrefWidth(150);//100
        tcStatus.setMaxWidth(150);
        tcStatus.setStyle("-fx-alignment: CENTER;");
        tcStatus.setResizable(false);
        tcStatus.setSortable(false);
        return tcStatus;
    };

}
