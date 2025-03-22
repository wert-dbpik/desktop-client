package ru.wert.tubus.chogori.chat.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import ru.wert.tubus.client.entity.models.User;

public class UserOnline {
    private final User user;
    private final BooleanProperty online;

    public UserOnline(User user, boolean online) {
        this.user = user;
        this.online = new SimpleBooleanProperty(online);
    }

    public User getUser() {
        return user;
    }

    public boolean isOnline() {
        return online.get();
    }

    public void setOnline(boolean online) {
        this.online.set(online);
    }

    public BooleanProperty onlineProperty() {
        return online;
    }
}
