package ru.wert.tubus.chogori.pdf;

import java.util.logging.*;

public class ICEpdfLoggingBlocker {
    static {
        disableAllLogging();
    }

    public static void disableAllLogging() {
        try {
            // Получаем корневой логгер
            Logger rootLogger = Logger.getLogger("");

            // Удаляем все хендлеры
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
                handler.close();
            }

            // Устанавливаем уровень OFF для корневого логгера
            rootLogger.setLevel(Level.OFF);

            // Отключаем логирование для всех пакетов ICEpdf
            String[] icepdfPackages = {
                    "org.icepdf",
                    "org.icepdf.core",
                    "org.icepdf.ri",
                    "org.icepdf.viewer",
                    "icepdf"
            };

            for (String pkg : icepdfPackages) {
                Logger logger = Logger.getLogger(pkg);
                logger.setLevel(Level.OFF);
                logger.setUseParentHandlers(false);

                // Удаляем хендлеры у конкретных логгеров
                Handler[] pkgHandlers = logger.getHandlers();
                for (Handler handler : pkgHandlers) {
                    logger.removeHandler(handler);
                    handler.close();
                }
            }

            // Блокируем создание новых хендлеров
            System.setProperty("java.util.logging.config.file", "");
            System.setProperty("java.util.logging.manager", "java.util.logging.LogManager");

        } catch (Exception e) {
            // Игнорируем ошибки при блокировке логирования
        }
    }
}
