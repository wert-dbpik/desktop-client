package ru.wert.datapik.winform.enums;

import lombok.Getter;

@Getter
public enum EOperation {

    ADD("Добавить"),
    COPY("Копировать"),
    CHANGE("Изменить"),
    DELETE("Удалить"),

    ADD_FOLDER("Добавить папку"),
    REPLACE("Заменить");

    private final String name;

    EOperation(String name) {
        this.name = name;
    }
}
