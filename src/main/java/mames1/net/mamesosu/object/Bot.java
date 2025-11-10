package mames1.net.mamesosu.object;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import mames1.net.mamesosu.listener.BotReadyListener;
import mames1.net.mamesosu.listener.VoiceJoinRequestListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Getter
public class Bot {

    JDA transBot1;
    JDA transBot2;
    JDA mainBot;
    String mainToken;
    String token1;
    String token2;

    public Bot() {
        Dotenv dotenv = Dotenv.configure().load();
        this.mainToken = dotenv.get("MAIN_BOT_TOKEN");
        this.token1 = dotenv.get("BOT_TOKEN_1");
        this.token2 = dotenv.get("BOT_TOKEN_2");
    }

    // 1対1で翻訳を行うBotを起動する
    public void startBots() {

        mainBot = JDABuilder.createDefault(mainToken)
                .setRawEventsEnabled(true)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS
                ).enableCache(
                        CacheFlag.MEMBER_OVERRIDES,
                        CacheFlag.ROLE_TAGS,
                        CacheFlag.EMOJI
                )
                .disableCache(
                        CacheFlag.STICKER,
                        CacheFlag.SCHEDULED_EVENTS
                ).setActivity(
                        Activity.listening("Main Bot")
                ).setMemberCachePolicy(
                        MemberCachePolicy.ALL
                ).setChunkingFilter(
                        ChunkingFilter.ALL
                ).addEventListeners(
                        // Botの準備が完了したら通知するリスナーを追加
                        new BotReadyListener(),
                        new VoiceJoinRequestListener()
                )
                .build();

        transBot1 = JDABuilder.createDefault(token1)
                .setRawEventsEnabled(true)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS
                ).enableCache(
                        CacheFlag.MEMBER_OVERRIDES,
                        CacheFlag.ROLE_TAGS,
                        CacheFlag.EMOJI
                )
                .disableCache(
                        CacheFlag.STICKER,
                        CacheFlag.SCHEDULED_EVENTS
                ).setActivity(
                        Activity.listening("Translate Bot1")
                ).setMemberCachePolicy(
                        MemberCachePolicy.ALL
                ).setChunkingFilter(
                        ChunkingFilter.ALL
                ).addEventListeners(
                        // Botの準備が完了したら通知するリスナーを追加
                        new BotReadyListener()
                )
                .build();

        transBot2 = JDABuilder.createDefault(token2)
                .setRawEventsEnabled(true)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS
                ).enableCache(
                        CacheFlag.MEMBER_OVERRIDES,
                        CacheFlag.ROLE_TAGS,
                        CacheFlag.EMOJI
                )
                .disableCache(
                        CacheFlag.STICKER,
                        CacheFlag.SCHEDULED_EVENTS
                ).setActivity(
                        Activity.listening("Translate Bot2")
                ).setMemberCachePolicy(
                        MemberCachePolicy.ALL
                ).setChunkingFilter(
                        ChunkingFilter.ALL
                ).addEventListeners(
                        // Botの準備が完了したら通知するリスナーを追加
                        new BotReadyListener()
                )
                .build();

        mainBot.updateCommands().queue();
        mainBot.upsertCommand("join", "ボイスチャンネルに接続します").addOption(OptionType.USER, "1人目のメンバー", "1人目のメンバーを指定します", true).addOption(OptionType.USER, "2人目のメンバー", "2人目のメンバーを指定します", true)
                .addOption(OptionType.STRING, "language1", "1人目のメンバーの翻訳先の言語を指定します (例: ja, en)", true)
                .addOption(OptionType.STRING, "language2", "2人目のメンバーの翻訳先の言語を指定します (例: ja, en)", true)
                .queue();

    }
}
