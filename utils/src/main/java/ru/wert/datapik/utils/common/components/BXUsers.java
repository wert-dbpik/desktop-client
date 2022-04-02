package ru.wert.datapik.utils.common.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import okhttp3.internal.cache.DiskLruCache;
import ru.wert.datapik.client.entity.models.Density;
import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.User;

import java.util.Comparator;
import java.util.List;

import static ru.wert.datapik.utils.services.ChogoriServices.CH_USERS;

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
