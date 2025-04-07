package ru.wert.tubus.chogori.application.services;

import lombok.Getter;
import lombok.Setter;
import ru.wert.tubus.client.entity.models.*;

import java.util.List;

@Getter
@Setter
public class BatchResponse {
    private List<Remark> remarks;
    private List<Draft> drafts;
    private List<Product> products;
    private List<Folder> folders;
    private List<Passport> passports;
    private List<AnyPart> anyParts;
    private List<Prefix> prefixes;
}
