package mames1.net.mamesosu;

import mames1.net.mamesosu.object.Bot;

public class Main {

    public static Bot bot;

    public static void main(String[] args) {
        bot = new Bot();
        bot.startBots();
    }
}
