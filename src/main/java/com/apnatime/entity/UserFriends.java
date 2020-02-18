package com.apnatime.entity;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserFriends {
    private Integer userId;
    private Integer friendId;
    private OffsetDateTime connectionDate;
}
