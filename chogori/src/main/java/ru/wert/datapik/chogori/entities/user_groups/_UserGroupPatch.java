package ru.wert.datapik.chogori.entities.user_groups;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import lombok.Getter;

import java.io.IOException;

public class _UserGroupPatch {

    @Getter private UserGroup_Controller userGroupController;
    @Getter
    private Parent userGroupsPatch;

    public _UserGroupPatch() {
        createPanel();

    }

    private void createPanel() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chogori-fxml/user_group/userGroupsPatch.fxml"));
            userGroupsPatch = loader.load();
            userGroupController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HBox getUsersButtons(){
        return userGroupController.getUserGroupsButtons();
    }

}
