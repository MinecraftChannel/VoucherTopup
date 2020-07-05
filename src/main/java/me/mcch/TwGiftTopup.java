package me.mcch;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class TwGiftTopup extends JavaPlugin {

    static TwGiftTopup instance;
    static String plugin_version;

    public TwGift twGift;
    public FileConfiguration config;

    @Override
    public void onEnable() {
        instance = this;
        plugin_version = instance.getDescription().getVersion();

        config = this.getConfig();
        this.saveDefaultConfig();

        try{
            Integer.parseInt(config.getString("general.phone_number"));
            twGift = new TwGift(config.getString("general.phone_number"));
        }catch (Exception ex){
            Bukkit.getLogger().severe("Phone number is invalid please check in config!");
            Bukkit.getLogger().severe("Disabling " + instance.getDescription().getName());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Bukkit.getPluginManager().registerEvents(new Events(),this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can be executed in-game.");
            return false;
        }

        Player p = (Player) sender;
        if (args.length > 0) {
            p.sendMessage(Utils.replaceMessage(null, config.getString("message.chat.check"), p));
            Utils.sendActionbar(p, Utils.replaceMessage(null, config.getString("message.action_bar.check"), p));
            Utils.sendTitle(p, Utils.replaceMessage(null, config.getString("message.title.check"), p), Utils.replaceMessage(null, config.getString("message.sub_title.check"), p));

            Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
                try {
                    JsonObject redeem_result = twGift.redeem(args[0]);
                    JsonObject status = redeem_result.getAsJsonObject().get("status").getAsJsonObject();
                    if (status.get("code").getAsString().equalsIgnoreCase("SUCCESS")) {
                        for (String s : config.getStringList("message.general.console_command")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.replaceMessage(redeem_result, s, p));
                        }
                        p.sendMessage(Utils.replaceMessage(redeem_result, config.getString("message.chat.seccess"), p));
                        Utils.sendTitle(p, Utils.replaceMessage(redeem_result, config.getString("message.title.seccess"), p), Utils.replaceMessage(redeem_result, config.getString("message.sub_title.seccess"), p));
                        Utils.sendActionbar(p, Utils.replaceMessage(redeem_result, config.getString("message.action_bar.success"), p));
                    }else {
                        p.sendMessage(Utils.replaceMessage(redeem_result, config.getString("message.chat.fail"), p));
                        Utils.sendTitle(p, Utils.replaceMessage(redeem_result, config.getString("message.title.fail"), p), Utils.replaceMessage(redeem_result, config.getString("message.sub_title.fail"), p));
                        Utils.sendActionbar(p, Utils.replaceMessage(redeem_result, config.getString("message.action_bar.fail"), p));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    p.sendMessage(Utils.replaceMessage(null, config.getString("message.chat.error"), p));
                    p.sendMessage(Utils.replaceMessage(null, config.getString("message.chat.error"), p));
                    Utils.sendTitle(p, Utils.replaceMessage(null, config.getString("message.title.error"), p), Utils.replaceMessage(null, config.getString("message.sub_title.error"), p));
                    Utils.sendActionbar(p, Utils.replaceMessage(null, config.getString("message.action_bar.error"), p));
                }
            });
        } else {
            for (String s : config.getStringList("message.chat.help")) {
                p.sendMessage(s.replaceAll("&","§").replaceAll("%version%", plugin_version).replaceAll("%player%", p.getName()));
            }
        }

        return super.onCommand(sender, command, label, args);
    }

    public static TwGiftTopup getInstance() {
        return instance;
    }
}
