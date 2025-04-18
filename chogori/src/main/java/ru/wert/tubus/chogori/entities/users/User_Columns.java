package ru.wert.tubus.chogori.entities.users;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.wert.tubus.chogori.popups.HintPopup;
import ru.wert.tubus.client.entity.models.User;

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
            User user = cd.getValue();
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

    /**
     * ОНЛАЙН
     */
    public static TableColumn<User, Label> createTcOnline(){
        TableColumn<User, Label> tcStatus = new TableColumn<>("Онлайн");
        tcStatus.setCellValueFactory(cd->{
            User user = cd.getValue();
            Label lblOnline = new Label();
            lblOnline.setMouseTransparent(true); //Подсказка не работает
            if(user.isOnline()) {
                lblOnline.setText("ОНЛАЙН");
                lblOnline.setStyle("-fx-text-fill: darkgreen");
            } else{
                lblOnline.setText("ВЫШЕЛ");
                lblOnline.setStyle("-fx-text-fill: darkred");
            }
            return new ReadOnlyObjectWrapper<>(lblOnline);
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
