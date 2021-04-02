package th.in.mcch.vouchertopup.utils;

import com.google.gson.JsonObject;
import th.in.mcch.vouchertopup.VoucherTopup;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Utils {
    public static String getServerNMSVersion() {
        String a = VoucherTopup.getInstance().getServer().getClass().getPackage().getName();
        String version = a.substring(a.lastIndexOf('.') + 1);
        return version;
    }

    public static GameVersion getServerMCVersion() {
        String version = getServerNMSVersion();
        GameVersion gameVersion = GameVersion.UNKNOWN;
        if (version.contains("1_8")) {
            gameVersion = GameVersion.MC_1_8;
        }
        if (version.contains("1_9")) {
            gameVersion = GameVersion.MC_1_9;
        }
        if (version.contains("1_10")) {
            gameVersion = GameVersion.MC_1_10;
        }
        if (version.contains("1_11")) {
            gameVersion = GameVersion.MC_1_11;
        }
        if (version.contains("1_12")) {
            gameVersion = GameVersion.MC_1_12;
        }
        if (version.contains("1_13")) {
            gameVersion = GameVersion.MC_1_13;
        }
        if (version.contains("1_14")) {
            gameVersion = GameVersion.MC_1_14;
        }
        if (version.contains("1_15")) {
            gameVersion = GameVersion.MC_1_15;
        }
        if (version.contains("1_16")) {
            gameVersion = GameVersion.MC_1_16;
        }
        return gameVersion;
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        if (getServerMCVersion().getVersionID() > 3) {
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

    public static String replaceMessage(TrueMoneyGiftService service,JsonObject redeem_result, String message, Player p) {
        String message_result = message;
        try{
            message_result = message_result.replaceAll("&", "§");
            message_result = message_result.replaceAll("%version%", VoucherTopup.getVersion());
            message_result = message_result.replaceAll("%player%", p.getName());
            JsonObject status = redeem_result.getAsJsonObject().get("status").getAsJsonObject();
            message_result = message_result.replaceAll("%message%", status.get("message").getAsString());
            message_result = message_result.replaceAll("%code%", status.get("code").getAsString());
            JsonObject voucher = redeem_result.getAsJsonObject().get("data").getAsJsonObject().get("voucher").getAsJsonObject();
            message_result = message_result.replaceAll("%amount%", String.valueOf((int) voucher.get("redeemed_amount_baht").getAsDouble()));
            message_result = message_result.replaceAll("%amount_multiply%", String.valueOf(Double.parseDouble(voucher.get("redeemed_amount_baht").getAsString()) * service.multiply));
        }catch (IllegalStateException | NullPointerException ex){}
        return message_result;
    }
}
