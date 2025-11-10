package mames1.net.mamesosu.app.audio;

import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CreateLocalTranscriptHandler {

    public static String getTextResponse(Path audioFile)
            throws IOException{

        OkHttpClient client = new OkHttpClient();
        String url = "http://localhost:3030/transcribe";

        File file = audioFile.toFile();

        MediaType mediaType = MediaType.parse("audio/wav");
        RequestBody fileBody = RequestBody.create(file, mediaType);

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody) // フィールド名は `file`
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                System.err.println("Request failed: " + response.code());
                System.err.println("Response body: " + body);
            } else {
                System.out.println("Response: " + body);
            }

            // JSON から "text" フィールドを抽出（簡易実装）
            Pattern p = Pattern.compile("\"text\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);
            Matcher m = p.matcher(body);
            if (m.find()) {
                return m.group(1)
                        .replace("\\n", "\n")
                        .replace("\\r", "\r")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
            }
        }

        return "";
    }
}