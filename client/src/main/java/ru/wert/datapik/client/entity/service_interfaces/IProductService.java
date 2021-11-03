package ru.wert.datapik.client.entity.service_interfaces;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import ru.wert.datapik.client.entity.models.Draft;
import ru.wert.datapik.client.entity.models.Product;
import ru.wert.datapik.client.entity.models.ProductGroup;
import ru.wert.datapik.client.interfaces.ItemService;

import java.util.List;
import java.util.Set;


public interface IProductService extends ItemService<Product> {

    Product findByPassportId(Long id);

    List<Product> findAllByFolderId(Long id);

    List<Product> findAllByGroupId(Long id);

    List<Product> findAllByProductGroup(ProductGroup group);

    List<Draft> findDrafts(Product product);

    List<Draft> addDrafts(Product product, Draft draft);

    List<Draft> removeDrafts(Product product, Draft draft);


}
