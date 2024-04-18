package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class cart extends AppCompatActivity {
String[] quantity,med_name,quantity_db;
String bal,key,error_msg="",book_admin="",name;
LinearLayout lk;
int flag=0,j=0;
Button buy_btn;
FirebaseUser user;
TextView bal_db,bal_1,total_tv;
String admin_key;
DatabaseReference databaseReference;
ValueEventListener valueEventListener,val,val1;
String book_hist="";
int total=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        final ProgressDialog progressDialog=new ProgressDialog(cart.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Placing Order Please Wait....");
        buy_btn=findViewById(R.id.buy_btn);
        user= FirebaseAuth.getInstance().getCurrentUser();
        lk=findViewById(R.id.lk);
        total_tv=findViewById(R.id.total_tv);
        bal_db=findViewById(R.id.db_bal1);bal_1=findViewById(R.id.bal_2);
        Intent intent=getIntent();
        quantity=intent.getStringArrayExtra("quantity");
        quantity_db=intent.getStringArrayExtra("quantity_db");
        med_name=intent.getStringArrayExtra("med_name");
        key=intent.getStringExtra("key");
        databaseReference=FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(key).child("medicine");
        for(int i=1;i<=quantity.length;i=i+2)
        {
            if(Integer.parseInt(quantity[i])>0){
                TextView textView = new TextView(cart.this);
                textView.setText(med_name[i]);
                textView.setTextSize(18);
                TextView textView1 = new TextView(cart.this);
                textView1.setText(quantity[i]);
                textView1.setTextSize(18);
                RelativeLayout relativeLayout = new RelativeLayout(cart.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 100, 25);
                relativeLayout.setLayoutParams(params);
                relativeLayout.addView(textView);
                relativeLayout.addView(textView1);
                addOrRemoveProperty(textView, RelativeLayout.ALIGN_PARENT_LEFT, true);
                addOrRemoveProperty(textView1, RelativeLayout.ALIGN_PARENT_RIGHT, true);
                lk.addView(relativeLayout);
            }
        }
        val1=FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).removeEventListener(val1);
               bal=snapshot.child("wallet").getValue().toString();
               name=snapshot.child("Name").getValue().toString();
               book_admin=book_admin+"Name: "+name;
                bal_db.setText("BALANCE: ₹"+bal);
                for(int i=1;i<=quantity.length;i=i+2)
                {
                    total=total+Integer.parseInt(quantity[i]);
                }
                if(Integer.parseInt(bal)>=total*100){
                bal_1.setText("BAL AFTER PURCHASE: ₹"+Integer.toString(Integer.parseInt(bal)-total*100));}
                else {
                    bal_1.setText("ADDITIONAL BAL REQUIRED: ₹"+Integer.toString(total*100-Integer.parseInt(bal)));
                }
                total_tv.setText("₹"+Integer.toString(total*100));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
startTimer(1000);
        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if(Integer.parseInt(bal)>=total*100) {
                    for (int i = 1; i <= quantity.length; i = i + 2) {
                        if(Integer.parseInt(quantity[i])>0) {
                            if(Integer.parseInt(quantity_db[i])>=Integer.parseInt(quantity[i]))
                            {

                            }
                            else {
                                progressDialog.hide();
                                error_msg="Only "+error_msg+quantity_db[i]+" "+med_name[i]+" is available in stock\n";
                                flag=1;
                            }
                        }
                    }
                    if(flag==0){
                        FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("wallet").setValue(Integer.parseInt(bal)-total*100);

                        valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                databaseReference.removeEventListener(valueEventListener);
                                for(int i=1;i<=quantity.length;i=i+2) {
                                    if (Integer.parseInt(quantity[i] )> 0) {
                                        int a = Integer.parseInt(snapshot.child(med_name[i]).child("quantity").getValue().toString());
                                        FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(key)
                                                .child("medicine").child(med_name[i]).child("quantity").setValue(a - Integer.parseInt(quantity[i]));
                                        j++;
                                            book_hist=book_hist+"\n"+j+") "+(med_name[i] + " (X" + quantity[i] + ")");
                                            book_admin=book_admin+"\n"+j+") "+(med_name[i] + " (X" + quantity[i] + ")");





                                    }
                                }
                                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("booking").push().setValue(book_hist);
                                FirebaseDatabase.getInstance().getReference().child("Member").child(admin_key).child("book_admin").push().setValue(book_admin);

                                String key1=FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("Transaction_History").push().getKey();
                                history_db historyDb=new history_db(date,key1,"-"+Integer.toString(total*100));
                                FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("Transaction_History").child(key1).setValue(historyDb);
                                progressDialog.hide();
                                Toast.makeText(cart.this,"Order Placed Successfully",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(cart.this,HomeScreen.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.hide();
                            }
                        });
                    }
                    else {
                            Toast.makeText(cart.this,error_msg,Toast.LENGTH_LONG).show();
                            progressDialog.hide();
                            error_msg="";
                    }
                }
                else {
                    Toast.makeText(cart.this,"Insufficient Balance",Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }

            }
        });
    }
    private void addOrRemoveProperty(View view, int property, boolean flag){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if(flag){
            layoutParams.addRule(property);
        }else {
            layoutParams.removeRule(property);
        }
        view.setLayoutParams(layoutParams);
    }
    private void startTimer(int noOfMinutes) {
        CountDownTimer countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            }
            public void onFinish() {

                val=FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(key).removeEventListener(val);
                        String date = new SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault()).format(new Date());
                        String date1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        book_admin=book_admin+"\nOrder ID: "+date+"\nTotal Price: ₹"+total*100+" (Prepaid)\n\nMedicine Name:";
                        admin_key=snapshot.child("id").getValue().toString();
                        book_hist=book_hist+snapshot.child("Name").getValue().toString()+"\n\nOrder ID: "+date+"\nDate: "+date1+"\nTotal Price: ₹"+total*100+"\n\nMedicine Name:";
                    buy_btn.setEnabled(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }.start();

    }
}