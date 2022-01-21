package ru.wert.datapik.utils.entities.product_groups;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.winform.window_decoration.WindowDecoration;

import java.io.IOException;

import static ru.wert.datapik.utils.statics.AppStatic.closeWindow;

public class ProductGroup_Chooser {

    static ProductGroup group = null;

    public static ProductGroup create(Window stage){

        try {
            TreeView<ProductGroup> treeView = new _ProductGroup_TreeViewPatch<>().create();

            FXMLLoader loader = new FXMLLoader(ProductGroup_Chooser.class.getResource("/utils-fxml/productGroup/productGroupChooser.fxml"));
            Parent parent = loader.load();

            StackPane stackPane = (StackPane)parent.lookup("#stackPane");
            stackPane.getChildren().add(treeView);

            treeView.setOnMouseClicked((e)->{
                if(e.getClickCount() == 2) chooseGroup(treeView, e);
            });

            //CANCEL
            Button btnCancel = (Button)parent.lookup("#btnCancel");
            btnCancel.setOnAction((e)->{
                group = null;
                closeWindow(e);
            });

            //OK
            Button btnOk = (Button)parent.lookup("#btnOk");
            btnOk.setOnAction((e)->{
                chooseGroup(treeView, e);
            });

            new WindowDecoration("Выберите группу", parent, true,  (Stage)stage, true);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return group;
    }

    private static void chooseGroup(TreeView<ProductGroup> treeView, Event e) {
        group = treeView.getSelectionModel().getSelectedItem().getValue();
        closeWindow(e);
    }

}


