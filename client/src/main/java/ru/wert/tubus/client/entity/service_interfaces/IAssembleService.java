package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Assemble;
import ru.wert.tubus.client.interfaces.ItemService;
import ru.wert.tubus.client.interfaces.PartItem;

import java.util.List;


public interface IAssembleService extends ItemService<Assemble>, PartItem {

    Assemble findByPassportId(Long id);

    List<Assemble> findAllByDraftId(Long id);

    List<Assemble> findAllByFolderId(Long id);

}
