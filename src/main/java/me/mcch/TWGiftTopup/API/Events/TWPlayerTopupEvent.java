package me.mcch.TWGiftTopup.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import org.bukkit.event.Cancellable;

public class TWPlayerTopupEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Player player;
    private String input;
    public TWPlayerTopupEvent(Player p, String input) {
        player = p;
        this.input = input;
    }
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean bool) {
        cancelled = bool;
    }

    public Player getPlayer() {
        return player;
    }

    public String getInput() {
        return input;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

}