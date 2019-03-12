package project.finalyearapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Map;

import project.finalyearapp.Interface.ItemClickListener;
import project.finalyearapp.Model.Transaction;
import project.finalyearapp.ViewHolder.TransRequestHolder;
import project.finalyearapp.ViewHolder.TransViewHolder;

public class Request extends AppCompatActivity {

    private static final String TAG = "Request";

    //Firebase Stuff
    FirebaseDatabase database;
    DatabaseReference start, transaction, ref,use;
    DatabaseReference user;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    Dialog custDialog, shopaDialog, shopbDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        final String passedEmail = getIntent().getStringExtra("Email");
        //final String passedUserType= getIntent().getStringExtra("userType");

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        start = database.getReference("Transaction");
        transaction = start.child(passedEmail);
        ref = database.getReference("User");
        use = ref.child(passedEmail);

        //Load Menu
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();
    }

    private void cancel(){
        Log.d(TAG, "cancelMethod: Called.");
        Toast.makeText(Request.this, "Canceled Approval", Toast.LENGTH_SHORT).show();
    }

    private void okMethod1(){
        Log.d(TAG, "okMethod1: Called.");
        Toast.makeText(Request.this, "Awaiting Approval", Toast.LENGTH_SHORT).show();
    }

    private void okMethod2(final String passedEmail, String key){
        Log.d(TAG, "okMethod2: Called.");
        start.child(key).child("shopAflag").setValue(true);
        Toast.makeText(Request.this, "ShopA Approval", Toast.LENGTH_SHORT).show();
    }


    private void okMethod3(final String passedEmail, String key){
        Log.d(TAG, "okMethod3: Called.");
        start.child(key).child("shopBflag").setValue(true);
        Toast.makeText(Request.this, "ShopB Approval", Toast.LENGTH_SHORT).show();
    }

    public void customDialog(String title, String message, final String cancelMethod, final String okMethod, final String passedEmail, final String key) {
        final android.support.v7.app.AlertDialog.Builder builderSingle = new android.support.v7.app.AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_stat_name);
        builderSingle.setTitle(title);
        builderSingle.setMessage(message);

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: Cancel Called.");
                        cancel();
                    }
                }
        );
        builderSingle.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: OK Called.");
                        if(okMethod.equals("okMethod1")) {
                            okMethod1();
                        }
                        else if(okMethod.equals("okMethod2")) {
                            okMethod2(passedEmail, key);
                        }
                        else {
                            okMethod3(passedEmail, key);
                        }
                    }
                }
        );
        builderSingle.show();
    }

    private void loadMenu() {
        FirebaseRecyclerAdapter<Transaction, TransRequestHolder> adapter = new FirebaseRecyclerAdapter<Transaction, TransRequestHolder>(Transaction.class, R.layout.trans_item, TransRequestHolder.class, start) {
            @Override
            protected void populateViewHolder(TransRequestHolder viewHolder, final Transaction model, int position) {
                final String passedUserType= getIntent().getStringExtra("userType");
                final String passedEmail= getIntent().getStringExtra("Email");
                final String receiver = model.getReceiver().replace('.', ' ');
                final String shopA = model.getShopA().replace('.', ' ');

                //custB
                if(passedEmail.equalsIgnoreCase(model.getReceiver()) && model.getCustflag() == false) {
                    viewHolder.txtTransactionA.setText(model.getAmount());
                    viewHolder.txtTransactionC.setText(model.getCurrency());
                    viewHolder.txtTransactionR.setText(model.getReceiver());
                    final Transaction clickItem = model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(android.view.View view, int position, boolean isLongClick) {
                            Toast.makeText(Request.this, "Receiving: " + shopA, Toast.LENGTH_SHORT).show();
                            final String key = model.getKey();
                            custDialog = new Dialog(Request.this);
                            custDialog.setContentView(R.layout.dialog_template);
                            final EditText Write = custDialog.findViewById((R.id.write));
                            Button Approve = custDialog.findViewById(R.id.approve);

                            Write.setEnabled(true);
                            Approve.setEnabled(true);

                            Approve.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String newShopB = Write.getText().toString().replace(".", " ");
                                    start.child(key).child("custflag").setValue(true);
                                    start.child(key).child("shopB").setValue(newShopB);
                                    Toast.makeText(Request.this, "CustB Approval", Toast.LENGTH_SHORT).show();
                                    custDialog.cancel();
                                }
                            });
                            custDialog.show();
                        }
                    });
                }
                //shopA
                else if (passedEmail.equalsIgnoreCase(model.getShopA()) && (model.getCustflag() == true && model.getShopAflag() == false)) {
                    viewHolder.txtTransactionA.setText(model.getAmount());
                    viewHolder.txtTransactionC.setText(model.getCurrency());
                    viewHolder.txtTransactionR.setText(model.getReceiver());
                    final Transaction clickItem = model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(android.view.View view, int position, boolean isLongClick) {
                            Toast.makeText(Request.this, "Receiving: " + shopA, Toast.LENGTH_SHORT).show();
                            final String key = model.getKey();
                            //Dialog option number one
                            customDialog("Transaction Approval", "Are you sure you want to approve this transaction?", "cancel", "okMethod2", passedEmail, key);
                            //Dialog option number two
                            /*shopaDialog = new Dialog(Request.this);
                            shopaDialog.setContentView(R.layout.dialog_template2);
                            final TextView View = (TextView) shopaDialog.findViewById((R.id.view));
                            Button Approve = (Button) shopaDialog.findViewById(R.id.approve);

                            View.setEnabled(true);
                            Approve.setEnabled(true);

                            Approve.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    transaction.child(key).child("shopAflag").setValue(true);
                                    Toast.makeText(Request.this, "ShopA Approval", Toast.LENGTH_SHORT).show();
                                    shopaDialog.cancel();
                                }
                            });
                            shopaDialog.show();*/
                        }
                    });
                }
                //shopB
                else if (passedEmail.equalsIgnoreCase(model.getShopB()) && (model.getShopAflag() == true && model.getShopBflag() == false)) {
                    viewHolder.txtTransactionA.setText(model.getAmount());
                    viewHolder.txtTransactionC.setText(model.getCurrency());
                    viewHolder.txtTransactionR.setText(model.getReceiver());
                    final Transaction clickItem = model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(android.view.View view, int position, boolean isLongClick) {
                            Toast.makeText(Request.this, "Receiving: " + shopA, Toast.LENGTH_SHORT).show();
                            final String key = model.getKey();
                            //Dialog option number one
                            customDialog("Transaction Approval", "Are you sure you want to approve this transaction?", "cancel", "okMethod3", passedEmail, key);
                            //Dialog option number two
                            /*shopbDialog = new Dialog(Request.this);
                            shopbDialog.setContentView(R.layout.dialog_template2);
                            final TextView View = (TextView) shopbDialog.findViewById((R.id.view));
                            Button Approve = (Button) shopbDialog.findViewById(R.id.approve);

                            View.setEnabled(true);
                            Approve.setEnabled(true);

                            Approve.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //transaction.child("key").child("shopBflag").setValue(true);
                                    Toast.makeText(Request.this, "ShopB Approval", Toast.LENGTH_SHORT).show();
                                    shopbDialog.cancel();
                                }
                            });
                            shopbDialog.show();*/
                        }
                    });
                }
                else {
                    viewHolder.itemView.setVisibility(View.GONE);
                }
            }
        };
        recycler_menu.setAdapter(adapter);
    }
}