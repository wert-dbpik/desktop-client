package ru.wert.datapik.utils.common.tableView;
import javafx.collections.FXCollections;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.contextMenuACC.FormView_ContextMenu;
import ru.wert.datapik.utils.search.Searchable;

import java.util.ArrayList;
import java.util.List;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

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
    @Override//Searchable
    public abstract void setSearchedText(String searchedText);
    @Override//Searchable
    public abstract String getSearchedText();

    public RoutineTableView(String promptText) {
        super(promptText);
    }

    /**
     * Обновляет данные формы
     */
    public void updateTableView() {
        updateRoutineTableView();
    }

    /**
     * Обновляет данные формы
     */
    public void updateRoutineTableView() {
        TaskUpdateItemsInRoutineTableView<P> taskUpdate = new TaskUpdateItemsInRoutineTableView<>(this);
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
}
