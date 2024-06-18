package ru.wert.tubus.chogori.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import ru.wert.tubus.client.entity.models.Passport;

import java.util.Iterator;

import static ru.wert.tubus.chogori.statics.AppStatic.KOMPLEKT;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class SearchHistoryList extends ListView<String> {

    private final ObservableList<String> history;

    public SearchHistoryList() {
        history = FXCollections.observableArrayList();
        setItems(history);
        createCellFactory();
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
                    setText(item);
                    setStyle("-fx-font-weight: normal; -fx-text-fill: #000000");
                    if(item.startsWith(KOMPLEKT))
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #4c1d0f");
                }
            }

        });
    }

    /**
     * Добавляет пасспорт в историю поиска
     */
    public void updateSearchHistoryWithPassport(Passport passport) {
        if(passport == null) return;
        updateSearchHistory(passport.toUsefulString());
    }

    public void updateSearchHistory(String stringPassport) {
        if(stringPassport == null) return;

        //Отключаем слушатели при изменении истории
        String selectedItem = getSelectionModel().getSelectedItem();

        ObservableList<String> searchHistory = getItems();
        //Если чертеж уже в поле поиска, то ничего делать не надо
        if (searchHistory.contains(stringPassport) ||
                stringPassport.equals(CH_SEARCH_FIELD.getText())) {
            getSelectionModel().select(selectedItem);
            return;
        } else {
            searchHistory.add(0, stringPassport);
        }

        //Включаем слушатели обратно
        getSelectionModel().select(selectedItem);


    }

}
