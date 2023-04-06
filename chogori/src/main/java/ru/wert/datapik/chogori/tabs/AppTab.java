package ru.wert.datapik.chogori.tabs;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import lombok.Getter;
import ru.wert.datapik.chogori.application.drafts.DraftsEditorController;
import ru.wert.datapik.client.interfaces.ITabController;
import ru.wert.datapik.client.interfaces.SearchableTab;
import ru.wert.datapik.client.interfaces.UpdatableTabController;

import static ru.wert.datapik.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

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
            if(tabController instanceof DraftsEditorController){
                CH_SEARCH_FIELD.changeSearchedTableView(((DraftsEditorController) tabController).getDraftsTable(), "ЧЕРТЕЖ");
            }

            if(tabController instanceof SearchableTab){
                ((SearchableTab) tabController).tuneSearching();
            }

        });
    }

//    public void update(){
////        Thread t = new Thread(updateTask);
////        t.setDaemon(true);
////        t.start();
//    }
}
