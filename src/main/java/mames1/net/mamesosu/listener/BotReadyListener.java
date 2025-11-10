package mames1.net.mamesosu.listener;

import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent e) {

        AppLogger.log(e.getJDA().getSelfUser().getName() + " を起動しました.", LogLevel.INFO);
    }
}
