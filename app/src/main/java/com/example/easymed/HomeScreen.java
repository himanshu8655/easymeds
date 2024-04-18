package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeScreen extends AppCompatActivity {
    ProgressBar progress_id;
    FirebaseUser user;
    FirebaseAuth auth;
    FloatingActionButton floatingActionButton;
    DatabaseReference reff1;
    ImageButton mSignOut;
    int status;
    private FusedLocationProviderClient fusedLocationProviderClient;
    GridLayout gridlayout;
    CardView cardview;

    EditText ed_name,ed_phone_number;
    Button db_button;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    // public static final String TAG="Account "

    TextView name,wallettv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        setupFirebaseListener();
        floatingActionButton=findViewById(R.id.float_btn);
        progress_id=findViewById(R.id.progress_id);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        db_button=findViewById(R.id.db_button);
        ed_phone_number=findViewById(R.id.ed_phone_no);
        ed_name=findViewById(R.id.ed_name);
        mSignOut=findViewById(R.id.sign_out);
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });
        gridlayout=findViewById(R.id.gridlayout);
        cardview=findViewById(R.id.cardview);
        final CardView cv1 = (CardView) findViewById (R.id.cv1);
        final  CardView cv2 = (CardView) findViewById (R.id.cv2);
        final  CardView cv3 = (CardView) findViewById (R.id.cv3);
        final  CardView cv4 = (CardView) findViewById (R.id.cv4);
        final CardView cv5 = (CardView) findViewById (R.id.cv5);
        final CardView cv6 = (CardView) findViewById (R.id.cv6);
        final CardView cv7 = (CardView) findViewById (R.id.cv7);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        name=(TextView)findViewById(R.id.name);
        wallettv=(TextView)findViewById(R.id.wallettv);

        reff1=FirebaseDatabase.getInstance().getReference().child("Member");
        FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    gridlayout.setVisibility(View.VISIBLE);
                    progress_id.setVisibility(View.GONE);
                    name.setText("Welcome "+snapshot.child("Name").getValue().toString());
                    wallettv.setText("BALANCE : ₹"+snapshot.child("wallet").getValue().toString());
                    if(snapshot.child("Usertype").getValue().toString().equals("ADMIN"))
                    {
                        cv7.setVisibility(View.VISIBLE);
                        floatingActionButton.setVisibility(View.VISIBLE);

                    }
                    else if(snapshot.child("Usertype").getValue().toString().equals("user"))
                    {
                        cv7.setVisibility(View.GONE);
                    }
                    else
                    {
                        cv7.setVisibility(View.GONE);
                    }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if(getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location!=null){
                                    Double lat=location.getLatitude();
                                    Double lon=location.getLongitude();
                                }
                            }
                        });
                }
                else{
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION },1);
                }

            }
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeScreen.this,Query.class);
                startActivity(intent);
            }
        });
        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        if(getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(HomeScreen.this, ScrollingActivity.class);
                            startActivity(intent);

                        }
                        else{
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION },1);
                        }

                    }


                }
                else                 Toast.makeText(HomeScreen.this, "Check Your Internet Connection.....", Toast.LENGTH_SHORT).show();

            }
        });
        cv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {

                    Intent intent = new Intent(HomeScreen.this, Booking.class);
                    startActivity(intent);
                }
                else                 Toast.makeText(HomeScreen.this, "Check Your Internet Connection.....", Toast.LENGTH_SHORT).show();

            }
        });
        cv3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isConnected()) {

                    Intent intent = new Intent(HomeScreen.this, Wallet.class);
                    startActivity(intent);
                }
                else  Toast.makeText(HomeScreen.this, "Check Your Internet Connection.....", Toast.LENGTH_SHORT).show();

            }
        });
        cv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {

                    Intent intent=new Intent(HomeScreen.this,Profile.class);
                    startActivity(intent);
                }
                else                 Toast.makeText(HomeScreen.this, "Check Your Internet Connection.....", Toast.LENGTH_SHORT).show();

            }
        });
        cv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreen.this,chatbot.class);
                startActivity(intent);
            }
        });
        cv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    Intent intent = new Intent(HomeScreen.this, Txn_History.class);
                    startActivity(intent);
                }
                else                 Toast.makeText(HomeScreen.this, "Check Your Internet Connection.....", Toast.LENGTH_SHORT).show();

            }
        });
        cv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreen.this,ChemistSetting.class);
                startActivity(intent);
            }
        });
        db_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String namee = name.getText().toString().trim();
                final String phone_number = ed_phone_number.getText().toString().trim();
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
                if (namee.toLowerCase().equals("admin")) {
                    Toast.makeText(getApplicationContext(), "Name cannot be admin", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean di=TextUtils.isDigitsOnly(phone_number);

                if (!di) {
                    Toast.makeText(getApplicationContext(), "Phone number must contain digits only", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phone_number.length() !=10 && phone_number.matches("^[0-9]*$")) {
                    Toast.makeText(getApplicationContext(), "Phone number must be of 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                Member member=new Member(ed_name.getText().toString(),ed_phone_number.getText().toString(),user.getEmail(),0,"user",user.getUid());

            }
        });
    }



    public boolean isConnected() {
        return true;
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(HomeScreen.this,Login.class);
        startActivity(intent);
    }
    private void setupFirebaseListener(){
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null)
                {

                }
                else {
                    Intent intent=new Intent(HomeScreen.this,Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener!=null)
        {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

}

