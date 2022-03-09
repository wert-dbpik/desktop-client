package ru.wert.datapik.utils.common.treeView;

import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import ru.wert.datapik.client.interfaces.CatalogGroup;
import ru.wert.datapik.client.interfaces.Item;
import ru.wert.datapik.utils.common.tableView.CatalogTableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.wert.datapik.utils.images.AppImages.TREE_NODE_IMG;

/**
 * Класс описывает поведенеие класса Item_TreeView при применении COPY-PASTE и DragAndDrop
 * Для добавлении функционала данного класса достаточно в классе Item_TreeView создать экземпляр
 * данного класса и вызвать метод createDragAndDropHandler(), при применении  COPY-PASTE следует
 * использовать метод onKeyPressed()
 * @param <P>
 * @param <T>
 */
public class Item_TreeViewDragAndDrop<P extends Item, T extends CatalogGroup> {

    //Переменные, необходимые для DragAndDrop и Erase-Paste
    private TreeItem<T> draggedTarget; //то, куда вставляем
    private TreeItem<T> draggedSource; //то, что переносим
    private List<String> backToDraggedSource = Arrays.asList("", ""); //Parent узла, который перенесли

    private TreeItem<T> hoveredItem = null;
    private Thread hoveredThread;

    private Item_TreeView<P, T> treeView;
    private CatalogTableView<P, T> tableView;

    public Item_TreeViewDragAndDrop(Item_TreeView<P, T> treeView, CatalogTableView<P, T> tableView) {
        this.treeView = treeView;
        this.tableView = tableView;
    }

    public void onKeyPressed(KeyEvent e){

        if(treeView.isFocused()) {

            if (e.getCode() == KeyCode.DELETE) {
                treeView.getItemCommands().delete(e, findChosenItems()); //DELETE удаляем
            }

            if (e.getCode() == KeyCode.C && e.isControlDown()) {
                eraseItem(e); //(CTRL + C) вырезаем
            }

            if (e.getCode() == KeyCode.V && e.isControlDown()) {
                pasteItem(e, false); //(CTRL + V) вставляем
            }

            if (e.getCode() == KeyCode.INSERT && e.isControlDown()) {
                eraseItem(e); //(CTRL + INSERT) вырезаем
            }

            if (e.getCode() == KeyCode.INSERT && e.isShiftDown()) {
                pasteItem(e, false); //(SHIFT + INSERT) вставляем
            }

            if (e.getCode() == KeyCode.B && e.isControlDown()) {
                //Определяем откуда произошло перемещение
                TreeItem<T> sourceTreeItem = findTreeItemByName(backToDraggedSource.get(0));
                treeView.getSelectionModel().select(sourceTreeItem);

//                connectedForm.updateTable(sourceTreeItem, Boolean.valueOf(backToDraggedSource.get(1)));
            }
        }
    }

    /**
     * На treeView добавляются возможности DragAndDrop, дублирующие в нашем случае Cut-Paste.
     */
    public void createDragAndDropHandler(){
        treeView.setCellFactory(new Callback<TreeView<T>, TreeCell<T>>() {
            @Override
            public TreeCell<T> call(TreeView<T> param) {
                TreeCell<T> treeCell = new TreeCell<T>() {
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null) {
                            //Остаются исходные данные
                            setText(item.getName());
                            setGraphic(new ImageView(TREE_NODE_IMG));
                        } else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };

                treeCell.setOnDragDetected(e -> createOnDragDetected(e, treeCell));
                treeCell.setOnDragOver(e -> createOnDragOver(e, treeCell));
                treeCell.setOnDragDropped(e -> createOnDragDropped(e, treeCell));
                treeCell.setOnDragDone(e -> createOnDragDone(e, treeCell));

                treeCell.selectedProperty().addListener((observable, oldValue, newValue) ->{
                    if(newValue){
//                        if(AppWindow.appKeys.contains(KeyCode.CONTROL) )
//                            getConnectedForm().updateTable(treeCell.getTreeItem(), true);
//
//                        else
//
//                            getConnectedForm().updateTable(treeCell.getTreeItem(), false);

                    }
                });

                return treeCell;
            }
        });
    }

    /**
     * Обработка события OnDragOver
     */
    private void createOnDragOver(DragEvent e, TreeCell<T> treeCell){
        //При переносе строк из tableView
        if (e.getGestureSource() instanceof TableView
                && e.getDragboard().hasString()) {
            draggedTarget = treeCell.getTreeItem();

            //Если в клювике что-то есть можно переносить
            if (e.getDragboard().hasString()){
                e.acceptTransferModes(TransferMode.MOVE);
            }

            treeView.getSelectionModel().select(draggedTarget);
            expandIfNeeded(draggedTarget);

            //При переносе узлов TreeItem
        }else{
            draggedTarget = treeCell.getTreeItem();
            List<TreeItem<T>> unacceptableItems =
                    treeView.findAllChildren(draggedSource);
            unacceptableItems.add(draggedSource);
            unacceptableItems.add(draggedSource.getParent());

            //Нельзя бросить в самого себя,
            if (!unacceptableItems.contains(draggedTarget) &&
                    //и необходима строка в клювике
                    e.getDragboard().hasString()
            ) {

                e.acceptTransferModes(TransferMode.MOVE);
//                TreeItem<CatalogGroup> hoveredItem = treeCell.getTreeItem();
                treeView.getSelectionModel().select(draggedTarget);
                expandIfNeeded(draggedTarget);
            }
        }
        e.consume();
    }

    /**
     * Обработка события OnDragDropped
     */
    private void createOnDragDropped(DragEvent event, TreeCell<T> treeCell){
        //Если бросаются строки TableView
        if (!(event.getGestureSource() instanceof TableView))
            pasteTreeItems(null, event,true);
        event.consume();
    }

    /**
     * По завершении переноса возвращаем переменные в исходное состояние
     */
    private void createOnDragDone(DragEvent event, TreeCell<T> treeCell){

        draggedSource = null;
        draggedTarget = null;
//        getConnectedForm().getSourceTableRows().clear();
    }

    /**
     * Обработка события OnDragDetected только для TreeView
     */
    private void createOnDragDetected(MouseEvent e, TreeCell<T> treeCell){

        Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);

//      //Это нужно, зачем - хз
        ClipboardContent content = new ClipboardContent();
        content.putString(e.toString());
        db.setContent(content);

        draggedSource = treeCell.getTreeItem();

        //сохраняем имя папки, чтобы потом к ней вернуться
        backToDraggedSource.set(0, draggedSource.getParent().getValue().getName());

        //Преобразуем название treeCell в изображение, чтобы добавить его как сопровождение DnD
        String s = treeCell.getTreeItem().getValue().getName();
        Text t = new Text(s);
        WritableImage image = t.snapshot(null, null);

        //Хитрость, чтобы расположить картинку НАД курсором, а не ПОД
        Bounds b = treeCell.getBoundsInLocal();
        db.setDragView(image, b.getMinX(), b.getMinY() + b.getHeight()/1.8);

        //Возможно перемещение узла в виде картинки, но мне не понравилось
/*                    SnapshotParameters snapshotParams = new SnapshotParameters();
                    WritableImage image2 = treeCell.snapshot(snapshotParams, null);
                    db.setDragView(image2, b.getMinX(), b.getMinY() + b.getHeight()/1.8);*/

        e.consume();
    }

    /**
     * Разворачивает узел, над которым завис курсор
     */
    private void expandIfNeeded(TreeItem<T> targetItem){
        //Избавляемся от NullPointerException
        if (targetItem != null) {
            //Если выделенный targetItem не совпадает с предыдущим
            if (this.hoveredItem == null) {
                this.hoveredItem = targetItem;
                //создаем поток
                hoveredThread = new Thread(() -> {
                    try {
                        //поток засыпает и после сна раскрывает узел
                        Thread.sleep(1000);
                        hoveredItem.setExpanded(true);

                        hoveredThread.join();
                    } catch (InterruptedException e) {
                    }
                });
                hoveredThread.start();
            }else{
                //Если выделен новый узел, то старое значение узла обнуляем,
                // имеющийся поток прерываем и запускаем новый поток
                if(this.hoveredItem != targetItem){
                    hoveredThread.interrupt();
                    hoveredItem = null;
                    expandIfNeeded(targetItem);
                }
            }
        }
    }

    /**
     * Проверка на допустимость вставки
     * @param itemTarget - куда вставляется
     * @param itemsToMove - элементы, кторые нужно переместить
     * @return true - если перемещение допустимо
     */
    private boolean goodToPaste(TreeItem<T> itemTarget, List<TreeItem<T>> itemsToMove){
        //Копирование не допускается если вырезанный объект не существует
        if( draggedSource == null ||
                //если вырезанный объект вставляется на старое место
                itemTarget.getValue().getId().equals((draggedSource).getValue().getParentId()) ||
                //если вставляется сам в себя
                itemTarget.getValue().getId().equals((draggedSource).getValue().getId()))
            return false;

        //Если вырезанный объект вставляется в своих последышей
        for(TreeItem<T> tg : itemsToMove) {
            if (itemTarget.getValue().getParentId().equals(tg.getValue().getId()))
                return false;
        }

        return true;
    }

    /**
     * Вставить объект
     */
    private void pasteItem(Event event, boolean isDnDJob){
        if (this.draggedSource == null) {
//            pasteTableItems(event, isDnDJob);

        }else
            pasteTreeItems(null, event, isDnDJob);
    }

    /*
     *//**
     * Вставка строк таблицы
     *//*
    @SuppressWarnings("unchecked")
    private void pasteTableItems(Event event, boolean isDnDJob){
        //Определяем куда вставлять
        //Если это перетаскивание, то пользуемся данными, полученными ранее
        //Иначе, получаем данные прямо с дерева
        TreeItem<T> draggedTarget = null;
        if(isDnDJob){
            draggedTarget = this.draggedTarget;
        }
        else
            draggedTarget = currentSelectedTreeItem();

        List<CatalogableItem> selectedRows = connectedForm.getSourceTableRows();

        //Если перед нами массив папок
        if(selectedRows.get(0).getId().equals(0L)){
            //Перебираем папки
            for(CatalogableItem folderItem : selectedRows){
                String folderName = folderItem.getName();
                //Находим узел дерева с именем папки
                TreeItem<T> movedTreeItem = findTreeItemByName(folderName);
                //Вызываем соответствующего специалиста для ее перемещения
                pasteTreeItems(movedTreeItem, event, true);

            }
            event.consume();
        } else {
            //Иначе, работаем по плану
            //Строки вставляются в цикле, если их несколько
            for (CatalogableItem ci : selectedRows) {
                ci.setCatalogGroup(draggedTarget.getValue());
                //Очень важно вызвать нужнй update - для объектов таблицы, не дерева
                ((TableAndTreeMaster)catalogableTableController.getFormMaster()).getItemService().update(ci);
            }
            //Мы перенесли, обнуляем лист со строками
            getConnectedForm().getSourceTableRows().clear();
            //И обновляем таблицу
            getSelectionModel().select(draggedTarget);
            getConnectedForm().updateTable(draggedTarget, false);
        }
    }

 */   /**
     * Вставка узлов дерева
     */
    private void pasteTreeItems(TreeItem<T> movedTreeItem, Event event, boolean isDnDJob){
        //Определяем куда вставлять
        //Если это перетаскивание, то пользуемся данными, полученными ранее
        //Иначе, получаем данные прямо с дерева
        TreeItem<T> draggedTarget = null;
        if(isDnDJob){
            draggedTarget = this.draggedTarget;
        }
        else
            draggedTarget = nowSelectedTreeItem();

        //Если метод вызван из метода pasteTableItems()
        if(movedTreeItem != null) draggedSource = movedTreeItem;

        //Получаем лист перемещаемых объектов
        List<TreeItem<T>> itemsToMove = treeView.findAllChildren(draggedSource);
        itemsToMove.add(draggedSource);

        //Делаем проверку на допустимость операции и вставляем
        if(goodToPaste(draggedTarget, itemsToMove)){
            T catalogGroup = draggedSource.getValue();
            catalogGroup.setParentId(draggedTarget.getValue().getId());

            treeView.getItemService().update(catalogGroup);

            //перерисовываем дерево
            draggedTarget.setExpanded(true);

            treeView.updateView();

            //Определяем имя узла
            String name = draggedTarget.getValue().getName();

            treeView.getSelectionModel().select(findTreeItemByName(name));


//            getConnectedForm().updateTable(findTreeItemByName(name), false);

        }
    }

    /**
     * Вырезать объект
     * Происходит условное вырезание объекта, на самом деле он остается в каталоге,
     * а сохраняется только его копия
     */
    private void eraseItem(Event event){
        draggedSource = nowSelectedTreeItem();
    }

    /**
     * Ищет TreeItem в TreeView по имени
     */
    public TreeItem<T> findTreeItemByName(String name){
        List<TreeItem<T>> listTreeItems = treeView.findAllChildren(treeView.getRoot());
        listTreeItems.add(treeView.getRoot());
        for(TreeItem<T> treeItem : listTreeItems){
            if(treeItem.getValue().getName().equals(name))
                return treeItem;
        }
        return null;
    }

    /**
     * Возвращает выделенный в данный момент узел деоева
     */
    public TreeItem<T> nowSelectedTreeItem(){
        return treeView.getSelectionModel().getSelectedItem();
    }

    /**
     * Возвращает список выделенных элементов (названий групп) - на самом деле группа выделена всего одна
     * т.к. множественное выделение хапрещено
     */
    private List<T> findChosenItems(){
        T chosenItem = nowSelectedTreeItem().getValue();
        return new ArrayList<>(Arrays.asList(chosenItem));
    }
}
