package ru.wert.datapik.utils.editor.context_menu;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ru.wert.datapik.utils.editor.model.EditorRow;
import ru.wert.datapik.utils.editor.table.EditorTable;
import ru.wert.datapik.utils.editor.table.ExecutionColumn;

import java.util.ArrayList;

import static ru.wert.datapik.utils.editor.table.TableMaster.setIdToColumns;


public class EditorExContextMenu extends ContextMenu {

    private EditorTable editorTable;
    private TableView<EditorRow> tv;

    /**
     * Конструктор
     * @param editorTable
     * @param exCol
     * @param exNum
     */
    public EditorExContextMenu(EditorTable editorTable, TableColumn<EditorRow, ?> exCol, int exNum) {
        this.editorTable = editorTable;
        this.tv = editorTable.getTableView();

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
            if (editorTable.getExecutions().size() == 1) return;
            else if (ex == editorTable.getExecutions().size() - 1) { //Если столбец крайний справа
                tv.getSelectionModel().clearSelection();
                for (EditorRow row : tv.getItems()) {
                    row.getExecutions().remove(ex);
                }
                tv.getColumns().remove(col);
            } else {
                for (EditorRow row : tv.getItems()) {
                    row.getExecutions().remove(ex);
                }
                tv.getColumns().remove(col);

            }
            reArrangeExecutions(ex);
            editorTable.changeTableWidth();
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
            int colPos = editorTable.getExecutions().get(ex);

            //Меняем модель таблицы добавив в него новое исполнение
            if (ex == editorTable.getExecutions().size() - 1) //Если столбец крайний справа
                for (EditorRow row : tv.getItems()) {
                    String exId = "ex" + ex;
                    row.getExecutions().add(new EditorRow.Execution(exId, "", ""));
                }
            else { //Если столбец по середине
                for (EditorRow row : tv.getItems()) {
                    String exId = "ex" + ex;
                    row.getExecutions().add(ex + 1, new EditorRow.Execution(exId, "", ""));
                }
            }

            colPos+=1;

            //Создадим столбец с новым исполнением
            TableColumn<EditorRow, ?> newExCol = new ExecutionColumn("", "", ex + 1, editorTable);
            tv.getColumns().add(colPos, newExCol);
            reArrangeExecutions(ex);
            editorTable.changeTableWidth();
            selectExecutionColumn(colPos);
        });
    }

    /**
     * При удалении или добавлении столбцов внутренние переменные столбцов смещаются
     * Этот метод приводит внутренние переменные столбцов в соответствие
     * @param ex
     */
    private void reArrangeExecutions(int ex) {
        editorTable.reArrangeExecutionCols();
        editorTable.reArrangeExecutionRows();
        setIdToColumns(tv);

        int lastEx = editorTable.getExecutions().size()-1;
        while(ex <= lastEx){

            int colIndex = editorTable.getExecutions().get(ex);
            int finalEx = ex;
            TableColumn<EditorRow, ?> tc = tv.getColumns().get(colIndex);
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
        TableColumn<EditorRow, ?> exCol = tv.getColumns().get(colPos);
        TableColumn<EditorRow, ?> tcAmount = exCol.getColumns().get(0);
        TableColumn<EditorRow, ?> tcAmountPerAssemble = exCol.getColumns().get(1);
        //Перед новым выделением снимаем прочее выделение
        tv.getSelectionModel().clearSelection();
        for(int row = 0; row < tv.getItems().size(); row++){
            tv.getSelectionModel().select(row, tcAmount);
            tv.getSelectionModel().select(row, tcAmountPerAssemble);
        }
    }

    private void renumerate(){
        Platform.runLater(()->{
            for(EditorRow row : tv.getItems()){
                int num = 0;
                for(EditorRow.Execution ex :row.getExecutions()){
                    ex.setId("ex" + (num++));
                }
            }
        });
    }

}

