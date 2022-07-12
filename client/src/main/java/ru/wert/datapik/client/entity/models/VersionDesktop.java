package ru.wert.datapik.client.entity.models;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.wert.datapik.client.interfaces.Item;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"}, callSuper = false)
public class VersionDesktop extends _BaseEntity implements Item {

    private String data;
    private String name;
    private String path;
    private String note;

    @Override
    public String toUsefulString() {
        return name;
    }

}
