package me.mcch.twgifttopup;

import com.google.gson.JsonObject;
import me.mcch.twgifttopup.api.events.TWPlayerTopupEvent;
import me.mcch.twgifttopup.api.events.TWTopupErrorEvent;
import me.mcch.twgifttopup.api.events.TWTopupFailedEvent;
import me.mcch.twgifttopup.api.events.TWTopupSuccessEvent;
import me.mcch.twgifttopup.utils.TrueMoneyGiftService;
import me.mcch.twgifttopup.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class TwGiftTopup extends JavaPlugin {

    public static FileConfiguration config;
    static TwGiftTopup instance;
    static String version;
    public TrueMoneyGiftService twGift;

    public static TwGiftTopup getInstance() {
        return instance;
    }

    public static String getVersion() {
        return version;
    }

    @Override
    public void onEnable() {
        instance = this;
        version = instance.getDescription().getVersion();
        config = this.getConfig();
        this.saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        try {
            Integer.parseInt(config.getString("general.phone_number"));
            twGift = new TrueMoneyGiftService(config.getString("general.phone_number"));
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Phone number is invalid please check in config!");
            Bukkit.getLogger().severe("Disabling " + instance.getDescription().getName());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            this.reloadConfig();
            config = this.getConfig();
            sender.sendMessage("twgifttopup reloaded!");
            return true;
        }

        Player p = (Player) sender;
        if (args.length > 0) {
            TWPlayerTopupEvent event = new TWPlayerTopupEvent(p, args[0]);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return true;
            p.sendMessage(Utils.replaceMessage(null, config.getString("message.chat.check"), p));
            Utils.sendActionbar(p, Utils.replaceMessage(null, config.getString("message.action_bar.check"), p));
            Utils.sendTitle(p, Utils.replaceMessage(null, config.getString("message.title.check"), p), Utils.replaceMessage(null, config.getString("message.sub_title.check"), p));
            Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
                try {
                    JsonObject redeem_result = twGift.redeem(args[0]);
                    JsonObject status = redeem_result.getAsJsonObject().get("status").getAsJsonObject();
                    if (status.get("code").getAsString().equalsIgnoreCase("SUCCESS")) {
                        Bukkit.getScheduler().runTask(instance, () -> {
                            for (String s : config.getStringList("general.console_command")) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.replaceMessage(redeem_result, s, p));
                            }
                            Bukkit.getPluginManager().callEvent(new TWTopupSuccessEvent(p, redeem_result));
                        });
                        p.sendMessage(Utils.replaceMessage(redeem_result, config.getString("message.chat.success"), p));
                        Utils.sendTitle(p, Utils.replaceMessage(redeem_result, config.getString("message.title.success"), p), Utils.replaceMessage(redeem_result, config.getString("message.sub_title.success"), p));
                        Utils.sendActionbar(p, Utils.replaceMessage(redeem_result, config.getString("message.action_bar.success"), p));
                        p.playSound(p.getLocation(), config.getString("sound.on_success.sound"), SoundCategory.PLAYERS, (float) config.getDouble("sound.on_success.volume"), (float) config.getDouble("sound.on_success.pitch"));
                    } else {
                        Bukkit.getScheduler().runTask(instance, () -> {
                            Bukkit.getPluginManager().callEvent(new TWTopupFailedEvent(p, redeem_result));
                        });
                        p.sendMessage(Utils.replaceMessage(redeem_result, config.getString("message.chat.fail"), p));
                        Utils.sendTitle(p, Utils.replaceMessage(redeem_result, config.getString("message.title.fail"), p), Utils.replaceMessage(redeem_result, config.getString("message.sub_title.fail"), p));
                        Utils.sendActionbar(p, Utils.replaceMessage(redeem_result, config.getString("message.action_bar.fail"), p));
                        p.playSound(p.getLocation(), config.getString("sound.on_fail.sound"), SoundCategory.PLAYERS, (float) config.getDouble("sound.on_fail.volume"), (float) config.getDouble("sound.on_fail.pitch"));
                    }
                } catch (Exception ex) {
                    Bukkit.getScheduler().runTask(instance, () -> {
                        Bukkit.getPluginManager().callEvent(new TWTopupErrorEvent(p, ex));
                    });
                    ex.printStackTrace();
                    p.sendMessage(Utils.replaceMessage(null, config.getString("message.chat.error"), p));
                    Utils.sendTitle(p, Utils.replaceMessage(null, config.getString("message.title.error"), p), Utils.replaceMessage(null, config.getString("message.sub_title.error"), p));
                    Utils.sendActionbar(p, Utils.replaceMessage(null, config.getString("message.action_bar.error"), p));
                    p.playSound(p.getLocation(), config.getString("sound.on_error.sound"), SoundCategory.PLAYERS, (float) config.getDouble("sound.on_error.volume"), (float) config.getDouble("sound.on_error.pitch"));
                }
            });
        } else {
            for (String s : config.getStringList("message.chat.help")) {
                p.sendMessage(s.replaceAll("&", "§").replaceAll("%version%", version).replaceAll("%player%", p.getName()));
            }
            p.playSound(p.getLocation(), config.getString("sound.on_type_command.sound"), SoundCategory.PLAYERS, (float) config.getDouble("sound.on_type_command.volume"), (float) config.getDouble("sound.on_type_command.pitch"));
        }

        return super.onCommand(sender, command, label, args);
    }
}
