package ru.wert.datapik.utils.entities.user_groups;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.wert.datapik.client.entity.models.UserGroup;

public class UserGroup_Columns {

    /**
     * ID
     */
    public static TableColumn<UserGroup, String> createTcId(){
        TableColumn<UserGroup, String> tcId = new TableColumn<>("ID");
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcId.setStyle("-fx-alignment: CENTER;");
        return tcId;
    };

    /**
     * ПОЛЬЗОВАТЕЛЬ
     */
    public static TableColumn<UserGroup, String> createTcName(){
        TableColumn<UserGroup, String> tcName = new TableColumn<>("Наименование");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcName.setMinWidth(300);
        return tcName;
    };

}
