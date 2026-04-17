package com.qg.lostfound.service.ai;

import java.time.LocalDateTime;

public interface AiAssistService {

    String generateLostItemDescription(Integer userId, Integer lostItemId);

    String generateFoundItemDescription(Integer userId, Integer foundItemId);

    String regenerateLostItemDescription(Integer userId, Integer lostItemId);

    String regenerateFoundItemDescription(Integer userId, Integer foundItemId);

    String generateAdminSummary(LocalDateTime startTime, LocalDateTime endTime);
}