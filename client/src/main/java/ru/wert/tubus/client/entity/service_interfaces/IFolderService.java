package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Folder;
import ru.wert.tubus.client.interfaces.GroupedItemService;

import java.util.List;


public interface IFolderService extends GroupedItemService<Folder> {

    Folder findByName(String name);

    List<Folder> findAllByGroupId(Long id);

}
