package ru.wert.datapik.utils.editor.table;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import ru.wert.datapik.utils.editor.context_menu.Excel_ExContextMenu;
import ru.wert.datapik.utils.editor.model.EditorRow;

import java.util.ArrayList;

public class Excel_ExecutionColumn extends TableColumn<EditorRow, String> {

    private String name = "";
    private String description = "";
    Integer currentEx;

    private Excel_TableView editorTable;
    private TableView<EditorRow> tv;

    private final double initialWidth = 35;

    public Excel_ExecutionColumn(String name, String description, int ex, Excel_TableView excelTable) {
        super();
        this.name = name;
        this.description = description;
        this.currentEx = ex;
        this.editorTable = excelTable;
        this.tv = excelTable.getTableView();

        //Присваиваем id сполнению
        setId("ex" + ex);

        if(name.equals(""))
            name = (ex == 0) ? "-" : ((ex < 10) ? ("0" + ex) : String.valueOf(ex));
        setId("ex" + ex);

        VBox vBox = new VBox();
        Label exInfo = new Label(name);
        vBox.getChildren().add(exInfo);
        vBox.setFillWidth(true);
//                exInfo.setOnMouseClicked(mouseClickedEventHandler(ex));
//        vBox.setStyle("-fx-background-color: grey; -fx-border-color: black; -fx-alignment: center");
        vBox.setStyle("-fx-alignment: center");
//        exInfo.setStyle("-fx-background-color: grey; -fx-text-fill: white");
        contextMenuProperty().setValue(new Excel_ExContextMenu(excelTable, this, currentEx));
        vBox.setOnMousePressed( e->{
            if(e.isSecondaryButtonDown()) {
                vBox.onContextMenuRequestedProperty();
                e.consume();
            }
            if(e.isPrimaryButtonDown()) {
                TableColumn<EditorRow, ?> tcAmount = tv.getColumns().get(excelTable.getExecutions().get(currentEx)).getColumns().get(0);
                TableColumn<EditorRow, ?> tcAmountPerAssemble = tv.getColumns().get(excelTable.getExecutions().get(currentEx)).getColumns().get(1);
                if(e.isControlDown()){
                    selectExecutionColumn(tcAmount, tcAmountPerAssemble);
                } else {
                    tv.getSelectionModel().clearSelection();
                    selectExecutionColumn(tcAmount, tcAmountPerAssemble);
                }
            }
        });
        setGraphic(vBox);

        //Кол
        TableColumn<EditorRow, String> totalAmount = new TableColumn<>("Кол");
        totalAmount.setPrefWidth(initialWidth);
        totalAmount.setComparator(excelTable.createIntegerComparator(totalAmount));
        totalAmount.setCellValueFactory(val->{
            ArrayList<EditorRow.Execution> exx = val.getValue().getExecutions();
            exx.get(ex).setId("ex" + ex); //Присваиваем текущему исполнению id
            return new ReadOnlyStringWrapper(exx.get(currentEx).getTotalAmount());
        });
        totalAmount.setCellFactory(param -> new Excel_TableCellFactory(excelTable));
        totalAmount.setOnEditCommit((CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                    .getExecutions().get(currentEx).setTotalAmount(t.getNewValue());
        });

        //(кол)
        TableColumn<EditorRow, String> amountPerAssemble = new TableColumn<>("(кол)");
        amountPerAssemble.setPrefWidth(initialWidth);
        amountPerAssemble.setCellValueFactory(val->{
            ArrayList<EditorRow.Execution> exx = val.getValue().getExecutions();
            return new ReadOnlyStringWrapper(exx.get(currentEx).getAmountPerAssemble());
        });
        amountPerAssemble.setCellFactory(param -> new Excel_TableExCellFactory(excelTable, getId()));
        amountPerAssemble.setOnEditCommit((CellEditEvent<EditorRow, String> t) -> {
            (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                    .getExecutions().get(currentEx).setAmountPerAssemble(t.getNewValue());
        });

        getColumns().addAll(totalAmount, amountPerAssemble);
    }

    /**
     * Метод выделяет две колонки с исполнением
     * @param tcAmount
     * @param tcAmountPerAssemble
     */
    private void selectExecutionColumn(TableColumn<EditorRow, ?> tcAmount, TableColumn<EditorRow, ?> tcAmountPerAssemble) {
        for(int row = 0; row < tv.getItems().size(); row++){
            tv.getSelectionModel().select(row, tcAmount);
            tv.getSelectionModel().select(row, tcAmountPerAssemble);
        }
    }
}
