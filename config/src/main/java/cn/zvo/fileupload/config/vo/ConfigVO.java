package cn.zvo.fileupload.config.vo;

import java.util.ArrayList;
import java.util.List;

import com.xnx3.BaseVO;

import cn.zvo.fileupload.config.vo.bean.Custom;
import cn.zvo.fileupload.config.vo.bean.Storage;

/**
 * 配置参数相关
 * @author 管雷鸣
 *
 */
public class ConfigVO extends BaseVO{
	private List<Storage> storageList; //当前的存储方式列表
	private Custom custom;		//用户当前保存的自定义存储设置。 使用场景为，用户取出用户自己设置的存储方式，进行返回。 如果这里为null，没有值，则是第一次使用，还没有进行保存过自定义存储设置
	
	public ConfigVO() {
		storageList = new ArrayList<Storage>();
	}

	public List<Storage> getStorageList() {
		return storageList;
	}

	public void setStorageList(List<Storage> storageList) {
		this.storageList = storageList;
	}

	public Custom getCustom() {
		return custom;
	}

	public void setCustom(Custom custom) {
		this.custom = custom;
	}

	@Override
	public String toString() {
		return "ConfigVO [storageList=" + storageList + ", custom=" + custom + ", getResult()=" + getResult()
				+ ", getInfo()=" + getInfo() + "]";
	}

	
}
