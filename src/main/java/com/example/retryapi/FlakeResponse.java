package com.example.retryapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlakeResponse {
    private String id;
    private String type;
    private String receiptCode;
}
