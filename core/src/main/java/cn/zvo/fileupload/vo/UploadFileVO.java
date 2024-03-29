package cn.zvo.fileupload.vo;

import com.xnx3.BaseVO;

/**
 * 文件上传相关（最初为aliyun oss所写）
 * @author 管雷鸣
 */
public class UploadFileVO extends BaseVO {
	
	/**
	 * 无文件
	 */
	public final static int NOTFILE=2;
	
	public String name;	//上传成功后的文件名，如 "xnx3.jar"
	public String path;		//上传成功后的路径，如 "/jar/file/xnx3.jar"
	public String url;			//文件上传成功后，外网访问的url
	public long size;			//上传的文件的大小，单位 KB
	
	public UploadFileVO() {
		
	}
	
	/**
	 * 这是OSS上传成功后的返回值
	 * @param fileName 上传成功后的文件名，如 "xnx3.jar"
	 * @param path 上传成功后的路径，如 "/jar/file/xnx3.jar"
	 * @param url 文件上传成功后，外网访问的url
	 */
	public UploadFileVO(String name,String path,String url) {
		this.name = name;
		this.path = path;
	}
	
	/**
	 * 上传成功后的文件名，如 "xnx3.jar"
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * 上传成功后的文件名，如 "xnx3.jar"
	 * @param fileName
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 上传成功后的路径，如 "/jar/file/xnx3.jar"
	 * @return
	 */
	public String getPath() {
		return path;
	}
	/**
	 * 上传成功后的路径，如 "/jar/file/xnx3.jar"
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * 文件上传成功后，外网访问的url
	 * @return 返回如  http://test.zvo.cn/jar/file/xnx3.jar
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * 文件上传成功后，外网访问的url
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "UploadFileVO [name=" + name + ", path=" + path + ", url=" + url + ", size=" + size + ", getResult()="
				+ getResult() + ", getInfo()=" + getInfo() + "]";
	}

	
}
