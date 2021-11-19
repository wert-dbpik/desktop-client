package ru.wert.datapik.utils.common.tableView;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.interfaces.ITableView;
import ru.wert.datapik.utils.search.Searchable;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public abstract class ItemTableView<P extends Item>  extends TableView<P> implements ITableView<P>, Searchable<P> {


    private final String promptItemName;

    public abstract void setTableColumns();
    public abstract void updateTableView();
    public abstract void createContextMenu();
    public abstract List<P> prepareList();

    @Override//Searchable
    public abstract void setCurrentItemSearchedList(List<P> currentItemList);
    @Override//Searchable
    public abstract List<P> getCurrentItemList();
    @Override//Searchable
    public abstract void setSearchedText(String searchedText);
    @Override//Searchable
    public abstract String getSearchedText();


    public ItemTableView(String promptText) {
        this.promptItemName = promptText;

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        setTableColumns();

        if(getColumns().size() <= 1) hideHeader();

//        createFocusListener(); //Отвечает за поиск

        createRowFactory();


    }

    /**
     * При нажатии на пустую строку выделение других строк снимается
     */
    private void createRowFactory() {
        setRowFactory(param -> {
            TableRow<P> row = new TableRow<>();
            row.setOnMouseClicked((e)->{
                if (row.isEmpty()) {
                    getSelectionModel().clearSelection();
                    e.consume();
                }
            });
            return row;
        });
    }


    /**
     * Обновляет данные формы
     */
    @Override //Searchable
    public void updateView(){
        updateTableView();

    }

    /**
     * Обновляет данные формы
     */
    @Override //Searchable
    public void updateSearchedView(){
        List<P> list = getCurrentItemList();
        List<P> foundList = new ArrayList<>();
        String searchedText = CH_SEARCH_FIELD.getText();
        for(P item : list){
            if(item.toUsefulString().contains(searchedText))
                foundList.add(item);
        }
        updateForm(foundList);
    }

    /**
     * Обновляет данные формы
     */
    @Override //Searchable
    public void easyUpdate(ItemService<P> service) {
        getItems().clear();
        refresh();
        String searchedText = getSearchedText();
//        if (searchedText == null || searchedText.equals(""))
        List<P> list = service.findAll();
            setItems(FXCollections.observableArrayList(list));
            setCurrentItemSearchedList(list);
//        else
//            setItems(service.findAllByText(searchedText));
    }

    @Override //IFormView
    public List<P> getAllSelectedItems(){
        return getSelectionModel().getSelectedItems();
    }

    /**
     * Устаналивает FocusListener на таблицу
     * При получении фокуса в поле SearchField появляется подсказка searchName,
     * либо последняя набранная в SearchField строка searchedText
     */
    private void createFocusListener() {
        focusedProperty().addListener((observable) -> {

            CH_SEARCH_FIELD.setSearchableTableController(this);

            if (getSearchedText() == null || getSearchedText().equals("")) {
                CH_SEARCH_FIELD.setText("");
                CH_SEARCH_FIELD.setPromptText(promptItemName);
            } else {
                CH_SEARCH_FIELD.setText(getSearchedText());
            }

        });

    }

    /**
     * Метод скрывает заголовок таблицы если столбец всего один
     */
    private void hideHeader(){
        skinProperty().addListener((a, b, newSkin) -> {
            TableHeaderRow headerRow = ((TableViewSkinBase)newSkin).getTableHeaderRow();
            headerRow.setPrefHeight(0);
            headerRow.setVisible(false);
        });
    }

    /**
     * Обновление таблицы на основании переданного списка
     */
    public void updateForm(List<P> listToShow) {

        ObservableList<P> list = FXCollections.observableArrayList(listToShow);

        getItems().clear();
        refresh(); //Убирает артефакты
        setItems(list);
    }

}
