package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NormTimes {

    private double mechTime = 0.0; //МК
    private double paintTime = 0.0; //ППК
    private double assmTime = 0.0; //Сборка
    private double packTime = 0.0; //Упаковка
    private double totalTime = 0.0; //Общее время
}
