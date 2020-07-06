package me.mcch.twgifttopup.utils;

import com.google.gson.JsonObject;
import me.mcch.twgifttopup.TwGiftTopup;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Utils {
    public static String getServerNMSVersion() {
        String a = TwGiftTopup.getInstance().getServer().getClass().getPackage().getName();
        String version = a.substring(a.lastIndexOf('.') + 1);
        return version;
    }

    public static GameVersion getServerMCVersion() {
        String version = getServerNMSVersion();
        GameVersion gameVersion = GameVersion.UNKNOWN;
        if (version.equalsIgnoreCase("1_8_R1") || version.equalsIgnoreCase("1_8_R2") || version.equalsIgnoreCase("1_8_R3")) {
            gameVersion = GameVersion.MC_1_8;
        }
        if (version.equalsIgnoreCase("1_9_R1") || version.equalsIgnoreCase("1_9_R2")) {
            gameVersion = GameVersion.MC_1_9;
        }
        if (version.equalsIgnoreCase("1_10_R1")) {
            gameVersion = GameVersion.MC_1_10;
        }
        if (version.equalsIgnoreCase("1_11_R1")) {
            gameVersion = GameVersion.MC_1_11;
        }
        if (version.equalsIgnoreCase("1_12_R1")) {
            gameVersion = GameVersion.MC_1_12;
        }
        if (version.equalsIgnoreCase("1_13_R1") || version.equalsIgnoreCase("1_13_R2")) {
            gameVersion = GameVersion.MC_1_13;
        }
        if (version.equalsIgnoreCase("1_14_R1")) {
            gameVersion = GameVersion.MC_1_14;
        }
        if (version.equalsIgnoreCase("1_15_R1")) {
            gameVersion = GameVersion.MC_1_15;
        }
        return gameVersion;
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        if (getServerMCVersion().getVersionID() > 2) {
            player.sendTitle(title, subtitle, 2, 50, 2);
        } else {
            player.sendTitle(title, subtitle);
        }
    }

    public static void sendActionbar(Player player, String message) {
        if (player == null || message == null) return;

        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

        //new version (1.10 or newest)
        if (getServerMCVersion().getVersionID() > 2) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            return;
        } else {
            //1.10 or lower
            try {
                Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
                Object craftPlayer = craftPlayerClass.cast(player);
                Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
                Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
                Object packetPlayOutChat;
                Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
                Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
                Method method = null;
                if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);
                Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
                packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);
                Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
                Object iCraftPlayer = handle.invoke(craftPlayer);
                Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
                Object playerConnection = playerConnectionField.get(iCraftPlayer);
                Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
                sendPacket.invoke(playerConnection, packetPlayOutChat);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String replaceMessage(JsonObject redeem_result, String message, Player p) {
        String message_result = message
                .replaceAll("&", "§")
                .replaceAll("%version%", TwGiftTopup.getVersion())
                .replaceAll("%player%", p.getName());
        if (redeem_result != null) {
            JsonObject status = redeem_result.getAsJsonObject().get("status").getAsJsonObject();
            JsonObject voucher = redeem_result.getAsJsonObject().get("data").getAsJsonObject().get("voucher").getAsJsonObject();
            message_result = message_result
                    .replaceAll("%amount%", String.valueOf((int) voucher.get("redeemed_amount_baht").getAsDouble()))
                    .replaceAll("%amount_double%", voucher.get("redeemed_amount_baht").getAsString())
                    .replaceAll("%message%", status.get("message").getAsString())
                    .replaceAll("%code%", status.get("code").getAsString());
        }
        return message_result;
    }
}
