package ru.wert.datapik.chogori.excel.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public enum EColName {
    ECOLOR("Цвет"),
    ROW_NUM("N"),
    KRP("КРП"),
    DEC_NUM("Номер"),
    NAME("Наименование"),
    LACQUER("Лак"),
    COAT("Покр"),
    ZCP("ЦСГ"),
    TOTAL_AMOUNT("Кол"),
    AMOUNT("(кол)"),
    FOLDER("Источник"),
    MATERIAL("Материал А"),
//    S("S"),
    A("a, \nмм"),
    B("b, \nмм")
    ;


    private final String colName;

    EColName(String colName) {
        this.colName = colName;
    }

    public String getColName() {
        return colName;
    }

    public static List<String> getColNamesList(){
        List<String> names = new ArrayList<>();
        for(EColName cn : EColName.values())
            names.add(cn.getColName());
        return names;

    }

    @Override
    public String toString() {
        return colName;
    }

    //Метод возвращает объект класса EColName, для которого совпадает заголовок столбца
    public static EColName getEColNameByColText(String colText){

        for(EColName eColName : EColName.values()){
            if(eColName.getColName().equals(colText))
                return eColName;
        }
        throw new NoSuchElementException("There is no such EColName with name = " + colText);
    }
}

