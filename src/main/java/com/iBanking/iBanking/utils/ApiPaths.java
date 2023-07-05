package com.iBanking.iBanking.utils;

public class ApiPaths {
    public static final String BASE_URL = "https://omnichannelapi.fastcredit-ng.com//expertbridge/api/";
    static final String GENERATE_TOKEN = "token/generate-token";
    static final String ENCRYPT_PAYLOAD = "customerService/encrypt";
    static final String DECRYPT_PAYLOAD = "customerService/decrypt";
    public static final String CUSTOMER_DETAILS = "customerService/details";

    public static final String PASSWORD_RESET = "customerService/update/customer";

    public static final String CUSTOMER_REGISTER = "customerService/register";

    public static final String CUSTOMER_CREATE = "customerService/create/individual";
    public static final String CUSTOMER_LOGIN = "customerService/authenticate/user";

    public static final String ACCOUNT_DETAILS = "customerService/account/details";

    public static final String ACCOUNT_DETAILS_LIST = "customerService/account/list";

    public static final String SEND_OTP = "customerService/otp/send";

    public static final String AIRTIME_TOP_UP = "fusionService/airtime/purchase";

    public static final String DATA_TOP_UP = "fusionService/data/purchase";

    public static final String DATA_PLANS = "fusionService/data/plans";

    public static final String SEND_MONEY_LOCAL = "genericService/ft/local";

    public static final String SEND_MONEY_OTHERS = "genericService/ft/others";

    public static final String GET_BANKS_LIST = "genericService/banks";

    public static final String OTHER_BANKS_NAME_ENQUIRY = "genericService/ft/name-enquiry";

    public static final String CABLE_TV_BILLERS_LIST = "fusionService/cabletv/billers";

    public static final String ELECTRICITY_BILLERS_LIST = "fusionService/electricity/billers";
    public static final String VALIDATE_CABLE_TV = "fusionService/cabletv/validate";

    public static final String VALIDATE_ELECTRICITY = "fusionService/electricity/validate";

    public static final String CABLE_TV_PAYMENT = "fusionService/cabletv/pay-bill";



}
