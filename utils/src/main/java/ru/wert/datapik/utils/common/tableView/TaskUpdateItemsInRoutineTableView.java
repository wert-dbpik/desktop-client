package ru.wert.datapik.utils.common.tableView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.interfaces.Sorting;

import java.util.List;

public class TaskUpdateItemsInRoutineTableView<P extends Item> extends Task<Void> {

    private final RoutineTableView<P> itemView;
    private final ProgressIndicator progressIndicator;


    public TaskUpdateItemsInRoutineTableView(RoutineTableView<P> itemView) {
        this.itemView = itemView;

        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(90, 90);
        itemView.setPlaceholder(new StackPane(progressIndicator));

    }

    @Override
    protected Void call() throws Exception {

        List<P> items = itemView.prepareList();

        if (items.isEmpty()) {
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

        System.err.println("The task TaskUpdateItemsInRoutineTableView failed with the following exception:");
        getException().printStackTrace(System.err);
    }


}
