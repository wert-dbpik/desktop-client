package ru.wert.tubus.client.entity.models;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.wert.tubus.client.interfaces.Item;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"user", "text"}, callSuper = false)
public class Remark extends _BaseEntity implements Item, Comparable {

    private Passport passport;
    private User user;
    private String text;
    private String creationTime;

    private List<Pic> picsInRemark;


    @Override
    public String getName() {
        return null;
    }

    @Override
    public String toUsefulString() {
        return user + ": " + text + "pics: " + picsInRemark.toString();
    }

    @Override
    public int compareTo(Object o) {
        return ((Remark)o).getCreationTime().compareTo(creationTime);
//        return creationTime.compareTo(((Remark)o).getCreationTime());
    }

}
