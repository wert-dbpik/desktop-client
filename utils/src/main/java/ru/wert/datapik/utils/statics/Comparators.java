package ru.wert.datapik.utils.statics;

import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.interfaces.Item;

import java.util.Comparator;

public class Comparators {

    /**
     * Компаратор сравнивает чертеж по НОМЕРУ -> ТИПУ -> СТРАНИЦЕ
     */
    public static Comparator<Draft> draftsComparator() {
        return (o1, o2) -> {
            //Сравниваем номер чертежа, причем 745 должен быть выше, чем 469
            int result = o2.getPassport().getNumber()
                    .compareTo(o1.getPassport().getNumber());
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

}
