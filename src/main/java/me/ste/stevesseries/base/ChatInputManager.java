package me.ste.stevesseries.base;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Utility class for accepting player chat input
 */
public final class ChatInputManager {
    private ChatInputManager() {}

    private final static Multimap<UUID, CompletableFuture<String>> playerMap = ArrayListMultimap.create();

    /**
     * Accept chat input from the specified player
     * @param player the player
     * @return completable future that will resolve once the player has sent a message
     */
    public static CompletableFuture<String> input(Player player) {
        CompletableFuture<String> future = new CompletableFuture<>();
        ChatInputManager.playerMap.put(player.getUniqueId(), future);
        return future;
    }

    /**
     * Clear all chat input callbacks for the specified player
     * @param player the player
     */
    public static void cancel(Player player) {
        ChatInputManager.playerMap.removeAll(player.getUniqueId());
    }

    /**
     * Check whether are there chat input callbacks for the specified player
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
    public static Multimap<UUID, CompletableFuture<String>> getPlayerMap() {
        return ChatInputManager.playerMap;
    }
}