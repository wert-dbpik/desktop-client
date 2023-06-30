package ru.wert.tubus.chogori.excel.table;

import ru.wert.tubus.chogori.excel.model.EditorRow;
import ru.wert.tubus.chogori.excel.table.commands.EditorCommand;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public class Excel_TableUtil {

    private final LinkedList<EditorCommand> undoStack = new LinkedList<>();
    private final LinkedList<EditorCommand> redoStack = new LinkedList<>();

    /**
     * Компаратор для строковых значений Id, ParentId и т.д
     */
    public static Comparator<EditorRow> createRowNumberComparator() {

        return (b1, b2) -> {
            String o1 = b1.getRowNumber();
            String o2 = b2.getRowNumber();

            //Для того, чтобы пустые строки были всегда внизу
            if (o1.isEmpty()) o1 = String.valueOf(Integer.MAX_VALUE);
            if (o2.isEmpty()) o2 = String.valueOf(Integer.MAX_VALUE);


            Integer i1 = Integer.parseInt(Arrays.asList(o1.split("\\.", 2)).get(0));
            Integer i2 = Integer.parseInt(Arrays.asList(o2.split("\\.", 2)).get(0));

            return i1.compareTo(i2);
        };
    }
}
