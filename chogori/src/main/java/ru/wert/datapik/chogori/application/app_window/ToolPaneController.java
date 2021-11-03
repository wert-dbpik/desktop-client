package ru.wert.datapik.chogori.application.app_window;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import ru.wert.datapik.utils.statics.UtilStaticNodes;
import ru.wert.datapik.utils.search.SearchField;
import ru.wert.datapik.utils.toolpane.ChogoriToolBar;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;
import static ru.wert.datapik.utils.images.BtnImages.BTN_CLEAN_IMG_W;

public class ToolPaneController {
    @FXML
    private ChogoriToolBar toolBar;

    @FXML
    private HBox extraToolPane;

    @FXML
    void initialize() {
        UtilStaticNodes.CH_TOOL_BAR = toolBar;
        Platform.runLater(this::createSearchField);

    }

    private void createSearchField(){
        CH_SEARCH_FIELD = new SearchField();
        CH_SEARCH_FIELD.setPrefWidth(300);
        Button btnClean = new Button();
        btnClean.setOnAction((e)->{
            CH_SEARCH_FIELD.setText("");
            CH_SEARCH_FIELD.requestFocus();
        });
        btnClean.setGraphic(new ImageView(BTN_CLEAN_IMG_W));
        extraToolPane.getChildren().addAll(CH_SEARCH_FIELD, btnClean);

    }

}
