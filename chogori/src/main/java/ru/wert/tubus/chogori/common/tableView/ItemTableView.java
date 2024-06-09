package ru.wert.tubus.chogori.common.tableView;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.chogori.common.interfaces.ITableView;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemTableView<P extends Item>  extends TableView<P> implements ITableView<P> {


    protected final String promptItemName;

    public abstract void setTableColumns();
    public abstract void updateTableView();
    public abstract void createContextMenu();
    public abstract List<P> prepareList();

    @Getter@Setter protected String searchedText; //Искомый текст
    @Getter@Setter protected List<String> searchHistory = new ArrayList<>(); //История поиска

    protected BooleanProperty globalOffProperty = new SimpleBooleanProperty(); //Режим всеобщего представления вкл
    public BooleanProperty getGlobalOffProperty(){return globalOffProperty;}
    public boolean isGlobalOff(){return this.globalOffProperty.get();}
    public void setGlobalOff(boolean globalOff){this.globalOffProperty.set(globalOff);}

    public ItemTableView(String promptText) {
        this.promptItemName = promptText;

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);

        setTableColumns();

        if(getColumns().size() <= 1) hideHeader();

        createRowFactory();

        //Запрет на реодеринг столбцов
        restrictColumnReodering();
    }

    /**
     * Запрет на перемещение столбцов
     */
    private void restrictColumnReodering() {
        skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });
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
