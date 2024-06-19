package ru.wert.tubus.chogori.search;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import ru.wert.tubus.client.entity.models.Passport;

import static ru.wert.tubus.chogori.statics.AppStatic.KOMPLEKT;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class SearchHistoryListView extends ListView<String> {

    private final ObservableList<String> history;
    private static SearchHistoryListView instance;

    public static SearchHistoryListView getInstance(){
        if(instance == null)
            instance = new SearchHistoryListView();
        return
                instance;
    }

    private SearchHistoryListView() {
        history = FXCollections.observableArrayList();
        setItems(history);
        createCellFactory();

        getSelectionModel().selectedItemProperty().addListener(changeListener());
        getItems().add("ПИК.745351.109, Панель");
        getItems().add("ПИК.305413.010, Тумба СМЛ");
        getItems().add("ПИК.301261.320, Дно");
        getItems().add("ПИК.745423.162, Швеллер рамы");
    }

    private ChangeListener<String> changeListener(){
        return (observable, oldValue, newValue) ->{
            moveToTop(newValue);
        };
    }

    /**
     * Перемещает newValue на самый верх истории поиска
     */
    public void moveToTop(String value){
        getItems().removeIf(next -> next.equals(value));
        getItems().add(0, value);
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
