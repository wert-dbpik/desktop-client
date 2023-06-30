package ru.wert.tubus.client.utils;

import ru.wert.tubus.client.entity.serviceREST.*;

public class ConfigBD {


    public ConfigBD() {

        Props.getInstance().getParams();
        setServices();
    }

    private void setServices(){
        UserService.getInstance();
        UserGroupService.getInstance();
        CoatService.getInstance();
        PrefixService.getInstance();
        ProcessService.getInstance();
        FolderService.getInstance();
        MatTypeService.getInstance();
        MaterialService.getInstance();
        ProductService.getInstance();
        ProductGroupService.getInstance();
        MaterialGroupService.getInstance();
        AnyPartGroupService.getInstance();
        AnyPartService.getInstance();
        DensityService.getInstance();
        DetailService.getInstance();
        AssembleService.getInstance();
        AsmItemService.getInstance();
        DraftService.getInstance();
    }
}
