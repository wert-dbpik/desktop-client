package ru.wert.datapik.utils.tabs;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Tab;

public class AppTab extends Tab {

    private final Task<Void> updateTask;

    public AppTab(String name, Node content, Task<Void> updateTask) {
        this.updateTask = updateTask;
        setId(name);
        setText(name);
        setContent(content);
    }

    public void update(){
        Thread t = new Thread(updateTask);
        t.setDaemon(true);
        t.start();
    }
}
