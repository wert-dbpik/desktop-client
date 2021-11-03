package ru.wert.datapik.utils.editor;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import lombok.Getter;
import ru.wert.datapik.utils.editor.controllers.EditorPatchController;
import ru.wert.datapik.utils.editor.poi.POIReader;
import ru.wert.datapik.utils.editor.table.EditorTable;

import java.io.File;
import java.io.IOException;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_TAB_PANE;
import static ru.wert.datapik.winform.statics.WinformStatic.CH_MAIN_STAGE;

public class EditorPatch {

    private FileChooser fileChooser;
    private static EditorPatch instance;

    @Getter private POIReader poiReader;
    @Getter private TableView tableView;
    @Getter private Parent parent;
    @Getter private String fileName;

    public static EditorPatch getInstance(){
        if(instance == null)
            instance = new EditorPatch();
        return instance;
    }

    private EditorPatch(){
        fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("exel", "*.xlsx")
        );
        fileChooser.setInitialDirectory(new File("C:/test"));
    }

    public void invokeFileChooser(){
        File selectedFile = fileChooser.showOpenDialog(CH_MAIN_STAGE);
        if (selectedFile == null) return;
        try {
            poiReader = new POIReader(selectedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        showEditor(selectedFile.getName());
    }

    private void showEditor(final String fileName){
        this.fileName = fileName;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utils-fxml/editor/editorPatch.fxml"));
            parent = loader.load();
            EditorPatchController editorPatchController = loader.getController();

            HBox hbox = new HBox();
            hbox.setAlignment(Pos.TOP_CENTER);
            tableView = new EditorTable(poiReader, hbox).getTableView();
            tableView.setEditable(true);
            hbox.getChildren().add(tableView);
            tableView.refresh();
            editorPatchController.init(hbox);

//            CH_TAB_PANE.createNewTab(fileName, parent, true, null);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
