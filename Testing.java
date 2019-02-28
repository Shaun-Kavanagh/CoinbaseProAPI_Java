package com.example.shaun.apiv2;


import android.annotation.TargetApi;
import android.os.Build;

import com.coinbase.exchange.api.accounts.Account;
import com.google.gson.Gson;

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

public class Testing {

    private final String publicKey;
    private final String passphrase;
    private final String baseUrl;
    private final String secret;

    public Testing(String publicKey,
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
    private Account[] getAccounts() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        String timestamp = Instant.now().getEpochSecond() + "";
        String path = "/accounts";
        URL url = new URL(baseUrl + path);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestProperty("CB-ACCESS-KEY", publicKey);
        con.setRequestProperty("CB-ACCESS-SIGN", signMessage(timestamp, "GET", path));
        con.setRequestProperty("CB-ACCESS-TIMESTAMP", timestamp);
        con.setRequestProperty("CB-ACCESS-PASSPHRASE", passphrase);
        con.setRequestProperty("content-type", "application/json");

        System.out.println("curl -H \"CB-ACCESS-KEY: " + publicKey+ "\" "
                + "-H \"CB-ACCESS-SIGN: " + signMessage(timestamp,"GET", path) + "\" "
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
            Account[] accounts = gson.fromJson(content.toString(), Account[].class);
            Arrays.stream(accounts).forEach(a -> {
                System.out.println("Account: " + a.getCurrency() + ", "
                        + "Balance: " + a.getBalance().toPlainString() + ", "
                        + "Available balance: " + a.getAvailable().toPlainString() + ", "
                        + "Held: " + a.getHold().toPlainString()+ ", "
                        + "Profile ID:" + a.getProfile_id()+ ", "
                        + "ID: "+ a.getId());
            });
            return accounts;
        } else {
            throw new RuntimeException("Something went wrong. Response from server: " + status);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private String signMessage(String timestamp, String method, String path) throws NoSuchAlgorithmException, InvalidKeyException {
        String prehash = timestamp + method + path;

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        byte[] secretDecoded = Base64.getDecoder().decode(secret);
        SecretKeySpec secret_key = new SecretKeySpec(secretDecoded, "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(prehash.getBytes()));
    }

    public static void testMe() {
        Testing t = null;
        try {
            /*  t = new Testing("b02f43f7d67159a3c86a9546ed25ecf8",
                    "shaunPassWord20",
                    "psKNuj+pmAC8Y8Bb7n0+wTXTrbEz/Vp0ehi0OK5UTEsuxWcfrF+UT6e6/s1F619YmyjSf7PDPEHBwrGhmMivoA==",
                    "https://api.pro.coinbase.com");*/
            t = new Testing("ac9984498b0abce0b918f5d2241cd749",
                    "shaunPassWord20",
                    "QaTWz3fUBCXJp6D7pW+LMoOvwMHLeWAYG7Lqw7LhubXl8hltWOXb15SFAQPqz8ENWW7NLsA2tSVsYtXyGj9u5g==",
                    "https://api-public.sandbox.pro.coinbase.com");
            t.getAccounts();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
