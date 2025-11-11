package mames1.net.mamesosu.app.audio;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.app.translate.GetTranslateTextHandler;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.utils.audio.PlayerManager;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public abstract class GenerateTextHandler {

    public static void generate(File file, User user) {
        generate(file, user, "");
    }

    public static void generate(File file, User user, String key) {
        String text;
        String lang = Main.botMemberMap.get(user).getLang();

        try {

            if(key.isEmpty()) {
                text = CreateLocalTranscriptHandler.getTextResponse(Objects.requireNonNull(file).toPath());
            } else {
                text = CreateOnlineTranscriptHandler.getTextResponse(key, Objects.requireNonNull(file).toPath());
            }

            AppLogger.log("音声の文字起こしが完了しました (ネイティブ): " + text, LogLevel.INFO);

            text = GetTranslateTextHandler.getText(text, lang);

            AppLogger.log("翻訳が完了しました (" + lang + "): " + text, LogLevel.INFO);

            Path generatedVoice = GenerateTTSHandler.synthesizeViaLocalTts(
                    text,
                    lang
            );

            JDA readBot = Main.botMemberMap.get(user).getJda();
            Guild guild = readBot.getGuildById(Main.botMemberMap.get(user).getGuildId());

            PlayerManager.getManager().loadAndPlay(guild, generatedVoice.toString());

            AppLogger.log("音声の再生が開始されました: " + generatedVoice.toString(), LogLevel.INFO);
        } catch (Exception e) {
            AppLogger.log("音声の文字起こし中にエラーが発生しました: " + e.getMessage(), LogLevel.ERROR);
        }
    }
}
