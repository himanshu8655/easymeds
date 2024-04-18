package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ChemistSetting extends AppCompatActivity {
    DatabaseReference reff;
    FirebaseUser user;
    LinearLayout linearLayout;
    TextView order_history;
    Context context;
    Button book_history,offline_booking,add_turf;
    LocationManager locationManager;

    String provider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chemist_setting);
        context=getApplicationContext();
        order_history=findViewById(R.id.order_history);
        order_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChemistSetting.this,Book_admin.class);
                startActivity(intent);
            }
        });
        user= FirebaseAuth.getInstance().getCurrentUser();
        locationManager= (LocationManager) context.getSystemService(LOCATION_SERVICE);
        book_history=findViewById(R.id.book_history);
        provider= locationManager.getBestProvider(new Criteria(), false);
        offline_booking=findViewById(R.id.offline_booking);

        add_turf=findViewById(R.id.add_turf);
        offline_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChemistSetting.this,Offline_QR.class);
                startActivity(intent);
            }
        });

            FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                    visible();
                    }
                    else {
                        add_turf.setText("Edit chemist");
                        visible();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        add_turf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChemistSetting.this,add_chemist.class);
                startActivity(intent);
            }
        });
        book_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChemistSetting.this,MainActivity2.class);
                startActivity(intent);
            }
        });

    }

    private void visible() {
        add_turf.setVisibility(View.VISIBLE);
        order_history.setVisibility(View.VISIBLE);
        book_history.setVisibility(View.VISIBLE);
        offline_booking.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ChemistSetting.this,HomeScreen.class);
        startActivity(intent);
    }


}