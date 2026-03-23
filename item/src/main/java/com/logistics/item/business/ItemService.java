package com.logistics.item.business;

import com.logistics.item.business.model.Item;
import com.logistics.item.repository.ItemRepository;
import com.logistics.item.repository.entity.ItemEntity;
import com.logistics.item.repository.mapper.ItemEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {
    final ItemRepository itemRepository;
    final ItemEntityMapper itemEntityMapper;

    public List<Item> getAllItems() {
        List<ItemEntity> items = itemRepository.findAll();
        return items.stream().map(itemEntityMapper::toDomain).toList();
    }

    public String createItem(Item item) throws InvalidItemException {
        if (!StringUtils.hasText(item.getName()))
            throw new InvalidItemException();
        itemRepository.save(itemEntityMapper.fromDomain(item));
        return item.getId();
    }

    public Optional<Item> getItemById(String id) {
        Optional<ItemEntity> response =  itemRepository.findById(id);
        return response.map(itemEntityMapper::toDomain);
    }
    public void deleteItem(String id) throws InvalidItemException, ItemNotFoundException {
        try {UUID.fromString(id);} catch (IllegalArgumentException e) {
            throw new InvalidItemException();
        }
        if (itemRepository.findById(id).isEmpty())
            throw new ItemNotFoundException();
        itemRepository.deleteById(id);
    }

    public void updateItem(Item item) throws InvalidItemException {
        if (!StringUtils.hasText(item.getName()))
            throw new InvalidItemException();

        itemRepository.save(itemEntityMapper.fromDomain(item));
    }
}
