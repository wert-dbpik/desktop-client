package ru.wert.tubus.chogori.application.services;


import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import ru.wert.tubus.client.entity.serviceQUICK.*;
import ru.wert.tubus.client.entity.serviceREST.*;
import ru.wert.tubus.client.entity.service_interfaces.*;
import ru.wert.tubus.winform.warnings.Warning1;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class ChogoriServices {

    public static IPicService CH_PICS;
    public static IRemarkService CH_REMARKS;

    public static IFilesService CH_FILES;

    public static IRoomService CH_ROOMS;
    public static IMessageService CH_MESSAGES;
    public static IChatMessageStatusService CH_CHAT_MESSAGE_STATUSES;

    public static IAppSettingsService CH_SETTINGS;
    public static IUserService CH_USERS;
    public static IAppLogService CH_LOGS;
    public static IUserGroupService CH_USER_GROUPS;
    public static IProductGroupService CH_PRODUCT_GROUPS;
    public static IProductService CH_PRODUCTS;
    public static IDraftService CH_DRAFTS;
    public static IPrefixService CH_PREFIXES;
    public static IFolderService CH_FOLDERS;
    public static IPassportService CH_PASSPORTS;
    public static IDetailService CH_DETAILS;
    public static IAnyPartService CH_ANY_PARTS;
    public static IAnyPartGroupService CH_ANY_PART_GROUPS;
    public static IMaterialService CH_MATERIALS;
    public static IMaterialGroupService CH_MATERIAL_GROUPS;
    public static IMatTypeService CH_MAT_TYPES;
    public static IDensityService CH_DENSITIES;
    public static IVersionDesktopService CH_VERSIONS_DESKTOP;
    public static IVersionAndroidService CH_VERSIONS_ANDROID;
    public static IVersionServerService CH_VERSIONS_SERVER;

    public static FolderQuickService CH_QUICK_FOLDERS;
    public static ProductQuickService CH_QUICK_PRODUCTS;
    public static DraftQuickService CH_QUICK_DRAFTS;
    public static PrefixQuickService CH_QUICK_PREFIXES;
    public static DetailQuickService CH_QUICK_DETAILS;
    public static AnyPartQuickService CH_QUICK_ANY_PARTS;
    public static PassportQuickService CH_QUICK_PASSPORTS;
    public static MaterialQuickService CH_QUICK_MATERIALS;
    public static UserQuickService CH_QUICK_USERS;

    public static void initServices(){

        ChogoriServices.CH_PICS = PicService.getInstance();
        ChogoriServices.CH_REMARKS = RemarkService.getInstance();

        ChogoriServices.CH_FILES = FilesService.getInstance();

        ChogoriServices.CH_ROOMS = RoomService.getInstance();
        ChogoriServices.CH_MESSAGES = MessageService.getInstance();
        ChogoriServices.CH_CHAT_MESSAGE_STATUSES = ChatMessageStatusService.getInstance();

        ChogoriServices.CH_USERS = UserService.getInstance();
        ChogoriServices.CH_LOGS = AppLogService.getInstance();

        ChogoriServices.CH_USER_GROUPS = UserGroupService.getInstance();
        ChogoriServices.CH_SETTINGS = AppSettingsService.getInstance();
        ChogoriServices.CH_PRODUCT_GROUPS = ProductGroupService.getInstance();
        ChogoriServices.CH_PRODUCTS = ProductService.getInstance();
        ChogoriServices.CH_DRAFTS = DraftService.getInstance();
        ChogoriServices.CH_PREFIXES = PrefixService.getInstance();
        ChogoriServices.CH_FOLDERS = FolderService.getInstance();
        ChogoriServices.CH_PASSPORTS = PassportService.getInstance();
        ChogoriServices.CH_DETAILS = DetailService.getInstance();
        ChogoriServices.CH_ANY_PARTS = AnyPartService.getInstance();
        ChogoriServices.CH_ANY_PART_GROUPS = AnyPartGroupService.getInstance();
        ChogoriServices.CH_MATERIALS = MaterialService.getInstance();
        ChogoriServices.CH_MATERIAL_GROUPS = MaterialGroupService.getInstance();
        ChogoriServices.CH_MAT_TYPES = MatTypeService.getInstance();
        ChogoriServices.CH_DENSITIES = DensityService.getInstance();
        ChogoriServices.CH_VERSIONS_DESKTOP = VersionDesktopService.getInstance();
        ChogoriServices.CH_VERSIONS_ANDROID = VersionAndroidService.getInstance();
        ChogoriServices.CH_VERSIONS_SERVER = VersionServerService.getInstance();
    }

    public static void initQuickServicesWithCache() {
        // 1. Пытаемся загрузить из кэша (если есть)
        BatchResponse cached = LocalCacheManager.loadFromCache("initial_data", BatchResponse.class);
        if(cached != null) {
            initFromBatch(cached);
            log.info("Initial data loaded from cache");
        } else {
            log.info("No cached data available");
        }

        // 2. Загружаем свежие данные с сервера в фоне
        Task<Void> updateTask = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    log.info("Starting background data update from server...");
                    BatchResponse fresh = BatchService.loadInitialData();

                    LocalCacheManager.saveToCache("initial_data", fresh);
                    log.info("Data successfully updated from server");

                    Platform.runLater(() -> {
                        initFromBatch(fresh);
                        log.info("UI updated with fresh data");
                    });

                } catch (IOException e) {
                    log.error("Server data update failed: {}", e.getMessage());
                    // Можно показать notification пользователю
                    Platform.runLater(() ->
                            showErrorNotification("Не удалось обновить данные с сервера"));
                }
                return null;
            }
        };

        new Thread(updateTask).start();
    }

    private static void showErrorNotification(String message) {
        log.error("Ошибка загрузки данных с сервера в КЭШ! - {}", message);
        Warning1.create("ВНИМАНИЕ!",
                "Произошел сбой при загрузке данных с сервера в кэш",
                "Возможно сервер не доступен. Обратитесь к администратору.");
    }

    private static void initFromBatch(BatchResponse batch) {
        CH_QUICK_FOLDERS = FolderQuickService.getInstance();
        if (batch.getFolders() != null) {
            FolderQuickService.LOADED_FOLDERS = new ArrayList<>(batch.getFolders());
        }

        CH_QUICK_PRODUCTS = ProductQuickService.getInstance();
        if (batch.getProducts() != null) {
            ProductQuickService.LOADED_PRODUCTS = new ArrayList<>(batch.getProducts());
        }

        CH_QUICK_DRAFTS = DraftQuickService.getInstance();
        if (batch.getDrafts() != null) {
            DraftQuickService.LOADED_DRAFTS = new ArrayList<>(batch.getDrafts());
        }

        CH_QUICK_PREFIXES = PrefixQuickService.getInstance();
        if (batch.getPrefixes() != null) {
            PrefixQuickService.LOADED_PREFIXES = new ArrayList<>(batch.getPrefixes());
        }

        CH_QUICK_ANY_PARTS = AnyPartQuickService.getInstance();
        if (batch.getAnyParts() != null) {
            AnyPartQuickService.LOADED_ANY_PARTS = new ArrayList<>(batch.getAnyParts());
        }

        CH_QUICK_PASSPORTS = PassportQuickService.getInstance();
        if (batch.getPassports() != null) {
            PassportQuickService.LOADED_PASSPORTS = new ArrayList<>(batch.getPassports());
        }

        if (batch.getUsers() != null) {
            UserQuickService.LOADED_USERS = new ArrayList<>(batch.getUsers());
        }
    }


    public static void initQuickServices(){
        ChogoriServices.CH_QUICK_USERS = UserQuickService.getInstance();
        ChogoriServices.CH_QUICK_FOLDERS = FolderQuickService.getInstance();
        ChogoriServices.CH_QUICK_PRODUCTS = ProductQuickService.getInstance();
        ChogoriServices.CH_QUICK_DRAFTS = DraftQuickService.getInstance();
        ChogoriServices.CH_QUICK_PREFIXES = PrefixQuickService.getInstance();
//        ChogoriServices.CH_QUICK_DETAILS = DetailQuickService.getInstance();
        ChogoriServices.CH_QUICK_ANY_PARTS = AnyPartQuickService.getInstance();
        ChogoriServices.CH_QUICK_PASSPORTS = PassportQuickService.getInstance();
//        ChogoriServices.CH_QUICK_MATERIALS = MaterialQuickService.getInstance();
    }


}
