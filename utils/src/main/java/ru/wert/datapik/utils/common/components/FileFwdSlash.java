package ru.wert.datapik.utils.common.components;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FileFwdSlash extends File {

    public FileFwdSlash(@NotNull String pathname) {
        super(pathname);
    }

    public String toStrong(){
        return super.toString().replace("\\", "/");
    }
}
