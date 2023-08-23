package com.iBanking.iBanking.utils;

import com.google.gson.Gson;

import com.iBanking.iBanking.payload.generics.AccessTokenRequestPayload;
import com.iBanking.iBanking.payload.generics.AccessTokenResponsePayload;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;
import static com.iBanking.iBanking.utils.ApiPaths.*;


@Slf4j
@Component
public class AuthenticationApi {
    static Gson gson = new Gson();

//    @Autowired
//    FastCreditConfig fastCreditConfig;

    @Autowired
    Environment env;

    public String getAccessToken() throws UnirestException {
        AccessTokenRequestPayload accessTokenRequestPayload = new AccessTokenRequestPayload();
        AccessTokenResponsePayload accessTokenResponsePayload = new AccessTokenResponsePayload();
        String userName = env.getProperty("fast-credit.user-name");
        String passWord = env.getProperty("fast-credit.password");
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
            String secret = env.getProperty("fast-credit.secret-key");
            String iv = env.getProperty("fast-credit.iv");
            String padding = env.getProperty("fast-credit-padding");
            assert secret != null;
            byte[] key = secret.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            assert padding != null;
            Cipher cipher = Cipher.getInstance(padding);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            //Perform Encryption
            return Base64.getEncoder().encodeToString(cipher.doFinal(requestPayloads.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
        }
        return null;
    }


    public String decryptPayload(String cipherText) {

        try {
            String secret = env.getProperty("fast-credit.secret-key");
            String iv = env.getProperty("fast-credit.iv");
            String padding = env.getProperty("fast-credit-padding");
            assert secret != null;
            byte[] key = secret.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            assert padding != null;
            Cipher cipher = Cipher.getInstance(padding);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            //Perform Decryption
            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
        }
        return null;
    }

}
