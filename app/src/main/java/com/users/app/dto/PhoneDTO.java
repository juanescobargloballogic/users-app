package com.users.app.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public class PhoneDTO {

    @NotNull
    @Schema(description = "Phone number", example = "3001231212")
    private Long number;

    @NotNull
    @Schema(description = "City code", example = "4")
    private Integer cityCode;

    @NotBlank
    @Schema(description = "Country code", example = "57")
    private String countryCode;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
