package mames1.net.mamesosu.listener;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.app.audio.VoiceCaptureHandler;
import mames1.net.mamesosu.constants.LanguageCodes;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.object.Bot;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Objects;


// / "join" スラッシュコマンドが実行されたときにボイスチャンネルに参加するリスナー (Bot1とBot2を参加させる)
public class VoiceJoinRequestListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {

        if(!e.getName().equals("join")) {
            return;
        }

        Guild guild = e.getGuild();
        Bot bot = Main.bot;
        JDA transBot1 = bot.getTransBot1();
        JDA transBot2 = bot.getTransBot2();

        // コマンド実行者がボイスチャンネルに参加しているか確認
        if(Objects.requireNonNull(e.getMember()).getVoiceState() == null || !e.getMember().getVoiceState().inAudioChannel()) {
            e.reply("ボイスチャンネルに参加をしてから実行してください.").setEphemeral(true).queue();
            return;
        }

        AudioChannel audioChannel = e.getMember().getVoiceState().getChannel();

        // オプションで指定された1人目のメンバーがボイスチャンネルに参加しているか確認
        Member firstMember = Objects.requireNonNull(e.getOption("1人目のメンバー")).getAsMember();
        if (firstMember != null) {
            if (firstMember.getVoiceState() == null || !firstMember.getVoiceState().inAudioChannel()) {
                e.reply("1人目のメンバーがボイスチャンネルに参加していません.").setEphemeral(true).queue();
                return;
            }
            audioChannel = firstMember.getVoiceState().getChannel();
        }

        // 言語コードの妥当性を確認
        if (!LanguageCodes.getLanguageCodes().contains(Objects.requireNonNull(e.getOption("language1")).getAsString())) {
            e.reply("1人目のメンバーの言語コードが無効です.").setEphemeral(true).queue();
            return;
        }

        // オプションで指定された2人目のメンバーがボイスチャンネルに参加しているか確認
        Member secondMember = Objects.requireNonNull(e.getOption("2人目のメンバー")).getAsMember();
        if (secondMember != null) {
            if (secondMember.getVoiceState() == null || !secondMember.getVoiceState().inAudioChannel()) {
                e.reply("2人目のメンバーがボイスチャンネルに参加していません.").setEphemeral(true).queue();
                return;
            }

            // 1人目と2人目のメンバーが同じボイスチャンネルに参加しているか確認
            if (Objects.requireNonNull(firstMember).getVoiceState().getChannel() != secondMember.getVoiceState().getChannel()) {
                e.reply("1人目と2人目のメンバーが同じボイスチャンネルに参加していません.").setEphemeral(true).queue();
                return;
            }

            audioChannel = secondMember.getVoiceState().getChannel();
        }

        // 言語コードの妥当性を確認
        if (!LanguageCodes.getLanguageCodes().contains(Objects.requireNonNull(e.getOption("language2")).getAsString())) {
            e.reply("2人目のメンバーの言語コードが無効です.").setEphemeral(true).queue();
            return;
        }

        // チェックOK: MainBotとBot1とBot2をボイスチャンネルに参加させる

        AudioManager audioManager = Objects.requireNonNull(guild).getAudioManager();
        audioManager.setReceivingHandler(new VoiceCaptureHandler());
        audioManager.openAudioConnection(Objects.requireNonNull(audioChannel));

        for(JDA jda : Main.bot.getAllSpeakBots()) {
            Guild g = jda.getGuildById(Objects.requireNonNull(guild).getId());
            if (g != null) {
                AudioManager am = g.getAudioManager();
                if (!am.isConnected()) {
                    am.openAudioConnection(Objects.requireNonNull(audioChannel));
                }
            }

        }

        Main.botMemberMap.put(firstMember, transBot1);
        AppLogger.log( transBot1.getSelfUser().getName() + " の発言 を " + Objects.requireNonNull(firstMember).getEffectiveName() + " に割り当てました.", LogLevel.INFO);

        Main.botMemberMap.put(secondMember, transBot2);
        AppLogger.log( transBot2.getSelfUser().getName() + " の発言 を " + Objects.requireNonNull(secondMember).getEffectiveName() + " に割り当てました.", LogLevel.INFO);

        e.reply("ボイスチャンネルに参加しました.").setEphemeral(true).queue();

        AppLogger.log("ボイスチャンネルに接続しました. 自動翻訳を開始します.", LogLevel.INFO);
    }
}
