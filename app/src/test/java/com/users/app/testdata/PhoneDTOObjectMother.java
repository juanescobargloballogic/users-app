package com.users.app.testdata;

import java.util.Arrays;
import java.util.List;

import com.users.app.dto.PhoneDTO;

public class PhoneDTOObjectMother {

    public static List<PhoneDTO> userPhoneList() {
        return Arrays.asList(
            buildPhone(12345678),
            buildPhone(87654321)
        );
    }

    private static PhoneDTO buildPhone(final long number) {
        final PhoneDTO phone = new PhoneDTO();
        phone.setNumber(number);
        phone.setCountryCode("57");
        phone.setCityCode(4);
        return phone;
    }

    public static PhoneDTO defaultPhone() {
        PhoneDTO dto = new PhoneDTO();
        dto.setNumber(123456789L);
        dto.setCityCode(1);
        dto.setCountryCode("57");
        return dto;
    }


    public static PhoneDTO withValues(long number, int cityCode, String countryCode) {
        PhoneDTO dto = new PhoneDTO();
        dto.setNumber(number);
        dto.setCityCode(cityCode);
        dto.setCountryCode(countryCode);
        return dto;
    }

}
