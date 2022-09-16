package ru.wert.datapik.utils.remarks;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.entity.models.Passport;
import ru.wert.datapik.client.interfaces.SearchableTab;
import ru.wert.datapik.client.interfaces.UpdatableTabController;

public class RemarksController  implements UpdatableTabController {

    @FXML
    private Label lblPassportsData; //Данные пасспорта

    @FXML
    private VBox vbRemarksContainer; //Контенер для таблицы с камментариями

    Remark_TableView tableView;

    public void init(Passport passport){
        tableView = new Remark_TableView("КОММЕНТАРИИ", true, passport);
        tableView.updateView();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vbRemarksContainer.getChildren().add(tableView);
    }


    @Override
    public void updateTab() {

    }
}
