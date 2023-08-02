package com.iBanking.iBanking.Forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class DataTransactionForms {
    private String mobileNumber;
    @NotEmpty(message = "{mobile-number.notEmpty}")
    @Size(min = 11, max = 13, message = "{mobile-number.size}")
    private String beneficiaryMobileNumber;
    private String debitAccount;
    @NotNull(message = "{field.not-null}")
    @NotEmpty(message = "{field.not-empty}")
    private String telco;
    @NotNull(message = "{field.not-null}")
    @NotEmpty(message = "{field.not-empty}")
    private String amount;
    private String date = String.valueOf(LocalDate.now());
    private String dataPlans;
}
