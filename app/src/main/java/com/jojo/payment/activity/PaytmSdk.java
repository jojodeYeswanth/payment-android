package com.jojo.payment.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jojo.payment.R;
import com.jojo.payment.util.JSONParser;
import com.paytm.pgsdk.Log;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PaytmSdk extends AppCompatActivity implements PaytmPaymentTransactionCallback {
    private static String MercahntKey = "ATMIMC97739829232153";
    EditText price;
    String order_id, cust_id, price_amt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm_sdk);

        Button btn = findViewById(R.id.start_transaction);
        price = findViewById(R.id.orderamt);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order_id = createRandomOrderId();
                cust_id = createRandomCustId();
                price_amt = price.getText().toString();
                sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
                dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        if (ContextCompat.checkSelfPermission(PaytmSdk.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PaytmSdk.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @SuppressLint("StaticFieldLeak")
    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(PaytmSdk.this);
        String url ="http://enactive-words.000webhostapp.com/paytm/generateChecksum.php";
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading...");
            this.dialog.show();
        }
        @SafeVarargs
        protected final String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(PaytmSdk.this);
            String param= "MID="+ MercahntKey + "&ORDER_ID=" + order_id + "&CUST_ID="+ cust_id +
                    "&CHANNEL_ID=WAP&TXN_AMOUNT="+ price_amt +"&WEBSITE=WEBSTAGING"+
                    "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            Log.e("CheckSum result >>",jsonObject.toString());
            Log.e("CheckSum result >>",jsonObject.toString());
            try {
                CHECKSUMHASH = jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                Log.e("CheckSum result >>",CHECKSUMHASH);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            PaytmPGService Service = PaytmPGService.getStagingService();

            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("MID", MercahntKey);
            paramMap.put("ORDER_ID", order_id);
            paramMap.put("CUST_ID", cust_id);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", price_amt);
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            Service.startPaymentTransaction(PaytmSdk.this, true, true, PaytmSdk.this  );
        }
    }
    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
    }
    @Override
    public void networkNotAvailable() {
    }
    @Override
    public void clientAuthenticationFailed(String s) {
    }
    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
    }
    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
    }
    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  " );
    }
    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
    }
    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    protected String createRandomCustId() {
        String val = "DI";
        int ranChar = 65 + (new Random()).nextInt(90-65);
        char ch = (char)ranChar;
        val += ch;

        Random r = new Random();
        int numbers = 100000 + (int)(r.nextFloat() * 899900);
        val += String.valueOf(numbers);

        val += "-";
        for(int i = 0; i<6;){
            int ranAny = 48 + (new Random()).nextInt(90-65);
            if(!(57 < ranAny && ranAny<= 65)){
                char c = (char)ranAny;
                val += c;
                i++;
            }
        }
        return val;
    }
    protected String createRandomOrderId() {
        long timeSeed = System.nanoTime();
        double randSeed = Math.random() * 1000;
        long midSeed = (long) (timeSeed * randSeed);
        String s = midSeed + "";

        return s.substring(0, 9);
    }
}
