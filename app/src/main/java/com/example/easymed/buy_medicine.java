package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class  buy_medicine extends AppCompatActivity implements ElegantNumberButton.OnValueChangeListener {
String key;

LinearLayout layout;
int sum=0;
ProgressBar progress;
Button cart_btn;
TextView display;
DatabaseReference databaseReference;
ValueEventListener valueEventListener;
int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine);
        display=findViewById(R.id.display);
        progress=findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        layout=findViewById(R.id.layot);
        final String[] quantity_db=new String[1000];
        cart_btn=findViewById(R.id.cart_btn);
        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(buy_medicine.this,cart.class);
                String med_name[]=new String[i];
                String quantity[]=new String[i];
                for(int j=1;j<=i;j=j+2)
                {
                    ElegantNumberButton elegantNumberButton=findViewById(j);
                    TextView textView=findViewById(j+1);
                    med_name[j]=textView.getText().toString();
                    quantity[j]=elegantNumberButton.getNumber();
                }

                intent.putExtra("med_name",med_name);
                intent.putExtra("quantity",quantity);
                intent.putExtra("quantity_db",quantity_db);
                intent.putExtra("key",key);
                startActivity(intent);
            }
        });

        Intent intent=getIntent();
       key= intent.getStringExtra("key");
       display.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").child(key).child("medicine");
        valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.removeEventListener(valueEventListener);
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    display.setVisibility(View.GONE);
                    String name = dataSnapshot.child("medicine_name").getValue().toString();
                    String quantity = dataSnapshot.child("quantity").getValue().toString();
                    if(Integer.parseInt(quantity)>0) {
                        RelativeLayout relativeLayout = new RelativeLayout(buy_medicine.this);
                        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        param.setMargins(10, 10, 10, 10);
                        relativeLayout.setLayoutParams(param);
                        layout.addView(relativeLayout);
                        ElegantNumberButton elegantNumberButton = new ElegantNumberButton(buy_medicine.this);
                        i++;
                        elegantNumberButton.setId(i);//1
                        quantity_db[i]=quantity;
                        elegantNumberButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        elegantNumberButton.setBackground(null);
                        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(250, 150);
                        para.setMargins(0, 0, 10, 10);
                        elegantNumberButton.setLayoutParams(para);
                        elegantNumberButton.setOnValueChangeListener(buy_medicine.this);
                        relativeLayout.addView(elegantNumberButton);
                        addOrRemoveProperty(elegantNumberButton, RelativeLayout.ALIGN_PARENT_RIGHT, true);
                        addOrRemoveProperty(elegantNumberButton, RelativeLayout.CENTER_VERTICAL, true);
                        TextView textView = new TextView(buy_medicine.this);
                        textView.setText(name);
                        i++;
                        textView.setId(i);//2
                        textView.setTextSize(24);

                        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params1.addRule(RelativeLayout.LEFT_OF, elegantNumberButton.getId());
                        params1.setMargins(10, 10, 10, 10);
                        textView.setLayoutParams(params1);
                        relativeLayout.addView(textView);
                        addOrRemoveProperty(textView, RelativeLayout.CENTER_VERTICAL, true);
//                    View v = new View(buy_medicine.this);
//                    v.setLayoutParams(new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            5
//                    ));
//                    v.setBackgroundColor(Color.parseColor("#B3B3B3"));
//
//                    layout.addView(v);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    @Override
    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
        if(oldValue<newValue){
            sum++;
        }
        if(newValue<oldValue){
            sum--;
        }
        if(sum==0)
        {
            cart_btn.setVisibility(View.GONE);
        }
        else {
            cart_btn.setVisibility(View.VISIBLE);
        }
    }
}