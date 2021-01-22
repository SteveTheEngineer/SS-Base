package me.ste.stevesseries.base;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.concurrent.CompletableFuture;

public class ChatInputListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(ChatInputManager.getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {
            for(CompletableFuture<String> future : ChatInputManager.getPlayerMap().get(event.getPlayer().getUniqueId())) {
                future.complete(event.getMessage());
            }
            ChatInputManager.getPlayerMap().removeAll(event.getPlayer().getUniqueId());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(ChatInputManager.getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {
            for(CompletableFuture<String> future : ChatInputManager.getPlayerMap().get(event.getPlayer().getUniqueId())) {
                future.complete(event.getMessage());
            }
            ChatInputManager.getPlayerMap().removeAll(event.getPlayer().getUniqueId());
            event.setCancelled(true);
        }
    }
}