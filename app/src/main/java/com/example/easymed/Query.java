package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Query extends AppCompatActivity {
    LinearLayout linearLayout;
    ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        linearLayout=findViewById(R.id.query_ll);
        valueEventListener=FirebaseDatabase.getInstance().getReference().child("SUPPORT").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance().getReference().child("SUPPORT").removeEventListener(valueEventListener);
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    View v = new View(Query.this);
                    v.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            5
                    ));
                    v.setBackgroundColor(Color.parseColor("#B3B3B3"));

                    linearLayout.addView(v);
                   String message=dataSnapshot.getValue().toString();

                    TextView textView=new TextView(Query.this);
                    textView.setText(message);
                    textView.setTextSize(18);
                    linearLayout.addView(textView);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}