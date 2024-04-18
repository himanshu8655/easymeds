package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class chemist_filter extends AppCompatActivity implements View.OnClickListener{
    String key,arr[];
LinearLayout tp;
int h=0;
DatabaseReference databaseReference,databaseReference1;
ValueEventListener valueEventListener,valueEventListener1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chemist_filter);
        tp=findViewById(R.id.tp);
        Intent intent=getIntent();
        key=intent.getStringExtra("chemist_key");

        arr=key.split("/");

        for(int i=0;i<arr.length;i++)
        {

            databaseReference=FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(arr[i]);

            valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Button button=new Button(chemist_filter.this);
                    button.setId(h);
                    h++;
                    String phone = snapshot.child("ph").getValue().toString();
                    String location = snapshot.child("location").getValue().toString();
                    String name =snapshot.child("Name").getValue().toString();
                    CardView cv = new CardView(chemist_filter.this);
                    cv.setCardElevation(5);
                    cv.setRadius(25);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(50, 5, 50, 40);
                    cv.setLayoutParams(params);
                    cv.setPadding(10, 10, 10, 10);
                    cv.setBackgroundResource(R.color.cardview);
                    tp.addView(cv);
                    LinearLayout ll = new LinearLayout(chemist_filter.this);
                    ll.setPadding(20, 15, 20, 25);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    cv.addView(ll);
                    TextView tv = new TextView(chemist_filter.this);

                    tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                    tv.setTextColor(getResources().getColor(R.color.colorWhite));
                    tv.setText(name+"\n");
                    tv.setTextSize(25);
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    ll.addView(tv);
                    TextView tv1 = new TextView( chemist_filter.this);
                    tv1.setTextSize(17);
                    tv1.setTextColor(getResources().getColor(R.color.colorWhite));
                    String string = "Contact No: " + phone + "\n\n"+"Location: " + location + "\n";
                    tv1.setText(string);
                    ll.addView(tv1);



                    button.setOnClickListener(chemist_filter.this);
                    button.setText("BUY MEDICINES");
                    button.setPadding(20,20,20,20);
                    button.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shop_blue, 0, 0, 0);
                    button.setBackgroundResource(R.color.colorWhite);
                    button.setTextColor(getResources().getColor(R.color.cardview));
                    ll.addView(button);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        int j=view.getId();
        databaseReference1=FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(arr[view.getId()]);
        valueEventListener1=databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                Intent intent=new Intent(chemist_filter.this,buy_medicine.class);
                intent.putExtra("key",snapshot.child("id").getValue().toString());
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(chemist_filter.this,chatbot.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        databaseReference.removeEventListener(valueEventListener);
        databaseReference1.removeEventListener(valueEventListener1);

        super.onPause();
    }

    @Override
    protected void onStop() {

        databaseReference.removeEventListener(valueEventListener);
        databaseReference1.removeEventListener(valueEventListener1);

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        databaseReference.removeEventListener(valueEventListener);
        databaseReference1.removeEventListener(valueEventListener1);
        super.onDestroy();
    }
}