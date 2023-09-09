package cn.zvo.fileupload.config.json;

import com.xnx3.BaseVO;

/**
 * 配置持久化存储的方式
 * @author 管雷鸣
 *
 */
public interface ConfigStorageInterface {
	
	public BaseVO save(String key, String json);
	
	public BaseVO get(String key);
}
