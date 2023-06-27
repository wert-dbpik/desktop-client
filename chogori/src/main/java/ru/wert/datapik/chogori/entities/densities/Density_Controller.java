package ru.wert.datapik.chogori.entities.densities;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.interfaces.SearchableTab;
import ru.wert.datapik.client.interfaces.UpdatableTabController;

import static ru.wert.datapik.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.datapik.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class Density_Controller implements SearchableTab, UpdatableTabController {

    @FXML
    private AnchorPane apMainPatch;

    @FXML
    private VBox vbHeader;

    @FXML
    private HBox controlButtons;

    private Density_TableView tableView;


    @FXML
    void initialize() {
        //Создаем панели инструментов
        createDensities_ToolBar();

        //Создаем связанные между собой панели каталога и изделий
        createDensities_TableView();
    }


    public HBox getControlButtons() {
        return controlButtons;
    }


    /**
     * ТАБЛИЦА ИЗДЕЛИЙ
     */
    private void createDensities_TableView() {

        boolean useContextMenu = CH_CURRENT_USER.getUserGroup().isEditMaterials();
        tableView = new Density_TableView("МАТЕРИАЛ", useContextMenu);
        tableView.updateView();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vbHeader.getChildren().add(tableView);

    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ ДЛЯ КАТАЛОГА ИЗДЕЛИЙ
     */
    private void createDensities_ToolBar(){

        // КНОПОК УПРАВЛЕНИЯ НЕТ

    }

    public AnchorPane getApMainPatch() {
        return apMainPatch;
    }

    public Density_TableView getDensityTableView() {
        return tableView;
    }

    @Override
    public void tuneSearching() {
        Platform.runLater(()->tableView.requestFocus());
        CH_SEARCH_FIELD.changeSearchedTableView(tableView, "МАТЕРИАЛ");
    }

    @Override
    public void updateTab() {
        tableView.updateTableView();
    }
}
