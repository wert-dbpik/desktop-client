package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Assemble;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.client.interfaces.PartItem;

import java.util.List;


public interface IAssembleService extends ItemService<Assemble>, PartItem {

    Assemble findByPassportId(Long id);

    List<Assemble> findAllByDraftId(Long id);

    List<Assemble> findAllByFolderId(Long id);

}
