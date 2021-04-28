package unnet.weixin.netdisk.services.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import unnet.weixin.netdisk.entity.HistoryKeyword;
import unnet.weixin.netdisk.mapper.HistoryMapper;
import unnet.weixin.netdisk.services.HistoryService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, HistoryKeyword> implements HistoryService {

    @Resource
    HistoryMapper historyMapper;

    @Override
    public int addKeyWord(String openid, String keyWord) {

        HistoryKeyword historyKeyword = new HistoryKeyword();
        historyKeyword.setKeyWord(keyWord);
        historyKeyword.setOpenid(openid);
        historyKeyword.setCreateTime(LocalDateTime.now());

        if(openid != null) {
            QueryWrapper<HistoryKeyword> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("key_word", keyWord).eq("openid", openid);
            int count = historyMapper.selectCount(queryWrapper);
            if(count == 0) {
                return historyMapper.insert(historyKeyword);
            }else if(count == 1) {
                HistoryKeyword keyword = new HistoryKeyword();
                UpdateWrapper<HistoryKeyword> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("key_word", keyWord).eq("openid", openid).set("create_time", LocalDateTime.now());
                return historyMapper.update(keyword, updateWrapper);
            }
        }
        return 0;
    }

    @Override
    public List<HistoryKeyword> getKeyWordList(String openid, int count) {
        QueryWrapper<HistoryKeyword> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid).orderByDesc("create_time").last("limit "+count);
        List<HistoryKeyword> list = historyMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public int deleteAllKeyWords(String openid) {
        UpdateWrapper<HistoryKeyword> wrapper = new UpdateWrapper<>();
        wrapper.eq("openid", openid);
        return historyMapper.delete(wrapper);
    }
}
