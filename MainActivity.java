package com.example.shaun.apiv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.example.shaun.apiv2.Orders2.OrderMe;
import static com.example.shaun.apiv2.Testing.testMe;
import static com.example.shaun.apiv2.conversion.convertMe;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    //get order params
                   // String size= "0.01";
                    String funds = "10.00";
                    String price="0.100";
                    String product_id="BTC-EUR";
                    String side="sell";
                    OrderMe(funds,side,product_id );
                    convertMe();
                    testMe();


                    } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
