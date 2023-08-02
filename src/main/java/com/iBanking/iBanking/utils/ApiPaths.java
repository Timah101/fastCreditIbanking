package com.iBanking.iBanking.utils;

public interface ApiPaths {

    //    http://omnichannelapi2.fastcredit-ng.com
    String BASE_URL = "http://omnichannelapi2.fastcredit-ng.com/expertbridge/api/";
    String GENERATE_TOKEN = "token/generate-token";
    String ENCRYPT_PAYLOAD = "customerService/encrypt";
    String DECRYPT_PAYLOAD = "customerService/decrypt";
    String CUSTOMER_DETAILS = "customerService/details";

    String PASSWORD_RESET = "customerService/update/customer";

    String CUSTOMER_REGISTER = "customerService/register";

    String CUSTOMER_CREATE = "customerService/create/individual";
    String CUSTOMER_AUTH = "customerService/authenticate/user";

    String ACCOUNT_DETAILS = "customerService/account/details";

    String ACCOUNT_DETAILS_LIST = "customerService/account/list";

    String SEND_OTP = "customerService/otp/send";

    String AIRTIME_TOP_UP = "fusionService/airtime/purchase";

    String DATA_TOP_UP = "fusionService/data/purchase";

    String DATA_PLANS = "fusionService/data/plans";

    String SEND_MONEY_LOCAL = "genericService/ft/local";

    String SEND_MONEY_OTHERS = "genericService/ft/others";

    String GET_BANKS_LIST = "genericService/banks";

    String OTHER_BANKS_NAME_ENQUIRY = "genericService/ft/name-enquiry";

    String CABLE_TV_BILLERS_LIST = "fusionService/cabletv/billers";

    String ELECTRICITY_BILLERS_LIST = "fusionService/electricity/billers";
    String VALIDATE_CABLE_TV = "fusionService/cabletv/validate";

    String VALIDATE_ELECTRICITY = "fusionService/electricity/validate";

    String CABLE_TV_PAYMENT = "fusionService/cabletv/pay-bill";


}
