package ru.wert.datapik.utils.common.tableView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.CatalogService;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.treeView.Item_TreeView;
import ru.wert.datapik.utils.statics.Comparators;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class TaskUpdateItemsInCatalogTableView< P extends Item, T extends CatalogGroup> extends Task<Void> {
    private final List<Long> ids = new ArrayList<>();
    private final CatalogTableView<P, T> itemView;
    private final ProgressIndicator progressIndicator;
    private final boolean setFocus;
    private final CatalogService<P> itemService;


    public TaskUpdateItemsInCatalogTableView(TreeItem<T> treeItem,
                                             Item_TreeView<P,T> catalogTree,
                                             CatalogTableView<P, T> itemView,
                                             boolean setFocus,
                                             CatalogService<P> itemService) {
        this.itemView = itemView;
        this.setFocus = setFocus;
        this.itemService = itemService;



        progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(90, 90);
        itemView.setPlaceholder(new StackPane(progressIndicator));

        //Для выделенного в дереве treeItem находим его children
        List<TreeItem<T>> treeItems = catalogTree.findAllChildren(treeItem);
        treeItems.add(treeItem);

        //Для всех найденных children формируем список id
        for (TreeItem<T> ti : treeItems) {
            ids.add(ti.getValue().getId());
        }
    }

    @Override
    protected Void call() throws Exception {

        List<P> items = new ArrayList<>();
        //Формируем список искомых элементов для каждого id
        for (Long id : ids) {
            items.addAll(itemService.findAllByGroupId(id));
        }

        //Если список пуст, ничего не показываем
        if (items.isEmpty()) {
            showEmptyTable();
        } else {

            itemView.setCurrentItemSearchedList(items);
            //Дальнейший фильтр
            if (items.isEmpty()) { //Если для искомого текста не найдено элементов
                itemView.setCurrentItemSearchedList(items);
                showEmptyTable();
            } else {
                items.sort(Comparators.usefulStringComparator());
                itemView.setCurrentItemSearchedList(items);
                Platform.runLater(() -> {
                    itemView.getItems().clear();
                    itemView.refresh();
                    itemView.setItems(FXCollections.observableArrayList(items));
                    if (setFocus) itemView.getSelectionModel().select(0);
                });

            }
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
        super.failed();
        progressIndicator.setVisible(false);
        log.error("The task TaskUpdateItemsInRoutineTableView failed with the following exception:\n" + System.err);

//        System.err.println("The task TaskUpdateItemsInCatalogTableView failed with the following exception:");
//        getException().printStackTrace(System.err);


    }


}
