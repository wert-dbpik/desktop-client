package ru.wert.tubus.chogori.common.tableView;

import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import ru.wert.tubus.client.interfaces.Item;

import java.util.List;

public class TableViewWithResizableColumns {

    private TableView table;
    private List<TableColumn<Item, ?>> columns;
    private VBox container;
    private boolean isFirstRun = true;


    public TableViewWithResizableColumns(TableView table, VBox container) {
        this.table = table;
        this.container = container;
        this.columns = table.getColumns();


        container.widthProperty().addListener((observable, oldValue, newValue) -> {

        });

        for (TableColumn col : columns) {
            if (col.isResizable())
                col.widthProperty().addListener((observable, oldValue, newValue) -> {

                    if ((newValue.doubleValue() < col.getMinWidth() && newValue.doubleValue() > col.getMaxWidth())) return;
                    double deltaW = newValue.doubleValue() - oldValue.doubleValue();

                    Platform.runLater(() -> {
                        container.resize(table.getWidth() + deltaW, table.getHeight());
                        container.setPrefWidth(table.getWidth() + deltaW);
//                        table.resize(table.getWidth() + deltaW, table.getHeight());
//                        table.setPrefWidth(table.getWidth() + deltaW);
                    });

                });

        }

    }

}
