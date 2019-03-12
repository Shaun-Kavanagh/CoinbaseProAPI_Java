package project.finalyearapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import project.finalyearapp.Common.Common;
import project.finalyearapp.Interface.ItemClickListener;
import project.finalyearapp.Model.Transaction;
import project.finalyearapp.Model.User;
import project.finalyearapp.ViewHolder.TransViewHolder;

public class View extends AppCompatActivity {

    //Firebase Stuff
    FirebaseDatabase database;
    DatabaseReference start, transaction, ref,use;
    DatabaseReference user;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        final String passedEmail = getIntent().getStringExtra("Email");
        final String passedUserType = getIntent().getStringExtra("UserType");

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        start = database.getReference("Transaction");
        transaction = start.child(passedEmail);
        ref = database.getReference("User");
        use = ref.child(passedEmail);

        //Load Menu
        recycler_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();
    }

    private void loadMenu() {
        FirebaseRecyclerAdapter<Transaction, TransViewHolder> adapter = new FirebaseRecyclerAdapter<Transaction, TransViewHolder>(Transaction.class, R.layout.trans_item, TransViewHolder.class, start) {
            @Override
            protected void populateViewHolder(TransViewHolder viewHolder, Transaction model, int position) {
                final String passedUserType = getIntent().getStringExtra("UserType");
                final String passedEmail = getIntent().getStringExtra("Email");
                final String receiver = model.getReceiver().toString().replace(' ', '.');
                final String shopA = model.getShopA().toString().replace(' ', '.');
                final String shopB = model.getShopB().toString().replace(' ', '.');

                System.out.println("Amount: " + model.getAmount());
                System.out.println("User a customer? " + passedUserType.equalsIgnoreCase("customer"));
                System.out.println("User the creator? " + passedEmail.equalsIgnoreCase(model.getCreator()));
                //customer
                if (passedUserType.equalsIgnoreCase("customer")) {
                    //creator
                    if (passedEmail.equalsIgnoreCase(model.getCreator())) {
                        viewHolder.txtTransactionA.setText(model.getAmount());
                        viewHolder.txtTransactionC.setText(model.getCurrency());
                        viewHolder.txtTransactionR.setText(model.getReceiver());
                        final Transaction clickItem = model;
                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(android.view.View view, int position, boolean isLongClick) {
                                Toast.makeText(View.this, "Receiving: " + shopA + " Collection: " + shopB, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    //receiver
                    else if (passedEmail.equalsIgnoreCase(model.getReceiver())) {
                        viewHolder.txtTransactionA.setText(model.getAmount());
                        viewHolder.txtTransactionC.setText(model.getCurrency());
                        viewHolder.txtTransactionR.setText(model.getReceiver());
                        final Transaction clickItem = model;
                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(android.view.View view, int position, boolean isLongClick) {
                                Toast.makeText(View.this, "Receiving: " + shopA + " Collection: " + shopB, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        viewHolder.itemView.setVisibility(android.view.View.GONE);
                    }
                }
                //shop owner
                else if (passedUserType.equalsIgnoreCase("shopowner")) {
                    //shopA
                    if(passedEmail.equalsIgnoreCase(model.getCreator()) || passedEmail.equalsIgnoreCase(model.getShopA())) {
                        viewHolder.txtTransactionA.setText(model.getAmount());
                        viewHolder.txtTransactionC.setText(model.getCurrency());
                        viewHolder.txtTransactionR.setText(model.getReceiver());
                        final Transaction clickItem = model;
                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(android.view.View view, int position, boolean isLongClick) {
                                Toast.makeText(View.this, "Receiving: " + shopA + " Collection: " + shopB, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    //shopB
                    else if(passedEmail.equalsIgnoreCase(model.getCreator()) || passedEmail.equalsIgnoreCase(model.getShopB())) {
                        viewHolder.txtTransactionA.setText(model.getAmount());
                        viewHolder.txtTransactionC.setText(model.getCurrency());
                        viewHolder.txtTransactionR.setText(model.getReceiver());
                        final Transaction clickItem = model;
                        viewHolder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(android.view.View view, int position, boolean isLongClick) {
                                Toast.makeText(View.this, "Receiving: " + shopA + " Collection: " + shopB, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        viewHolder.itemView.setVisibility(android.view.View.GONE);
                    }
                }
                //none
                else {
                    viewHolder.itemView.setVisibility(android.view.View.GONE);
                }

                //original working view transaction with no conditionals
                /*viewHolder.txtTransactionA.setText(model.getAmount());
                viewHolder.txtTransactionC.setText(model.getCurrency());
                viewHolder.txtTransactionR.setText(receiver);
                final Transaction clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(android.view.View view, int position, boolean isLongClick) {
                        Toast.makeText(View.this, "Receiving: " + shopA + " Collection: " + shopB, Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        };
        recycler_menu.setAdapter(adapter);
    }
}
