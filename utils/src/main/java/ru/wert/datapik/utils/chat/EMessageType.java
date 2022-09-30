package ru.wert.datapik.utils.chat;

public enum EMessageType {

    CHAT_TEXT("текст"),
    CHAT_DRAFTS("чертежи"),
    CHAT_PICS("изображения");

    String typeName;

    EMessageType(String typeName) {
        this.typeName = typeName;
    }
}
