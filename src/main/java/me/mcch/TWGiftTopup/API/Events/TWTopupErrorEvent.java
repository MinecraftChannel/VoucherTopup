package me.mcch.twgifttopup.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TWTopupErrorEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Exception exception;

    public TWTopupErrorEvent(Player p, Exception ex) {

    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}