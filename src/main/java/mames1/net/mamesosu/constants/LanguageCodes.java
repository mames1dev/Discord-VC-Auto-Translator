package mames1.net.mamesosu.constants;

import java.util.List;

// APIでサポートされている言語コードの定数クラス
public abstract class LanguageCodes {

    public static List<String> getLanguageCodes() {
        return List.of(
                "bg", "ca", "cs", "da", "nl", "en", "fi", "fr", "gl", "de", "el",
                "hi", "id", "it", "ja", "kn", "ms", "ml", "mk", "no", "pl", "pt",
                "ro", "ru", "sr", "sk", "es", "sv", "tr", "uk", "vi", "bn", "be",
                "bs", "yue", "et", "tl", "gu", "hu", "kk", "lv", "lt", "zh", "mr",
                "ne", "or", "fa", "sl", "ta", "te", "af", "ar", "hy", "as", "ast",
                "az", "my", "ceb", "hr", "ka", "ha", "he", "is", "jv", "", "ko",
                "ky", "ln", "mt", "mn", "mi", "oc", "pa", "sd", "sw", "tg", "th",
                "ur", "uz", "cy", "am", "ny", "ff", "lg", "ig", "ga", "km", "ku",
                "lo", "lb", "luo", "nso", "ps", "sn", "so", "umb", "wo", "xh", "zu"
        );
    }
}
