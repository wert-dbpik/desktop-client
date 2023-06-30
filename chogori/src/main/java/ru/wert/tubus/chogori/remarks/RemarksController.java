package ru.wert.tubus.chogori.remarks;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ru.wert.tubus.client.entity.models.Passport;
import ru.wert.tubus.client.interfaces.UpdatableTabController;

public class RemarksController  implements UpdatableTabController {

    @FXML
    private Label lblPassportsData; //Данные пасспорта

    @FXML
    private VBox vbRemarksContainer; //Контенер для таблицы с камментариями

    private Remark_TableView tableView;
    @Getter private Passport passport;

    public void init(Passport passport){
        this.passport = passport;
        tableView = new Remark_TableView("КОММЕНТАРИИ", true, passport);
        tableView.updateView();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vbRemarksContainer.getChildren().add(tableView);

        lblPassportsData.setText(passport.toUsefulString());
        lblPassportsData.setStyle("-fx-text-fill: royalblue");
    }


    @Override
    public void updateTab() {

    }
}
