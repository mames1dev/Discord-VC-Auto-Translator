package mames1.net.mamesosu.app.translate;

import mames1.net.mamesosu.Main;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public abstract class GetTranslateTextHandler {

    private final static String translateUrl = "https://api-free.deepl.com/v2/translate";

    public static String getText(String text, String lang) throws Exception {

        String apiKey = Main.bot.getDeeplKey();
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

        // リクエストボディの作成
        String requestBody = "text=" + encodedText + "&target_lang=" + lang;

        // Httpクライアントの作成
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(translateUrl))
                .header("Authorization", "DeepL-Auth-Key " + apiKey)
                .header("User-Agent", "YourApp/1.2.3")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        // 翻訳語のデータをorg.jsonのAPIよりJsonに変換
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONArray translations = jsonResponse.getJSONArray("translations");
        JSONObject translation = translations.getJSONObject(0);

        //Jsonのtextより翻訳語のデータの取り出し
        return translation.getString("text");
    }
}
