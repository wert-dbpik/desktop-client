package ru.wert.datapik.chogori.tabs;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import lombok.Getter;
import ru.wert.datapik.client.interfaces.ITabController;
import ru.wert.datapik.client.interfaces.UpdatableTabController;

public class AppTab extends Tab {

    @Getter
    private final ITabController tabController;


    public AppTab(String name, Node content, ITabController tabController) {
        this.tabController = tabController;

        setId(name);
        setText(name);
        setContent(content);

        //Обновление вкладки всякий раз при открытии
        setOnSelectionChanged(event ->{
            if(tabController instanceof UpdatableTabController){
                ((UpdatableTabController)tabController).updateTab();
            }
        });
    }

//    public void update(){
////        Thread t = new Thread(updateTask);
////        t.setDaemon(true);
////        t.start();
//    }
}
