package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Material;
import ru.wert.tubus.client.interfaces.ItemService;

public interface IMaterialService extends ItemService<Material> {

    Material findByName(String name);

    Material createFakeProduct(String name);
}
