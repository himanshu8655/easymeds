package com.example.easymed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PaymentGateway extends AppCompatActivity {
    RelativeLayout send;
    TextView transaction_amt;
    Button upi_id;
    int flag=0;
    final String UPI_ID="urmilagarg26@okhdfcbank";
    Button paytm_btn,airtel_btn,PhonePe_btn,amazon_btn;
    String TAG ="main",bal;
    ProgressBar progressBar;
    Context context;
    final int UPI_PAYMENT = 0;
    ValueEventListener valueEventListener;
    DatabaseReference connectedRef;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_payment_gateway);
        progressBar=findViewById(R.id.progrssBar);
        user= FirebaseAuth.getInstance().getCurrentUser();
        context=getApplicationContext();
        transaction_amt=findViewById(R.id.transaction_amt);
        Intent intent=getIntent();
        bal=intent.getStringExtra("wallet");
        transaction_amt.setText("TRANSACTION AMOUNT: ₹"+bal);
        amazon_btn=findViewById(R.id.amazon_btn);
        amazon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    boolean isInstalled = isPackageInstalled("in.amazon.mShop.android.shopping", context.getPackageManager());
                    if(!isInstalled){
                        Toast.makeText(context,"Not Installed",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    payUsingAmazonUpi("HIMANSHU GARG",UPI_ID,"Test",bal);
                    progressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                    Date todayDate = new Date();
                    String thisDate = currentDate.format(todayDate);
                }
            }
        });
        airtel_btn=findViewById(R.id.airtel_btn);
//        upi_id=findViewById(R.id.upi_id);
//        upi_id.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                payUsingAnyUpi("HIMANSHU GARG",UPI_ID,"Test",bal);
//                progressBar.setVisibility(View.VISIBLE);
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
//                Date todayDate = new Date();
//                String thisDate = currentDate.format(todayDate);
////                Transaction_history transaction_history=new Transaction_history(key,thisDate,"+"+bal,null,"PENDING");
////                documentReference.collection("Pending_Txn").document(key).set(transaction_history);
//
//            }
//        });
        paytm_btn=findViewById(R.id.paytm_btn);
        paytm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInstalled = isPackageInstalled("net.one97.paytm", context.getPackageManager());
                if(!isInstalled){
                    Toast.makeText(context,"Not Installed",Toast.LENGTH_SHORT).show();
                    return;
                }
                payUsingPaytmUpi("HIMANSHU GARG",UPI_ID,"Test",bal);
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                Date todayDate = new Date();
                String thisDate = currentDate.format(todayDate);
            }
        });
        send =  findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isConnectionAvailable(PaymentGateway.this)){
                    Toast.makeText(context,"Check your connection",Toast.LENGTH_SHORT).show();
                    return;
                }
                //Getting the values from the EditTexts
                boolean isInstalled = isPackageInstalled("com.google.android.apps.nbu.paisa.user", context.getPackageManager());
                if(!isInstalled){
                    Toast.makeText(context,"NOt Installed",Toast.LENGTH_SHORT).show();
                    return;
                }

                payUsingGoogleUpi("HIMANSHU GARG",UPI_ID,"Test",bal);
                SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                Date todayDate = new Date();
                String thisDate = currentDate.format(todayDate);
            }
        });
        airtel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInstalled = isPackageInstalled("com.myairtelapp", context.getPackageManager());
                if(!isInstalled){
                    Toast.makeText(context,"Not Installed",Toast.LENGTH_SHORT).show();
                    return;
                }

                payUsingAirtelUpi("HIMANSHU GARG",UPI_ID,"Test",bal);
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                Date todayDate = new Date();
                String thisDate = currentDate.format(todayDate);
            }
        });
        PhonePe_btn=findViewById(R.id.PhonePe_btn);
        PhonePe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isInstalled = isPackageInstalled("com.phonepe.app", context.getPackageManager());
                if(!isInstalled){
                    Toast.makeText(context,"NOt Installed",Toast.LENGTH_SHORT).show();
                    return;
                }
                payUsingPhonePeUpi("HIMANSHU GARG",UPI_ID,"Test",bal);
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                Date todayDate = new Date();
                String thisDate = currentDate.format(todayDate);
            }
        });
    }

    void payUsingAmazonUpi(  String name,String upiId, String note, String amount) {

        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);


// will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

// check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            String GOOGLE_PAY_PACKAGE_NAME = "in.amazon.mShop.android.shopping";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
            startActivityForResult(intent, UPI_PAYMENT);
        } else {
            Toast.makeText(PaymentGateway.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }
    void payUsingAirtelUpi(  String name,String upiId, String note, String amount) {

        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);


// will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

// check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            String GOOGLE_PAY_PACKAGE_NAME = "com.myairtelapp";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
            startActivityForResult(intent, UPI_PAYMENT);
        } else {
            Toast.makeText(PaymentGateway.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }
    void payUsingPhonePeUpi(  String name,String upiId, String note, String amount) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);


// will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

// check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            String GOOGLE_PAY_PACKAGE_NAME = "com.phonepe.app";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
            startActivityForResult(intent, UPI_PAYMENT);
        } else {
            Toast.makeText(PaymentGateway.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }
    void payUsingGoogleUpi(  String name,String upiId, String note, String amount) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);


// will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

// check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
            startActivityForResult(intent, UPI_PAYMENT);
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            Toast.makeText(PaymentGateway.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }
    void payUsingPaytmUpi(  String name,String upiId, String note, String amount) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);


// will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

// check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            String GOOGLE_PAY_PACKAGE_NAME = "net.one97.paytm";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
            startActivityForResult(intent, UPI_PAYMENT);
        } else {
            Toast.makeText(PaymentGateway.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }

    }
    void payUsingAnyUpi(  String name,String upiId, String note, String amount) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                .appendQueryParameter("tr", "your-transaction-ref-id")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

// will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

// check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(PaymentGateway.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("main ", "response "+resultCode );
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(final ArrayList<String> data) {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        FirebaseDatabase.getInstance().getReference().child("CACHE").push().setValue("TP");
        FirebaseDatabase.getInstance().getReference().child("CACHE").removeValue();
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        valueEventListener=connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    connectedRef.removeEventListener(valueEventListener);
                    String str = data.get(0);
                    Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
                    String paymentCancel = "";
                    if(str == null) str = "discard";
                    String status = "";
                    String approvalRefNo = "";
                    String response[] = str.split("&");
                    for (int i = 0; i < response.length; i++) {
                        String equalStr[] = response[i].split("=");
                        if(equalStr.length >= 2) {
                            if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                                status = equalStr[1].toLowerCase();
                            }
                            else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                                approvalRefNo = equalStr[1];
                            }
                        }
                        else {
                            paymentCancel = "Payment cancelled by user.";
                            progressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Intent intent=new Intent(PaymentGateway.this,Wallet.class);
                            startActivity(intent);

                        }
                    }

                    if (status.equals("success")) {
                        add_money(approvalRefNo);
                    }
                    else if("Payment cancelled by user.".equals(paymentCancel)) {
                        Toast.makeText(PaymentGateway.this, "Payment cancelled by user."+approvalRefNo, Toast.LENGTH_SHORT).show();
                        Log.e("UPI", "Cancelled by user: "+approvalRefNo);
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Intent intent=new Intent(PaymentGateway.this,Wallet.class);
                        startActivity(intent);

                    }
                    else {
                        Toast.makeText(PaymentGateway.this, "Transaction failed.Please try again"+approvalRefNo, Toast.LENGTH_SHORT).show();
                        Log.e("UPI", "failed payment: "+approvalRefNo);

                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Intent intent=new Intent(PaymentGateway.this,Wallet.class);
                        startActivity(intent);


                    }
                    connectedRef.removeEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void add_money(final String approvalRefNo) {

        Intent intent=new Intent(PaymentGateway.this,Wallet.class);
        startActivity(intent);
        //Code to handle successful transaction here.
        Toast.makeText(PaymentGateway.this, "UPI Transaction successful.", Toast.LENGTH_SHORT).show();
        Log.e("UPI", "payment successfull: "+approvalRefNo);
        String key1=FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("Transaction_History").push().getKey();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        history_db historyDb=new history_db(date,key1,"+"+bal);
        FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("Transaction_History").child(key1).setValue(historyDb);
        FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid()).child("wallet")
                        .setValue(Integer.parseInt(snapshot.child("wallet").getValue().toString())+Integer.parseInt(bal));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        String thisDate = currentDate.format(todayDate);



    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }


    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            return packageManager.getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    @Override
    protected void onDestroy() {

        if(valueEventListener!=null) {
            connectedRef.removeEventListener(valueEventListener);
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {

        if(valueEventListener!=null) {
            connectedRef.removeEventListener(valueEventListener);
        }
        super.onStop();
    }
    @Override
    public void onBackPressed() {
        if(valueEventListener!=null){

        }
        else {
            super.onBackPressed();
        }
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}