package ru.wert.datapik.utils.views.tables;

import javafx.scene.control.TableView;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.search.Searchable;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

@Slf4j
@Getter
@Setter
public abstract class SimpleTableView<T extends Item> extends TableView<T>  implements Searchable {



    public SimpleTableView() {

        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

    }

    /**
     * Обновляет таблицу
     */
    @Override
    public abstract  void updateView();


    /**
     * Устаналивает FocusListener на таблицу
     * При получении фокуса в поле SearchField появляется подсказка searchName,
     * либо последняя набранная в SearchField строка searchedText
     * @param searchedText String - Сохраненое значение поиска
     * @param searchName String - Наименование поиска (чертеж, папка, и т.д.)
     */
    protected void createFocusListener(String searchedText, String searchName){
        focusedProperty().addListener(observable -> {
            if (searchedText == null || searchedText.equals("")) {
                CH_SEARCH_FIELD.setText("");
                CH_SEARCH_FIELD.setPromptText(searchName);
            } else
                CH_SEARCH_FIELD.setText(searchedText);
            CH_SEARCH_FIELD.setSearchableTableController(this);
        });

    }
}
