package com.jojo.payment;

import com.google.android.gms.wallet.WalletConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Constants {
    public static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST;
    public static final List<String> SUPPORTED_NETWORKS = Arrays.asList("AMEX", "DISCOVER", "JCB", "MASTERCARD", "VISA");

    public static final List<String> SUPPORTED_METHODS = Arrays.asList("PAN_ONLY", "CRYPTOGRAM_3DS");
    public static final String COUNTRY_CODE = "US";
    public static final String CURRENCY_CODE = "USD";

    public static final List<String> SHIPPING_SUPPORTED_COUNTRIES = Arrays.asList("US", "GB");

    public static final String PAYMENT_GATEWAY_TOKENIZATION_NAME = "example";

    public static final HashMap<String, String> PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS = new HashMap<String, String>() {{
        put("gateway", PAYMENT_GATEWAY_TOKENIZATION_NAME);
        put("gatewayMerchantId", "exampleGatewayMerchantId");
    }};

    public static final String DIRECT_TOKENIZATION_PUBLIC_KEY = "REPLACE_ME";

    public static final HashMap<String, String> DIRECT_TOKENIZATION_PARAMETERS = new HashMap<String, String>() {{
        put("protocolVersion", "ECv2");
        put("publicKey", DIRECT_TOKENIZATION_PUBLIC_KEY);
    }};
}
