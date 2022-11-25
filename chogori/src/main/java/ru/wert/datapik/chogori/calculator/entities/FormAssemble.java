package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.IOpPlate;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FormAssemble extends OpData {

    private String name;
    private List<IOpPlate> operations;

}
