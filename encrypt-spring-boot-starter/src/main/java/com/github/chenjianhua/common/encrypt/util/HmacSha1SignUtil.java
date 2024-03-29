package com.github.chenjianhua.common.encrypt.util;


import com.github.chenjianhua.common.encrypt.util.param.AbstractSign;
import com.github.chenjianhua.common.json.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.TreeMap;

/**
 * 签名校验工具
 *
 * @author xzx
 * @date 2019/12/3
 */
@Slf4j
public class HmacSha1SignUtil {

    private static final String CHARSET = StandardCharsets.UTF_8.name();


    /**
     * 拼接签名字符串
     */
    public static String getSpliceSignStr(TreeMap<String, Object> treeMap) {
        // 拼接参数
        StringBuilder sb = new StringBuilder();
        getSpliceSignStr(sb, treeMap);
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static void getSpliceSignStr(StringBuilder sb, TreeMap<String, Object> treeMap) {
        treeMap.forEach((k, v) -> {
            if (null == v) {
                return;
            }
            if (!(v instanceof List)) {
                sb.append(k).append("=").append(v).append("&");
            } else {
                List<Object> objectList = (List) v;
                objectList.forEach(item -> {
                    if (item instanceof String) {
                        sb.append(k).append("=").append(item).append("&");
                    } else {
                        TreeMap<String, Object> treeItemMap = JsonUtil.toBean(JsonUtil.toJsonString(item), TreeMap.class);
                        getSpliceSignStr(sb, treeItemMap);
                    }
                });
            }
        });
    }

    /**
     * 获取HmacSha1签名
     */
    public static String getHmacSha1Sign(TreeMap<String, Object> treeMap, String secretKey) {
        try {
            // 使用HMAC-SHA1算法计算签名，按照base64编码
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(CHARSET), mac.getAlgorithm());
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(getSpliceSignStr(treeMap).getBytes(CHARSET));
            return DatatypeConverter.printBase64Binary(hash);
        } catch (Exception e) {
            log.error("签名失败:{}", e);
            throw new RuntimeException("签名失败");
        }
    }

    /**
     * 生成签名
     */
    public static void generateSign(Object obj, String secretKey) {
        if (!(obj instanceof AbstractSign)) {
            return;
        }
        TreeMap<String, Object> treeMap = JsonUtil.toBean(JsonUtil.toJsonString(obj), TreeMap.class);
        // 获取客户端签名信息，同时从参数列表删除
        String sign = (String) treeMap.remove("sign");
        String checkSign = getHmacSha1Sign(treeMap, secretKey);
        AbstractSign signObj = (AbstractSign) obj;
        signObj.setSign(checkSign);
    }

    /**
     * 校验签名
     */
    public static boolean checkSign(Object obj, String secretKey) {
        if (!(obj instanceof AbstractSign)) {
            log.error("校验对象不符合要求");
            return false;
        }
        AbstractSign signObj = (AbstractSign) obj;
        TreeMap<String, Object> treeMap = JsonUtil.toBean(JsonUtil.toJsonString(obj), TreeMap.class);
        // 获取客户端签名信息，同时从参数列表删除
        String sign = (String) treeMap.remove("sign");
        String checkSign = getHmacSha1Sign(treeMap, secretKey);
        if (checkSign.equals(sign)) {
            return true;
        }
        return false;
    }
}
