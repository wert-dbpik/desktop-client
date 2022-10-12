package ru.wert.datapik.chogori.common.tableView;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import ru.wert.datapik.client.interfaces.Item;

import java.util.List;

import static ru.wert.datapik.chogori.excel.table.TableMaster.countPrefTableWidth;
import static ru.wert.datapik.chogori.excel.table.TableMaster.getAllColumns;

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
