package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Offline_Cart extends AppCompatActivity {
    FirebaseUser user;
    String barcodeData;
    Button db_btn;
    EditText quantity;
    TextView medicine_name,dataset_tv;
    Button scan_again;
    ImageButton btn_plus,btn_minus;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline__cart);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dataset_tv=findViewById(R.id.dataset_tv);
        btn_plus=findViewById(R.id.btn_plus);
        btn_minus=findViewById(R.id.btn_minus);
        medicine_name=findViewById(R.id.medicine_name);
        quantity=findViewById(R.id.quantity);
        scan_again=findViewById(R.id.scan_again);
        scan_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Offline_Cart.this,Offline_QR.class);
                startActivity(intent);
            }
        });
        db_btn=findViewById(R.id.db_btn);
        user= FirebaseAuth.getInstance().getCurrentUser();
        Intent intent=getIntent();
        barcodeData=intent.getStringExtra("data");
        medicine_name.setText(barcodeData);
        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a=quantity.getText().toString();
                if(a==null || a.equals("")){
                    quantity.setText("1");
                }
                else {
                    quantity.setText(Integer.toString(Integer.parseInt(a)+1));
                }
            }
        });

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a=quantity.getText().toString();
                if(a.equals(null) ||a.equals("") ||Integer.parseInt(a)<=0 ){
                    quantity.setText("0");
                }
                else {
                    quantity.setText(Integer.toString(Integer.parseInt(a)-1));
                }
            }
        });
        db_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity.getText().toString().equals(null)){
                    return;
                }
                databaseReference=FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(user.getUid());
               valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.removeEventListener(valueEventListener);
                        if(snapshot.child("medicine").child(barcodeData).exists()){
                            int a=Integer.parseInt(snapshot.child("medicine").child(barcodeData).child("quantity").getValue().toString());
                            if(a>=Integer.parseInt(quantity.getText().toString())){
                                FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(user.getUid()).child("medicine").child(barcodeData).child("quantity").setValue(a-Integer.parseInt(quantity.getText().toString()));

                            Toast.makeText(Offline_Cart.this,"Buy Successful",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Offline_Cart.this,Offline_QR.class);
                            startActivity(intent);
                            }
                            else{
                                Toast.makeText(Offline_Cart.this,"Only "+a+" medicines are in stock",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(Offline_Cart.this,"Medicine Not Recognized",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Offline_Cart.this,Offline_QR.class);
        startActivity(intent);
    }
}