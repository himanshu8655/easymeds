package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easymed.adapters.ChatAdapter;
import com.example.easymed.helpers.SendMessageInBg;
import com.example.easymed.interfaces.BotReply;
import com.example.easymed.models.Message;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class chatbot extends AppCompatActivity implements BotReply,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    DatabaseReference mDatabase, mDatabase1;
    FirebaseUser user;
    private ImageView bu;
    LinearLayout chatlayout;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    private EditText ed;
    ProgressBar progressBar;
    String previous_msg="";
    private ScrollView scrollView;
    int current_count=0;
    private FirebaseAuth auth;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private double latitude,longitude;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationManager locationManager;

    String chemist_key_str="";
    int chemist_key_int=0;
    int chemist_count=0;
    String str[] = new String[100];
    int flag=0;
    String date;
    String name,timer_status,timer_status1;
    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private String uuid = UUID.randomUUID().toString();
    private String TAG = "mainactivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation();
        progressBar=findViewById(R.id.progressBar);
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                if (isConnected()) {
//                    progressDialog.hide();
//                } else {
//                    progressDialog.setMessage("Checking Internet Connection...");
//                    progressDialog.setCancelable(false);
//                    progressDialog.show();
//
//                }
//                handler.postDelayed(this, delay);
//            }
//        }, delay);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        bu = (ImageView) findViewById(R.id.send_button);
        ed = (EditText) findViewById(R.id.ed);
        scrollView = (ScrollView) findViewById(R.id.scrollview);
        chatlayout=findViewById(R.id.chatlayout);
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds
        startTimer(1000);
//        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(840,130);
//        lparams.setMargins(54,0,0,10);
//        ed.setLayoutParams(lparams);
//        final LinearLayout.LayoutParams lparams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1830);
//        scrollView.setLayoutParams(lparams1);
//

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("chat");

        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid());
        name="hi";
        bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed.getText().toString().trim().length() == 0){
                    return;
                }
                String message=ed.getText().toString().toLowerCase();
                if(!message.equals(null)){
                    rightmsg(message);
                    switch (previous_msg) {
                        case "Sure, enter the name of the medicine?":

                            dataset(message);

                            break;

                        case "Enter your query in the chat, press 9 or exit to get back to me":
                            if (message.equals("9") || message.equals("exit")) {
                                leftmsg("Hey, this is your personal assistant, how may I assist you?");
                            } else {
                                String msg="\nID : "+user.getUid()+"\n\nMessage: "+message;
                                FirebaseDatabase.getInstance().getReference().child("SUPPORT").push().setValue(msg);
                                Toast.makeText(chatbot.this,"Query Has Been Raised",Toast.LENGTH_SHORT).show();

                            }
                            previous_msg = "";
                            break;
                        default:
                            sendMessageToBot(message);
                            break;
                    }
                }
            }
        });

        setUpBot();

    }

    private void dataset(final String message) {

        valueEventListener=FirebaseDatabase.getInstance().getReference().child("DATASET").child((message.substring(0,1)).toUpperCase()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance().getReference().child("DATASET").child((message.substring(0,1)).toUpperCase()).removeEventListener(valueEventListener);
                if(snapshot.exists()){
                    String dataset=snapshot.child("medicine_name").getValue().toString();

                    if ((dataset.toLowerCase()).contains("'"+message.toLowerCase()+"'")){
                        leftmsg("Please wait for a moment,we are finding your medicine...");
                        medicine_availiblity(message);
                    }
                    else {leftmsg("Check your spelling and try again");leftmsg("Hey, this is your personal assistant, how may I assist you?");}
                }
                else {leftmsg("Check your spelling and try again");leftmsg("Hey, this is your personal assistant, how may I assist you?");}

                previous_msg="";
                ed.setText(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void medicine_availiblity(final String message) {
        valueEventListener=FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB").removeEventListener(valueEventListener);
                for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Double lat = Double.parseDouble(dataSnapshot.child("lat").getValue().toString());
                    Double lon = Double.parseDouble(dataSnapshot.child("lon").getValue().toString());
                    if (lat - 0.03 <= latitude && lat + 0.03 >= latitude && lon - 0.03 <= longitude && lon + 0.03 >= longitude) {

                    chemist_count = Integer.parseInt(Long.toString(snapshot.getChildrenCount()));

                    if (dataSnapshot.child("medicine").child(message).child("quantity").exists()) {
                        String quanity = dataSnapshot.child("medicine").child(message).child("quantity").getValue().toString();
                        if (Integer.parseInt(quanity) > 0) {
                            chemist_key_str = chemist_key_str  + dataSnapshot.getKey()+ "/";
                            chemist_key_int++;
                        }


                    }
                }
                }
                if(chemist_key_int==0)
                {
                    leftmsg("Medicine is not in stock");
                    previous_msg="";
                    chemist_key_str="";
                    chemist_key_int=0;
                }
                else if(chemist_key_int>0) {

                    leftmsg("Chemist Found In Search Result: " +Integer.toString(chemist_key_int)+"\nRedirecting You Please Wait...... ");
                    startTimer1(2200);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


















    }

    private void rightmsg(String message) {
        String c=ed.getText().toString();
        c=message.substring(0,1).toUpperCase()+message.substring(1,message.length());

        ed.setText("");
        TextView textView = new TextView(chatbot.this);
        textView.setBackgroundResource(R.drawable.tv_send);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        textView.setTextColor(getResources().getColor(R.color.colorWhite));
        params.setMargins(200, 30, 10, 30);
        textView.setLayoutParams(params);
        textView.setPadding(40, 9, 65, 10);
        textView.setTextSize(18);

        textView.getResources().getColor(R.color.colorWhite);
        date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        date = date.substring(0, 10) + " AT" + date.substring(10);
        String temp1 = c + "\n" + date;

        Spannable ss = new SpannableString(temp1);
        ss.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), c.length() + 1, temp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan black = new ForegroundColorSpan(Color.BLACK);

        ss.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), c.length() + 1, temp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(black, c.length() + 1, temp1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new RelativeSizeSpan(0.7f), c.length() + 1, temp1.length(), 0);

        textView.setText(ss);
        chatlayout.addView(textView);

        scrollView.post(new Runnable() {
            @Override
            public void run() {

                if(scrollView.fullScroll(ScrollView.FOCUS_DOWN))
                {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });

    }

    private void setUpBot() {
        try {
            InputStream stream = this.getResources().openRawResource(R.raw.credential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);

            Log.d(TAG, "projectId : " + projectId);
        } catch (Exception e) {
            Log.d(TAG, "setUpBot: " + e.getMessage());
        }
    }

    private void sendMessageToBot(String message) {
        QueryInput input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build();
        new SendMessageInBg(this, sessionName, sessionsClient, input).execute();
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void callback(DetectIntentResponse returnResponse) {
        if(returnResponse!=null) {
            String botReply = returnResponse.getQueryResult().getFulfillmentText();
            if(!botReply.isEmpty()){
                {
                    previous_msg=botReply;
                        leftmsg(botReply);
                        progressBar.setVisibility(View.GONE);

                        if(previous_msg.equals("Retrieving Wallet Info......")){
                    valueEventListener=FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).removeEventListener(valueEventListener);
                            leftmsg("Your Wallet Balance is: "+snapshot.child("wallet").getValue().toString());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                        else if(previous_msg.equals("Please Wait! Redirecting You to Profile Page....")){
                            startTimer2(2200);
                        }
                        else if(previous_msg.equals("Redirecting You Please Wait.....")) {
                            startTimer3(2200);
                        }


                }
            }else {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "failed to connect!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void leftmsg(String botReply) {
        {
            String c=botReply;
            TextView textView = new TextView(chatbot.this);
            textView.setBackgroundResource(R.drawable.tv_recieve);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            textView.setTextColor(getResources().getColor(R.color.colorWhite));
            params.setMargins(10, 30, 200, 30);
            textView.setLayoutParams(params);
            textView.setPadding(65, 3, 40, 10);
            textView.setTextSize(18);

            textView.getResources().getColor(R.color.colorWhite);
            date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
            date = date.substring(0, 10) + " AT" + date.substring(10);
            String temp1 = "~EasyMed ChatBot\n"+c + "\n" + date;

            Spannable ss = new SpannableString(temp1);
            ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.BLACK);
            ss.setSpan(new android.text.style.StyleSpan(Typeface.BOLD_ITALIC), 0, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new RelativeSizeSpan(0.75f), 0, 16, 0);
            ss.setSpan(fcsRed, 0, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), c.length() + 1, temp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan black = new ForegroundColorSpan(Color.BLACK);

            ss.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), c.length() + 17, temp1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(black, c.length() + 17, temp1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new RelativeSizeSpan(0.7f), c.length() + 17, temp1.length(), 0);

            textView.setText(ss);
            chatlayout.addView(textView);

            scrollView.post(new Runnable() {
                @Override
                public void run() {

                    if(scrollView.fullScroll(ScrollView.FOCUS_DOWN))
                    {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }
            });



        }
    }


    private void startTimer(int noOfMinutes) {
        CountDownTimer  countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            }
            public void onFinish() {
            //leftmsg("Hey, this is your personal assistant, Here are some shortcut keys for better & faster assistance:\n\nPress \"1\" for Profile Settings\nPress \"2\"to Buy Medicine\nPress \"3\" to Check Bal\nPress \"4\"to Add Money\nPress \"5\" if you need help from our support team");
                leftmsg("Hey, this is your personal assistant, Here are some shortcut keys for faster assistance:\n" +
                        "Press \"1\"for Profile Settings\n" +
                        "Press \"2\"to Buy Medicine\n" +
                        "Press \"3\"to Check Bal\n" +
                        "Press \"4\"to Add Money\n" +
                        "Press \"5\"if you need help from the support team");
            }
        }.start();

    }
    private void startTimer1(int noOfMinutes) {
        CountDownTimer  countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            }
            public void onFinish() {
                Intent intent=new Intent(chatbot.this,chemist_filter.class);
                intent.putExtra("chemist_key",chemist_key_str);
                startActivity(intent);
            }
        }.start();

    }
    private void startTimer2(int noOfMinutes) {
        CountDownTimer  countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            }
            public void onFinish() {
                Intent intent=new Intent(chatbot.this,Profile.class);
                startActivity(intent);
            }
        }.start();

    }
    private void startTimer3(int noOfMinutes) {
        CountDownTimer  countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                //Convert milliseconds into hour,minute and seconds
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            }
            public void onFinish() {
                Intent intent=new Intent(chatbot.this,Wallet.class);
                startActivity(intent);
            }
        }.start();

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(chatbot.this,HomeScreen.class);
        startActivity(intent);
    }

    //location
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }
    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        latitude=location.getLatitude();
        longitude=location.getLongitude();
    }
    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


}
