package com.qg.lostfound.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStatsVO {

    private Long publishCount;

    private Long resolvedCount;

    private Integer activeUserCount;
}