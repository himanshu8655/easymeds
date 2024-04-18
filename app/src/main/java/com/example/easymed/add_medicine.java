package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;

public class add_medicine extends AppCompatActivity {
    String barcodeData,dataset,a;
    Button db_btn;
    FirebaseUser user;
    EditText quantity;
    TextView medicine_name,dataset_tv;
    Button scan_again;
    int flag=0;
    DatabaseReference connectedRef;
    ValueEventListener valueEventListener;
    ImageButton btn_plus,btn_minus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);
        dataset_tv=findViewById(R.id.dataset_tv);
        btn_plus=findViewById(R.id.btn_plus);
        btn_minus=findViewById(R.id.btn_minus);
        medicine_name=findViewById(R.id.medicine_name);
        quantity=findViewById(R.id.quantity);
        scan_again=findViewById(R.id.scan_again);
        scan_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(add_medicine.this,MainActivity2.class);
                startActivity(intent);
            }
        });
        db_btn=findViewById(R.id.db_btn);
        user= FirebaseAuth.getInstance().getCurrentUser();
        Intent intent=getIntent();
        barcodeData=intent.getStringExtra("data");
        medicine_name.setText(barcodeData);
        FirebaseDatabase.getInstance().getReference().child("DATASET").child((barcodeData.substring(0,1)).toUpperCase()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot != null) {
                    dataset=snapshot.child("medicine_name").getValue().toString();
                    if ((dataset.toLowerCase()).contains("'"+barcodeData.toLowerCase()+"'")) {
                        dataset_tv.setText("Our System recognized this medicine");
                        dataset_tv.setVisibility(View.VISIBLE);
                        flag=0;
                    }
                    else {
                        dataset_tv.setText("Our System did couldn't find this medicine.On adding medicine it will automatically get added to our dataset");
                        dataset_tv.setVisibility(View.VISIBLE);
                        flag=1;
                    }
                }
                else
                {
                    dataset_tv.setText("Our System did couldn't find this medicine.On adding medicine it will automatically get added to our dataset");
                    dataset_tv.setVisibility(View.VISIBLE);
                    flag=1;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(barcodeData.contains("/")){
                    Toast.makeText(add_medicine.this,"slash not allowed in name",Toast.LENGTH_SHORT).show();
                    return;
                }
                {
                    connectedRef = FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(user.getUid()).child("medicine").child(barcodeData.toLowerCase());
                    valueEventListener=connectedRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            connectedRef.removeEventListener(valueEventListener);
                            if(snapshot.exists())
                            {
                                FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(user.getUid()).child("medicine")
                                        .child(barcodeData.toLowerCase()).child("quantity").setValue( Integer.parseInt(snapshot.child("quantity").getValue().toString())+Integer.parseInt(quantity.getText().toString()));
                                Toast.makeText(add_medicine.this,"medicine inventory updated",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(add_medicine.this,MainActivity2.class);
                                startActivity(intent);

                            }
                            else {
                                if(flag==1) {
                                    Toast.makeText(add_medicine.this,"medicine added to dataset",Toast.LENGTH_SHORT).show();
                                    HashMap<String, Object> map1 = new HashMap<>();
                                    if(dataset==null){
                                        map1.put("medicine_name","'"+barcodeData + "'");
                                        FirebaseDatabase.getInstance().getReference().child("DATASET").child((barcodeData.substring(0, 1)).toUpperCase()).setValue(map1);
                                    }
                                    else {
                                        map1.put("medicine_name", dataset + ", '" + barcodeData + "'");
                                        FirebaseDatabase.getInstance().getReference().child("DATASET").child((barcodeData.substring(0, 1)).toUpperCase()).setValue(map1);
                                    }
                                }
                                Map<String,Object> map=new HashMap<>();
                                map.put("quantity",Integer.parseInt(quantity.getText().toString()));
                                map.put("medicine_name",barcodeData.toLowerCase());
                                FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(user.getUid()).child("medicine").child(barcodeData.toLowerCase()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(add_medicine.this,"medicine added successfully",Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(add_medicine.this,MainActivity2.class);
                                        startActivity(intent);
                                    }
                                });

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }


            }
        });
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

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(add_medicine.this,MainActivity2.class);
        startActivity(intent);
    }
}