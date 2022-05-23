package ru.wert.datapik.utils.excel.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class EditorPatchController {


    @FXML
    private StackPane stpExcelTable;

    @FXML
    private Label lblExcelFile;

    @FXML
    private HBox hbButtons;


    public EditorPatchController() {

    }

    @FXML
    public void initialize() {

    }

    public void init(HBox hbox) {
        stpExcelTable.getChildren().add(hbox);

    }

}
