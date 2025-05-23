package com.users.app.transformer;

import java.util.List;
import java.util.stream.Collectors;

import com.users.app.dto.PhoneDTO;
import com.users.app.entity.PhoneEntity;
import com.users.app.entity.UserEntity;

import java.util.Collections;

import org.springframework.util.CollectionUtils;

public class PhoneTransformer {

    private PhoneTransformer() {
        // Private constructor to prevent instantiation
    }

    public static List<PhoneEntity> toEntityList(final List<PhoneDTO> phoneList, final UserEntity user) {
        if (CollectionUtils.isEmpty(phoneList)) {
            return Collections.emptyList();
        }
        return phoneList.stream()
            .map(phoneDTO -> toEntity(phoneDTO, user))
            .collect(Collectors.toList());
    }

    private static PhoneEntity toEntity(PhoneDTO phoneDTO, UserEntity user) {
        PhoneEntity phone = new PhoneEntity();
        phone.setNumber(phoneDTO.getNumber());
        phone.setCityCode(phoneDTO.getCityCode());
        phone.setCountryCode(phoneDTO.getCountryCode());
        phone.setUser(user);
        return phone;
    }

    public static List<PhoneDTO> toDTOList(final List<PhoneEntity> phoneList) {
        if (CollectionUtils.isEmpty(phoneList)) {
            return Collections.emptyList();
        }
        return phoneList.stream()
            .map(PhoneTransformer::toDTO)
            .collect(Collectors.toList());
    }

    private static PhoneDTO toDTO(PhoneEntity phoneEntity) {
        PhoneDTO phone = new PhoneDTO();
        phone.setNumber(phoneEntity.getNumber());
        phone.setCityCode(phoneEntity.getCityCode());
        phone.setCountryCode(phoneEntity.getCountryCode());
        return phone;
    }

}
