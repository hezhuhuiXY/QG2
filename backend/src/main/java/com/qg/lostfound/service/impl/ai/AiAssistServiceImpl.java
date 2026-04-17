package com.qg.lostfound.service.impl.ai;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qg.lostfound.entity.ai.AiRecord;
import com.qg.lostfound.entity.item.FoundItem;
import com.qg.lostfound.entity.item.LostItem;
import com.qg.lostfound.exception.type.BusinessException;
import com.qg.lostfound.mapper.AiRecordMapper;
import com.qg.lostfound.mapper.FoundItemMapper;
import com.qg.lostfound.mapper.LostItemMapper;
import com.qg.lostfound.service.ai.AiAssistService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * AI助手服务实现类
 * 功能：AI生成物品描述、AI生成平台管理总结、记录AI调用日志
 */
@Service
@RequiredArgsConstructor
public class AiAssistServiceImpl implements AiAssistService {
    // 失物信息Mapper
    private final LostItemMapper lostItemMapper;
    // 拾物信息Mapper
    private final FoundItemMapper foundItemMapper;
    // AI调用记录Mapper
    private final AiRecordMapper aiRecordMapper;
    // Spring AI 客户端构造器
    private final ChatClient.Builder chatClientBuilder;
    /**
     * AI生成【失物】描述
     * @param userId 操作用户ID
     * @param lostItemId 失物ID
     * @return 生成后的描述文本
     */
    @Override
    public String generateLostItemDescription(Integer userId, Integer lostItemId) {
        LostItem lostItem = lostItemMapper.selectById(lostItemId);
        if (lostItem == null || lostItem.getStatus() == 2) {
            throw new BusinessException("失物信息不存在");
        }
        // 调用AI生成描述
        String generatedText = callDescriptionModel(
                "丢失物品",
                lostItem.getItemName(),
                lostItem.getLostLocation(),
                lostItem.getDescription()
        );
        // 更新到数据库
        lostItem.setDescription(generatedText);
        lostItemMapper.updateById(lostItem);
        // 保存AI调用记录
        saveAiRecord(1, lostItemId, userId, 1, buildDescriptionInput(
                "丢失物品",
                lostItem.getItemName(),
                lostItem.getLostLocation(),
                lostItem.getDescription()
        ), generatedText);

        return generatedText;
    }
    /**
     * AI生成【拾物】描述
     * @param userId 操作用户ID
     * @param foundItemId 拾物ID
     * @return 生成后的描述文本
     */
    @Override
    public String generateFoundItemDescription(Integer userId, Integer foundItemId) {
        FoundItem foundItem = foundItemMapper.selectById(foundItemId);
        if (foundItem == null || foundItem.getStatus() == 2) {
            throw new BusinessException("拾物信息不存在");
        }

        String generatedText = callDescriptionModel(
                "拾取物品",
                foundItem.getItemName(),
                foundItem.getFoundLocation(),
                foundItem.getDescription()
        );

        foundItem.setDescription(generatedText);
        foundItemMapper.updateById(foundItem);

        saveAiRecord(2, foundItemId, userId, 1, buildDescriptionInput(
                "拾取物品",
                foundItem.getItemName(),
                foundItem.getFoundLocation(),
                foundItem.getDescription()
        ), generatedText);

        return generatedText;
    }
    /**
     * 重新生成【失物】描述（权限校验）
     */
    @Override
    public String regenerateLostItemDescription(Integer userId, Integer lostItemId) {
        LostItem lostItem = lostItemMapper.selectById(lostItemId);
        if (lostItem == null || lostItem.getStatus() == 2) {
            throw new BusinessException("失物信息不存在");
        }
        if (!lostItem.getUserId().equals(userId)) {
            throw new BusinessException("无权重新生成他人的失物描述");
        }

        return generateLostItemDescription(userId, lostItemId);
    }
    /**
     * 重新生成【拾物】描述（权限校验）
     */
    @Override
    public String regenerateFoundItemDescription(Integer userId, Integer foundItemId) {
        FoundItem foundItem = foundItemMapper.selectById(foundItemId);
        if (foundItem == null || foundItem.getStatus() == 2) {
            throw new BusinessException("拾物信息不存在");
        }
        if (!foundItem.getUserId().equals(userId)) {
            throw new BusinessException("无权重新生成他人的拾物描述");
        }

        return generateFoundItemDescription(userId, foundItemId);
    }
    /**
     * AI生成管理员平台总结（统计分析）
     */
    @Override
    public String generateAdminSummary(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<LostItem> lostWrapper = new LambdaQueryWrapper<>();
        lostWrapper.ne(LostItem::getStatus, 2);

        LambdaQueryWrapper<FoundItem> foundWrapper = new LambdaQueryWrapper<>();
        foundWrapper.ne(FoundItem::getStatus, 2);
        // 时间范围筛选
        if (startTime != null) {
            lostWrapper.ge(LostItem::getCreateTime, startTime);
            foundWrapper.ge(FoundItem::getCreateTime, startTime);
        }
        if (endTime != null) {
            lostWrapper.le(LostItem::getCreateTime, endTime);
            foundWrapper.le(FoundItem::getCreateTime, endTime);
        }

        List<LostItem> lostItems = lostItemMapper.selectList(lostWrapper);
        List<FoundItem> foundItems = foundItemMapper.selectList(foundWrapper);
        // 统计高发丢失地点
        Map<String, Long> lostLocationCount = lostItems.stream()
                .filter(item -> item.getLostLocation() != null && !item.getLostLocation().isBlank())
                .collect(Collectors.groupingBy(LostItem::getLostLocation, Collectors.counting()));
        // 统计高频物品
        Map<String, Long> itemNameCount = lostItems.stream()
                .filter(item -> item.getItemName() != null && !item.getItemName().isBlank())
                .collect(Collectors.groupingBy(LostItem::getItemName, Collectors.counting()));
        // 拾物物品合并统计
        foundItems.stream()
                .filter(item -> item.getItemName() != null && !item.getItemName().isBlank())
                .forEach(item -> itemNameCount.merge(item.getItemName(), 1L, Long::sum));
        // 获取最多的地点
        String topLocation = lostLocationCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("暂无明显高发区域");
        // 获取最多的物品
        String topItemName = itemNameCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("暂无明显高发物品");
        // 构造AI提示词
        String prompt = "你是校园失物招领平台的管理分析助手。"
                + "请根据以下统计数据，生成一段 50~100 字的中文总结，重点说明高发区域和高频物品，语气客观简洁。"
                + "失物数量：" + lostItems.size()
                + "；拾物数量：" + foundItems.size()
                + "；高发区域：" + topLocation
                + "；高频物品：" + topItemName + "。";
        // 调用AI生成总结
        String summary = chatClientBuilder.build()
                .prompt()
                .user(prompt)
                .call()
                .content();

        saveAiRecord(0, 0, 0, 3, prompt, summary);

        return summary;
    }
    //调用AI生成物品描述
    private String callDescriptionModel(String type, String itemName, String location, String oldDescription) {
        String prompt = "你是校园失物招领平台的AI助手。"
                + "请根据给定信息，生成一段适合失物招领平台展示的中文物品描述。"
                + "要求："
                + "1. 不要编造过度细节；"
                + "2. 语气自然；"
                + "3. 40到80字左右；"
                + "4. 不要输出标题，不要分点。"
                + "物品类型：" + type
                + "；物品名称：" + itemName
                + "；地点：" + location
                + "；已有描述：" + (oldDescription == null || oldDescription.isBlank() ? "无" : oldDescription);

        return chatClientBuilder.build()
                .prompt()
                .user(prompt)
                .call()
                .content();
    }
    //构建描述输入文本（用于保存记录）
    private String buildDescriptionInput(String type, String itemName, String location, String oldDescription) {
        return "物品类型：" + type
                + "；物品名称：" + itemName
                + "；地点：" + location
                + "；已有描述：" + (oldDescription == null ? "" : oldDescription);
    }
    /**
     * 保存AI调用记录到数据库
     * @param itemType 物品类型 1-失物 2-拾物
     * @param itemId 物品ID
     * @param userId 用户ID
     * @param aiType 功能类型 1-生成描述 3-生成总结
     * @param inputText 输入提示词
     * @param outputText AI输出内容
     */
    private void saveAiRecord(Integer itemType, Integer itemId, Integer userId, Integer aiType,
                              String inputText, String outputText) {
        AiRecord aiRecord = new AiRecord();
        aiRecord.setItemType(itemType);
        aiRecord.setItemId(itemId);
        aiRecord.setUserId(userId);
        aiRecord.setAiType(aiType);
        aiRecord.setInputText(inputText);
        aiRecord.setOutputText(outputText);
        aiRecord.setModelName("spring-ai");
        aiRecordMapper.insert(aiRecord);
    }
}