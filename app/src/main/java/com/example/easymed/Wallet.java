package com.example.easymed;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Wallet extends AppCompatActivity {
    Button Btn10,Btn200,Btn50,Btn100,Btn500,Btn1000,add_money,Txn_History,clear;
    TextView dp_bal;
    ImageButton back_btn;
    int sum=0,ed_bal;
    EditText ed_add;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference reff,reff_txn;
    FirebaseDatabase database;
    DatabaseReference connectedRef;
    ValueEventListener valueEventListener;
    int db_int_bal;
    DatabaseReference mDatabaseRef,mDatabse;
    String db_bal,wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wallet);
        back_btn=findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Wallet.this,HomeScreen.class);
                startActivity(intent);
            }
        });
        Btn10=findViewById(R.id.Btn10);
        Btn200=findViewById(R.id.Btn200);
        Btn50=(Button)findViewById(R.id.Btn50);
        Btn100=(Button)findViewById(R.id.Btn100);
        Btn500=(Button)findViewById(R.id.Btn500);
        Txn_History=(Button)findViewById(R.id.Txn_History) ;
        Btn1000=(Button)findViewById(R.id.Btn1000);
        clear=(Button)findViewById(R.id.clear);
        add_money=(Button)findViewById(R.id.add_money);
        ed_add=(EditText)findViewById(R.id.ed_add) ;
        dp_bal=(TextView)findViewById(R.id.dp_bal);
        database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        mDatabse = FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("wallet_txn");
        reff = FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid());
        reff_txn = FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid());
        Txn_History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Wallet.this, Txn_History.class);
                startActivity(intent);
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dp_bal.setText("₹ "+snapshot.child("wallet").getValue().toString());
                add_money.setClickable(true);
                add_money.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed_add.setText(null);
            }
        });
        Btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = ed_add.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    sum=10;
                    ed_add.setText(Integer.toString(sum));
                }
                else {
                    sum = sum + 10;
                    ed_add.setText(Integer.toString(sum));
                }

            }
        });
        Btn50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = ed_add.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    sum=50;
                    ed_add.setText(Integer.toString(sum));
                }
                else {
                    sum = sum + 50;
                    ed_add.setText(Integer.toString(sum));
                }

            }
        });
        Btn100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = ed_add.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    sum=100;
                    ed_add.setText(Integer.toString(sum));
                }
                else {


                    sum = sum + 100;
                    ed_add.setText(Integer.toString(sum));
                }

            }
        });
        Btn200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = ed_add.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    sum=200;
                    ed_add.setText(Integer.toString(sum));
                }
                else {
                    sum = sum + 200;
                    ed_add.setText(Integer.toString(sum));
                }

            }
        });
        Btn500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = ed_add.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    sum=500;
                    ed_add.setText(Integer.toString(sum));
                }
                else {
                    sum = sum + 500;
                    ed_add.setText(Integer.toString(sum));
                }

            }
        });
        Btn1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = ed_add.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    sum=1000;
                    ed_add.setText(Integer.toString(sum));
                }
                else {

                    sum = sum + 1000;
                    ed_add.setText(Integer.toString(sum));

                }
            }
        });
        add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                wallet = ed_add.getText().toString().trim();
                ed_add.setText(null);
                if (TextUtils.isEmpty(wallet)) {
                    Toast.makeText(getApplicationContext(), "Enter amount to add", Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                boolean di=TextUtils.isDigitsOnly(wallet);
                if(!di) {
                    Toast.makeText(Wallet.this,"Decimal number/special character not allowed",Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                ed_bal=(int)Float.parseFloat(wallet);

                if(ed_bal<=0){
                    Toast.makeText(getApplicationContext(), "Enter amount greater than 0", Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ed_add.setText(null);
                        Intent intent=new Intent(Wallet.this,PaymentGateway.class);
                        intent.putExtra("wallet",wallet);
                        startActivity(intent);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });

    }

    //    private void process_money() {
//        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
//        Date todayDate = new Date();
//        String thisDate = currentDate.format(todayDate);
//        documentReference= FirebaseFirestore.getInstance().collection("Member").document(user.getUid());
//        FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Double>() {
//            @Override
//            public Double apply(Transaction transaction) throws FirebaseFirestoreException {
//                DocumentSnapshot snapshot = transaction.get(documentReference);
//                double wallet1 = snapshot.getDouble("wallet");
//                if (wallet1 >= 0) {
//                    double wallet2=wallet1+ed_bal;
//                    transaction.update(documentReference, "wallet", wallet2);
//                    return wallet2;
//                } else {
//                    throw new FirebaseFirestoreException("Contact support for assistance",
//                            FirebaseFirestoreException.Code.ABORTED);
//                }
//            }
//        }).addOnSuccessListener(new OnSuccessListener<Double>() {
//            @Override
//            public void onSuccess(Double result) {
//                Toast.makeText(Wallet.this,"SUCCESS",Toast.LENGTH_SHORT).show();
//            }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(Wallet.this,"FAILURE",Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Wallet.this,HomeScreen.class);
        startActivity(intent);
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }
    @Override
    protected void onDestroy() {

        if(valueEventListener!=null) {
            connectedRef.removeEventListener(valueEventListener);
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {

        if(valueEventListener!=null) {
            connectedRef.removeEventListener(valueEventListener);
        }
        super.onStop();
    }

}
