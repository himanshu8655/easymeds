package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Txn_History extends AppCompatActivity {
    DatabaseReference mDatabase;
    ScrollView mScrollView;
    FirebaseUser user;
    Button button;
    int i=0;
    private TextView txn_count;
    private FirebaseAuth auth;
    LinearLayout ll;
    ValueEventListener valueEventListener;
    String tp,key,date,amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txn__history);
        auth = FirebaseAuth.getInstance();
        final LinearLayout linearLayout=(LinearLayout)findViewById(R.id.ll);
        mScrollView=(ScrollView)findViewById(R.id.mscroll);
        ll=findViewById(R.id.ll);

        txn_count=(TextView)findViewById(R.id.txn_count);
        user=auth.getCurrentUser();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Member/"+user.getUid()+"/Transaction_History");
        valueEventListener=mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                if(snapshot.exists()) {

                    key = snapshot.child("UpiID").getValue().toString();
                    date = snapshot.child("date").getValue().toString();
                    amount = snapshot.child("amount").getValue().toString();
                    View v = new View(Txn_History.this);
                    v.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            5
                    ));
                    v.setBackgroundColor(Color.parseColor("#B3B3B3"));

                    ll.addView(v);
                    if (amount.contains("+")) {
                        TextViewGreen();
                        i++;
                        txn_count.setText("Transactions: " + Integer.toString(i));

                    } else if (amount.contains("-")) {
                        TextViewRed();
                        i++;
                        txn_count.setText("Transactions: " + Integer.toString(i));
                    }
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    public void TextViewRed()
    {
        TextView textView = new TextView(Txn_History.this);
        textView.setTextColor(Color.rgb(63, 81, 181));
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
        textView.setText("PAYMENT SUCCESSFUL");
        textView.setTextSize(30);
        textView.setPadding(80,30,0,0);
        ll.addView(textView);

        TextView textView1 = new TextView(Txn_History.this);
        textView1.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
        textView1.setText("Txn ID: "+key);
        textView1.setTextSize(20);
        textView1.setTypeface(null, Typeface.ITALIC);
        textView1.setPadding(20,10,0,0);
        textView1.setTextColor(Color.rgb(217, 7, 7));
        ll.addView(textView1);

        TextView textView2 = new TextView(Txn_History.this);
        textView2.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
        textView2.setText("Date: "+date);
        textView2.setTextSize(20);
        textView2.setTypeface(null, Typeface.BOLD);
        textView2.setPadding(20,10,0,0);
        textView2.setTextColor(Color.rgb(217, 7, 7));
        ll.addView(textView2);

        TextView textView3 = new TextView(Txn_History.this);
        textView3.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
        textView3.setText("Balance: "+amount);
        textView3.setTextSize(20);
        textView3.setTypeface(null, Typeface.BOLD);
        textView3.setPadding(20,10,0,0);
        textView3.setTextColor(Color.rgb(217, 7, 7));
        ll.addView(textView3);
    }


    public void TextViewGreen()
    {
        TextView textView = new TextView(Txn_History.this);
        textView.setTextColor(Color.rgb(63, 81, 181));
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
        textView.setText("PAYMENT SUCCESSFUL");
        textView.setTextSize(30);
        textView.setPadding(80,30,0,0);
        ll.addView(textView);

        TextView textView1 = new TextView(Txn_History.this);
        textView1.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
        textView1.setText("Txn ID: "+key);
        textView1.setTextSize(20);
        textView1.setTypeface(null, Typeface.ITALIC);
        textView1.setPadding(20,10,0,0);
        textView1.setTextColor(Color.rgb(0,127,0));
        ll.addView(textView1);

        TextView textView2 = new TextView(Txn_History.this);
        textView2.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
        textView2.setText("Date: "+date);
        textView2.setTextSize(20);
        textView2.setTypeface(null, Typeface.BOLD);
        textView2.setPadding(20,10,0,0);
        textView2.setTextColor(Color.rgb(0,127,0));
        ll.addView(textView2);

        TextView textView3 = new TextView(Txn_History.this);
        textView3.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
        textView3.setText("Balance: "+amount);
        textView3.setTextSize(20);
        textView3.setTypeface(null, Typeface.BOLD);
        textView3.setPadding(20,10,0,0);
        textView3.setTextColor(Color.rgb(0,127,0));
        ll.addView(textView3);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Txn_History.this,HomeScreen.class);
        startActivity(intent);

    }

}