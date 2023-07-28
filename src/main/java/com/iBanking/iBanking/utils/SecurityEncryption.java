//package com.iBanking.iBanking.utils;
//
//
//import com.google.gson.Gson;
//import java.io.UnsupportedEncodingException;
//import java.security.InvalidAlgorithmParameterException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.util.Base64;
//import java.util.Locale;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.crypto.BadPaddingException;
//import javax.crypto.Cipher;
//import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.NoSuchPaddingException;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.MessageSource;
//import org.springframework.context.NoSuchMessageException;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
///**
// * @author dofoleta
// */
//@Component
//public class SecurityEncryption {
//
//    @Autowired
//    MessageSource messageSource;
//
//    @Autowired
//    Gson gson;
//
//    @Value("${aes.encryption.key}")
//    private String aesEncryptionKey;
//
//    public String encrypt(String plaintext, String secret) {
//        try {
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
//
//    public String decrypt(String cipherText, String secret) {
//        try {
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
//            //Initialize Cipher for DECRYPT_MODE
//            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
//
//            //Perform Decryption
//            return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
//        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException |
//                 InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
//                 BadPaddingException unsupportedEncodingException) {
//        }
//        return null;
//    }
//
////    public ValidationPayload validateRequest(GenericPayload genericRequestPayload) {
////
//////        LoggingUtil.debugInfo("Validating Encrypted Request: " + gson.toJson(genericRequestPayload), this.getClass(), LogMode.INFO.name());
////        String encryptedRequest = genericRequestPayload.getRequest();
////        String decryptedRequest;
//////        String errorMessage;
////
////        ValidationPayload validatorPayload = new ValidationPayload();
////        try {
////            decryptedRequest = decrypt(encryptedRequest, aesEncryptionKey);
////            if (decryptedRequest == null) {
////                validatorPayload.setError(true);
////                ExceptionResponse exResponse = new ExceptionResponse();
////                exResponse.setResponseCode(ResponseCodes.NO_ROLE.getResponseCode());
////                exResponse.setResponseMessage(messageSource.getMessage("appMessages.encryption", new Object[0], Locale.ENGLISH));
////                String exceptionJson = gson.toJson(exResponse);
////
////                GenericPayload responsePayload = new GenericPayload();
////                responsePayload.setResponse(encrypt(exceptionJson, aesEncryptionKey));
////
////                validatorPayload.setResponse(gson.toJson(responsePayload));
////            } else {
////                validatorPayload.setError(false);
////                validatorPayload.setResponse("SUCCESS");
////                validatorPayload.setPlainTextPayload(decryptedRequest);
////            }
////        } catch (NoSuchMessageException ex) {
////            validatorPayload.setError(true);
////            String errorMessage = ex.getMessage();
////            ExceptionResponse exResponse = new ExceptionResponse();
////            exResponse.setResponseCode(ResponseCodes.FORMAT_EXCEPTION.getResponseCode());
////            exResponse.setResponseMessage(errorMessage);
////            String exceptionJson = gson.toJson(exResponse);
////
////            GenericPayload responsePayload = new GenericPayload();
////            responsePayload.setResponse(encrypt(exceptionJson, aesEncryptionKey));
////
////            return validatorPayload;
////        }
////        return validatorPayload;
////    }
//}
