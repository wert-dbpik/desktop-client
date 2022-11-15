package ru.wert.datapik.chogori.calculator.part_calculator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ETimeMeasurement {

    SEC("сек"),
    MIN("мин");

    @Getter String timeName;

    ETimeMeasurement(String timeName) {
    }

    public static List<ETimeMeasurement> allValues(){
        return Arrays.asList(ETimeMeasurement.values());
    }

    public static List<String> allNames(){
        List<String> list = new ArrayList<>();
        for(ETimeMeasurement m : ETimeMeasurement.values()){
            list.add(m.getTimeName());
        }
        return list;
    }

}
