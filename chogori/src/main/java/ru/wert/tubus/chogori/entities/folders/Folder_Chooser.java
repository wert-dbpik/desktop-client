package ru.wert.tubus.chogori.entities.folders;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.chogori.entities.catalogOfFolders.CatalogOfFoldersPatch;
import ru.wert.tubus.winform.window_decoration.WindowDecoration;

import java.io.IOException;

import static ru.wert.tubus.chogori.statics.AppStatic.closeWindow;

public class Folder_Chooser {

    static Folder chosenFolder = null;
    static Folder_TableView folderTableView;

    public static Folder create(Window stage){

        try {
            CatalogOfFoldersPatch catalogPatch = new CatalogOfFoldersPatch().create();
            folderTableView = catalogPatch.getFolderTableView();
            folderTableView.setOnMouseClicked((event -> {
                if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)
                    chooseFolder(event);
            }));


            FXMLLoader loader = new FXMLLoader(Folder_Chooser.class.getResource("/chogori-fxml/folders/folderChooser.fxml"));
            Parent parent = loader.load();

            StackPane stackPane = (StackPane)parent.lookup("#stackPane");
            stackPane.getChildren().add(catalogPatch.getCatalogOfFoldersPatch());

            //CANCEL
            Button btnCancel = (Button)parent.lookup("#btnCancel");
            btnCancel.setOnAction((e)->{
                chosenFolder = null;
                closeWindow(e);
            });

            //OK
            Button btnOk = (Button)parent.lookup("#btnOk");
            btnOk.setOnAction(Folder_Chooser::chooseFolder);

            new WindowDecoration("Выберите пакет", parent, true,  (Stage)stage, true);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return chosenFolder;
    }

    private static void chooseFolder(Event e) {
        Item item = folderTableView.getSelectionModel().getSelectedItem();
        if(item instanceof Folder)
            chosenFolder = (Folder) item;
        closeWindow(e);
    }

}


