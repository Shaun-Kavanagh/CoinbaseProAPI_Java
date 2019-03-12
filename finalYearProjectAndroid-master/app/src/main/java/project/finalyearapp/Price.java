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

public class Price {

    private final String publicKey;
    private final String passphrase;
    private final String baseUrl;
    private final String secret;

    public Price(String publicKey,
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
    private String getPrice( String product_id) throws IOException, InvalidKeyException, NoSuchAlgorithmException, JSONException {
        String timestamp = Instant.now().getEpochSecond() + "";
        String path = "/products"+"/"+ product_id+"/ticker";


        URL url = new URL(baseUrl + path);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("CB-ACCESS-KEY", publicKey);
        con.setRequestProperty("CB-ACCESS-SIGN", signMessage(timestamp, "GET", path));
        con.setRequestProperty("CB-ACCESS-TIMESTAMP", timestamp);
        con.setRequestProperty("CB-ACCESS-PASSPHRASE", passphrase);
        con.setRequestProperty("content-type", "application/json");
        con.setDoInput(true);
        System.out.println("curl -H \"CB-ACCESS-KEY: " + publicKey+ "\" "
                + "-H \"CB-ACCESS-SIGN: " + signMessage(timestamp,"GET", path) + "\" "
                + "-H \"CB-ACCESS-TIMESTAMP: " + timestamp + "\" "
                + "-H \"CB-ACCESS-PASSPHRASE: " + passphrase + "\" " + baseUrl + path);

        Log.i("STATUS!", String.valueOf(con.getResponseCode()));
        Log.i("MSG" , con.getResponseMessage());


        String status = con.getResponseMessage();
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        if (status.equals("OK")) {

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            JSONObject returnedJSON= new JSONObject(content.toString());
            String theprice = returnedJSON.getString("price");

            Log.i("OPEN price:", theprice);



            return theprice;
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

    public static String findPrice(String product_id) throws JSONException{
        Price t = null;
        try {

            t = new Price("ac9984498b0abce0b918f5d2241cd749",
                    "shaunPassWord20",
                    "QaTWz3fUBCXJp6D7pW+LMoOvwMHLeWAYG7Lqw7LhubXl8hltWOXb15SFAQPqz8ENWW7NLsA2tSVsYtXyGj9u5g==",
                    "https://api-public.sandbox.pro.coinbase.com");
            String price= t.getPrice(product_id);
            return price;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return "Error";
    }

}

