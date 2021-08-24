package com.github.chenjianhua.common.excel.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Setter
@Getter
@Component
public class OosConfig {

	private Base64 base64 = new Base64();

	public String endpoint = "endpoint";

	/**
	 * 创建和查看访问密钥的链接地址是：https://ak-console.aliyun.com/#/。
	 */
	public String accessKeyId = "accessKeyId";

	public String accessKeySecret = "accessKeySecret";

	public String bucketName = "bucketName";

	public String getEndpoint(){
		return new String(base64.decode(endpoint));
	}

	public String getAccessKeyId(){
		return new String(base64.decode(accessKeyId));
	}

	public String getAccessKeySecret(){
		return new String(base64.decode(accessKeySecret));
	}
}
