package com.apnatime.domain;

import com.apnatime.entity.User;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchResult {
    private User user;
    private Integer numberOfMutualConnections;
    private Integer level;

}
