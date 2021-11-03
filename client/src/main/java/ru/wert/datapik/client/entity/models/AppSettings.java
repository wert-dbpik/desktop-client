package ru.wert.datapik.client.entity.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.Item;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class AppSettings extends _BaseEntity implements Item {

    private String name; //Наименование группы настроек
    private User user;   //Пользователь, к которому относятся эти настройки
    private Integer monitor; //монитор

    @Override
    public String toUsefulString() {
        return null; //Пока нет применения
    }
}
