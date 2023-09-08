package cn.zvo.fileupload.storage.sftp.bean;

/**
 * 将传入的path参数转化为 path、fileName
 * @author 管雷鸣
 *
 */
public class PathBean {
	
	private String path; //路径，如  a/bc/
	private String fileName; //文件名，如 a.html
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public String toString() {
		return "PathBean [path=" + path + ", fileName=" + fileName + "]";
	}
	
}
