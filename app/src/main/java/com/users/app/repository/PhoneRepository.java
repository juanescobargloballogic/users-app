package com.users.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.users.app.dto.PhoneDTO;
import com.users.app.entity.PhoneEntity;
import com.users.app.entity.UserEntity;

public interface PhoneRepository extends JpaRepository<PhoneEntity, Long> {

    List<PhoneEntity> findAllByUser(UserEntity user);
}
