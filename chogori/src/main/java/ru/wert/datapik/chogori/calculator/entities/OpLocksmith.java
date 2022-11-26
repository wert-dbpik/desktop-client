package ru.wert.datapik.chogori.calculator.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.datapik.chogori.calculator.enums.EOpType;

@Getter
@Setter
@NoArgsConstructor
public class OpLocksmith extends OpData {

    private EOpType op = EOpType.LOCKSMITH;
    private Integer rivets; //Вытяжные заклепки
    private Integer countersinkings; //Зенкования
    private Integer threadings; //Нарезания резьбы
    private Integer smallSawings; //Пиление на малой пиле
    private Integer bigSawings;//Пиление на большой пиле

}
