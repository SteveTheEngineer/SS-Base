package me.ste.stevesseries.base;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ChatInputManager {
    private ChatInputManager() {}

    private final static Map<UUID, CompletableFuture<String>> playerMap = new HashMap<>();

    /**
     *
     * @param player
     * @return
     */
    public static CompletableFuture<String> input(Player player) {
        CompletableFuture<String> future = new CompletableFuture<>();
        ChatInputManager.playerMap.put(player.getUniqueId(), future);
        return future;
    }

    public static void cancel(Player player) {
        ChatInputManager.playerMap.remove(player.getUniqueId());
    }

    public static boolean isAcceptingInput(Player player) {
        return ChatInputManager.playerMap.containsKey(player.getUniqueId());
    }

    @Deprecated
    public static Map<UUID, CompletableFuture<String>> getPlayerMap() {
        return ChatInputManager.playerMap;
    }
}