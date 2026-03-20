package io.github.FMG9167.ywna.effects;

import io.github.FMG9167.ywna.YWNAMod;
import io.github.FMG9167.ywna.profile.PlayerProfile;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Random;

public class ChatCorruptionEvent {
    private static final Random RANDOM = new Random();

    private static final char[] CORRUPTED_CHARS = {
            '▒', '░', '█', '▓', '�', '#', '@', '%', '&', '?', '!', '~'
    };

    private static final String[] ENTITY_MESSAGES = {
            "i s33 y0u",
            "y0u w3r3 n3v3r al0n3",
            "i kn0w wh3r3 y0u sl33p",
            "d0 y0u h3ar m3",
            "w4tch1ng",
            "y0u f33l 1t t00",
            "i h4v3 4lw4ys b33n h3r3",
            "turn 4r0und",
            "y0ur h4nds ar3 n0t y0ur 0wn"
    };

    public static void fire(ServerPlayerEntity player, PlayerProfile profile) {
        String playerName = player.getName().getString();
        String message;

        if (profile.fearScore >= 0.30f && RANDOM.nextFloat() < getEntityMessageChance(profile.fearScore)) {
            message = corruptEntityMessage(ENTITY_MESSAGES[RANDOM.nextInt(ENTITY_MESSAGES.length)], profile.fearScore);
        } else {
            message = generateNoise(4 + RANDOM.nextInt(6));
        }

        Text chat = Text.literal("<" + playerName + "> " + message);
        player.sendMessage(chat, false);

        YWNAMod.LOGGER.info("[YWNA] ChatCorruption: sent '{}' to {}", message, playerName);
    }

    private static float getEntityMessageChance(float fearScore) {
        if(fearScore >= 0.70f) return 0.80f;
        if(fearScore >= 0.50f) return 0.50f;
        if(fearScore >= 0.30f) return 0.25f;
        return 0.00f;
    }

    private static String generateNoise(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CORRUPTED_CHARS[RANDOM.nextInt(CORRUPTED_CHARS.length)]);
        }
        return sb.toString();
    }

    private static String corruptEntityMessage(String message, float fearScore) {
        float corruptionAmount = 1f - fearScore;
        StringBuilder sb = new StringBuilder();
        for(char c : message.toCharArray()) {
            if(c == ' ') {
                sb.append(RANDOM.nextFloat() < corruptionAmount * 0.3f ? CORRUPTED_CHARS[RANDOM.nextInt(CORRUPTED_CHARS.length)] : ' ');
            } else if (RANDOM.nextFloat() < corruptionAmount) {
                sb.append(CORRUPTED_CHARS[RANDOM.nextInt(CORRUPTED_CHARS.length)]);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
