package com.jojo.payment.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.jojo.payment.R;
import com.jojo.payment.util.Notifications;
import com.jojo.payment.util.PaymentsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentTransparentActivity extends AppCompatActivity {
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Notifications.ACTION_PAY_GOOGLE_PAY.equals(getIntent().getAction())) {
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }
        showPaymentsSheet();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                    case AutoResolveHelper.RESULT_ERROR:
                        break;
                }
                finishAndRemoveTask();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showPaymentsSheet() {
        long priceCents = getIntent().getLongExtra(Notifications.OPTION_PRICE_EXTRA, 2500L);
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request = PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());
        if (request != null) {
            final PaymentsClient paymentsClient = PaymentsUtil.createPaymentsClient(this);
            AutoResolveHelper.resolveTask(paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {
            return;
        }
        Notifications.remove(this);
        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");
            Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            throw new RuntimeException("The selected garment cannot be parsed from the list of elements");
        }
    }
}
