package com.logistics.item.business.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Item {
    @Builder.Default
    final String id = UUID.randomUUID().toString();
    @With
    String name;

}
