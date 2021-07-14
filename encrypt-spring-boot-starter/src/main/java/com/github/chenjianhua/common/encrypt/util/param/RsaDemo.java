package com.github.chenjianhua.common.encrypt.util.param;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author chenjianhua
 * @date 2021/7/2
 */
@Data
public class RsaDemo extends AbstractRsaSign {

    private String outOrderNumber = "order123";
    private List<DemoItem> demoItems;

    public RsaDemo() {
        List<DemoItem> demoItems = new LinkedList<>();
        DemoItem demoItem = new DemoItem();
        demoItem.setProductCode("productCode123");
        demoItem.setProductName("测试商品");
        demoItems.add(demoItem);
        this.demoItems = demoItems;
    }

    @Data
    private static class DemoItem {
        private String productCode;
        private String productName;
    }
}