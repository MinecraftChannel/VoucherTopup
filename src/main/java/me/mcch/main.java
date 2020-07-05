package me.mcch;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public final class main extends JavaPlugin {
    public static Plugin plugin;
    public twgift twGift;
    public FileConfiguration config;
    public static String plugin_version;
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new events(),this);
        plugin = this;
        config = this.getConfig();
        this.saveDefaultConfig();
        twGift = new twgift(config.getString("general.phone_number"));
        plugin_version = plugin.getDescription().getVersion();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
                sender.sendMessage(util.replaceMessage(null,config.getString("message.chat.check"), (Player) sender));
                util.sendActionbar((Player) sender,util.replaceMessage(null,config.getString("message.action_bar.check"), (Player) sender));
                util.sendTitle((Player) sender,util.replaceMessage(null,config.getString("message.title.check"), (Player) sender),util.replaceMessage(null,config.getString("message.sub_title.check"), (Player) sender));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            JsonObject redeem_result = twGift.redeem(args[0]);
                            JsonObject status = redeem_result.getAsJsonObject().get("status").getAsJsonObject();
                            if (status.get("code").getAsString().equalsIgnoreCase("SUCCESS")) {
                                for (String s : config.getStringList("message.general.console_command")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),util.replaceMessage(redeem_result,s,(Player) sender));
                                }
                                sender.sendMessage(util.replaceMessage(redeem_result,config.getString("message.chat.seccess"), (Player) sender));
                                util.sendTitle((Player) sender,util.replaceMessage(redeem_result,config.getString("message.title.seccess"), (Player) sender),util.replaceMessage(redeem_result,config.getString("message.sub_title.seccess"), (Player) sender));
                                util.sendActionbar((Player) sender,util.replaceMessage(redeem_result,config.getString("message.action_bar.success"), (Player) sender));
                            }else {
                                sender.sendMessage(util.replaceMessage(redeem_result,config.getString("message.chat.fail"), (Player) sender));
                                util.sendTitle((Player) sender,util.replaceMessage(redeem_result,config.getString("message.title.fail"), (Player) sender),util.replaceMessage(redeem_result,config.getString("message.sub_title.fail"), (Player) sender));
                                util.sendActionbar((Player) sender,util.replaceMessage(redeem_result,config.getString("message.action_bar.fail"), (Player) sender));
                            }
                        } catch (IOException exception) {
                            exception.printStackTrace();
                            sender.sendMessage(util.replaceMessage(null,config.getString("message.chat.error"), (Player) sender));
                            sender.sendMessage(util.replaceMessage(null,config.getString("message.chat.error"), (Player) sender));
                            util.sendTitle((Player) sender,util.replaceMessage(null,config.getString("message.title.error"), (Player) sender),util.replaceMessage(null,config.getString("message.sub_title.error"), (Player) sender));
                            util.sendActionbar((Player) sender,util.replaceMessage(null,config.getString("message.action_bar.error"), (Player) sender));
                        }
                    }
                }.runTaskAsynchronously(plugin);
        }else {
            for (String s : config.getStringList("message.chat.help")) {
                sender.sendMessage(s.replaceAll("&","§").replaceAll("%version%",plugin_version).replaceAll("%player%",sender.getName()));
            }
        }
        return super.onCommand(sender, command, label, args);
    }
}
