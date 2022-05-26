package com.momo.deathban.helpers;

import com.momo.deathban.DeathBan;
import com.momo.deathban.config.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class MessageParser {

    public static String deathReasonMessage(ServerPlayer deadPlayer, DamageSource source) {
        String deathMessage = source.getLocalizedDeathMessage(deadPlayer).getString();
        return deathMessage
                .replaceFirst(deadPlayer.getName().getString(), "You")
                .replaceFirst("was", "were");
    }

    public static Component banMessage(String reason, String expire) {

        return new TranslatableComponent(
                """
                §4§lYou died!§r
                Cause of death: §e{0}§r
                Ban expires in: §e{1}§r
                """,
                reason, String.valueOf(expire)
        );
    }

    public static Component firstTimeMessage(ServerPlayer joinedPlayer) {
        return new TranslatableComponent(
                "[{0}] §bWelcome {1}! This server is currently running §4{0}§r§b. Upon death, you will be banned for §6{2}§r§b.",
                DeathBan.MOD_NAME, joinedPlayer.getName().getString(), getBanTimeFromConfig());
    }

    public static String getTimeRemaining(LocalDateTime currentDate, LocalDateTime expireDate) {
        long days = currentDate.until(expireDate, ChronoUnit.DAYS);
        currentDate = currentDate.plusDays(days);
        long hours = currentDate.until(expireDate, ChronoUnit.HOURS);
        currentDate = currentDate.plusHours(hours);
        long minutes = currentDate.until(expireDate, ChronoUnit.MINUTES);
        currentDate = currentDate.plusMinutes(minutes);
        long seconds = currentDate.until(expireDate, ChronoUnit.SECONDS);

        Long[] concatenatedDate = {seconds, minutes, hours, days};
        String[] timeUnits = {" second(s)", " minute(s), ", " hour(s), ", " day(s), "};

        StringBuilder toReturn = new StringBuilder();
        for (int i = 0; i < concatenatedDate.length; i++) {
            Long time = concatenatedDate[i];
            if (i != 0 && time == 0) {
                break;
            }
            toReturn.insert(0, timeUnits[i]);
            toReturn.insert(0, concatenatedDate[i]);
        }
        return String.valueOf(toReturn);
    }

    public static String getBanTimeFromConfig() {
        long weeks = Config.weekTime.get();
        long days = Config.dayTime.get();
        long hours = Config.hourTime.get();
        long minutes = Config.minuteTime.get();

        Long[] concatenatedDate = {minutes, hours, days, weeks};
        String[] timeUnits = {" minute(s)", " hour(s)", " day(s)", " week(s)"};

        boolean firstPassed = false;

        StringBuilder toReturn = new StringBuilder();
        for (int i = 0; i < concatenatedDate.length; i++) {
            Long time = concatenatedDate[i];
            if (time == 0) {
                continue;
            }
            if (firstPassed) {
                toReturn.insert(0, ", ");
            }
            toReturn.insert(0, timeUnits[i]);
            toReturn.insert(0, concatenatedDate[i]);

            firstPassed = true;
        }

        return String.valueOf(toReturn);
    }
}
