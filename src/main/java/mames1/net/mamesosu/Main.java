package mames1.net.mamesosu;

import mames1.net.mamesosu.object.Bot;
import mames1.net.mamesosu.object.Translate;
import net.dv8tion.jda.api.entities.User;

import java.util.Map;

public class Main {

    public static Bot bot;
    public static Map<User, Translate> botMemberMap;

    public static void main(String[] args) {
        bot = new Bot();
        bot.startBots();
    }
}
