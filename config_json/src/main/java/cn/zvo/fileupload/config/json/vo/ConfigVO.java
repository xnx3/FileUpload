package cn.zvo.fileupload.config.json.vo;

import java.util.ArrayList;
import java.util.List;

import com.xnx3.BaseVO;

import cn.zvo.fileupload.config.json.vo.bean.Storage;

/**
 * 配置参数相关
 * @author 管雷鸣
 *
 */
public class ConfigVO extends BaseVO{
	private List<Storage> storageList; //当前的存储方式列表
	
	public ConfigVO() {
		storageList = new ArrayList<Storage>();
	}

	public List<Storage> getStorageList() {
		return storageList;
	}

	public void setStorageList(List<Storage> storageList) {
		this.storageList = storageList;
	}

	@Override
	public String toString() {
		return "ConfigVO [storageList=" + storageList + ", getResult()=" + getResult() + ", getInfo()=" + getInfo()
				+ "]";
	}
	
}
