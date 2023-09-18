package cn.zvo.fileupload.config.vo;

import com.xnx3.BaseVO;

import cn.zvo.fileupload.FileUpload;
import cn.zvo.fileupload.StorageInterface;
import net.sf.json.JSONObject;

/**
 * 根据json格式的配置创建的 Storage 对象
 * @author 管雷鸣
 */
public class StorageVO extends BaseVO {
	private JSONObject json;		//json字符串格式的配置数据
	private StorageInterface storage;	//实例化后的 storage 对象，可以直接使用进行相关存储
	private FileUpload fileupload;	//文件存储对象
	
	public StorageInterface getStorage() {
		return storage;
	}

	public void setStorage(StorageInterface storage) {
		this.storage = storage;
	}
	
	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public FileUpload getFileupload() {
		if(this.getStorage() == null) {
			return null;
		}
		fileupload = new FileUpload();
		fileupload.setStorage(storage);
		return fileupload;
	}

	public void setFileupload(FileUpload fileupload) {
		this.fileupload = fileupload;
	}

	@Override
	public String toString() {
		return "StorageVO [json=" + json + ", storage=" + storage + ", fileupload=" + fileupload + ", getResult()="
				+ getResult() + ", getInfo()=" + getInfo() + "]";
	}
	
}
