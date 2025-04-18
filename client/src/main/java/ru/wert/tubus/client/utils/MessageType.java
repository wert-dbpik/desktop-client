package ru.wert.tubus.client.utils;

import lombok.Getter;

public enum MessageType {
    HEARTBEAT("Пользователь онлай"),

    USER_IN("Вошел пользователь"),
    USER_OUT("Вышел пользователь"),

    CHAT_TEXT("текст"),
    CHAT_DRAFTS("чертежи"),
    CHAT_FOLDERS("комплекты чертежей"),
    CHAT_PICS("изображения"),
    CHAT_PASSPORTS("пасспорта"),
    CHAT_SEPARATOR("сепаратор с датой"),
    CHAT_UPDATE_TEMP_ID("Обновить временный ID"),
    MESSAGE_DELIVERED("Сообщение доставлено"),

    UPDATE_PASSPORT("Обновление пасспорта"), //Переименование чертежа

    ADD_DRAFT("Добавление чертежа"),
    UPDATE_DRAFT("Обновлен чертеж"),
    DELETE_DRAFT("Удален чертеж"),

    ADD_FOLDER("Добавлен комплект чертежей"),
    UPDATE_FOLDER("Обновлен комплект чертежей"),
    DELETE_FOLDER("Удален комплект чертежей"),

    ADD_PRODUCT("Добавлено изделие"),
    UPDATE_PRODUCT("Обновлено изделие"),
    DELETE_PRODUCT("Удалено изделие"),

    ADD_PRODUCT_GROUP("Добавлена группа изделий"),
    UPDATE_PRODUCT_GROUP("Обновлена группа изделий"),
    DELETE_PRODUCT_GROUP("Удалена группа изделий"),

    PUSH("Уведомление"),
    DELETE_MESSAGE("Удалить сообщение"),
    UPDATE_MESSAGE("Изменить сообщение");

    @Getter
    private final String typeName;

    MessageType(String typeName) {
        this.typeName = typeName;
    }
}
