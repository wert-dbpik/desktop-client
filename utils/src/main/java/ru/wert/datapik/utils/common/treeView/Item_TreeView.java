package ru.wert.datapik.utils.common.treeView;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.client.interfaces.ItemService;
import ru.wert.datapik.utils.common.commands.ItemCommands;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;
import ru.wert.datapik.utils.common.interfaces.IFormView;
import ru.wert.datapik.utils.common.tableView.ItemTableView;

import java.util.*;

/**
 * Класс описывает ПРОСТОЕ НЕРЕДАКТИРУЕМОЕ дерево, расширяя класс TreeView<T>
 */

@Slf4j
public abstract class Item_TreeView<P extends Item, T extends CatalogGroup> extends TreeView<T> implements IFormView<T> {


    @Getter private final ItemService<T> itemService;
    private final T rootItem;
    private TreeItem<T> root;
    private ItemTableView<Item> connectedForm;

    public abstract void createContextMenu();
    public abstract ItemCommands<T> getItemCommands();

    @Override//IFormView
    public List<T> getAllSelectedItems(){
        List<T> items = new ArrayList<>();
        ObservableList<TreeItem<T>> treeItems = getSelectionModel().getSelectedItems();
        for(TreeItem<T> treeItem : treeItems){
            items.add(treeItem.getValue());
        }
        return items;
    }


    /**
     * Конструктор принимет:
     * 1) itemService - это интерфейс содержащий метод как findAll() для обращения к БД
     * 2) rootItem - это элемент типа CatalogGroup, с помощью которого будет создан корень дерева root
     * CatalogGroup это интерфес, содержащий методы getParentId() и setParentId(), расширяет интерфейс Item
     *
     * @param itemService ItemService<T>
     * @param rootItem CatalogGroup
     */
    public Item_TreeView(ItemService<T> itemService, T rootItem) {
        this.itemService = itemService;
        this.rootItem = rootItem;

        //Собственная CellFactory нужна, для появления значков и коротких имен
        setCellFactory((TreeView<T> tv) -> new ItemTreeCell<>());

        setShowRoot(false);

        buildTree();

//        Item_TreeViewDragAndDrop<P, T> myDragAndDrop = new Item_TreeViewDragAndDrop<>(this, (CatalogTableView<P, T>) connectedForm);
//
//        setOnKeyPressed(myDragAndDrop::onKeyPressed);
//
//        myDragAndDrop.createDragAndDropHandler();


    }

    /**
     * Построение дерева
     */
    public void buildTree(){
        //Защитим построение дерева от зацикливания
        normalizeTreeView();
        log.debug("Нормализация дерева {} проведена успешно", rootItem.getClass().getSimpleName());

        //Получаем копию таблицы элементов из БД
        ObservableList<T> elements = itemService.findAll();
        elements.sort(createTreeViewComparator());

        root = new TreeItem<T>(rootItem);
        root.setExpanded(true);

        List<TreeItem<T>> level = new ArrayList<>();
        List<TreeItem<T>> newLevel = new ArrayList<>();
        level.add(root);
        // Создаем итератор для прохождения коллекции
        Iterator<TreeItem<T>> iteratorByLevel = level.iterator();
        //===============================================
        while(true){
            // Проходим по узлам очередного уровня дерева
            while(iteratorByLevel.hasNext()){
                // Получаем новый элемент из позиции итератора
                TreeItem<T> parentTi = iteratorByLevel.next();

                // Перебираем элементы массива БД
                for (Iterator<T> i = elements.iterator(); i.hasNext();) {
                   T treeBuildingItem = i. next();
                    // Если ID элемента в узле дерева совпадает parentID с из массива элементов БД
                    if ((parentTi.getValue()).getId().equals(treeBuildingItem.getParentId())) {

                        //То из этого элемента создаем новый узел дерева
                        TreeItem<T> newTi = new TreeItem<>(treeBuildingItem);

                        //И пристегиваем его к родительскому узлу
                        parentTi.getChildren().add(newTi);

                        // Полученный узел добавляем к новому уровню
                        newLevel.add(newTi);

                        i.remove();
                    }
                }
            }
            // Если узлов в новом слое нет
            if(elements.isEmpty())
                // то прикрываем лавочку
                break;
            else
                // иначе, начинаем сначала
                level.clear();
            level.addAll(newLevel);
            iteratorByLevel = level.iterator();
        }
        //Устанавливаем root на TreeView
        setRoot(root);
    }


    /**
     * Записи без родительского узла (если такие будут) пристегиваем к корню каталога
     * Защита построения дерева от зацикливания
     */
    private void normalizeTreeView(){
        //Создаем копию элементов из БД
        ObservableList<T> Ts = itemService.findAll();
        //Создаем для коллекции итератор
        //Пробегаем по коллекции
        for (T tg1 : Ts) {
            Long parentId = tg1.getParentId();
            boolean match = false;
            //Если элемент коллекции ссылается на корень дерева, берем следующий элемент
            if (parentId.equals(1L)) continue;

            for (T tg2 : Ts) {
                //Если родитель в коллекции присутствует, берем следующий элемент
                if (parentId.equals((tg2).getId())) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                //Иначе, присваиваем элементу родителя - корень дерева
                log.error("Ошибка построения каталога, не найден parentId для {}, id={}", tg1, tg1.getId());
                //Достаем из БД элемент
                T item = itemService.findById(tg1.getId());
                item.setParentId(1L);
                itemService.update(item);
            }
        }
    }

    /**
     * Компаратор для сравнения узлов дерева
     */
    private Comparator<T> createTreeViewComparator(){
        return (o1, o2) -> {
            String s1 = o1.getName();
            String s2 = o2.getName();

            return s1.compareTo(s2);
        };
    }

    /**
     * Перестроение дерева. При перестроении сохраняется вид узлов (развернут/свернут) в HashMap
     * Потом дерево строится заново, а состояние его узлов переносится из сохраненного Map
     */
    @Override
    public void updateView(){
        //Создаем лист текущего состояния элементов дерева
        //Для этого создаем текущий лист всех узлов
        List<TreeItem<T>> list1 = new ArrayList<>(findAllChildren(root));
        //создаем HashMap в котором будем хранить состояние узла
        Map<Long, Boolean> expandedMap = new HashMap<>();
        //В цикле
        for (TreeItem<T> ti : list1)
            //заполняем HashMap значениями. Ключ - Id хранящегося в узле значения БД
            expandedMap.put(((CatalogGroup)ti.getValue()).getId(), ti.isExpanded());
        // Строим новое дерево
        buildTree();
        //Снова создаем лист из всех его узлов
        List<TreeItem<T>> list2 = new ArrayList<>(findAllChildren(root));
        //В цикле
        for (TreeItem<T> treeItem : list2) {
            //Если узел присутствует в новом листе
            if (expandedMap.containsKey((treeItem.getValue()).getId()))
                //меняем его состояние исходя из того, как оно сохранено в HashMap
                treeItem.setExpanded(expandedMap.get((treeItem.getValue()).getId()));
        }
    }

    /**
     * Определение всех потомков элемента дерева, метод обобщенный, так как исп-ся для TreeTable
     */
    public List<TreeItem<T>> findAllChildren(TreeItem<T> treeItem){
        //Создаем три листа
        //лист, где будут храниться, найденые потомки в текущей итерации
        List<TreeItem<T>> newList = new ArrayList<>();
        //лист со всеми найденными потомками
        List<TreeItem<T>> finalList = new ArrayList<>();
        //текущий лист для итерации
        List<TreeItem<T>> list = new ArrayList<>();
        //В него добавляем сам узел, от которого будем искать потомков
        list.add(treeItem);

        //В цикле получаем потомков текущего узла, складываем их во временный лист,
        //потом временный лист снова перебираем и для каждого узла находим своих потомков
        //Найденных потомков снова складываем в промежуточный лист, а предыдущий лист
        //суммируем с finalList.
        while(true){
            for(TreeItem<T> ti : list)
                newList.addAll(ti.getChildren());
            if(newList.isEmpty()) break;
            else {
                finalList.addAll(newList);
                list.clear();
                list.addAll(newList);
                newList.clear();
            }
        }
        return finalList;
    }

    public List<T> findAllGroupChildren(TreeItem<T> treeItem){
        List<TreeItem<T>> allItemChildren = findAllChildren(treeItem);
        List<T> allGroupChildren = new ArrayList<>();
        for(TreeItem<T> baby : allItemChildren){
            allGroupChildren.add(baby.getValue());
        }
        return allGroupChildren;
    }

    /**
     * Свернуть все узлы
     */
    public void foldTreeView(){
        List<TreeItem<T>> listOfItemsToUnfold = findAllChildren(root);
        for(TreeItem<T> ti : listOfItemsToUnfold)
            ti.setExpanded(false);
    }


    /**
     * Развернуть все узлы дерева
     */
    public void unfoldTreeView(){
        int index = getFocusModel().getFocusedIndex();
        List<TreeItem<T>> allItems = findAllChildren(root);
        for(TreeItem<T> item : allItems){
            item.setExpanded(true);
        }
        getFocusModel().focus(index);
    }

    /**
     * Найти строку в котором находится объект, работает только если все дерево раскрыто
     */
    public int findTreeItemRow(Item item){
        List<TreeItem<T>> list = findAllChildren(root);
        for(TreeItem<T> ti : list){
            if(ti.getValue().getName().equals(item.getName()))
                return getRow(ti);
        }
        return 0;
    }

    /**
     * Ищет TreeItem в TreeView по id
     * @param id Long
     * @return
     */
    public TreeItem<T> findTreeItemById(Long id){
        List<TreeItem<T>> listTreeItems = findAllChildren(getRoot());
        listTreeItems.add(getRoot());
        for(TreeItem<T> treeItem : listTreeItems){
            if(treeItem.getValue().getId().equals(id))
                return treeItem;
        }
        return null;
    }

    /**
     * Получить таблицу, с которой взаимодействует каталог treeView
     */
    public ItemTableView<Item> getConnectedForm() {
        return connectedForm;
    }

    /**
     * Подключить таблицу, с которой будет взаимодействовать каталог treeView
     */
    public void setConnectedForm(ItemTableView<Item> connectedForm) {
        this.connectedForm = connectedForm;
    }
}
