package th.in.mcch.vouchertopup.api.events;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TopupSuccessEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private JsonObject result;

    public TopupSuccessEvent(Player p, JsonObject result) {
        this.player = p;
        this.result = result;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public JsonObject getResult() {
        return result;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}