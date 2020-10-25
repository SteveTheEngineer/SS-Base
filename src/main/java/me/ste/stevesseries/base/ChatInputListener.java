package me.ste.stevesseries.base;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatInputListener implements Listener {
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(ChatInputManager.getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {
            ChatInputManager.getPlayerMap().get(event.getPlayer().getUniqueId()).complete(event.getMessage());
            ChatInputManager.getPlayerMap().remove(event.getPlayer().getUniqueId());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if(ChatInputManager.getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {
            ChatInputManager.getPlayerMap().get(event.getPlayer().getUniqueId()).complete(event.getMessage());
            ChatInputManager.getPlayerMap().remove(event.getPlayer().getUniqueId());
            event.setCancelled(true);
        }
    }
}