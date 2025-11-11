package mames1.net.mamesosu.constants;

import java.util.Arrays;
import java.util.List;

// APIでサポートされている言語コードの定数クラス
public abstract class LanguageCodes {

    public static List<String> getLanguageCodes() {
        return Arrays.asList(
                "en", "ja", "zh", "de", "hi", "fr", "ko", "pt", "it", "es",
                "id", "nl", "tr", "fil", "pl", "sv", "bg", "ro", "ar", "cs",
                "el", "fi", "hr", "ms", "sk", "da", "ta", "uk", "ru", "hu",
                "no", "vi"
        );
    }
}
