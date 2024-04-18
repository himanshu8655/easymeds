package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener  {

    LinearLayout linearLayout,hide;
    TextView display;
    FirebaseUser user;
    private TextView location_tv1;
    int i=0,flag=0,filter_flag=0;
       private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    private LocationManager mLocationManager;
    private double latitude,longitude;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationManager locationManager;
    ProgressBar progress_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        location_tv1=findViewById(R.id.location_tv);
        user= FirebaseAuth.getInstance().getCurrentUser();
        display=findViewById(R.id.display);
        progress_location=findViewById(R.id.progress_location);
        linearLayout=findViewById(R.id.linearlayout);
        hide=findViewById(R.id.hide);
        location_tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLocationEnabled()){
                    location_tv1.setText("Updating Location...");
                    flag=0;
                }
                else {
                    checkLocation();
                }

            }
        });



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation(); //check whether location service is enable or not in your  phone
    }
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
        if (flag == 0) {
            getCityName(location.getLatitude(),location.getLongitude());
            // You can now create a LatLng Object for use with maps
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            flag=1;
            {
                {

                linearLayout.removeViewsInLayout(1,linearLayout.getChildCount()-1);
                    databaseReference=FirebaseDatabase.getInstance().getReference().child("CHEMIST_DB");
                valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        databaseReference.removeEventListener(valueEventListener);
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            hide.setVisibility(View.VISIBLE);
                            display.setVisibility(View.GONE);
                            progress_location.setVisibility(View.GONE);
                            String phone = snapshot.child("ph").getValue().toString();
                            String location = snapshot.child("location").getValue().toString();
                            String name =snapshot.child("Name").getValue().toString();
                            Double lat=Double.parseDouble(snapshot.child("lat").getValue().toString());
                            Double lon=Double.parseDouble(snapshot.child("lon").getValue().toString());
                            CardView cv = new CardView(ScrollingActivity.this);
                            cv.setCardElevation(5);
                            cv.setRadius(25);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(50, 5, 50, 40);
                            cv.setLayoutParams(params);
                            cv.setPadding(10, 10, 10, 10);
                            cv.setBackgroundResource(R.color.cardview);
                            linearLayout.addView(cv);
                            LinearLayout ll = new LinearLayout(ScrollingActivity.this);
                            ll.setPadding(20, 15, 20, 25);
                            ll.setOrientation(LinearLayout.VERTICAL);
                            cv.addView(ll);
                            TextView tv = new TextView(ScrollingActivity.this);

                            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                            tv.setTextColor(getResources().getColor(R.color.colorWhite));
                            tv.setText(name+"\n");
                            tv.setTextSize(25);
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);
                            ll.addView(tv);
                            TextView tv1 = new TextView(ScrollingActivity.this);
                            tv1.setTextSize(17);
                            tv1.setTextColor(getResources().getColor(R.color.colorWhite));
                            String string = "Contact No: " + phone + "\n\n"+"Location: " + location + "\n";
                            tv1.setText(string);
                            ll.addView(tv1);

                            Button button = new Button(ScrollingActivity.this);
                            i++;
                            button.setId(i);
                            button.setBackgroundResource(R.color.colorWhite);
                            button.setOnClickListener(ScrollingActivity.this);
                            button.setText("BUY MEDICINES");
                            button.setPadding(20,20,20,20);
                            button.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shop_blue, 0, 0, 0);

                            button.setTextColor(getResources().getColor(R.color.cardview));
                            ll.addView(button);
                            TextView textView=new TextView(ScrollingActivity.this);
                            textView.setText(snapshot.child("id").getValue().toString());
                            i++;
                            textView.setId(i);
                            textView.setVisibility(View.GONE);
                            ll.addView(textView);

                            if(lat-0.03<=latitude && lat+0.03>=latitude && lon-0.03<=longitude && lon+0.03>=longitude){
                                filter_flag=1;
                            }
                            else {
                                cv.setVisibility(View.GONE);
                            }
                        }
                        if(filter_flag==0)
                        {
                            Toast.makeText(ScrollingActivity.this,"No nearby chemist found",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            }
        }
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


    private String getCityName(Double lat, Double lon) {
        {
            String myCity = "";
            Geocoder geocoder = new Geocoder(ScrollingActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat,lon, 1);
                String address = addresses.get(0).getAddressLine(0);
                myCity = addresses.get(0).getFeatureName();
                String disply="";
                if(addresses.get(0).getFeatureName() != null){
                    disply=addresses.get(0).getFeatureName()+", ";
                }
                if(addresses.get(0).getSubLocality() != null){
                    disply=disply+addresses.get(0).getSubLocality()+", ";
                }
                if(addresses.get(0).getLocality() != null){
                    disply=disply+addresses.get(0).getLocality();
                }
                disply=disply+", Postal Code- "+addresses.get(0).getPostalCode();
                location_tv1.setText(disply);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return myCity;
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(ScrollingActivity.this,buy_medicine.class);
        int j=view.getId();
        j++;
        TextView tv=findViewById(j);
        intent.putExtra("key",tv.getText().toString());
        startActivity(intent);
    }


}
