package mames1.net.mamesosu;

import mames1.net.mamesosu.object.Bot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;

import java.util.Map;

public class Main {

    public static Bot bot;
    public static Map<Member, JDA> botMemberMap;

    public static void main(String[] args) {
        bot = new Bot();
        bot.startBots();
    }
}
