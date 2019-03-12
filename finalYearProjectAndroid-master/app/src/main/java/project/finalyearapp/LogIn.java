package project.finalyearapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import project.finalyearapp.Common.Common;
import project.finalyearapp.Model.User;

public class LogIn extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;


    public String getEmail(String e) {
        edtEmail = (MaterialEditText)findViewById(R.id.edtEmail);
        String email = edtEmail.getText().toString().replace('.', ' ');
        return email;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        final String passedKey = getIntent().getStringExtra("key");

        edtEmail = (MaterialEditText)findViewById(R.id.edtEmail);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);

        btnLogin = (Button) findViewById(R.id.btnLogin);

        //Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        //final String email = edtEmail.getText().toString().replace('.', ' ');


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog mDialog = new ProgressDialog(LogIn.this);
                mDialog.setMessage("Please Wait...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String email = edtEmail.getText().toString().replace('.', ' ');
                        getEmail(email);
                        //Check if user doesn't exist in the database
                        if(dataSnapshot.child(email).exists()) {
                            mDialog.dismiss();
                            //Get User Information
                            User user = dataSnapshot.child(email).getValue(User.class);
                            if (edtPassword.getText().toString().equals(user.getPassword())) {
                                Toast.makeText(LogIn.this, "Log In Successful!", Toast.LENGTH_SHORT).show();
                                Intent mvWelcome = new Intent(LogIn.this, Welcome.class);
                                mvWelcome.putExtra("Email", email );
                                String userType = user.getUserType();
                                System.out.println(userType);
                                mvWelcome.putExtra("UserType", userType);
                                Common.currentUser = user;
                                startActivity(mvWelcome);
                                finish();
                            } else {
                                Toast.makeText(LogIn.this, "Log In failed!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            mDialog.dismiss();
                            Toast.makeText(LogIn.this, "User does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
