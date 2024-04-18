package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class Register extends AppCompatActivity {
    private EditText inputEmail, inputPassword;     //hit option + enter if you on mac , for windows hit ctrl + enter
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private EditText phn_no,name;
    DatabaseReference reff;

    Member member;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        phn_no = (EditText) findViewById(R.id.phone_no);
        name = (EditText) findViewById(R.id.name);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressDialog = new ProgressDialog(Register.this);
        user=auth.getCurrentUser();

        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        reff= FirebaseDatabase.getInstance().getReference().child("Member");
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String phone_number = phn_no.getText().toString().trim();
                final String namee = name.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(email.contains("@") && email.contains(".com"))
                {}
                else {
                    Toast.makeText(getApplicationContext(), "Email ID is not of proper format!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone_number)) {
                    Toast.makeText(getApplicationContext(), "Enter Phone Number!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(namee)) {
                    Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(namee.length()<3){
                    Toast.makeText(getApplicationContext(), "Name must contain atleast 4 letter", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (namee.toLowerCase().equals("admin") || (namee.toLowerCase().contains("admin"))) {
                    Toast.makeText(getApplicationContext(), "Name cannot be admin", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isDigitsOnly(namee)){
                    Toast.makeText(getApplicationContext(), "Name cannot contain numbers", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phone_number.length() !=10) {
                    Toast.makeText(getApplicationContext(), "Phone number must be of 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean di=TextUtils.isDigitsOnly(phone_number);

                if (!di) {
                    Toast.makeText(getApplicationContext(), "Phone number must contain digits only", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;

                }

                progressDialog.setMessage("Registering Please Wait...");
                progressDialog.show();
                progressDialog.setCancelable(false);

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (!task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else {
                                    String Usertype="user";
                                    String uid = task.getResult().getUser().getUid();
                                    Member member=new Member(namee,phone_number,email,0,Usertype,uid);
                                    
                                    FirebaseDatabase.getInstance().getReference().child("Member").child(uid).setValue(member).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(Register.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(Register.this,HomeScreen.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                AuthCredential credential = EmailAuthProvider
                                                        .getCredential(email,password);

// Prompt the user to re-provide their sign-in credentials
                                                user.reauthenticate(credential)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(Register.this,"Account deleted",Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        });
                                            }
                                        }
                                    });


                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.dismiss();
    }
}
