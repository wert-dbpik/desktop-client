package ru.wert.datapik.utils.common.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;
import ru.wert.datapik.utils.common.tableView.RoutineTableView;
import ru.wert.datapik.utils.search.Searchable;

import static ru.wert.datapik.utils.images.BtnImages.BTN_GLOBE_IMG;

/**
 * Класс описывает кнопку при нажатии на которую выводятся все доступные элементы сущности
 * Например, все изделия, все чертежи. При этом строка поиска обнуляется.
 */
public class BtnGlobe<P extends Item> extends Button {

    public BtnGlobe(Searchable<P> view) {
        setGraphic(new ImageView(BTN_GLOBE_IMG));
        setTooltip(new Tooltip("Показать все"));

        if(view instanceof RoutineTableView) {
            setOnAction((e) -> {
                view.setSearchedText("");
                ((RoutineTableView<P>) view).setModifyingItem(null);
                view.updateView();
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
