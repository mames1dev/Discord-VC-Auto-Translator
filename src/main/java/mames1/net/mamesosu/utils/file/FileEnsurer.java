package mames1.net.mamesosu.utils.file;

import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.utils.log.AppLogger;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class FileEnsurer {

    public static boolean ensureFile (Path file) {

        try {
            Path parent = file.getParent();

            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(file)) {
                Files.createFile(file);
                AppLogger.log("新規のログファイル: " + file + "を作成しました.", LogLevel.INFO);
            }
        } catch (Exception e) {
            AppLogger.log("ファイル作成中にエラーが発生しました: " + e.getMessage(), LogLevel.ERROR);
            return false;
        }

        return true;
    }
}
