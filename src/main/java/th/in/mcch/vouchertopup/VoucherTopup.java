package th.in.mcch.vouchertopup;

import com.google.gson.JsonObject;
import th.in.mcch.vouchertopup.api.events.PlayerTopupEvent;
import th.in.mcch.vouchertopup.api.events.TopupErrorEvent;
import th.in.mcch.vouchertopup.api.events.TopupFailedEvent;
import th.in.mcch.vouchertopup.api.events.TopupSuccessEvent;
import th.in.mcch.vouchertopup.utils.TrueMoneyGiftService;
import th.in.mcch.vouchertopup.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoucherTopup extends JavaPlugin {

    public static FileConfiguration config;
    static VoucherTopup instance;
    static String version;
    public TrueMoneyGiftService twGift;

    public static VoucherTopup getInstance() {
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
        String phone = config.getString("general.phone_number");
        Boolean skipCheck = config.getBoolean("general.skip_check");
        String multiply =  config.getString("general.multiply");
        Bukkit.getLogger().info("Skip check: " + skipCheck);
        if (skipCheck) {
            Bukkit.getLogger().warning("Warning: Skip check is enabled if config is invalid VoucherTopup will ignored");
        }
        if(skipCheck || phone.matches("^[0-9]*$") && phone.length() == 10 & multiply.matches("/^[0-9]+.[0-9]+$")) {
            twGift = new TrueMoneyGiftService(phone, Double.parseDouble(multiply));
        }else {
            Bukkit.getLogger().severe("Phone number or multiply number is invalid please check in config!");
            Bukkit.getLogger().info("Current phone value: " + phone);
            Bukkit.getLogger().info("Current multiply value: " + multiply);
            Bukkit.getLogger().severe("Disabling " + instance.getDescription().getName());
            Bukkit.getPluginManager().disablePlugin(this);
            Bukkit.getLogger().info("Run as version " + Utils.getServerNMSVersion() + " VersionID: " + Utils.getServerMCVersion());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            this.reloadConfig();
            config = this.getConfig();
            sender.sendMessage("VoucherTopup Reloaded!");
            return true;
        }

        Player p = (Player) sender;
        if (args.length > 0) {
            PlayerTopupEvent event = new PlayerTopupEvent(p, args[0]);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return true;
            p.sendMessage(Utils.replaceMessage(twGift,null, config.getString("message.chat.check"), p));
            Utils.sendActionbar(p, Utils.replaceMessage(twGift,null, config.getString("message.action_bar.check"), p));
            Utils.sendTitle(p, Utils.replaceMessage(twGift,null, config.getString("message.title.check"), p), Utils.replaceMessage(null,null, config.getString("message.sub_title.check"), p));
            Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
                try {
                    JsonObject redeem_result = twGift.redeem(args[0]);
                    JsonObject status = redeem_result.getAsJsonObject().get("status").getAsJsonObject();
                    if (status.get("code").getAsString().equalsIgnoreCase("SUCCESS")) {
                        Bukkit.getScheduler().runTask(instance, () -> {
                            for (String s : config.getStringList("general.console_command")) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.replaceMessage(twGift,redeem_result, s, p));
                            }
                            Bukkit.getPluginManager().callEvent(new TopupSuccessEvent(p, redeem_result));
                        });
                        p.sendMessage(Utils.replaceMessage(twGift,redeem_result, config.getString("message.chat.success"), p));
                        Utils.sendTitle(p, Utils.replaceMessage(twGift,redeem_result, config.getString("message.title.success"), p), Utils.replaceMessage(twGift,redeem_result, config.getString("message.sub_title.success"), p));
                        Utils.sendActionbar(p, Utils.replaceMessage(twGift,redeem_result, config.getString("message.action_bar.success"), p));
                        if (Utils.getServerMCVersion().getVersionID() > 3) {
                            p.playSound(p.getLocation(), config.getString("sound.on_success.sound"), SoundCategory.PLAYERS, (float) config.getDouble("sound.on_success.volume"), (float) config.getDouble("sound.on_success.pitch"));
                        }else {
                            p.playSound(p.getLocation(),config.getString("sound.on_success.sound"),(float) config.getDouble("sound.on_success.volume"), (float) config.getDouble("sound.on_success.pitch"));
                        }
                    } else {
                        Bukkit.getScheduler().runTask(instance, () -> {
                            Bukkit.getPluginManager().callEvent(new TopupFailedEvent(p, redeem_result));
                        });
                        p.sendMessage(Utils.replaceMessage(twGift,redeem_result, config.getString("message.chat.fail"), p));
                        Utils.sendTitle(p, Utils.replaceMessage(twGift,redeem_result, config.getString("message.title.fail"), p), Utils.replaceMessage(twGift,redeem_result, config.getString("message.sub_title.fail"), p));
                        Utils.sendActionbar(p, Utils.replaceMessage(twGift,redeem_result, config.getString("message.action_bar.fail"), p));
                        if (Utils.getServerMCVersion().getVersionID() > 3) {
                            p.playSound(p.getLocation(), config.getString("sound.on_fail.sound"), SoundCategory.PLAYERS, (float) config.getDouble("sound.on_fail.volume"), (float) config.getDouble("sound.on_fail.pitch"));
                        }else {
                            p.playSound(p.getLocation(),config.getString("sound.on_fail.sound"),(float) config.getDouble("sound.on_fail.volume"), (float) config.getDouble("sound.on_fail.pitch"));
                        }
                    }
                } catch (Exception ex) {
                    Bukkit.getScheduler().runTask(instance, () -> {
                        Bukkit.getPluginManager().callEvent(new TopupErrorEvent(p, ex));
                    });
                    ex.printStackTrace();
                    p.sendMessage(Utils.replaceMessage(twGift,null, config.getString("message.chat.error"), p));
                    Utils.sendTitle(p, Utils.replaceMessage(twGift,null, config.getString("message.title.error"), p), Utils.replaceMessage(twGift,null, config.getString("message.sub_title.error"), p));
                    Utils.sendActionbar(p, Utils.replaceMessage(twGift,null, config.getString("message.action_bar.error"), p));
                    if (Utils.getServerMCVersion().getVersionID() > 3) {
                        p.playSound(p.getLocation(), config.getString("sound.on_error.sound"), SoundCategory.PLAYERS, (float) config.getDouble("sound.on_error.volume"), (float) config.getDouble("sound.on_error.pitch"));
                    }else {
                        p.playSound(p.getLocation(),config.getString("sound.on_error.sound"),(float) config.getDouble("sound.on_error.volume"), (float) config.getDouble("sound.on_error.pitch"));
                    }
                }
            });
        } else {
            for (String s : config.getStringList("message.chat.help")) {
                p.sendMessage(s.replaceAll("&", "§").replaceAll("%version%", version).replaceAll("%player%", p.getName()));
            }
            if (Utils.getServerMCVersion().getVersionID() > 3) {
                p.playSound(p.getLocation(), config.getString("sound.on_type_command.sound"), SoundCategory.PLAYERS, (float) config.getDouble("sound.on_type_command.volume"), (float) config.getDouble("sound.on_type_command.pitch"));
            }else {
                p.playSound(p.getLocation(),config.getString("sound.on_type_command.sound"),(float) config.getDouble("sound.on_type_command.volume"), (float) config.getDouble("sound.on_type_command.pitch"));
            }
        }
        return super.onCommand(sender, command, label, args);
    }
}
