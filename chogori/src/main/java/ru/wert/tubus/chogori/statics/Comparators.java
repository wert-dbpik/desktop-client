package ru.wert.tubus.chogori.statics;

import com.sun.javafx.scene.control.TableColumnSortTypeWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import ru.wert.tubus.client.entity.models.AppLog;
import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.interfaces.Item;

import java.util.Comparator;

public class Comparators {
    /**
     * Компаратор сравнивает две надписи. Берет из них текст и сравнивает как две строки
     */
    public static Comparator<Label> createLabelComparator(TableColumn<?, Label> col) {
        return (o1, o2) -> {
            String n1 = o1.getText();
            String n2 = o2.getText();

            return n1.compareTo(n2);
        };
    }

    /**
     * Компаратор для строковых значений Id, ParentId и т.д
     */
    public static Comparator<String> createIntegerComparatorForStringColumn(TableColumn<?, String> col) {

        return (o1, o2) -> {

            //Для того, чтобы пустые строки были всегда внизу
            if (TableColumnSortTypeWrapper.isAscending(col)) {
                if (o1.isEmpty()) o1 = String.valueOf(Integer.MAX_VALUE);
                if (o2.isEmpty()) o2 = String.valueOf(Integer.MAX_VALUE);
            } else {
                if (o1.isEmpty()) o1 = String.valueOf(Integer.MIN_VALUE);
                if (o2.isEmpty()) o2 = String.valueOf(Integer.MIN_VALUE);
            }

            Integer i1 = Integer.parseInt(o1);
            Integer i2 = Integer.parseInt(o2);

            return i1.compareTo(i2);
        };
    }

    /**
     * Компаратор сравнивает чертеж по НОМЕРУ -> ТИПУ -> СТРАНИЦЕ
     */
    public static Comparator<Draft> draftsComparator() {
        return (o1, o2) -> {
            //Сравниваем номер чертежа, причем 745 должен быть выше, чем 469
            int result = o1.getPassport().getNumber()
                    .compareTo(o2.getPassport().getNumber());
            if (result == 0) {
                //Сравниваем тип чертежа
                result = o1.getDraftType() - o2.getDraftType();
                if (result == 0) {
                    //Сравниваем номер страницы
                    result = o1.getPageNumber() - o2.getPageNumber();
                }
            }
            return result;
        };
    }

    /**
     * Компаратор сравнивает чертеж по НОМЕРУ -> ТИПУ -> СТРАНИЦЕ
     */
    public static Comparator<Draft> draftsForPreviewerComparator() {
        return (o1, o2) -> {
                //Сравниваем тип чертежа
                int result = o1.getDraftType() - o2.getDraftType();
                if (result == 0) {
                    //Сравниваем номер страницы
                    result = o1.getPageNumber() - o2.getPageNumber();
                    if(result == 0)
                        result = o1.getStatus() - o2.getStatus();
                    if(result == 0)
                        result = o1.getStatusTime().compareTo(o2.getStatusTime());
                }

            return result;
        };
    }

    /**
     * Компаратор сравнивает чертеж по НОМЕРУ -> ТИПУ -> СТРАНИЦЕ
     * Реверсивность относится только к НОМЕРУ
     */
    public static Comparator<Draft> draftsReversComparator() {
        return (o1, o2) -> {
            //Сравниваем номер чертежа, причем 745 должен быть выше, чем 469
            int result = o1.getPassport().getNumber()
                    .compareTo(o2.getPassport().getNumber());
            if (result == 0) {
                //Сравниваем тип чертежа
                result = o1.getDraftType() - o2.getDraftType();
                if (result == 0) {
                    //Сравниваем номер страницы
                    result = o1.getPageNumber() - o2.getPageNumber();
                }
            }
            return result;
        };
    }

    /**
     * Компаратор сравнивает usefulString объекта
     */
    public static Comparator<Item> usefulStringComparator() {
        return (o1, o2) -> {

            String str1 = o1.toUsefulString();
            String str2 = o2.toUsefulString();
            return str1.compareTo(str2);
        };
    }

    /**
     * Компаратор сравнивает логи
     */
    public static Comparator<AppLog> logsComparator() {
        return (o1, o2) -> {
            String str1 = o1.getTime();
            String str2 = o2.getTime();
            return str1.compareTo(str2);
        };
    }

}
