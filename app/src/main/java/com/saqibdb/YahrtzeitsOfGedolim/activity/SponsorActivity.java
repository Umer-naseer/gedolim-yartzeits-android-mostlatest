package com.saqibdb.YahrtzeitsOfGedolim.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.saqibdb.YahrtzeitsOfGedolim.Constants;
import com.saqibdb.YahrtzeitsOfGedolim.R;
import com.saqibdb.YahrtzeitsOfGedolim.helper.Security;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SponsorActivity extends AppCompatActivity implements PurchasesUpdatedListener, ConsumeResponseListener {

    private BillingClient billingClient;

    EditText nameEt, emailEt, gedolOfSponsorEt, sponsorPageEt;
    String purchaseToken, purchaseTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor);
        billingClient = BillingClient.newBuilder(SponsorActivity.this)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        initViews();


    }

    private void initViews(){
        nameEt = findViewById(R.id.nameOfSponsorEt);
        emailEt = findViewById(R.id.emailOfSponsorEt);
        gedolOfSponsorEt = findViewById(R.id.gedolOfSponsorEt);
        sponsorPageEt = findViewById(R.id.sponsorPageEt);
    }

    private void sendEmail(){
//        SendMail mail = new SendMail("shakeelkhan706@gmail.com", "sender_pass",
//                "shinwari.khan474@gmail.com",
//                "Testing Email Sending",
//                "Yes, it's working well\nI will use it always.");
//        mail.execute();


        try {

            long time1 = Long.parseLong(purchaseTime);
            Calendar purchaseDate = Calendar.getInstance();
            purchaseDate.setTimeInMillis(time1);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            String dateTimeOfPurchase = dateFormat.format(purchaseDate.getTime());
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "", null));
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                    new String[] {"yartzeits@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Suggestions Yartzeits App");
            emailIntent.putExtra(Intent.EXTRA_TEXT   , "Name: " + nameEt.getText().toString().trim() + "\nEmail: " +
                    emailEt.getText().toString().trim() + "\nGedol you wish to sponsor: " + gedolOfSponsorEt.getText().toString() +
                    "\n Sponsor page text: " + sponsorPageEt.getText().toString().trim() + "\nPurchase Token: " + purchaseTime + "\nPurchase Time: " + dateTimeOfPurchase);

            goBackToHomeScreenLauncher.launch(Intent.createChooser(emailIntent, "Done!"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SponsorActivity.this, "No Email client found!!",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(SponsorActivity.this, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    ActivityResultLauncher<Intent> goBackToHomeScreenLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SponsorActivity.this, CalendarViewActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 200);


                }
            });


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        //if item subscribed
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        }
        //if item already subscribed then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if(alreadyPurchases!=null){
                handlePurchases(alreadyPurchases);
            }
        }
        //if Purchase canceled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
        }
        // Handle any other error msgs
        else {
            Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void initiatePurchase() {
        List<String> skuList = new ArrayList<>();
        skuList.add(Constants.PRODUCT_ID);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (skuDetailsList != null && skuDetailsList.size() > 0) {
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetailsList.get(0))
                                        .build();
                                billingClient.launchBillingFlow(SponsorActivity.this, flowParams);
                            } else {
                                //try to add item/product id "purchase" inside managed product in google play console
                                Toast.makeText(getApplicationContext(),"Purchase Item not Found",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void handlePurchases(List<Purchase>  purchases) {
        for(Purchase purchase:purchases) {
            //if item is purchased
            if (Constants.PRODUCT_ID.equals(purchase.getSkus().get(0)) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    Toast.makeText(getApplicationContext(), "Error : invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }
                // else purchase is valid
                //if item is purchased and not acknowledged
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase);
                    Toast.makeText(getApplicationContext(), "not Acknowledged", Toast.LENGTH_SHORT).show();
                }
                //else item is purchased and also acknowledged
                else {
                    Toast.makeText(getApplicationContext(), "Acknowledged", Toast.LENGTH_SHORT).show();
                    purchaseToken = purchase.getPurchaseToken();
                    purchaseTime = String.valueOf(purchase.getPurchaseTime());
                    sendEmail();
                    // Grant entitlement to the user on item purchase
                    // restart activity
//                    if(!getSubscribeValueFromPref()){
//                        saveSubscribeValueToPref(true);
//                        Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
//                        this.recreate();
//                    }
                }
            }
            //if purchase is pending
            else if(Constants.PRODUCT_ID.equals(purchase.getSkus().get(0)) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
            {
                Toast.makeText(getApplicationContext(),
                        "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
            }
            //if purchase is unknown mark false
            else if(Constants.PRODUCT_ID.equals(purchase.getSkus().get(0)) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
            {
//                saveSubscribeValueToPref(false);
//                premiumContent.setVisibility(View.GONE);
//                subscribe.setVisibility(View.VISIBLE);
//                subscriptionStatus.setText("Subscription Status : Not Subscribed");
                Toast.makeText(getApplicationContext(), "Purchase Status Unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        sendEmail();
//    }

    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                //if purchase is acknowledged
                // Grant entitlement to the user. and restart activity
//                saveSubscribeValueToPref(true);
                SponsorActivity.this.recreate();
                sendEmail();
            }
        }
    };
    @Override
    public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//            val consumeCountValue = purchaseCountValueFromPref + 1;
//            savePurchaseCountValueToPref(consumeCountValue);
            Toast.makeText(SponsorActivity.this, "Item Consumed", Toast.LENGTH_SHORT).show();
//            consumeCount.text = "Item Consumed $purchaseCountValueFromPref Time(s)"
        }
    }

    private boolean verifyValidSignature(String signedData, String signature){
        try {
            // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnVBnqXEFrWazcgFEOy48EOYzsiYMo5nlF1/waXNqJ1V9YHQxfxVVndKNIHEZcfSEg9f7ZQGYYCuu4Z8CmvtmgdEzbJg0qoTXTMXM1fFC7TJyZvX6Z3cJr/hhG9PKcx1JIYldDD10UtdYTE4LDedYEMzpBHSc21m0/osPr4sikEzRWl8UgHsSrddfG1glZYT3+FYlpiKk+Ajt6nK4tCPJ5vMQ9CrOGLC43k3smB+XhTSva23TfE+J6bs429TMRPZJ7ZYY2brFGuL2QnN9OmDUkneuQlK3Eh4CAdLTQX02lC1PZUOpRbdULsGO2CUZcicZqKUcylMT+HQkLC6IXic6QwIDAQAB";
           if (Security.verifyPurchase(base64Key, signedData, signature))
               return true;
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    //initiate purchase on consume button click
    public void purchase(View view) {
        if (nameEt.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Name field cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (emailEt.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Email field cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEt.getText().toString().trim()).matches()){
            Toast.makeText(getApplicationContext(), "Please enter af valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (gedolOfSponsorEt.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Gedol sponsor field cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sponsorPageEt.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Page details field cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

//        sendEmail();

        if (billingClient.isReady()) {
            initiatePurchase();
        }
        //else reconnect service
        else{
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase();
                    } else {
                        Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {
                    Toast.makeText(getApplicationContext(),"Service Disconnected ",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}