package cn.zvo.fileupload.framework.springboot;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * application.properties / yml 中的一些相关配置项目
 * @author 管雷鸣
 */
@Component(value = "fildUploadApplicationConfig")
@ConfigurationProperties(prefix = "fileupload")
public class ApplicationConfig {
	//自定义存储方式时，创建存储方式的初始化相关参数
	private Map<String, Map<String, String>> storage;
	
	//设置上传后文件所访问URL的域名，传入如： http://xxxx.com/  注意格式，后面以 / 结尾
	//@Value("${fileupload.domain}")
	private String domain;
	
	//设置允许上传的后缀名,传入格式如 png|jpg|gif|zip 多个用英文|分割。如果不设置，默认允许像是pdf、word、图片、音频、视频、zip等常用的且安全的文件后缀可上传
	//@Value("${fileupload.allowUploadSuffix}")
	private String allowUploadSuffix;
	
	//设置允许上传的文件最大是多大，比如10MB 单位为 KB、MB ， 如果此项不设置，这里默认是3MB
	//@Value("${fileupload.maxSize}")
	private String maxSize;
	
	//当前使用的是哪种存储方式，传入使用的存储方式的class的包名+类名。比如这个华为云OBS存储方式的，这个class类便是 https://github.com/xnx3/FileUpload/blob/main/storage_huaweicloudOBS/src/main/java/cn/zvo/fileupload/storage/huaweicloudOBS/HuaweicloudOBSStorage.java
	//如果此不设置，默认使用的是本地存储的方式。如果设置了，pom.xml 文件中，记得将此存储方式引入进来，不然会报错找不到这个class文件
	//@Value("${fileupload.storage.className}")
//	private String storage;
	
	
	public Map<String, Map<String, String>> getStorage() {
		return storage;
	}
	public void setStorage(Map<String, Map<String, String>> storage) {
		this.storage = storage;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getAllowUploadSuffix() {
		return allowUploadSuffix;
	}
	public void setAllowUploadSuffix(String allowUploadSuffix) {
		this.allowUploadSuffix = allowUploadSuffix;
	}
	public String getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(String maxSize) {
		this.maxSize = maxSize;
	}
	@Override
	public String toString() {
		return "ApplicationConfig [storage=" + storage + ", domain=" + domain + ", allowUploadSuffix="
				+ allowUploadSuffix + ", maxSize=" + maxSize + "]";
	}
	
}