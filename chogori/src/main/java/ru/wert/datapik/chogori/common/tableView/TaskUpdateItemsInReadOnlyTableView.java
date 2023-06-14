package ru.wert.datapik.chogori.common.tableView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.chogori.common.interfaces.Sorting;

import java.util.List;

@Slf4j
public class TaskUpdateItemsInReadOnlyTableView<P extends Item> extends Task<Void> {

    private final ReadOnlyTableView<P> itemView;
    private final ProgressIndicator progressIndicator;


    public TaskUpdateItemsInReadOnlyTableView(ReadOnlyTableView<P> itemView) {
        this.itemView = itemView;

        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(90, 90);
        Platform.runLater(()->itemView.setPlaceholder(new StackPane(progressIndicator)));


    }

    @Override
    protected Void call() throws Exception {

        List<P> items = itemView.prepareList();

        if (items == null || items.isEmpty()) {
            showEmptyTable();
        } else {

            if(itemView instanceof Sorting) {
                ((Sorting) itemView).sortItemList(items);
            }

            itemView.setCurrentItemSearchedList(items);

            Platform.runLater(() -> {
                itemView.getItems().clear();
                itemView.refresh();
                itemView.setItems(FXCollections.observableArrayList(items));
            });

        }

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
        log.error("The task ServiceUpdateItemsInRoutineTableView failed with the following exception:\n" + System.err);

//        System.err.println("The task ServiceUpdateItemsInRoutineTableView failed with the following exception:");
        getException().printStackTrace(System.err);
    }


}
