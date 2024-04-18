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
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Booking extends AppCompatActivity {
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    LinearLayout linearLayout;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        linearLayout=findViewById(R.id.lw);
        user= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("booking");
        valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.removeEventListener(valueEventListener);
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    String data=dataSnapshot.getValue().toString();
                    View v = new View(Booking.this);
                    v.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            5
                    ));
                    v.setBackgroundColor(Color.parseColor("#B3B3B3"));

                    linearLayout.addView(v);
                    CardView cv = new CardView(Booking.this);
                    cv.setCardElevation(5);
                    cv.setRadius(25);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(50, 40, 50, 40);
                    cv.setLayoutParams(params);
                    cv.setPadding(10, 10, 10, 10);
                    cv.setBackgroundResource(R.color.colorWhite);
                    linearLayout.addView(cv);
                    LinearLayout ll = new LinearLayout(Booking.this);
                    ll.setPadding(20, 15, 20, 25);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    cv.addView(ll);
                    TextView textView=new TextView(Booking.this);
                    SpannableString ss = new SpannableString(data);
                    ss.setSpan(new RelativeSizeSpan(1.4f),0, data.indexOf("\n") , 0);
                    ss.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0,data.indexOf("\n"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, data.indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(ss);
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