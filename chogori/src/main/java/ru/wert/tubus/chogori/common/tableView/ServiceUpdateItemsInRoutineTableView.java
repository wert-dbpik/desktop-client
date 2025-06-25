package ru.wert.tubus.chogori.common.tableView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.chogori.common.interfaces.Sorting;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Slf4j
public class ServiceUpdateItemsInRoutineTableView<P extends Item> extends Service<Void> {

    private final RoutineTableView<P> itemView;
    private final List<P> selectedItems;
    private final ProgressIndicator progressIndicator;
    private final boolean savePreparedList;

    /**
     * @param itemView ссылка на таблицу
     * @param selectedItems элемент, которые необходимо выделить после апдета
     * @param savePreparedList используется при поиске
     */
    public ServiceUpdateItemsInRoutineTableView(RoutineTableView<P> itemView, List<P> selectedItems, boolean savePreparedList) {
        this.itemView = itemView;
        this.savePreparedList = savePreparedList;
        this.selectedItems = (selectedItems != null)
                ? new ArrayList<>(selectedItems)  // создаём новый независимый список
                : null;

        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(90, 90);
        Platform.runLater(()->itemView.setPlaceholder(new StackPane(progressIndicator)));

    }

    @Override
    protected Task<Void> createTask() {

        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<P> items;

                if(!savePreparedList) {
                    items = new ArrayList<>(itemView.prepareList());
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
                log.debug("Before Platform.runLater: selectedItems size = {}", selectedItems != null ? selectedItems.size() : 0);
                List<P> finalItems = items;
                Platform.runLater(() -> {
                    itemView.getItems().clear();
                    itemView.refresh();
                    itemView.setItems(FXCollections.observableArrayList(finalItems));
                    log.debug("Inside Platform.runLater: selectedItems size = {}", selectedItems != null ? selectedItems.size() : 0);
                    if (selectedItems != null && !selectedItems.isEmpty()) {
                        for(P item : selectedItems) itemView.getSelectionModel().select(item);
                        itemView.scrollTo(selectedItems.get(selectedItems.size()-1));
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
                log.debug("table updating has been finished");
                progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                super.failed();
                progressIndicator.setVisible(false);
                log.error("The task ServiceUpdateItemsInRoutineTableView failed with the following exception:\n" + System.err);

//        System.err.println("The task ServiceUpdateItemsInRoutineTableView failed with the following exception:");
                getException().printStackTrace(System.err);
            }
        };
    }
}
