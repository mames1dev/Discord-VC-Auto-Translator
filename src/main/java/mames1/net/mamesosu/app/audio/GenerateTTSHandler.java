package mames1.net.mamesosu.app.audio;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class GenerateTTSHandler {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String LOCAL_TTS_ENDPOINT = "http://localhost:3031/tts";

    /**
     * ローカル FastAPI TTS サーバへ text/lang を送り WAV を生成し取得
     * @param text 合成するテキスト（必須）
     * @param lang 言語コード（例: ja, en）
     */
    public static Path synthesizeViaLocalTts(String text,
                                             String lang) throws IOException, InterruptedException {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(lang, "lang");

        String json = """
                {
                  "text": "%s",
                  "lang": "%s"
                }
                """.formatted(escapeJson(text), escapeJson(lang));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOCAL_TTS_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<byte[]> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() / 100 != 2) {
            throw new IOException("HTTP " + response.statusCode() + " " + new String(response.body(), StandardCharsets.UTF_8));
        }

        String body = new String(response.body(), StandardCharsets.UTF_8);
        String pathValue = extractPathField(body);
        if (pathValue == null || pathValue.isBlank()) {
            throw new IOException("`path` field not found in response: " + body);
        }

        Path serverFile = Path.of(pathValue);

        // サーバと同一ホストで直接ファイル参照できる前提
        if (!Files.exists(serverFile)) {
            throw new IOException("Generated file not found: " + serverFile);
        }

        return serverFile;
    }

    // 最低限の JSON エスケープ
    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // {"path":"..."} から path を抽出（単純実装）
    private static String extractPathField(String json) {
        int idx = json.indexOf("\"path\"");
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx);
        if (colon < 0) return null;
        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) return null;
        int secondQuote = json.indexOf('"', firstQuote + 1);
        if (secondQuote < 0) return null;
        return json.substring(firstQuote + 1, secondQuote);
    }
}