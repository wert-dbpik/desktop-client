package ru.wert.datapik.utils.common.interfaces;

import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;

public interface ITableView<P extends Item> extends IFormView<P> {

    void easyUpdate(ItemService<P> service);

}
