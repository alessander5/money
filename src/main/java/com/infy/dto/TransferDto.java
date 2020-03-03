package com.infy.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TransferDto {
    Long supplierId;
    Long consumerId;
    Long amount;
}
