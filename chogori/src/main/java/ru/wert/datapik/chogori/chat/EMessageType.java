package ru.wert.datapik.chogori.chat;

public enum EMessageType {

    CHAT_TEXT("текст"),
    CHAT_DRAFTS("чертежи"),
    CHAT_FOLDERS("комплекты чертежей"),
    CHAT_PICS("изображения");

    String typeName;

    EMessageType(String typeName) {
        this.typeName = typeName;
    }
}
