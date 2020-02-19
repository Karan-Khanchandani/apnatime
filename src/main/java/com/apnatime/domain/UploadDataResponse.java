package com.apnatime.domain;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UploadDataResponse {
    private Long numberOfRecordsAdded;
    private Boolean isSuccessful;
    private String errorMessage;
}
