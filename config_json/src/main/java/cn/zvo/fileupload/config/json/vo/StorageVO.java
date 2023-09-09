package cn.zvo.fileupload.config.json.vo;

import com.xnx3.BaseVO;

import cn.zvo.fileupload.StorageInterface;

/**
 * 根据json格式的配置创建的 Storage 对象
 * @author 管雷鸣
 */
public class StorageVO extends BaseVO {
	private StorageInterface storage;

	public StorageInterface getStorage() {
		return storage;
	}

	public void setStorage(StorageInterface storage) {
		this.storage = storage;
	}
	
}
