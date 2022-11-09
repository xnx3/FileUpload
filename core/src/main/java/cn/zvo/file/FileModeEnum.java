package cn.zvo.file;
/**
 * 使用的一些存储方式
 * @author 管雷鸣
 *
 */
public enum FileModeEnum {	
	ALIYUN_OSS("AliyunOSSMode"),		//阿里云OSS
	HUAWEIYUN_OBS("HuaweiyunOBSMode"),	//华为云OBS
	LOCAL_SERVER("LocalServerMode");	//本地存储，服务器本身存储
	
	
	public final String name;		//使用的存储方式的名字
	private FileModeEnum(String name) { 
		this.name = name;
	}
}
