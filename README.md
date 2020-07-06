<div align="center">
  <h1>TWGiftTopup</h1>
  TWGiftTopup (Truewallet gift topup) is a spigot plugin to allow player topup/refill/donate with truemoney wallet gift voucher system and very configurable, 100% free and open source!
</div>

## Get TWGiftTopup
### Download
+ [**Jenkins**]([http://www.google.com/](http://www.google.com/))
#### Requirements
* Java (JDK) 8 or above
* Maven
#### Compile
```sh
git coming soon!
```
## Demo Servers
* [Minecraft Deluxe](http://mc-deluxe.net) (1.15.2)

## Developer API
<details>
  <summary>Events</summary>
  
 ```java
import com.google.gson.JsonObject;  
import me.mcch.TWGiftTopup.API.Events.TWPlayerTopupEvent;  
import me.mcch.TWGiftTopup.API.Events.TWTopupSuccessEvent;  
import org.bukkit.entity.Player;  
import org.bukkit.event.EventHandler;  
import org.bukkit.event.Listener;  
  
public class MyPlugin implements Listener {  
  @EventHandler  
  public void onTopup(TWPlayerTopupEvent e){  
        Player player = e.getPlayer();  
         String input = e.getInput();  
         e.setCancelled(true);  
  }  
  
  @EventHandler  
  public void onTopupError(TWTopupSuccessEvent e){  
        Player player = e.getPlayer();  
       JsonObject result = e.getResult();  
  }  
  @EventHandler  
  public void onTopupFailed(TWTopupSuccessEvent e){  
        Player player = e.getPlayer();  
       JsonObject result = e.getResult();  
  }  
  @EventHandler  
  public void onTopupSuccess(TWTopupSuccessEvent e){  
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
* [httpcomponents-client](https://github.com/apache/httpcomponents-client) by [Apache](https://github.com/apache) | [Apache License 2.0](https://github.com/brettwooldridge/HikariCP/blob/dev/LICENSE)
