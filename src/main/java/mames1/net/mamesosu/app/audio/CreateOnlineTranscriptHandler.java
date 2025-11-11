package mames1.net.mamesosu.app.audio;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

public class CreateOnlineTranscriptHandler {

    public static String getTextResponse(String apiKey, Path audioFile)
            throws IOException, InterruptedException {

        Objects.requireNonNull(apiKey, "apiKey");
        Objects.requireNonNull(audioFile, "audioFile");

        if (!Files.isRegularFile(audioFile)) {
            throw new IllegalArgumentException("audioFile が存在しません: " + audioFile);
        }

        String boundary = "----JavaBoundary-" + UUID.randomUUID();
        byte[] body = buildMultipartBody(boundary, audioFile);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.elevenlabs.io/v1/speech-to-text"))
                .header("xi-api-key", apiKey)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new IOException("HTTP " + resp.statusCode() + ": " + resp.body());
        }

        JSONObject json = new JSONObject(resp.body());
        return json.optString("text", "");
    }


    private static byte[] buildMultipartBody(String boundary, Path audioFile) throws IOException {
        final String CRLF = "\r\n";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        // model_id (text part)
        bos.write(("--" + boundary + CRLF).getBytes(StandardCharsets.UTF_8));
        bos.write(("Content-Disposition: form-data; name=\"model_id\"" + CRLF).getBytes(StandardCharsets.UTF_8));
        bos.write(("Content-Type: text/plain; charset=UTF-8" + CRLF + CRLF).getBytes(StandardCharsets.UTF_8));
        bos.write("scribe_v1".getBytes(StandardCharsets.UTF_8));
        bos.write(CRLF.getBytes(StandardCharsets.UTF_8));

        // file (binary part)
        String filename = audioFile.getFileName().toString();
        String contentType = Files.probeContentType(audioFile);
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        bos.write(("--" + boundary + CRLF).getBytes(StandardCharsets.UTF_8));
        bos.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"" + CRLF)
                .getBytes(StandardCharsets.UTF_8));
        bos.write(("Content-Type: " + contentType + CRLF + CRLF).getBytes(StandardCharsets.UTF_8));
        bos.write(Files.readAllBytes(audioFile));
        bos.write(CRLF.getBytes(StandardCharsets.UTF_8));

        // end boundary
        bos.write(("--" + boundary + "--" + CRLF).getBytes(StandardCharsets.UTF_8));

        return bos.toByteArray();
    }
}
