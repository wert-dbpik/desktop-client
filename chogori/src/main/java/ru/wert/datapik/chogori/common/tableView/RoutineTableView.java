package ru.wert.datapik.chogori.common.tableView;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.chogori.common.commands.ItemCommands;
import ru.wert.datapik.chogori.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.chogori.search.Searchable;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public abstract class RoutineTableView<P extends Item> extends ItemTableView<P> implements Searchable<P> {

    protected FormView_ContextMenu<P> contextMenu;

    public abstract void setTableColumns();
    public abstract ItemCommands<P> getCommands();
    public abstract String getAccWindowRes();
    public abstract void setModifyingItem(Object item);

    @Override//Searchable
    public abstract void setCurrentItemSearchedList(List<P> currentItemList);
    @Override//Searchable
    public abstract List<P> getCurrentItemSearchedList();


    public RoutineTableView(String promptText) {
        super(promptText);
    }

    /**
     * Обновляет данные формы
     */
    public synchronized void updateTableView() {
        updateRoutineTableView(null, false);
    }

    /**
     * Обновляет данные формы без учета поисковой строки
     * P selectedItem - необходимый к выделению элемент после обновления
     * boolean savePreparedList - следует ли сохранить исходный перечень элементов в таблице
     */
    public synchronized void updateRoutineTableView(P selectedItem, boolean savePreparedList) {
        Service<Void> updateService = new ServiceUpdateItemsInRoutineTableView<>(this, selectedItem, savePreparedList);
        updateService.restart();
    }

    /**
     * Обновляет данные формы
     */
    @Override //Searchable
    public synchronized void updateSearchedView(){
        List<P> list = getCurrentItemSearchedList();
        List<P> foundList = new ArrayList<>();
        String searchedText = CH_SEARCH_FIELD.getText();
        for(P item : list){
            if(item.toUsefulString().toLowerCase().contains(searchedText.toLowerCase()))
                foundList.add(item);
        }
        updateForm(foundList);
    }

    /**
     * Обновляет данные формы
     */
    @Override //Searchable
    public synchronized void easyUpdate(ItemService<P> service) {
        getItems().clear();
        refresh();
        List<P> list = service.findAll();
        setItems(FXCollections.observableArrayList(list));
        setCurrentItemSearchedList(list);

    }

    @Override //IFormView
    public List<P> getAllSelectedItems(){
        return getSelectionModel().getSelectedItems();
    }

}
