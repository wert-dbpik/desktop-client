package ru.wert.tubus.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import ru.wert.tubus.client.entity.models.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_USERS;

public class BXUsers {

    public BXUsers(ComboBox<User> bxUsers) {

        //!!!!!!!!!!!!!!!!!!!
        bxUsers.setEditable(false);

        bxUsers.setStyle("-fx-font-size: 24; -fx-background-color: white");


        List<User> allUsers = new ArrayList<>();
        for(User u: CH_USERS.findAll())
            if(u.isActive()) allUsers.add(u);

        ObservableList<User> activeUsers = FXCollections.observableArrayList(allUsers);
        activeUsers.sort(Comparator.comparing(User::getName));
        bxUsers.setItems(activeUsers);

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
