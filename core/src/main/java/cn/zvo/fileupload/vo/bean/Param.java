package cn.zvo.fileupload.vo.bean;

/**
 * storage中具体配置的参数
 * 服务于 {@link ConfigVO}
 * @author 管雷鸣
 *
 */
public class Param {
	private String id;		//参数，唯一标识
	private String name;	//参数的名字，如本地存储的path参数，这是“路径”，比如sftp中 username，那么这里便是“用户名”，相当于input输入框前面的那几个字
	private String description;	//参数描述，这个参数设置的说明
	private boolean require;	//是否必填，true必填，  false非必填
	private String defaultValue;	//如果不设置，默认的值
	
	/**
	 * storage中具体配置的参数
	 * @param name 参数名
	 * @param description 参数描述，这个参数设置的说明
	 * @param require 是否必填，true必填，  false非必填
	 * @param defaultValue 如果不填写，默认的值
	 */
	public Param(String id, String name, String description, boolean require, String defaultValue) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.require = require;
		this.defaultValue = defaultValue;
	}
	
	public Param() {
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isRequire() {
		return require;
	}
	public void setRequire(boolean require) {
		this.require = require;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Param [id=" + id + ", name=" + name + ", description=" + description + ", require=" + require
				+ ", defaultValue=" + defaultValue + "]";
	}
	
}
