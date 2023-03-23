package me.ste.stevesseries.base;

import com.google.common.collect.Multimap;
import me.ste.stevesseries.base.api.BaseAPI;
import me.ste.stevesseries.base.api.extensions.PlayerExtensionsKt;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @deprecated For backwards compatibility only.
 */
@Deprecated
public final class ChatInputManager {
    private ChatInputManager() {}

    public static CompletableFuture<String> input(Player player) {
        CompletableFuture<String> future = new CompletableFuture<>();
        PlayerExtensionsKt.acceptChatInput(player, future::complete);
        return future;
    }

    public static void cancel(Player player) {
        PlayerExtensionsKt.cancelChatInput(player);
    }

    public static boolean isAcceptingInput(Player player) {
        return BaseAPI.Companion.getInstance().getChatInputListener(player) != null;
    }

    /**
     * @deprecated always throws an exception
     */
    @Deprecated
    public static Multimap<UUID, CompletableFuture<String>> getPlayerMap() {
        throw new UnsupportedOperationException("Not available on the current version of Base.");
    }
}
