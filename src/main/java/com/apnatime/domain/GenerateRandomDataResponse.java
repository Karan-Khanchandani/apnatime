package com.apnatime.domain;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenerateRandomDataResponse {
    private String errorMessage;
    private Boolean isSuccessful;
}
