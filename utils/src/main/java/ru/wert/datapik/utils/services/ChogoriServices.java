package ru.wert.datapik.utils.services;


import ru.wert.datapik.client.entity.serviceQUICK.*;
import ru.wert.datapik.client.entity.serviceREST.*;
import ru.wert.datapik.client.entity.service_interfaces.*;

public class ChogoriServices {

    public static IFilesService CH_FILES;

    public static IChatGroupService CH_CHAT_GROUPS;
    public static IChatMessageService CH_CHAT_MESSAGES;
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

    public static void initServices(){
        ChogoriServices.CH_FILES = FilesService.getInstance();

        ChogoriServices.CH_CHAT_GROUPS = ChatGroupService.getInstance();
        ChogoriServices.CH_CHAT_MESSAGES = ChatMessageService.getInstance();
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

    public static void initQuickServices(){
        ChogoriServices.CH_QUICK_FOLDERS = FolderQuickService.getInstance();
        ChogoriServices.CH_QUICK_PRODUCTS = ProductQuickService.getInstance();
        ChogoriServices.CH_QUICK_DRAFTS = DraftQuickService.getInstance();
        ChogoriServices.CH_QUICK_PREFIXES = PrefixQuickService.getInstance();
        ChogoriServices.CH_QUICK_DETAILS = DetailQuickService.getInstance();
        ChogoriServices.CH_QUICK_ANY_PARTS = AnyPartQuickService.getInstance();
        ChogoriServices.CH_QUICK_PASSPORTS = PassportQuickService.getInstance();
        ChogoriServices.CH_QUICK_MATERIALS = MaterialQuickService.getInstance();
    }


}
