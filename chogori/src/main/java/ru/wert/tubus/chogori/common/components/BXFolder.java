package ru.wert.tubus.chogori.common.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.chogori.popups.HintPopup;

import java.util.List;

import static ru.wert.tubus.client.entity.serviceQUICK.FolderQuickService.DEFAULT_FOLDER;
import static ru.wert.tubus.client.utils.BLConst.RAZLOZHENO;
import static ru.wert.tubus.chogori.application.services.ChogoriServices.CH_QUICK_FOLDERS;

public class BXFolder {

    private ComboBox<Folder> bxFolder;
    private HintPopup folderHint;
    private TextField editor;
    private Folder newFolder;

    public void create(ComboBox<Folder> bxFolder){
        this.bxFolder = bxFolder;
        this.editor = bxFolder.getEditor();

        bxFolder.setEditable(true);

        ObservableList<Folder> folders = FXCollections.observableArrayList(CH_QUICK_FOLDERS.findAll());
        bxFolder.setItems(folders);

        createCellFactory();

        createConverter();

        createTextListener();

        createDecNumberPrompt();

        bxFolder.setOnAction(event -> {
            if(bxFolder.getValue() != null)
                editor.positionCaret(bxFolder.getValue().getName().length());
        });

        bxFolder.setValue(DEFAULT_FOLDER);

    }

    private void createCellFactory() {
        //CellFactory определяет вид элементов комбобокса - только имя префикса
        bxFolder.setCellFactory(i -> new ListCell<Folder>() {
            @Override
            protected void updateItem (Folder item,boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    Label name = new Label(item.getName());
                    name.setStyle("-fx-text-fill: darkgreen; -fx-font-weight: bold");
                    setGraphic(name);
                }
            }

        });
    }

    /**
     * Конвертер вызывается первым
     */
    private void createConverter() {
        bxFolder.setConverter(new StringConverter<Folder>() {
            @Override
            public String toString(Folder folder) {
                String folderName = null;
                if(folder != null) {
                    folderName = folder.getName();
                    newFolder = folder;
                }
                return folderName;
            }

            @Override
            public Folder fromString(String string) {
                if(newFolder == null) return DEFAULT_FOLDER;
                return newFolder;
            }
        });
    }

    private void createTextListener() {

        editor.textProperty().addListener((observable, oldVal, newVal) -> {

            if (editor.isFocused()) {
                if (!newVal.equals(newFolder.getName())) {

                    //Находим подходящие изделия
                    List<Folder> folders = CH_QUICK_FOLDERS.findAllByText(newVal);


                    bxFolder.getItems().clear();
                    bxFolder.getSelectionModel().clearSelection();
                    bxFolder.hide();
                    if (!folders.isEmpty()) {
                        bxFolder.getItems().setAll(folders);
                        if (!bxFolder.isShowing())
                            bxFolder.show();
                    }

                }
            }
        });
    }

    /**
     * Выводит ниже комбобокса подсказку - децимальный номер
     */
    private void createDecNumberPrompt() {
        bxFolder.setOnMouseEntered(event ->{
            String name = "";
            String decNumber = "";
            Folder folder = bxFolder.getValue();
            if(folder != null) {
                name = folder.getName();
                if(name.equals(RAZLOZHENO)) return;
                folderHint = new HintPopup(bxFolder, name, 0.0);
                folderHint.showHint();
            }
        });
        bxFolder.setOnMouseExited(event ->{
            if(folderHint != null)
                folderHint.closeHint();
        });
        bxFolder.setOnMousePressed(event->{
            if(folderHint != null)
                folderHint.closeHint();
        });
    }

}
