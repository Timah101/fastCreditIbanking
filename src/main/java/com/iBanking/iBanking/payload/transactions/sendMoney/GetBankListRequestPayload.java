package com.iBanking.iBanking.payload.transactions.sendMoney;

import com.iBanking.iBanking.utils.Generics;
import lombok.Data;

@Data
public class GetBankListRequestPayload {
    private String bank = "ALL";
    private String requestId = Generics.generateRequestId();

}
