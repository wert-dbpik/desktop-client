package ru.wert.tubus.chogori;

import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.protocol.User;
import ru.wert.tubus.client.retrofit.AppProperties;

import java.util.HashMap;

import static ru.wert.tubus.chogori.setteings.ChogoriSettings.CH_CURRENT_USER;
import static ru.wert.tubus.winform.statics.WinformStatic.CURRENT_PROJECT_VERSION;
import static ru.wert.tubus.winform.statics.WinformStatic.PROGRAM_NAME;

public class SentryConfig {

    public static void initialize() {
        String ip = AppProperties.getInstance().getIpAddress();
        String port = AppProperties.getInstance().getPort();
        String dsn = "http://dummy-key@" + ip + ":" + port + "/crash-reports/1";

        Sentry.init(options -> {
            options.setDsn(dsn);
            options.setEnableUncaughtExceptionHandler(true);
            options.setBeforeSend(SentryConfig::beforeSend);
            options.setRelease(PROGRAM_NAME + "-" + CURRENT_PROJECT_VERSION);
        });
    }

    private static SentryEvent beforeSend(SentryEvent event, Object hint) {
        // 1. Устанавливаем пользователя
        User sentryUser = new User();
        sentryUser.setUsername(CH_CURRENT_USER.getName());
        event.setUser(sentryUser);

        // 2. Добавляем теги (аналоги ACRA-полей)
        event.setTag("device", "DESKTOP");
        event.setTag("version", CURRENT_PROJECT_VERSION);

        // 3. Кастомные данные (для CrashReportController)
        event.setExtra("custom_data", new HashMap<String, String>() {{
            put("crash_user_name", sentryUser.getUsername());
            put("crash_device", "DESKTOP");
        }});

        return event;
    }
}
