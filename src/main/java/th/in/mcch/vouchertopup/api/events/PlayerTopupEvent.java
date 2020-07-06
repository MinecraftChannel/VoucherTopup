package th.in.mcch.vouchertopup.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTopupEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Player player;
    private String input;

    public PlayerTopupEvent(Player p, String input) {
        player = p;
        this.input = input;
    }

    public static HandlerList getHandlerList() {
        return handlers;
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

}