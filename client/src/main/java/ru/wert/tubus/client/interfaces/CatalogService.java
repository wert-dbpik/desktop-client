package ru.wert.tubus.client.interfaces;

import java.util.List;

public interface CatalogService<P extends Item>{

    List<P> findAllByGroupId(Long id);

    List<P> findAll();


}
