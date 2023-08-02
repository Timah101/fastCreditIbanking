package com.iBanking.iBanking.Forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PinForm {
    @NotNull(message = "{field.not-null}")
    @NotEmpty(message = "{field.not-empty}")
    private String pin;
}
