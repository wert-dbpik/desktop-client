package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.ProductGroup;
import ru.wert.tubus.client.interfaces.ItemService;


public interface IProductGroupService extends ItemService<ProductGroup> {

    ProductGroup findByName(String name);

    ProductGroup getRootItem();
}
