package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemWithDraft;

import java.io.Serializable;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"passport"}, callSuper = false)
public class Detail extends _BaseEntity implements Item, ItemWithDraft, Comparable<Detail>, Serializable {

    private AnyPart part;
    private String krp;
    private Passport passport;
    private String variant;
    private Coat coat;
    private Folder folder; //Реальная папка в архиве
    private Material material;
    private Integer paramA;
    private Integer paramB;
    private Draft draft;
    private String note;

    @Override
    public int compareTo(@NotNull Detail o) {
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
