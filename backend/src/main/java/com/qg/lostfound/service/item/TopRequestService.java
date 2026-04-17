package com.qg.lostfound.service.item;

import com.qg.lostfound.dto.item.TopRequestAddDTO;

public interface TopRequestService {

    void addTopRequest(Integer userId, TopRequestAddDTO dto);

    void approveTopRequest(Integer adminUserId, Integer requestId);

    void rejectTopRequest(Integer adminUserId, Integer requestId);
}