package ru.wert.datapik.client.entity.service_interfaces;

import ru.wert.datapik.client.entity.models.Material;
import ru.wert.datapik.client.interfaces.ItemService;

public interface IMaterialService extends ItemService<Material> {

    Material findByName(String name);

    Material createFakeProduct(String name);
}
