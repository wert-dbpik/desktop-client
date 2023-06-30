package ru.wert.tubus.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.interfaces.Item;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"assemble", "line", "anyPart"}, callSuper = false)
public class AsmItem extends _BaseEntity implements Item {

    private Assemble assemble; //Сборка в которой находится деталь
    private Integer line;
    private AnyPart anyPart;
    private Integer quantity;

    @Override
    public String getName(){
        return toUsefulString();
    }

    @Override
    public String toUsefulString() {
        return assemble.toUsefulString();
    }

}
