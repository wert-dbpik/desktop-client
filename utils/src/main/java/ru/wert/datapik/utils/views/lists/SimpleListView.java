package ru.wert.datapik.utils.views.lists;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.components.LogicButton;
import ru.wert.datapik.utils.search.Searchable;

import java.util.List;

import static ru.wert.datapik.utils.statics.UtilStaticNodes.CH_SEARCH_FIELD;

public class SimpleListView<P extends Item> extends ListView<P> implements Searchable {

    protected ItemService<P> itemService;
    protected String searchedText;
    protected LogicButton dontUpdateView;

    public SimpleListView(ItemService<P> itemService, LogicButton dontUpdateView) {
        this.itemService = itemService;
        this.dontUpdateView = dontUpdateView;

        //Собственная CellFactory нужна, для появления значков и коротких имен
        setCellFactory((ListView<P> tv) -> new SimpleListCell<>());

    }

    @Override
    public void setSearchedText(String searchedText) {

    }

    @Override
    public String getSearchedText() {
        return null;
    }


    @Override //implements Searchable
    public void updateView() {
        if(dontUpdateView.getLogic().get()) return;

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(90, 90);
        setPlaceholder(new StackPane(progressIndicator));

        Task<Void> loadView = new Task<Void>(){

            @Override
            protected Void call() {

                searchedText = CH_SEARCH_FIELD.getText();
                List<P> items;
                if(searchedText.equals(""))
                    items = itemService.findAll();
                else
                    items = itemService.findAllByText(searchedText);

                if(items == null) {
                    Platform.runLater(()->{
                        getItems().clear();
                    });
                    progressIndicator.setVisible(false);
                } else {
                    Platform.runLater(()->{
                        getItems().clear();
                        getItems().addAll(items);
                    });
                }

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                progressIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                super.succeeded();
                progressIndicator.setVisible(false);
            }

        };

        Thread t = new Thread(loadView);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void updateSearchedView() {

    }

    @Override
    public List getCurrentItemSearchedList() {
        return null;
    }

    @Override
    public void setCurrentItemSearchedList(List currentItemList) {

    }

}
