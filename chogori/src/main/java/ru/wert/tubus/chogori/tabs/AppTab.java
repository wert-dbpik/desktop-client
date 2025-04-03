package ru.wert.tubus.chogori.tabs;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import lombok.Getter;
import ru.wert.tubus.chogori.application.drafts.DraftsEditorController;
import ru.wert.tubus.client.interfaces.ITabController;
import ru.wert.tubus.client.interfaces.SearchableTab;
import ru.wert.tubus.client.interfaces.UpdatableTabController;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

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

}
