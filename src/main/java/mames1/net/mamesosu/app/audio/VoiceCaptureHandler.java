package mames1.net.mamesosu.app.audio;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.User;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VoiceCaptureHandler implements AudioReceiveHandler {

    private static final int SILENCE_THRESHOLD = 4500; // 音量がこれ以下なら無音扱い
    private static final int SILENCE_LIMIT = 10; // 無音が20フレーム続いたら終了

    private static class UserSession {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int silenceFrames = 0;
        boolean active = true;
    }

    private final Map<Long, UserSession> sessions = new HashMap<>();

    @Override
    public boolean canReceiveUser() {
        return true;
    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {

        if(userAudio.getUser().isBot()) {
            return;
        }

        long userId = userAudio.getUser().getIdLong();
        String username = userAudio.getUser().getName();
        User user = Main.bot.getMainBot().getUserById(userId);
        byte[] data = userAudio.getAudioData(1.0);

        UserSession session = sessions.computeIfAbsent(userId, id -> new UserSession());

        // 音声をバッファに追加
        session.buffer.write(data, 0, data.length);

        // 音量を計算
        int volume = calculateRMS(data);
        if (volume < SILENCE_THRESHOLD) {
            session.silenceFrames++;
        } else {
            session.silenceFrames = 0;
        }

        // 一定時間無音なら終了
        if (session.silenceFrames >= SILENCE_LIMIT && session.active) {
            File file = saveUserAudio(userId, username, session.buffer.toByteArray());
            String key = Main.bot.getApiKey();
            sessions.remove(userId);

            try {
                if(Main.bot.isLocalTranscript()) {
                    GenerateTextHandler.generate(file, user);
                } else {
                    GenerateTextHandler.generate(file, user, key);
                }
            } catch (Exception e) {
                AppLogger.log("音声の文字起こし中にエラーが発生しました: " + e.getMessage(), LogLevel.ERROR);
            } finally {
                if (file != null && file.exists()) {
                    if (file.delete()) {
                        AppLogger.log("録音した音声ファイルを削除しました: " + file.getAbsolutePath(), LogLevel.INFO);
                    } else {
                        AppLogger.log("録音した音声ファイルの削除に失敗しました: " + file.getAbsolutePath(), LogLevel.ERROR);
                    }
                }
            }
        }
    }

    private int calculateRMS(byte[] data) {
        long sum = 0;
        for (int i = 0; i < data.length; i += 2) {
            short sample = (short) ((data[i + 1] << 8) | (data[i] & 0xFF));
            sum += sample * sample;
        }
        double mean = sum / (data.length / 2.0);
        return (int) Math.sqrt(mean);
    }

    private File saveUserAudio(long userId, String username, byte[] audioBytes) {
        try {
            AudioFormat format = AudioReceiveHandler.OUTPUT_FORMAT;
            AudioInputStream ais = new AudioInputStream(
                    new ByteArrayInputStream(audioBytes),
                    format,
                    audioBytes.length / format.getFrameSize()
            );

            String filename = "record_" + username + "_" + userId + "_" + System.currentTimeMillis() + ".wav";
            File file = new File(filename);

            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
            AppLogger.log("音声ファイルを保存しました: " + file.getAbsolutePath(), LogLevel.INFO);

            return file;
        } catch (Exception e) {
            AppLogger.log("音声保存中にエラーが発生しました: " + e.getMessage(), LogLevel.ERROR);

            return null;
        }
    }
}