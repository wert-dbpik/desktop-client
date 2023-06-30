package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Detail;
import ru.wert.tubus.client.interfaces.ItemService;

import java.util.List;


public interface IDetailService extends ItemService<Detail> {

    Detail findByPassportId(Long id);

    List<Detail> findAllByDraftId(Long id);

    List<Detail> findAllByFolderId(Long id);

}
