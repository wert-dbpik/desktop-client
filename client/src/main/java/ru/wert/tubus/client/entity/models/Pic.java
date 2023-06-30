package ru.wert.tubus.client.entity.models;

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
@EqualsAndHashCode(of = {"user", "time"}, callSuper = true)
public class Pic extends _BaseEntity implements Item {
    private String initName;
    private String extension;
    private Integer width;
    private Integer height;
    private User user;
    private String time;

    @Override
    public String getName() {
        return getId() + "." + getExtension();
    }

    @Override
    public String toUsefulString() {
        return user.getName() + ": " + time;
    }


}
