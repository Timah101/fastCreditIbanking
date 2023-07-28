package com.iBanking.iBanking.utils;

import com.google.gson.Gson;
import com.iBanking.iBanking.payload.accout.AccountDetailsRequestPayload;
import com.iBanking.iBanking.payload.generics.AccessTokenRequestPayload;
import com.iBanking.iBanking.payload.generics.AccessTokenResponsePayload;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


import static com.iBanking.iBanking.utils.ApiPaths.*;


@Slf4j
@Component
public class AuthenticationApi {
    static Gson gson = new Gson();

    @Autowired
    FastCreditConfig fastCreditConfig;

    public String getAccessToken() throws UnirestException {
        AccessTokenRequestPayload accessTokenRequestPayload = new AccessTokenRequestPayload();
        AccessTokenResponsePayload accessTokenResponsePayload = new AccessTokenResponsePayload();
        String userName = fastCreditConfig.userName();
        String passWord = fastCreditConfig.password();
        accessTokenRequestPayload.setUserName(userName);
        accessTokenRequestPayload.setPassword(passWord);
        String requestPayload = gson.toJson(accessTokenRequestPayload);
        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + GENERATE_TOKEN)
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(requestPayload)
                .asString();
//        System.out.println(jsonResponse.getStatus() +" : FIRST STATUS ");
        String jsonResponseBody = jsonResponse.getBody();
        accessTokenResponsePayload.setACCESS_TOKEN(jsonResponseBody);
//        log.info("ACCESS TOKEN {}", jsonResponseBody);
        return jsonResponseBody;
    }


    public String encryptPayload(String requestPayloads) throws UnirestException {

        try {
            String secret = fastCreditConfig.secretKey();
            byte[] IV = new byte[16];
            byte[] key = secret.getBytes("UTF-8");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            //Get Cipher Instance
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            //Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

            //Create IvParameterSpec
            IvParameterSpec ivSpec = new IvParameterSpec(IV);

            //Initialize Cipher for ENCRYPT_MODE
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            //Perform Encryption
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(requestPayloads.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 BadPaddingException unsupportedEncodingException) {
        }
        return null;
//
//        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + ENCRYPT_PAYLOAD)
//                .header("accept", "application/json")
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + getAccessToken())
//                .body(requestPayloads).asString();
//        String requestBody = jsonResponse.getBody();
//        String encryptResponsePayload = gson.fromJson(requestBody, EncryptResponsePayload.class);
////        log.info("ENCRYPTION RESPONSE FROM ENCRYPT METHOD {}", encryptResponsePayload);
//        return encryptResponsePayload;

    }

    public String decryptPayload(String cipherText) {
        try {
            String secret = fastCreditConfig.secretKey();
            byte[] IV = new byte[16];
            byte[] key = secret.getBytes("UTF-8");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            //Get Cipher Instance
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            //Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

            //Create IvParameterSpec
            IvParameterSpec ivSpec = new IvParameterSpec(IV);

            //Initialize Cipher for DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            //Perform Decryption
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 BadPaddingException unsupportedEncodingException) {
        }
        return null;
    }

    //OLD DECRYPTION API
//    public <T, R> R decryptPayload(T request, Class<R> responseType) throws UnirestException {
//        String requestPayload = gson.toJson(request);
////        log.info("DECRYPT REQUEST PAYLOAD INSIDE HERE {}", request);
//        HttpResponse<String> jsonResponse = Unirest.post(BASE_URL + DECRYPT_PAYLOAD)
//                .header("accept", "application/json")
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer " + getAccessToken())
//                .body(requestPayload).asString();
//        String requestBody = jsonResponse.getBody();
////        log.info("DECRYPTION RESPONSE {}", requestBody);
//
//        AccountDetailsResponsePayload configurations;
//        gson.fromJson(requestBody, responseType);
////        log.info("DECRYPTED RESPONSE INSIDE FROM DECRYPT METHOD {}", gson.fromJson(requestBody, responseType));
//        return gson.fromJson(requestBody, responseType);
//    }


//    public String encrypt(String plaintext) {
//        try {
//            String secret = fastCreditConfig.secretKey();
//            byte[] IV = new byte[16];
//            byte[] key = secret.getBytes("UTF-8");
//            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
//            //Get Cipher Instance
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//
//            //Create SecretKeySpec
//            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
//
//            //Create IvParameterSpec
//            IvParameterSpec ivSpec = new IvParameterSpec(IV);
//
//            //Initialize Cipher for ENCRYPT_MODE
//            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
//
//            //Perform Encryption
//            return Base64.getEncoder()
//                    .encodeToString(cipher.doFinal(plaintext.getBytes("UTF-8")));
//        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException |
//                 InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
//                 BadPaddingException unsupportedEncodingException) {
//        }
//        return null;
//    }




    public static void main(String[] args) throws UnirestException {
//        encryptPayload(HttpSession session);
        AccountDetailsRequestPayload getAccountDetails = new AccountDetailsRequestPayload();
//        System.out.println(getAccountDetails(getAccountDetails));
    }
}
