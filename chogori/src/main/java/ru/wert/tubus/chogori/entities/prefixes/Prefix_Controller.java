package ru.wert.tubus.chogori.entities.prefixes;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.wert.tubus.client.interfaces.SearchableTab;
import ru.wert.tubus.client.interfaces.UpdatableTabController;

import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class Prefix_Controller implements SearchableTab, UpdatableTabController {

    @FXML
    private AnchorPane apMainPatch;

    @FXML
    private VBox vbHeader;

    @FXML
    private HBox controlButtons;

    private Prefix_TableView tableView;


    @FXML
    void initialize() {
        //Создаем панели инструментов
        createPrefixes_ToolBar();

        //Создаем связанные между собой панели каталога и изделий
        createPrefixes_TableView();
    }


    public HBox getControlButtons() {
        return controlButtons;
    }


    /**
     * ТАБЛИЦА ИЗДЕЛИЙ
     */
    private void createPrefixes_TableView() {

        boolean useContextMenu = CH_CURRENT_USER.getUserGroup().isEditDrafts();
        tableView = new Prefix_TableView("ПРЕФИКС", useContextMenu);
        tableView.updateView();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vbHeader.getChildren().add(tableView);

    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ ДЛЯ КАТАЛОГА ИЗДЕЛИЙ
     */
    private void createPrefixes_ToolBar(){

        // КНОПОК УПРАВЛЕНИЯ НЕТ

    }

    public AnchorPane getApMainPatch() {
        return apMainPatch;
    }

    public Prefix_TableView getPrefixTableView() {
        return tableView;
    }

    @Override
    public void tuneSearching() {
        Platform.runLater(()->tableView.requestFocus());
        CH_SEARCH_FIELD.changeSearchedTableView(tableView, "ПРЕФИКС");
    }

    @Override
    public void updateTab() {
        tableView.updateTableView();
    }
}
