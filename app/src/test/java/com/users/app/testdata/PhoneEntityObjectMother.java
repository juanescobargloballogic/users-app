package com.users.app.testdata;

import java.util.Arrays;
import java.util.List;

import com.users.app.entity.PhoneEntity;
import com.users.app.entity.UserEntity;

public class PhoneEntityObjectMother {

    public static List<PhoneEntity> userPhoneList(final UserEntity user) {
        return Arrays.asList(
            buildPhone(user, 12345678),
            buildPhone(user, 87654321)
        );
    }

    private static PhoneEntity buildPhone(final UserEntity user, final long number) {
        final PhoneEntity phone = new PhoneEntity();
        phone.setUser(user);
        phone.setNumber(number);
        phone.setCountryCode("57");
        phone.setCityCode(4);
        return phone;
    }

    public static PhoneEntity defaultPhone() {
        PhoneEntity entity = new PhoneEntity();
        entity.setNumber(987654321L);
        entity.setCityCode(2);
        entity.setCountryCode("58");
        return entity;
    }

    public static PhoneEntity withValues(long number, int cityCode, String countryCode) {
        PhoneEntity entity = new PhoneEntity();
        entity.setNumber(number);
        entity.setCityCode(cityCode);
        entity.setCountryCode(countryCode);
        return entity;
    }

}
