package com.users.app.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PhoneDTO {

    @NotNull
    private Long number;

    @NotNull
    private Integer cityCode;

    @NotBlank
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
