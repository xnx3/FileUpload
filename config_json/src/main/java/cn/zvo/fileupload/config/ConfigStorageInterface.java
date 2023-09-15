package cn.zvo.fileupload.config;

import com.xnx3.BaseVO;

/**
 * 配置持久化存储的方式
 * @author 管雷鸣
 *
 */
public interface ConfigStorageInterface {
	
	/**
	 * 进行持久化保存
	 * @param key 也就是唯一标识符，比如 uuid 等。 场景比如，建站系统中每个网站都可以设置自己的存储方式，那这里便是某个网站的唯一标识
	 * @param json 存储方式的json配置，这个配置可能会有50~500个字符之间
	 * @return
	 */
	public BaseVO save(String key, String json);
	
	/**
	 * 从持久化存储中取出自定义的存储设置
	 * @param  key 也就是唯一标识符，比如 uuid 等。 场景比如，建站系统中每个网站都可以设置自己的存储方式，那这里便是某个网站的唯一标识
	 * @return BaseVO.info 中便是自定义的存储设置
	 */
	public BaseVO get(String key);
}
