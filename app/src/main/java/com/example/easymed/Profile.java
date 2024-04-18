package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.w3c.dom.Text;

public class Profile extends AppCompatActivity {
    FirebaseUser user;
    FirebaseAuth auth;
    EditText mail,not_edit;
    ImageButton edit_password,btn_back,activity_back;
    ProgressBar progressBar;
    LinearLayout layout_1,layout_2;
    private EditText name,phone_number,email,old_pass_ed,confirm_pass_ed,password1;
    private Button db_btn,change_passbtn;
    String name1,phone_number1,email1,timer_status1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);
        not_edit=findViewById(R.id.not_edit);
        mail=findViewById(R.id.mail);
        mail.setKeyListener(null);
        not_edit.setKeyListener(null);
        activity_back=findViewById(R.id.activity_back);
        activity_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this,HomeScreen.class);
                startActivity(intent);
            }
        });
        layout_1=findViewById(R.id.layout_1);
        layout_2=findViewById(R.id.layout_2);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        mail=findViewById(R.id.mail);
        btn_back=findViewById(R.id.btn_back);
        password1=findViewById(R.id.password1);
        old_pass_ed=findViewById(R.id.old_pass_ed);
        confirm_pass_ed=findViewById(R.id.confirm_pass_ed);
        db_btn=findViewById(R.id.db_button);
        phone_number=findViewById(R.id.phone_no);
        email=findViewById(R.id.email);
        email.setKeyListener(null);
        edit_password=findViewById(R.id.edit_password);
        change_passbtn=findViewById(R.id.pass_btn);

        name=findViewById(R.id.name);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        edit_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setError(null);
                phone_number.setError(null);
                layout_1.setVisibility(View.GONE);
                layout_2.setVisibility(View.VISIBLE);
                change_passbtn.setVisibility(View.VISIBLE);
                db_btn.setVisibility(View.GONE);
                btn_back.setVisibility(View.VISIBLE);
                activity_back.setVisibility(View.GONE);

            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_1.setVisibility(View.VISIBLE);
                layout_2.setVisibility(View.GONE);
                btn_back.setVisibility(View.GONE);
                activity_back.setVisibility(View.VISIBLE);
                db_btn.setVisibility(View.VISIBLE);
                change_passbtn.setVisibility(View.GONE);
            }
        });

        change_passbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if(!isConnected())
                {
                    if(timer_status1=="false"|| timer_status1==null) {
                        timerstart1();
                        Toast.makeText(Profile.this,"Check Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(old_pass_ed.getText().toString())) {
                    old_pass_ed.setError("Field can't be blank");
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                if(password1.getText().length()<6)
                {
                    old_pass_ed.setText(null);
                    confirm_pass_ed.setText(null);
                    password1.setText(null);
                    password1.setError("Minimum length should be of 6 digits");
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                if (!password1.getText().toString().equals(confirm_pass_ed.getText().toString())) {
                    old_pass_ed.setText(null);
                    confirm_pass_ed.setText(null);
                    password1.setText(null);
                    confirm_pass_ed.setError("Password do not match");
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                final String email = user.getEmail();
                AuthCredential credential = EmailAuthProvider.getCredential(email,old_pass_ed.getText().toString());
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(password1.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Profile.this, "Password failed to change!", Toast.LENGTH_SHORT).show();
                                        old_pass_ed.setText(null);
                                        confirm_pass_ed.setText(null);
                                        password1.setText(null);
                                        progressBar.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    } else {
                                        Toast.makeText(Profile.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                                        goback();
                                        old_pass_ed.setText(null);
                                        confirm_pass_ed.setText(null);
                                        password1.setText(null);
                                        progressBar.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Profile.this, "Password failed to update", Toast.LENGTH_SHORT).show();
                            old_pass_ed.setText(null);
                            confirm_pass_ed.setText(null);
                            password1.setText(null);
                            progressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }
                });
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("Name").getValue().toString());
                email.setText(snapshot.child("Email").getValue().toString());
                phone_number.setText(snapshot.child("ph").getValue().toString());
                mail.setText(snapshot.child("Email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if((name.getText().toString()).equals(name1)&& (phone_number.getText().toString()).equals(phone_number1) && (email.getText().toString()).equals(email1))
                {
                    Toast.makeText(Profile.this,"No Changes Detected",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                if (TextUtils.isEmpty(name.getText().toString())) {
                    name.setError("Field can't be empty");
                    progressBar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                if(name.getText().toString().length()<3){
                    Toast.makeText(getApplicationContext(), "Name must contain atleast 4 letter", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                if (name.getText().toString().toLowerCase().equals("admin")) {
                    Toast.makeText(getApplicationContext(), "Name cannot be admin", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                if(name.getText().toString().matches("[0-9]+")){
                    Toast.makeText(getApplicationContext(), "Name cannot contain numbers", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }

                if(name.getText().toString().matches("[\\`|\\~|\\!|\\@|\\#|\\$|\\%|\\^|\\&|\\*|\\(|\\)|\\+|\\=|\\[|\\{|\\]|\\}|\\||\\\\|\\'|\\<|\\,|\\.|\\>|\\?|\\/|\\\"\"|\\;|\\:|\\s]+")){
                    Toast.makeText(getApplicationContext(), "Name cannot contain special character", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }

                if (TextUtils.isEmpty(phone_number.getText().toString())) {
                    phone_number.setError("Field can't be empty");

                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                boolean di=TextUtils.isDigitsOnly(phone_number.getText().toString());

                if (!di) {
                    phone_number.setError("Phone number must contain digits only");
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }
                if(phone_number.length()!=10)
                {
                    phone_number.setError("Phone Number must be of 10 digits");
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    return;
                }

                FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("Name").setValue(name.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("ph").setValue(phone_number.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(Profile.this,HomeScreen.class);
                            startActivity(intent);
                            Toast.makeText(Profile.this,"Profile Updated",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                        else {
                            Toast.makeText(Profile.this,"Failed To Update Profile",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }
                });

            }

        });

    }

    private void startAnimation() {
        ProgressBarAnimation localProgressBarAnimation = new ProgressBarAnimation(0.0F, 75.0F);
        localProgressBarAnimation.setInterpolator(new OvershootInterpolator(0.5F));
        localProgressBarAnimation.setDuration(4000L);
        progressBar.startAnimation(localProgressBarAnimation);
    }
    private class ProgressBarAnimation extends Animation {
        private float from;
        private float to;

        public ProgressBarAnimation(float from, float to) {
            this.from = from;
            this.to = to;
        }

        protected void applyTransformation(float paramFloat, Transformation paramTransformation) {
            super.applyTransformation(paramFloat, paramTransformation);
            float f = this.from + paramFloat * (this.to - this.from);
            progressBar.setProgress((int) f);
        }
    }


    private void goback() {
        {

            old_pass_ed.setText(null);
            confirm_pass_ed.setText(null);
            password1.setText(null);
            old_pass_ed.setError(null);
            confirm_pass_ed.setError(null);
            layout_1.setVisibility(View.VISIBLE);
            layout_2.setVisibility(View.GONE);
            change_passbtn.setVisibility(View.GONE);
            db_btn.setVisibility(View.VISIBLE);
            old_pass_ed.setError(null);
        }
    }
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
    private void timerstart1() {
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
                timer_status1="true";
            }
            public void onFinish() {
                timer_status1="false";
            }
        }.start();
    }
    @Override
    public void onBackPressed() {
        if(change_passbtn.getVisibility()==View.VISIBLE) {
            goback();
        }
        else{
            Intent intent=new Intent(Profile.this,HomeScreen.class);
            startActivity(intent);
        }

    }
}
