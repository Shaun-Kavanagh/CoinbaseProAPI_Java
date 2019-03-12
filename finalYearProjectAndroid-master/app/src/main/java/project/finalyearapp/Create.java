package project.finalyearapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;

import project.finalyearapp.Model.Transaction;

import static java.lang.Boolean.TRUE;
import static java.lang.Thread.sleep;
import static project.finalyearapp.Order.OrderMe;
import static project.finalyearapp.Price.findPrice;

public class Create extends AppCompatActivity {

    MaterialEditText edtAmount, edtCurrency, edtReceiver, edtShopA, edtShopB;
    Button btnCreate;
    TextView textAfterconv;
    String product_id = "BTC-USD";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        final String passedEmail= getIntent().getStringExtra("Email");

        edtAmount = (MaterialEditText)findViewById(R.id.edtAmount);
        edtCurrency = (MaterialEditText)findViewById(R.id.edtCurrency);
        edtReceiver = (MaterialEditText)findViewById(R.id.edtReceiver);
        edtShopA = (MaterialEditText)findViewById(R.id.edtShopA);

        btnCreate = (Button)findViewById(R.id.btnCreate);

        textAfterconv=(TextView)findViewById(R.id.textAfterConversion);

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_trans = database.getReference("Transaction");
        final DatabaseReference loc = table_trans.child(passedEmail);



            /* final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    while(true) {

                        try {

                             textAfterconv.setText(findPrice(product_id));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            sleep(100000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }

            });
            thread.start();*/

       /* final Thread threadBuy = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    OrderMe(edtAmount.getText().toString(),"buy", product_id);
                    Log.i("Order placed", "Order placed");
                } catch (JSONException e) {
                    Log.i("Error", e.toString());

                    e.printStackTrace();
                }
            }

        });
        threadBuy.start();*/



        try {

            textAfterconv.setText(findPrice(product_id));
            //sandbox override
            textAfterconv.setText("1.08");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog mDialog = new ProgressDialog(Create.this);
                mDialog.setMessage("Please Wait...");
                mDialog.show();

                String receiver = edtReceiver.getText().toString().replace('.', ' ');
                String shopA = edtShopA.getText().toString().replace('.', ' ');

                try {
                    OrderMe(edtAmount.getText().toString(),"buy", product_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //get current price, multiply by funds, take away 3% under a certain amrount... do conversion, sell currency
                double currentPrice= 0;
                try {
                    currentPrice = Double.parseDouble(findPrice(product_id));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //override for sandbox,
                currentPrice=1.08;
                double toSell= Double.parseDouble(edtAmount.getText().toString())*currentPrice;
                toSell=toSell*0.97;
                try {
                    OrderMe(Double.toString(toSell),"sell", product_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                mDialog.dismiss();
                final String keyRef = table_trans.push().getKey();
                Transaction trans = new Transaction(edtAmount.getText().toString(), edtCurrency.getText().toString(), receiver, shopA, " ", false, false, false, keyRef, passedEmail);
                table_trans.child(keyRef).setValue(trans);
                Toast.makeText(Create.this, "Transaction Created!", Toast.LENGTH_SHORT).show();
                finish();
                Intent mvWelcome = new Intent(Create.this, Welcome.class);
                mvWelcome.putExtra("Email", passedEmail);
                startActivity(mvWelcome);

            }
        });
    }
}
