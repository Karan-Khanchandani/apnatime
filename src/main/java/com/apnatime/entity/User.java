package com.apnatime.entity;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    private Integer userId;
    private String name;
    private OffsetDateTime creationDate;
}
