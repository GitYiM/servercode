package unnet.weixin.netdisk.utils;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
*待修改
 *
 * */
public class Gensign {
    public static String gengeSign(Map<String, String> params) {
        SortedMap<String, String> sortedMap = new TreeMap<>(params);
        StringBuilder toSign = new StringBuilder();
        for (String key : sortedMap.keySet()) {
            String value = params.get(key);
            toSign.append(key).append("=").append(value).append("&");
        }
        String val = toSign.toString();
        val = val.substring(0, val.lastIndexOf("&"));
        System.out.println(val);
        return SignatureUtil.sha1Encrypt(val);
    }
}