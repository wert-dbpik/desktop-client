package ru.wert.tubus.chogori.search;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import static ru.wert.tubus.chogori.statics.AppStatic.KOMPLEKT;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class SearchHistoryListView extends ListView<String> {

    private static SearchHistoryListView instance;
    private boolean switchOnListener = true;

    public static SearchHistoryListView getInstance(){
        if(instance == null)
            instance = new SearchHistoryListView();
        return
                instance;
    }

    private SearchHistoryListView() {
        setItems(FXCollections.observableArrayList());
        fillHistory();
        createCellFactory();

        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(switchOnListener) {
                Platform.runLater(() -> CH_SEARCH_FIELD.setText(newValue));
                moveToTop(newValue);
            }
        });
    }

    private void fillHistory() {
        getItems().add("ПИК.745351.109, Панель");
        getItems().add("ПИК.305413.010, Тумба СМЛ");
        getItems().add("ПИК.301261.320, Дно");
        getItems().add("ПИК.745423.162, Швеллер рамы");
    }

    private ChangeListener<String> changeListener(){
        return (observable, oldValue, newValue) ->{
            if(switchOnListener) {
                Platform.runLater(() -> CH_SEARCH_FIELD.setText(newValue));
                moveToTop(newValue);
                refresh();
            }
        };
    }

    /**
     * Перемещает newValue на самый верх истории поиска
     */
    public void moveToTop(String value) {
        Platform.runLater(() -> {
            switchOnListener = false;
            int pos = getItems().indexOf(value);
            getItems().add(0, value);
            getItems().remove(pos + 1);
            getSelectionModel().clearSelection();
            getFocusModel().focus(0);
            switchOnListener = true;
        });
    }

    /**
     * Выделяет комплекты, чтобы отличать их от чертежей
     */
    private void createCellFactory() {
        setCellFactory(i -> new ListCell<String>() {
            @Override
            protected void updateItem (String item, boolean empty){
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    int pos = getIndex();
                    setText(item);
                    setStyle("-fx-font-weight: normal; -fx-text-fill: #000000");
                    if(item.startsWith(KOMPLEKT))
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #4c1d0f");

                    setOnMouseEntered(e->{
                        getFocusModel().focus(pos);
                    });

                }
            }

        });
    }


    public void updateSearchHistory(String string) {
        if(string == null) return;

        ObservableList<String> searchHistory = getItems();
        //Если чертеж уже в поле поиска, то ничего делать не надо
        if (string.equals(CH_SEARCH_FIELD.getText()))
            return;
        else if(searchHistory.contains(string))
            moveToTop(string);
        else {
            searchHistory.add(0, string);
        }

        getSelectionModel().select(string);

    }

}
