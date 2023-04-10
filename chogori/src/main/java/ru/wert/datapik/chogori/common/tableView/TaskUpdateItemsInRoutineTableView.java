package ru.wert.datapik.chogori.common.tableView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.chogori.common.interfaces.Sorting;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class TaskUpdateItemsInRoutineTableView<P extends Item> extends Task<Void> {

    private final RoutineTableView<P> itemView;
    private final P selectedItem;
    private final ProgressIndicator progressIndicator;
    private final boolean savePreparedList;


    public TaskUpdateItemsInRoutineTableView(RoutineTableView<P> itemView, P selectedItem, boolean savePreparedList) {
        this.itemView = itemView;
        this.selectedItem = selectedItem;
        this.savePreparedList = savePreparedList;

        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(90, 90);
        Platform.runLater(()->itemView.setPlaceholder(new StackPane(progressIndicator)));


    }

    @Override
    protected Void call() throws Exception {
        List<P> items;
        if(!savePreparedList) {
            items = itemView.prepareList();
        } else {
            items = new ArrayList<>(itemView.getItems());
        }
        if (items.isEmpty()) {
            showEmptyTable();
        } else {
            if (itemView instanceof Sorting) {
                ((Sorting) itemView).sortItemList(items);
            }
        }
        itemView.setCurrentItemSearchedList(items);

        List<P> finalItems = items;
        Platform.runLater(() -> {
                itemView.getItems().clear();
                itemView.refresh();
                itemView.setItems(FXCollections.observableArrayList(finalItems));
                if(selectedItem != null){
                    itemView.getSelectionModel().select(selectedItem);
                    itemView.scrollTo(selectedItem);
                }
            });

        return null;
    }

    private void showEmptyTable() {
        Platform.runLater(() -> {
            itemView.getItems().clear();
        });
        progressIndicator.setVisible(false);
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
        log.error("The task TaskUpdateItemsInRoutineTableView failed with the following exception:\n" + System.err);

//        System.err.println("The task TaskUpdateItemsInRoutineTableView failed with the following exception:");
        getException().printStackTrace(System.err);
    }


}
