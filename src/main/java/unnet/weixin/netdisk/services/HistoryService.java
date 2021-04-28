package unnet.weixin.netdisk.services;

import com.baomidou.mybatisplus.extension.service.IService;
import unnet.weixin.netdisk.entity.HistoryKeyword;

import java.util.List;

public interface HistoryService extends IService<HistoryKeyword> {

    int addKeyWord(String openid, String keyWord);

    List<HistoryKeyword> getKeyWordList(String openid, int count);

    int deleteAllKeyWords(String openid);
}
