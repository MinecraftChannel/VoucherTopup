package me.mcch;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class TwGift {

    //Rework form Maythiwat (Demza) source
    public String VERIFY_URL = "https://gift.truemoney.com/campaign/vouchers/%hash%/verify";
    public String REDEEM_URL = "https://gift.truemoney.com/campaign/vouchers/%hash%/redeem";
    final public String mobileNumber;

    public TwGift(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public JsonPrimitive getVoucherStatus(String vocherId){
        HttpGet get = new HttpGet(VERIFY_URL.replaceAll("%hash%",vocherId));
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = httpClient.execute(get)) {
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject redeemVoucher(String voucherId) throws IOException {
        //EXHash AdVgpElIUm9lb1qYjn
        //Gift Url: https://gift.truemoney.com/campaign/?v=xxxxxxxxxxxxxxxxxx <- numbers,uppercase,lowercase
        HttpPost post = new HttpPost(REDEEM_URL.replaceAll("%hash%", voucherId));
        post.addHeader("Content-Type", "application/json");

        JsonObject json = new JsonObject();
        json.addProperty("mobile", mobileNumber);
        json.addProperty("voucher_hash", voucherId);
        post.setEntity(new StringEntity(json.toString()));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            return (JsonObject) new JsonParser().parse(EntityUtils.toString(response.getEntity()));
        }catch (Exception ex) {
            ex.printStackTrace();
            return (JsonObject) new JsonParser().parse("{\"message\":\""+ex.getMessage()+".\",\"code\":\"JAVA_ERROR\"}");
        }
    }

    // TODO : Hostname must be "gift.truemoney.com"
    public JsonObject redeemVoucherFormUrl(String voucher_url) throws IOException {
        return redeemVoucher(urlToHash(voucher_url));
    }

    public JsonObject redeem(String voucher) throws IOException {
        if (voucher.contains("v=")) {
            return redeemVoucherFormUrl(voucher);
        } else {
            return redeemVoucher(voucher);
        }
    }

    public String urlToHash(String url) {
        return url.split("v=")[1].replaceAll("[^a-zA-Z0-9]", "");
    }
}
