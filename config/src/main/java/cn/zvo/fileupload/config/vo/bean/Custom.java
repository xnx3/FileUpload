package cn.zvo.fileupload.config.vo.bean;

import net.sf.json.JSONObject;

/**
 * 用户当前保存的自定义设置.
 * 使用场景为，用户取出用户自己设置的存储方式，进行返回
 * @author 管雷鸣
 *
 */
public class Custom {
	private String storage; //当前使用的存储方式，比如 cn.zvo.fileupload.storage.local.LocalStorage
	private JSONObject config;	//配置参数,json格式，比如 {"path":"/mnt/tomcat8/fileupload/"}
	
	public String getStorage() {
		return storage;
	}
	public void setStorage(String storage) {
		this.storage = storage;
	}
	public JSONObject getConfig() {
		return config;
	}
	public void setConfig(JSONObject config) {
		this.config = config;
	}
	@Override
	public String toString() {
		return "Custom [storage=" + storage + ", config=" + config.toString() + "]";
	}
	
}
