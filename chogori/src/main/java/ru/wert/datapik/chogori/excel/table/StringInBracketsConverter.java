package ru.wert.datapik.chogori.excel.table;

import javafx.util.StringConverter;

public class StringInBracketsConverter extends StringConverter<String> {

    @Override
    public String toString(String object) {
        return object == null ? "" : object;
    }
    @Override
    public String fromString(String string) {
        if(!string.equals("")) {
            string = string.replaceAll("[()]", "");
            return "(" + string + ")";
        } else
            return "";
    }
}
