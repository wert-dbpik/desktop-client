package ru.wert.datapik.chogori.entities.users;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.wert.datapik.client.entity.models.User;

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
        tcName.setMinWidth(300);
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
        TableColumn<User, String> tcUserPassport = new TableColumn<>("Пароль");
        tcUserPassport.setCellValueFactory(cd ->{
            String pass = "******";
            return new ReadOnlyStringWrapper(pass);
        });
        tcUserPassport.setMinWidth(150);
        tcUserPassport.setStyle("-fx-alignment: CENTER;");
        return tcUserPassport;
    };

}
