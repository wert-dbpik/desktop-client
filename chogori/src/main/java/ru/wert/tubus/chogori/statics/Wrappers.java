package ru.wert.tubus.chogori.statics;

import javafx.beans.property.ReadOnlyStringWrapper;
import ru.wert.tubus.client.interfaces.Item;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Wrappers {

    /**
     * Из объекта берем его имя и преобразуем в строку
     * @param item
     * @return
     */
    public static ReadOnlyStringWrapper getNameFromItem(Item item){
        if(item != null) {
            String name = item.getName();
            if (!name.equals(""))
                return new ReadOnlyStringWrapper(name);
        }

        return new ReadOnlyStringWrapper("");
    }

    /**
     * Крохотный double преобразуем в строку 7.85E-6 -> "0.00000785"
     * @param d double
     * @return ReadOnlyStringWrapper
     */
    public static ReadOnlyStringWrapper getStringFromDouble(double d){
        if(d != 0) {
            NumberFormat nf = new DecimalFormat("######.###");
            String s = nf.format(d);
            return new ReadOnlyStringWrapper(s);
        }

        return new ReadOnlyStringWrapper("");
    }
    /**
     * Число преобразуем в строку 5 -> "5"
     * @param d int
     * @return ReadOnlyStringWrapper
     */
    public ReadOnlyStringWrapper getStringFromInt(int d){
        if(d != 0) {
            String s = String.valueOf(d);
            return new ReadOnlyStringWrapper(s);
        }

        return new ReadOnlyStringWrapper("");
    }
}
