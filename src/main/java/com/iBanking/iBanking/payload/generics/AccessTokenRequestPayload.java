package com.iBanking.iBanking.payload.generics;

import lombok.Data;

@Data
public class AccessTokenRequestPayload {

    private String userName;
    private String password;
}
