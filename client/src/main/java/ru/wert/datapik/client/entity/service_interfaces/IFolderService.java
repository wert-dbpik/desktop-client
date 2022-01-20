package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Folder;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.interfaces.GroupedItemService;
import ru.wert.datapik.client.interfaces.ItemService;

import java.util.List;


public interface IFolderService extends GroupedItemService<Folder> {

    Folder findByName(String name);

    Folder findByDecNumber(String number);

    List<Folder> findAllByGroupId(Long id);

}
