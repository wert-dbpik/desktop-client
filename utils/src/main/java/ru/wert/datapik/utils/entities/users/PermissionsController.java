package ru.wert.datapik.utils.entities.users;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionsController {

    @FXML
    private VBox vbPermitions;

    @FXML
    void initialize(){

        // определяем набор вложенных узлов
        CheckBox chbUsers =  new CheckBox("Пользователи");
        CheckBox chbAddUsers =  new CheckBox("Добавлять");
        CheckBox chbChangeUsers =  new CheckBox("Изменять");
        CheckBox chbDeleteUsers =  new CheckBox("Удалять");

        CheckBox chbDrafts =  new CheckBox("Чертежи");
        CheckBox chbAddDrafts =  new CheckBox("Добавлять");
        CheckBox chbChangeDrafts =  new CheckBox("Изменять");
        CheckBox chbDeleteDrafts =  new CheckBox("Удалять");

        List<CheckBox> mainCheckboxes = Arrays.asList(chbUsers, chbDrafts);
        List<CheckBox> usersCheckboxes = Arrays.asList(chbAddUsers, chbChangeUsers, chbDeleteUsers);
        List<CheckBox> draftsCheckboxes = Arrays.asList(chbAddDrafts, chbChangeDrafts, chbDeleteDrafts);
        List<CheckBox> minions = new ArrayList<>();
        minions.addAll(usersCheckboxes);
        minions.addAll(draftsCheckboxes);

        for(CheckBox cb : mainCheckboxes)
            cb.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: black; -fx-end-margin: 10;");

        for(CheckBox cb : minions)
            cb.setStyle("-fx-font-style: oblique; -fx-font-size: 16; -fx-text-fill: black; -fx-end-margin: 5;");


        vbPermitions.getChildren().addAll(
                new Separator(),
                chbUsers,
                chbAddUsers,
                chbChangeUsers,
                chbDeleteUsers,
                new Separator(),
                chbDrafts,
                chbAddDrafts,
                chbChangeDrafts,
                chbDeleteDrafts,
                new Separator()
        );



    }

    public void init(){

    }





}