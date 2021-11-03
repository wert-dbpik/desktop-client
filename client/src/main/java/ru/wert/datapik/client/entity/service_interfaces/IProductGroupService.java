package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.ItemService;


public interface IProductGroupService extends ItemService<ProductGroup> {

    ProductGroup findByName(String name);

    ProductGroup getRootItem();
}
