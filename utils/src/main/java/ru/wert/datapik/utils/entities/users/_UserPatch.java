package ru.wert.datapik.utils.entities.users;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class _UserPatch {

    private User_Controller userController;
    private Parent usersPatch;

    public _UserPatch() {
        createPanel();

    }

    private void createPanel() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/users/users.fxml"));
            usersPatch = loader.load();
            userController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User_Controller getUserController() {
        return userController;
    }

    public Parent getUsersPatch() {
        return usersPatch;
    }

    public HBox getUsersButtons(){
        return userController.getUsersButtons();
    }

}
