<div align="center">
  <h1>VoucherTopup</h1>
  VoucherTopup is a spigot plugin to allow player topup/refill/donate with truemoney wallet gift voucher system and very configurable, 100% free and open source!
</div>

## Get TWGiftTopup
### Download
+ [**Github Releases**](https://github.com/MinecraftChannel/VoucherTopup/releases)
+ [**Jenkins**](#)

### Compile
```sh
git clone https://github.com/MinecraftChannel/VoucherTopup
cd VoucherTopup
mvn clean install
```

#### Requirements
* Java (JDK) 8 or above
* Maven

## Demo Servers
* [Minecraft Deluxe](http://mc-deluxe.net) (1.15.2)

## Developer API
<details>
  <summary>Events</summary>
  
 ```java
import com.google.gson.JsonObject;  
import th.in.mcch.vouchertopup.api.events.PlayerTopupEvent;  
import th.in.mcch.vouchertopup.api.events.TopupSuccessEvent;  
import org.bukkit.entity.Player;  
import org.bukkit.event.EventHandler;  
import org.bukkit.event.Listener;  
  
public class MyPlugin implements Listener {

    @EventHandler  
    public void onTopup(TWPlayerTopupEvent e) {  
        Player player = e.getPlayer();  
        String input = e.getInput();  
        e.setCancelled(true);  
    }

    @EventHandler  
    public void onTopupError(TWTopupErrorEvent e) { 
        Player player = e.getPlayer();  
        JsonObject result = e.getResult();  
    }

    @EventHandler  
    public void onTopupFailed(TWTopupFailedEvent e) {  
        Player player = e.getPlayer();  
        JsonObject result = e.getResult();  
    }

    @EventHandler  
    public void onTopupSuccess(TWTopupSuccessEvent e) {
        Player player = e.getPlayer();  
        JsonObject result = e.getResult();  
        System.out.println(result);  
        JsonObject status = e.getResult().getAsJsonObject().get("status").getAsJsonObject();  
        System.out.println(status);  
        JsonObject voucher = e.getResult().getAsJsonObject().get("data").getAsJsonObject().get("voucher").getAsJsonObject();  
        System.out.println(voucher);  
        double amount = voucher.get("redeemed_amount_baht").getAsDouble();  
        System.out.println("Redeem amount: " + amount + " by " + player.getName());  
    }
}
 ```
</details>

## Open Source Libraries
* [httpcomponents-client](https://github.com/apache/httpcomponents-client) by [Apache](https://github.com/apache) | [Apache License 2.0](https://github.com/apache/httpcomponents-client/blob/master/LICENSE.txt)
