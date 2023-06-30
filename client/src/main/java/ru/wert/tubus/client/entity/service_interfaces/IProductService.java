package ru.wert.tubus.client.entity.service_interfaces;

import ru.wert.tubus.client.entity.models.Draft;
import ru.wert.tubus.client.entity.models.Product;
import ru.wert.tubus.client.entity.models.ProductGroup;
import ru.wert.tubus.client.interfaces.GroupedItemService;

import java.util.List;


public interface IProductService extends GroupedItemService<Product> {

    Product findByPassportId(Long id);

    List<Product> findAllByFolderId(Long id);

    List<Product> findAllByGroupId(Long id);

    List<Product> findAllByProductGroup(ProductGroup group);

    List<Draft> findDrafts(Product product);

    List<Draft> addDrafts(Product product, Draft draft);

    List<Draft> removeDrafts(Product product, Draft draft);


}
