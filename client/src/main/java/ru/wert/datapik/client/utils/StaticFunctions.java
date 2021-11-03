package ru.wert.datapik.client.utils;

public class StaticFunctions {
    /**
     * Метод определяет contentType по расширению файла
     * @param fileName имя файла с расширением
     */
    public static String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        switch(extension.toLowerCase()){
            case "pdf" : return "application/pdf";
            case "png" : return "image/png";
            case "jpg" : return "image/jpeg";
            case "jpeg" : return "image/jpeg";
            case "jpe" : return "image/jpeg";
            default: return "multipart/form-data";
        }
    }
}
