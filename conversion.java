package com.example.shaun.apiv2;

import android.annotation.TargetApi;
import android.os.Build;

import com.coinbase.exchange.api.accounts.Account;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;

public class conversion {

    private final String publicKey;
    private final String passphrase;
    private final String baseUrl;
    private final String secret;

    public conversion(String publicKey,
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
    private String postConversion() throws IOException, InvalidKeyException, NoSuchAlgorithmException, JSONException {
        String timestamp = Instant.now().getEpochSecond() + "";
        String path = "/conversions";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("from", "EURO");
        jsonBody.put("to", "USDC");
        jsonBody.put("amount","20.00");

        String body= jsonBody.toString();
        URL url = new URL(baseUrl + path );

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestProperty("CB-ACCESS-KEY", publicKey);
        con.setRequestProperty("CB-ACCESS-SIGN", signMessage(timestamp, "POST", path, body));
        con.setRequestProperty("CB-ACCESS-TIMESTAMP", timestamp);
        con.setRequestProperty("CB-ACCESS-PASSPHRASE", passphrase);
        con.setRequestProperty("content-type", "application/json");

        System.out.println("curl -H \"CB-ACCESS-KEY: " + publicKey+ "\" "
                + "-H \"CB-ACCESS-SIGN: " + signMessage(timestamp,"POST", path, body) + "\" "
                + "-H \"CB-ACCESS-TIMESTAMP: " + timestamp + "\" "
                + "-H \"CB-ACCESS-PASSPHRASE: " + passphrase + "\" " + baseUrl + path);

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

            Gson gson = new Gson();
            String ret= gson.fromJson(content.toString(),null);
            System.out.println("curl this is the return"+ ret );
            /*Account[] accounts = gson.fromJson(content.toString(), Account[].class);
            Arrays.stream(accounts).forEach(a -> {
                System.out.println("Account: " + a.getCurrency() + ", "
                        + "Balance: " + a.getBalance().toPlainString() + ", "
                        + "Available balance: " + a.getAvailable().toPlainString() + ", "
                        + "Held: " + a.getHold().toPlainString()+ ", "
                        + "Profile ID:" + a.getProfile_id()+ ", "
                        + "ID: "+ a.getId());
            });*/
            String output="";
            return output;
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

    public static void convertMe() throws JSONException {
        conversion t = null;
        try {
            /*  t = new Testing("b02f43f7d67159a3c86a9546ed25ecf8",
                    "shaunPassWord20",
                    "psKNuj+pmAC8Y8Bb7n0+wTXTrbEz/Vp0ehi0OK5UTEsuxWcfrF+UT6e6/s1F619YmyjSf7PDPEHBwrGhmMivoA==",
                    "https://api.pro.coinbase.com");*/
            t = new conversion("ac9984498b0abce0b918f5d2241cd749",
                    "shaunPassWord20",
                    "QaTWz3fUBCXJp6D7pW+LMoOvwMHLeWAYG7Lqw7LhubXl8hltWOXb15SFAQPqz8ENWW7NLsA2tSVsYtXyGj9u5g==",
                    "https://api-public.sandbox.pro.coinbase.com");
            t.postConversion();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
