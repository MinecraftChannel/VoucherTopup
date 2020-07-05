package me.mcch;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public final class twgifttopup extends JavaPlugin {
    public Plugin plugin;
    public TwGift twGift;
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new events(),this);
        plugin = this;
        twGift = new TwGift("0882433029");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
                sender.sendMessage("§8[§bTWGiftTopup§8] §fChecking! onemonent!");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            JsonObject redeem_result = twGift.redeem(args[0]);
                            //Lazy to use debug tools
                            Bukkit.getLogger().info("Result: " + redeem_result.getAsJsonObject().get("status"));
                            if (redeem_result.getAsJsonObject().get("status").getAsJsonObject().get("code").getAsString().equalsIgnoreCase("SUCCESS")) {
                                sender.sendMessage("§8[§bTWGiftTopup§8] §aOK -> " + redeem_result.getAsJsonObject().get("data").getAsJsonObject().get("voucher").getAsJsonObject().get("redeemed_amount_baht").getAsString() + " THB");
                            }else {
                                sender.sendMessage("§8[§bTWGiftTopup§8] §cError -> " + redeem_result.getAsJsonObject().get("status").getAsJsonObject().get("message").getAsString());
                            }
                        } catch (IOException exception) {
                            exception.printStackTrace();
                            sender.sendMessage("§8[§bTWGiftTopup§8] §cSomething went wrong please contact admin");
                        }
                    }
                }.runTaskAsynchronously(plugin);
        }else {
            sender.sendMessage("§8[§bTWGiftTopup§8] §fTWGiftTopup v" + plugin.getDescription().getVersion() + " by MCCH team.");
            sender.sendMessage("§8[§bTWGiftTopup§8] §fUsage: /" + command.getName() + " <url/voucher_hash>");
        }
        return super.onCommand(sender, command, label, args);
    }
}
