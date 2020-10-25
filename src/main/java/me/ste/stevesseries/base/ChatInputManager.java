package me.ste.stevesseries.base;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class for accepting player chat input
 */
public final class ChatInputManager {
    private ChatInputManager() {}

    private final static Map<UUID, CompletableFuture<String>> playerMap = new HashMap<>();

    /**
     * Wait for the specified player to send something in the chat
     * @param player the player
     * @return completable future that will resolve once the player has sent a message
     */
    public static CompletableFuture<String> input(Player player) {
        CompletableFuture<String> future = new CompletableFuture<>();
        ChatInputManager.playerMap.put(player.getUniqueId(), future);
        return future;
    }

    /**
     * Stop waiting for the specified player to send something in the chat
     * @param player the player
     */
    public static void cancel(Player player) {
        ChatInputManager.playerMap.remove(player.getUniqueId());
    }

    /**
     * Check whether a plugin is waiting for the specified player to send something in the chat
     * @param player the player
     * @return true, if a plugin is waiting for the specified  player to send something in the chat
     */
    public static boolean isAcceptingInput(Player player) {
        return ChatInputManager.playerMap.containsKey(player.getUniqueId());
    }

    /**
     * @deprecated for internal use only
     */
    @Deprecated
    public static Map<UUID, CompletableFuture<String>> getPlayerMap() {
        return ChatInputManager.playerMap;
    }
}