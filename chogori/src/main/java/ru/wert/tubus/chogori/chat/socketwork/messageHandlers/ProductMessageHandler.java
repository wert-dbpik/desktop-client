package ru.wert.tubus.chogori.chat.socketwork.messageHandlers;

import com.google.gson.Gson;
import javafx.scene.control.Tab;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.chogori.application.drafts.DraftsEditorController;
import ru.wert.tubus.chogori.tabs.AppTab;
import ru.wert.tubus.client.entity.models.Product;
import ru.wert.tubus.client.entity.models.Message;
import ru.wert.tubus.client.entity.serviceQUICK.ProductQuickService;
import ru.wert.tubus.client.retrofit.GsonConfiguration;

import static ru.wert.tubus.chogori.statics.UtilStaticNodes.CH_TAB_PANE;

/**

 Класс-обработчик сообщений о продуктах (изделиях), получаемых через чат.

 Обеспечивает синхронизацию данных между клиентами в реальном времени.
 */
@Slf4j
public class ProductMessageHandler {

    /**

     Обрабатывает входящее сообщение в зависимости от его типа

     @param message Входящее сообщение

     @param type Тип сообщения (добавление/изменение/удаление)

     @param str StringBuilder для формирования ответного сообщения
     */
    public static void handle(Message message, Message.MessageType type, StringBuilder str) {
        Gson gson = GsonConfiguration.createGson();
        Product product = gson.fromJson(message.getText(), Product.class);

        switch (type) {
            case ADD_PRODUCT:
                handleAddProduct(product, str);
                break;
            case UPDATE_PRODUCT:
                handleUpdateProduct(product, str);
                break;
            case DELETE_PRODUCT:
                handleDeleteProduct(product, str);
                break;
            default:
                log.warn("Получен неизвестный тип сообщения для продукта: {}", type);
                break;
        }
    }

    /**

     Обрабатывает добавление нового продукта

     @param product Добавляемый продукт

     @param str StringBuilder для формирования сообщения
     */
    private static void handleAddProduct(Product product, StringBuilder str) {
        str.append("Добавлено новое изделие: ").append(product.toUsefulString());

// Добавляем в кеш, если его там нет
        if (!ProductQuickService.LOADED_PRODUCTS.contains(product)) {
            ProductQuickService.LOADED_PRODUCTS.add(product);
        }

        updateDraftsEditorTab();
    }

    /**

     Обрабатывает обновление существующего продукта

     @param product Обновляемый продукт

     @param str StringBuilder для формирования сообщения
     */
    private static void handleUpdateProduct(Product product, StringBuilder str) {
        str.append("Обновлено изделие: ").append(product.toUsefulString());

        // Обновляем данные в кеше
        ProductQuickService.LOADED_PRODUCTS.remove(product);
        ProductQuickService.LOADED_PRODUCTS.add(product);

        updateDraftsEditorTab();
    }

    /**

     Обрабатывает удаление продукта

     @param product Удаляемый продукт

     @param str StringBuilder для формирования сообщения
     */
    private static void handleDeleteProduct(Product product, StringBuilder str) {
        str.append("Удалено изделие: ").append(product.toUsefulString());

        // Удаляем из кеша
        ProductQuickService.LOADED_PRODUCTS.remove(product);

        updateDraftsEditorTab();
    }

    /**

     Обновляет все открытые вкладки редактора продуктов
     */
    private static void updateDraftsEditorTab() {
        for(Tab tab: CH_TAB_PANE.getTabs()){
            if(((AppTab)tab).getTabController() instanceof DraftsEditorController){
                ((DraftsEditorController)((AppTab)tab).getTabController()).updateTab();
            }
        }
    }
}
