package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Book_admin extends AppCompatActivity {
    LinearLayout linearLayout;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_admin);
        user= FirebaseAuth.getInstance().getCurrentUser();
        linearLayout=findViewById(R.id.linearlayout12);
        FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("book_admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String data=dataSnapshot.getValue().toString();
                    View v = new View(Book_admin.this);
                    v.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            5
                    ));
                    v.setBackgroundColor(Color.parseColor("#B3B3B3"));

                    linearLayout.addView(v);
                    CardView cv = new CardView(Book_admin.this);
                    cv.setCardElevation(5);
                    cv.setRadius(25);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(50, 40, 50, 40);
                    cv.setLayoutParams(params);
                    cv.setPadding(10, 10, 10, 10);
                    cv.setBackgroundResource(R.color.colorWhite);
                    linearLayout.addView(cv);
                    LinearLayout ll = new LinearLayout(Book_admin.this);
                    ll.setPadding(20, 15, 20, 25);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    cv.addView(ll);
                    TextView textView=new TextView(Book_admin.this);
                    textView.setText(data);
                    textView.setTextSize(20);
                    textView.setTextColor(getResources().getColor(R.color.cardview));
                    ll.addView(textView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}