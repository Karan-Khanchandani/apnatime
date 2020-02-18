package com.apnatime.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenerateRandomDataRequest {
    @JsonProperty("number_of_users")
    private Integer numberOfUsers;

    @JsonProperty("max_number_of_friendships")
    private Integer maxNumberOfFriendships;
}
