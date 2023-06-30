package ru.wert.tubus.client.interfaces;

import java.util.List;

/**
 *Иинтрефейс описывает группу интерфейсов классов типа Item и ниже
 * @param <T> <T extends Item>
 */
public interface ItemService<T extends Item> {

    T findById(Long id);

    T save(T t);

    boolean update(T t);

    boolean delete(T t);

    List<T> findAll();

    List<T> findAllByText(String text);

}
