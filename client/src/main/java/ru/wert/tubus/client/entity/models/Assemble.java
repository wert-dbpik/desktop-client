package ru.wert.tubus.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.interfaces.ItemWithDraft;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"passport"}, callSuper = false)
public class Assemble extends _BaseEntity implements Item, ItemWithDraft, Comparable<Assemble>, Serializable {

    private AnyPart anyPart;
    private Passport passport;
    private String variant;
    private Coat coat;
    private TechProcess techProcess;
    private Folder folder; //Реальная папка в архиве
    private Draft draft;
    private String note;
    private List<AsmItem> asmItemsInAssemble;

    @Override
    public int compareTo(@NotNull Assemble o) {
        return toUsefulString().toLowerCase().compareTo(o.toUsefulString().toLowerCase());
    }

    @Override
    public String getName() {
        return passport.getName();
    }

    @Override
    public String toUsefulString() {
        return passport.toUsefulString();
    }
}
