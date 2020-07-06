package th.in.mcch.vouchertopup;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Events implements Listener {
    //Command aliases
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        for (String s : VoucherTopup.config.getStringList("general.aliases")) {
            if (e.getMessage().replaceFirst("/", "").toLowerCase().startsWith(s)) {
                Bukkit.dispatchCommand(e.getPlayer(), "vouchertopup" + e.getMessage().replaceFirst(e.getMessage().split(" ")[0], ""));
                e.setCancelled(true);
                return;
            }
        }
    }
}
