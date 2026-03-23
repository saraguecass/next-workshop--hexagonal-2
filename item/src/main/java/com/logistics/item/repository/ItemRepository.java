package com.logistics.item.repository;

import com.logistics.item.repository.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, String> {
    List<ItemEntity> findByName(String name);
}
