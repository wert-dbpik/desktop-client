package ru.wert.datapik.chogori.entities.prefixes;

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

public class Prefix_Controller implements SearchableTab, UpdatableTabController {

    @FXML
    private AnchorPane apPrefixesPatch;

    @FXML
    private VBox vbPrefixes;

    @FXML
    private HBox prefixesButtons;

    private Prefix_TableView tableView;


    @FXML
    void initialize() {
        //Создаем панели инструментов
        createPrefixes_ToolBar();

        //Создаем связанные между собой панели каталога и изделий
        createPrefixes_TableView();
    }


    public HBox getPrefixesButtons() {
        return prefixesButtons;
    }


    /**
     * ТАБЛИЦА ИЗДЕЛИЙ
     */
    private void createPrefixes_TableView() {

        boolean useContextMenu = CH_CURRENT_USER.getUserGroup().isEditDrafts();
        tableView = new Prefix_TableView("ПРЕФИКС", useContextMenu);
//        prefixesTableView.updateView();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        vbPrefixes.getChildren().add(tableView);

    }

    /**
     * ИНСТРУМЕНТАЛЬНАЯ ПАНЕЛЬ ДЛЯ КАТАЛОГА ИЗДЕЛИЙ
     */
    private void createPrefixes_ToolBar(){

        // КНОПОК УПРАВЛЕНИЯ НЕТ

//        usersButtons.getChildren().add();
    }

    public AnchorPane getApPrefixesPatch() {
        return apPrefixesPatch;
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
