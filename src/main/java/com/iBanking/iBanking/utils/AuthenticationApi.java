package com.iBanking.iBanking.utils;

import com.google.gson.Gson;
import com.iBanking.iBanking.payload.accout.AccountDetailsRequestPayload;
import com.iBanking.iBanking.payload.accout.AccountDetailsResponsePayload;
import com.iBanking.iBanking.payload.generics.AccessTokenRequestPayload;
import com.iBanking.iBanking.payload.generics.AccessTokenResponsePayload;
import com.iBanking.iBanking.payload.generics.EncryptResponsePayload;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;


import static com.iBanking.iBanking.utils.ApiPaths.*;


@Slf4j
public class AuthenticationApi {
    static Gson gson = new Gson();
    static Configurations configurations = new Configurations();

    public static String getAccessToken() throws UnirestException {
        AccessTokenRequestPayload accessTokenRequestPayload = new AccessTokenRequestPayload();
        AccessTokenResponsePayload accessTokenResponsePayload = new AccessTokenResponsePayload();
        accessTokenRequestPayload.setUserName(configurations.getTokenUsername());
        accessTokenRequestPayload.setPassword(configurations.getTokenPassword());
        String requestPayload = gson.toJson(accessTokenRequestPayload);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + GENERATE_TOKEN)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .asString();

        String jsonResponseBody = jsonResponse.getBody();
        accessTokenResponsePayload.setACCESS_TOKEN(jsonResponseBody);
//        log.info("ACCESS TOKEN {}", jsonResponseBody);
        return jsonResponseBody;
    }


    public static EncryptResponsePayload encryptPayload(String requestPayloads) throws UnirestException {
        //        HttpSession session
        //        String accessToken = (String) session.getAttribute("accessToken");
//        log.info("ENCRYPTION REQUEST {}", requestPayloads);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + ENCRYPT_PAYLOAD)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getAccessToken())
                .body(requestPayloads).asString();
        String requestBody = jsonResponse.getBody();
        EncryptResponsePayload encryptResponsePayload = gson.fromJson(requestBody, EncryptResponsePayload.class);
//        log.info("ENCRYPTION RESPONSE FROM ENCRYPT METHOD {}", encryptResponsePayload);
        return encryptResponsePayload;

    }

    public static <T, R> R decryptPayload(T request, Class<R> responseType) throws UnirestException {
//        request.setResponse("oxjnc/iwXauEUn8rKAIrvgbATWArzBo/HyhOcSNgws65Os0YKxM4m/eWeUHLg5h/");

        String requestPayload = gson.toJson(request);
//        log.info("DECRYPT REQUEST PAYLOAD INSIDE HERE {}", request);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + DECRYPT_PAYLOAD)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + getAccessToken())
                .body(requestPayload).asString();
        String requestBody = jsonResponse.getBody();
//        log.info("DECRYPTION RESPONSE {}", requestBody);

        AccountDetailsResponsePayload configurations;
        gson.fromJson(requestBody, responseType);
//        log.info("DECRYPTED RESPONSE INSIDE FROM DECRYPT METHOD {}", gson.fromJson(requestBody, responseType));
        return gson.fromJson(requestBody, responseType);
    }


    public static void main(String[] args) throws UnirestException {
//        encryptPayload(HttpSession session);
        AccountDetailsRequestPayload getAccountDetails = new AccountDetailsRequestPayload();
//        System.out.println(getAccountDetails(getAccountDetails));
    }
}
