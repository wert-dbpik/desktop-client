package ru.wert.datapik.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import ru.wert.datapik.client.entity.models.User;

import java.util.Comparator;

import static ru.wert.datapik.chogori.application.services.ChogoriServices.CH_USERS;

public class BXUsers {

    public BXUsers(ComboBox<User> bxUsers) {

        //!!!!!!!!!!!!!!!!!!!
        bxUsers.setEditable(false);

        bxUsers.setStyle("-fx-font-size: 24; -fx-background-color: white");
        ObservableList<User> allUsers = FXCollections.observableArrayList(CH_USERS.findAll());
        allUsers.sort(Comparator.comparing(User::getName));
        bxUsers.setItems(allUsers);

        bxUsers.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user.getName();
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });
    }


}
