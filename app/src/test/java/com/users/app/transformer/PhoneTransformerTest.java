package com.users.app.transformer;

import com.users.app.dto.PhoneDTO;
import com.users.app.entity.PhoneEntity;
import com.users.app.entity.UserEntity;
import com.users.app.testdata.PhoneDTOObjectMother;
import com.users.app.testdata.PhoneEntityObjectMother;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneTransformerTest {

    @Test
    void testToEntityList_shouldTransformPhoneDTOListToEntityList() {
        final List<PhoneDTO> sourcePhoneList = List.of(
            PhoneDTOObjectMother.withValues(111111111L, 3, "44"),
            PhoneDTOObjectMother.defaultPhone()
        );

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());

        List<PhoneEntity> entities = PhoneTransformer.toEntityList(sourcePhoneList, user);

        assertThat(entities).hasSize(sourcePhoneList.size());

        assertThat(entities.get(0).getNumber()).isEqualTo(111111111L);
        assertThat(entities.get(0).getCityCode()).isEqualTo(3);
        assertThat(entities.get(0).getCountryCode()).isEqualTo("44");
        assertThat(entities.get(0).getUser()).isEqualTo(user);

        assertThat(entities.get(1).getNumber()).isEqualTo(123456789L);
        assertThat(entities.get(1).getCityCode()).isEqualTo(1);
        assertThat(entities.get(1).getCountryCode()).isEqualTo("57");
        assertThat(entities.get(1).getUser()).isEqualTo(user);
    }

    @Test
    void testToDTOList_shouldTransformPhoneEntityListToDTOList() {

        final List<PhoneEntity> sourcePhoneList = List.of(
            PhoneEntityObjectMother.withValues(999000111L, 5, "49"),
            PhoneEntityObjectMother.defaultPhone()
        );

        List<PhoneDTO> dtos = PhoneTransformer.toDTOList(sourcePhoneList);

        assertThat(dtos).hasSize(sourcePhoneList.size());

        assertThat(dtos.get(0).getNumber()).isEqualTo(999000111L);
        assertThat(dtos.get(0).getCityCode()).isEqualTo(5);
        assertThat(dtos.get(0).getCountryCode()).isEqualTo("49");

        assertThat(dtos.get(1).getNumber()).isEqualTo(987654321L);
        assertThat(dtos.get(1).getCityCode()).isEqualTo(2);
        assertThat(dtos.get(1).getCountryCode()).isEqualTo("58");
    }

    @Test
    void testToEntityList_shouldReturnEmptyListWhenInputIsEmpty() {
        List<PhoneDTO> emptyPhoneDTOs = Collections.emptyList();
        List<PhoneEntity> result = PhoneTransformer.toEntityList(emptyPhoneDTOs, new UserEntity());
        assertThat(result).isEmpty();
    }

    @Test
    void testToEntityList_shouldReturnEmptyListWhenInputIsNull() {
        List<PhoneEntity> result = PhoneTransformer.toEntityList(null, new UserEntity());
        assertThat(result).isEmpty();
    }

    @Test
    void testToDTOList_shouldReturnEmptyListWhenInputIsEmpty() {
        List<PhoneEntity> emptyPhoneEntities = Collections.emptyList();
        List<PhoneDTO> result = PhoneTransformer.toDTOList(emptyPhoneEntities);
        assertThat(result).isEmpty();
    }

    @Test
    void testToDTOList_shouldReturnEmptyListWhenInputIsNull() {
        List<PhoneDTO> result = PhoneTransformer.toDTOList(null);
        assertThat(result).isEmpty();
    }

}


