package com.logistics.item.facade.dto.response;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder
@Jacksonized
@Getter
@ToString
public class GetItemResponseDTO {
    final String id;
    final String name;
}
