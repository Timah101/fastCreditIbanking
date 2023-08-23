package com.iBanking.iBanking.Forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class SendMoneyForms {

    @NotNull(message = "{field.not-null}")
    @NotEmpty(message = "{field.not-empty}")
    private String amount;
    @NotNull(message = "{field.not-null}")
    @NotEmpty(message = "{field.not-empty}")
    private String narration;
    private String debitAccount;
    @NotNull(message = "{field.not-null}")
    @NotEmpty(message = "{field.not-empty}")
    @Size(min = 10, message = "{debit.account.size}")
    private String creditAccount;
    private String beneficiaryBank;
    private String beneficiaryName;
    private String date = String.valueOf(LocalDate.now());
    private String pin;
}
