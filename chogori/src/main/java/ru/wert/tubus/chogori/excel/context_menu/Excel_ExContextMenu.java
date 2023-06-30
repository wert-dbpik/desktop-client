package ru.wert.tubus.chogori.excel.context_menu;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import ru.wert.tubus.chogori.excel.model.EditorRow;
import ru.wert.tubus.chogori.excel.table.Excel_ExecutionColumn;
import ru.wert.tubus.chogori.excel.table.Excel_TableView;

import java.util.ArrayList;

import static ru.wert.tubus.chogori.excel.table.TableMaster.setIdToColumns;


public class Excel_ExContextMenu extends ContextMenu {

    private final Excel_TableView excelTable;

    /**
     * Конструктор
     * @param excelTable
     * @param exCol
     * @param exNum
     */
    public Excel_ExContextMenu(Excel_TableView excelTable, TableColumn<EditorRow, ?> exCol, int exNum) {
        this.excelTable = excelTable;

        MenuItem addExecution = new MenuItem("Добавить исполнение");
        addExecution.setOnAction(e-> addExecution(exCol, exNum));

        MenuItem deleteExecution = new MenuItem("Удалить исполнение");
        deleteExecution.setOnAction(e-> deleteExecution(exCol, exNum));

        getItems().addAll(addExecution, deleteExecution);
    }

    /**
     * Метод удаляет исполнение из таблицы
     * @param col - столбец
     * @param ex - исполнение
     */
    private void deleteExecution(TableColumn<EditorRow, ?> col, int ex) {
        Platform.runLater(() -> {
            //если осталось только одно исполнение, то его удалить невозмоожно
            if (excelTable.getPoi().getExecutions().size() == 1) return;
            else if (ex == excelTable.getPoi().getExecutions().size() - 1) { //Если столбец крайний справа
                excelTable.getSelectionModel().clearSelection();
                for (EditorRow row : excelTable.getItems()) {
                    row.getExecutions().remove(ex);
                }
                excelTable.getColumns().remove(col);
            } else {
                for (EditorRow row : excelTable.getItems()) {
                    row.getExecutions().remove(ex);
                }
                excelTable.getColumns().remove(col);

            }
            reArrangeExecutions(ex);
            excelTable.changeTableWidth();
        });
    }


    /**
     * Метод добавляет новое исполнение к существующим
     * @param col - Столбец, где произошло событие
     * @param ex - порядковый номер исполнения от 0..
     */
    private void addExecution(TableColumn<EditorRow, ?> col, int ex) {
        Platform.runLater(() -> {
            //Определяем порядковый номер столбца. где выполнен клик мышью
            int colPos = excelTable.getPoi().getExecutions().get(ex);

            //Меняем модель таблицы добавив в него новое исполнение
            if (ex == excelTable.getPoi().getExecutions().size() - 1) //Если столбец крайний справа
                for (EditorRow row : excelTable.getItems()) {
                    String exId = "ex" + ex;
                    row.getExecutions().add(new EditorRow.Execution(exId, "", ""));
                }
            else { //Если столбец по середине
                for (EditorRow row : excelTable.getItems()) {
                    String exId = "ex" + ex;
                    row.getExecutions().add(ex + 1, new EditorRow.Execution(exId, "", ""));
                }
            }

            colPos+=1;

            //Создадим столбец с новым исполнением
            TableColumn<EditorRow, ?> newExCol = new Excel_ExecutionColumn("", "", ex + 1, excelTable);
            excelTable.getColumns().add(colPos, newExCol);
            reArrangeExecutions(ex);
            excelTable.changeTableWidth();
            selectExecutionColumn(colPos);
        });
    }

    /**
     * При удалении или добавлении столбцов внутренние переменные столбцов смещаются
     * Этот метод приводит внутренние переменные столбцов в соответствие
     * @param ex
     */
    private void reArrangeExecutions(int ex) {
        excelTable.reArrangeExecutionCols();
        excelTable.reArrangeExecutionRows();
        setIdToColumns(excelTable);

        int lastEx = excelTable.getPoi().getExecutions().size()-1;
        while(ex <= lastEx){

            int colIndex = excelTable.getPoi().getExecutions().get(ex);
            int finalEx = ex;
            TableColumn<EditorRow, ?> tc = excelTable.getColumns().get(colIndex);
            ((TableColumn<EditorRow, String>)tc.getColumns().get(0)).setCellValueFactory(val->{
                ArrayList<EditorRow.Execution> exx = val.getValue().getExecutions();
                return new ReadOnlyStringWrapper(exx.get(finalEx).getTotalAmount());
            });
            ((TableColumn<EditorRow, String>)tc.getColumns().get(1)).setCellValueFactory(val->{
                ArrayList<EditorRow.Execution> exx = val.getValue().getExecutions();
                return new ReadOnlyStringWrapper(exx.get(finalEx).getAmountPerAssemble());
            });
            ex++;
        }
    }

    /**
     * Метод выделяет указанное исполнение
     * @param colPos - индекс столбца в таблице
     */
    private void selectExecutionColumn(int colPos) {
        TableColumn<EditorRow, ?> exCol = excelTable.getColumns().get(colPos);
        TableColumn<EditorRow, ?> tcAmount = exCol.getColumns().get(0);
        TableColumn<EditorRow, ?> tcAmountPerAssemble = exCol.getColumns().get(1);
        //Перед новым выделением снимаем прочее выделение
        excelTable.getSelectionModel().clearSelection();
        for(int row = 0; row < excelTable.getItems().size(); row++){
            excelTable.getSelectionModel().select(row, tcAmount);
            excelTable.getSelectionModel().select(row, tcAmountPerAssemble);
        }
    }

    private void renumerate(){
        Platform.runLater(()->{
            for(EditorRow row : excelTable.getItems()){
                int num = 0;
                for(EditorRow.Execution ex :row.getExecutions()){
                    ex.setId("ex" + (num++));
                }
            }
        });
    }

}

