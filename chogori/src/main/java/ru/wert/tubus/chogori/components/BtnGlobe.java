package ru.wert.tubus.chogori.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.chogori.common.tableView.CatalogTableView;
import ru.wert.tubus.chogori.common.tableView.RoutineTableView;
import ru.wert.tubus.chogori.search.Searchable;

import static ru.wert.tubus.chogori.images.BtnImages.BTN_GLOBE_IMG;
import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_SEARCH_FIELD;

/**
 * Класс описывает кнопку при нажатии на которую выводятся все доступные элементы сущности
 * Например, все изделия, все чертежи. При этом строка поиска обнуляется.
 */
public class BtnGlobe<P extends Item> extends Button {

    public BtnGlobe(Searchable<P> view) {

        setGraphic(new ImageView(BTN_GLOBE_IMG));
        setTooltip(new Tooltip("Показать все"));

        setId("patchButton");

        if(view instanceof RoutineTableView) {
            setOnAction((e) -> {
                view.setSearchedText("");
                ((RoutineTableView<P>) view).setModifyingItem(null);
                view.updateView();
                ((RoutineTableView<P>) view).requestFocus();
            });
        }

        if(view instanceof CatalogTableView){
            setOnAction((e)-> {
                view.setSearchedText("");
                ((CatalogTableView)view).updateFuck();
            });
        }


    }
}
