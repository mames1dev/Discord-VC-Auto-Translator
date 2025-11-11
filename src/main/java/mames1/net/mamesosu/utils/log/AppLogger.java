package mames1.net.mamesosu.utils.log;

import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.utils.date.Date;

public abstract class AppLogger {

    public static void log(String message, LogLevel level) {

        String log = Date.now() + " [DVAT] [" + level + "] " + message;

        if (level.equals(LogLevel.ERROR) || level.equals(LogLevel.FATAL)) {
            LogSaver.save(log + System.lineSeparator(), level);
        }

        System.out.println(log);
    }
}
