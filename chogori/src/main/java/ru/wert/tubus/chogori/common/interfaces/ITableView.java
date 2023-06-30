package ru.wert.tubus.chogori.common.interfaces;

import ru.wert.tubus.client.interfaces.Item;
import ru.wert.tubus.client.interfaces.ItemService;

public interface ITableView<P extends Item> extends IFormView<P> {

    void easyUpdate(ItemService<P> service);

}
