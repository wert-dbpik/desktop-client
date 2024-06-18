package ru.wert.tubus.chogori.common.tableView;
import javafx.collections.FXCollections;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.chogori.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.tubus.chogori.search.Searchable;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public abstract class ReadOnlyTableView<P extends Item> extends ItemTableView<P> implements Searchable<P> {

    protected FormView_ContextMenu<P> contextMenu;

    public abstract void setTableColumns();

    @Override//Searchable
    public abstract void setCurrentItemSearchedList(List<P> currentItemList);
    @Override//Searchable
    public abstract List<P> getCurrentItemSearchedList();


    public ReadOnlyTableView(String promptText) {
        super(promptText);
    }

    /**
     * Обновляет данные формы
     */
    public void updateTableView() {
        updateReadOnlyTableView();
    }

    /**
     * Обновляет данные формы
     */
    public void updateReadOnlyTableView() {
        TaskUpdateItemsInReadOnlyTableView<P> taskUpdate = new TaskUpdateItemsInReadOnlyTableView<>(this);
        Thread t = new Thread(taskUpdate);
        t.setDaemon(true);
        t.start();
    }

    /**
     * Обновляет данные формы
     */
    @Override //Searchable
    public void updateSearchedView(){
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
    public void easyUpdate(ItemService<P> service) {
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
