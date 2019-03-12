package project.finalyearapp;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

public class Order {

    private final String publicKey;
    private final String passphrase;
    private final String baseUrl;
    private final String secret;

    public Order(String publicKey,
                 String passphrase,
                 String secret,
                 String baseUrl)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        this.publicKey = publicKey;
        this.passphrase = passphrase;
        this.baseUrl = baseUrl;
        this.secret = secret;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void getOrders(String funds, String side, String product_id) throws IOException, InvalidKeyException, NoSuchAlgorithmException, JSONException {
        String timestamp = Instant.now().getEpochSecond() + "";
        String path = "/orders";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("funds", funds);
        jsonBody.put("side",side);
        jsonBody.put("type","market");

        jsonBody.put("product_id",product_id);

        String body= jsonBody.toString();
        URL url = new URL(baseUrl + path);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("CB-ACCESS-KEY", publicKey);
        con.setRequestProperty("CB-ACCESS-SIGN", signMessage(timestamp, "POST", path, body));
        con.setRequestProperty("CB-ACCESS-TIMESTAMP", timestamp);
        con.setRequestProperty("CB-ACCESS-PASSPHRASE", passphrase);
        con.setRequestProperty("content-type", "application/json");
        con.setDoOutput(true);
        con.setDoInput(true);
        System.out.println("curl -H \"CB-ACCESS-KEY: " + publicKey+ "\" "
                + "-H \"CB-ACCESS-SIGN: " + signMessage(timestamp,"POST", path, body) + "\" "
                + "-H \"CB-ACCESS-TIMESTAMP: " + timestamp + "\" "
                + "-H \"CB-ACCESS-PASSPHRASE: " + passphrase + "\" " + baseUrl + path);
        DataOutputStream os = new DataOutputStream(con.getOutputStream());
        os.writeBytes(jsonBody.toString());

        os.flush();
        os.close();
        Log.i("STATUS", String.valueOf(con.getResponseCode()));
        Log.i("MSG" , con.getResponseMessage());
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        String status = con.getResponseMessage();
        if (status.equals("OK")) {

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

        } else {
            throw new RuntimeException("Something went wrong. Response from server: " + status);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private String signMessage(String timestamp, String method, String path, String body) throws NoSuchAlgorithmException, InvalidKeyException {
        String prehash = timestamp + method + path+ body;

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        byte[] secretDecoded = Base64.getDecoder().decode(secret);
        SecretKeySpec secret_key = new SecretKeySpec(secretDecoded, "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(prehash.getBytes()));
    }

    public static void OrderMe(String funds, String side, String product_id) throws JSONException{
        Order t = null;
        try {
            t = new Order("ac9984498b0abce0b918f5d2241cd749",
                    "shaunPassWord20",
                    "QaTWz3fUBCXJp6D7pW+LMoOvwMHLeWAYG7Lqw7LhubXl8hltWOXb15SFAQPqz8ENWW7NLsA2tSVsYtXyGj9u5g==",
                    "https://api-public.sandbox.pro.coinbase.com");
            t.getOrders(funds,side,product_id);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
