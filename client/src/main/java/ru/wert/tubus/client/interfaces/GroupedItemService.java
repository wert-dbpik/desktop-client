package ru.wert.tubus.client.interfaces;

import java.util.List;

public interface GroupedItemService<P extends Item> extends ItemService<P>{

    List<P> findAllByGroupId(Long id);
}
