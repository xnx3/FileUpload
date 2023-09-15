package cn.zvo.fileupload.config.vo.bean;

import java.util.List;
import cn.zvo.fileupload.vo.bean.Param;

/**
 * 存储每个 storage 相关的config param 信息。 服务于 {@link ConfigVO}
 * @author 管雷鸣
 *
 */
public class Storage {
	private String id; //storage的唯一标识，也就是包名
	private String name; 	//存储的名字，比如本地存储、SFTP存储、阿里云OSS存储
	private String description;	//存储的描述，对存储名字的更详细解释，可能是两三句话，比如本地存储的话，这里是 “将上传文件存储到本服务器磁盘进行存储”
	private List<Param> paramList;	//配置参数列表
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<Param> getParamList() {
		return paramList;
	}
	public void setParamList(List<Param> paramList) {
		this.paramList = paramList;
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
	@Override
	public String toString() {
		return "Storage [id=" + id + ", name=" + name + ", description=" + description + ", paramList=" + paramList
				+ "]";
	}
	
}
