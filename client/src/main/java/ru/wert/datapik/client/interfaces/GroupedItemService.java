package ru.wert.datapik.client.interfaces;

import ru.wert.datapik.client.entity.models.Product;

import java.util.List;

public interface GroupedItemService<P extends Item> extends ItemService<P>{

    List<P> findAllByGroupId(Long id);
}
