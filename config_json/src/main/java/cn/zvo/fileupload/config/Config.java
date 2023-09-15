package cn.zvo.fileupload.config;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xnx3.BaseVO;
import com.xnx3.Log;
import com.xnx3.ScanClassUtil;
import com.xnx3.json.JSONUtil;

import cn.zvo.fileupload.StorageInterface;
import cn.zvo.fileupload.config.vo.ConfigVO;
import cn.zvo.fileupload.config.vo.StorageVO;
import cn.zvo.fileupload.config.vo.bean.Custom;
import cn.zvo.fileupload.config.vo.bean.Storage;
import cn.zvo.fileupload.vo.StorageConfigVO;
import net.sf.json.JSONObject;

/**
 * 配置方式
 * @author 管雷鸣
 *
 */
public class Config {
	//配置数据持久化存储的方式
	public static ConfigStorageInterface configStorageInterface;
	public static List<Class<?>> storageList; //当前有哪些存储方式，比如本地存储，华为云OBS存储等
	
	static {
		storageList = new ArrayList<Class<?>>();
		
		List<Class<?>> classList = ScanClassUtil.getClasses("cn.zvo.fileupload");
		if(classList.size() == 0) {
			System.err.println("====================");
			System.err.println(" 【【【 ERROR 】】】    ");
			System.err.println(" fileupload storage 扫描，未发现任何有关 storage 存储方式存在，请确认pom.xml是否加入了 https://gitee.com/mail_osc/FileUpload 的 storage 支持模块");
			System.err.println("====================");
		}
		
		List<Class<?>> storageClassList = ScanClassUtil.searchByInterfaceName(classList, "cn.zvo.fileupload.StorageInterface");
		for (int i = 0; i < storageClassList.size(); i++) {
			Class storageClass = storageClassList.get(i);
			Log.debug("fileupload storage : "+storageClass.getName());
			storageList.add(storageClass);
		}
	}
	
	/**
	 * 传入一个 storage 的 class name，判断当前jvm中是否存在这种存储方式
	 * @param storageClassName 传入如 cn.zvo.fileupload.storage.local.LocalStorage
	 * @return 如果存在，返回true
	 */
	public static boolean isExit(String storageClassName) {
		for(int i = 0; i < storageList.size(); i++) {
			if(storageClassName.equalsIgnoreCase(storageList.get(i).getName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取当前项目中存在的所有 Storage 存储方式、以及当前用户自定义的存储配置。
	 * 此适合配合 fileupload-config.js 一起使用效果最佳
	 * @return {@link ConfigVO}
	 */
	public static ConfigVO getAllStorage(String key) {
		getAllStorage();	//初始化，避免是第一次，还没有 static configVO
		
		ConfigVO vo = new ConfigVO();
		vo.setStorageList(configVO.getStorageList());
		
		//取当前这个用户自定义的存储设置
		BaseVO storageVO = configStorageInterface.get(key);
		if(storageVO.getResult() - BaseVO.FAILURE == 0) {
			//失败
			configVO.setBaseVO(ConfigVO.FAILURE, storageVO.getInfo());
		}else {
			//成功
			JSONObject json = JSONObject.fromObject(storageVO.getInfo());
			Custom custom = new Custom();
			custom.setStorage(JSONUtil.getString(json, "storage"));
			custom.setConfig(json.getJSONObject("config"));
			vo.setCustom(custom);
		}
		return vo;
	}
	
	/**
	 * 获取当前项目中存在的 Storage 存储方式
	 * @return 返回的是所有的存储方式 {@link ConfigVO} 注意，这个ConfigVO中的 custom 是不会赋予内容的，如果需要赋予内容，此方法需要传入 key
	 */
	public static ConfigVO getAllStorage() {
		if(configVO == null) {
			//需要初始化
			configVO = new ConfigVO();
			
			for(int i = 0; i < storageList.size(); i++) {
				Storage storage = new Storage();
				
				String storagePackageName = storageList.get(i).getName();
				storage.setId(storagePackageName);
				StorageInterface storageInterface = newStorage(storagePackageName, new HashMap<String, String>());
				
				StorageConfigVO vo = storageInterface.config();
				if(vo == null) {
					Log.error(storagePackageName+" 未实现 config() 方法！未能取到此 Storage 的实例化需要的传入参数");
					continue;
				}
				if(vo.getResult() - StorageConfigVO.FAILURE == 0) {
					Log.error(storagePackageName+" 实现的 config() 方法获取异常，未能取到此 Storage 的实例化需要的传入参数。 异常信息："+vo.getInfo());
					continue;
				}
				storage.setParamList(vo.getParamList());
				storage.setDescription(vo.getDescription());
				storage.setName(vo.getName());
				
				configVO.getStorageList().add(storage);
			}
		}
		
		return configVO;
	}
	public static ConfigVO configVO;
	
	/**
	 * 创建 Storage 存储对象
	 * @param storageClassName storage存储的具体类名，传入如  cn.zvo.fileupload.storage.local.LocalStorage
	 * @param configMap 存储方式附带的参数，如 本地存储则需要设置path参数等
	 * @return 如果有这种存储并且正常new出来了，那么返回具体存储对象。否则返回null
	 */
	public static StorageInterface newStorage(String storageClassName, Map<String, String> configMap) {
		for(int i = 0; i < storageList.size(); i++) {
			if(storageClassName.equalsIgnoreCase(storageList.get(i).getName())) {
				try {
					Object newInstance = storageList.get(i).getDeclaredConstructor(Map.class).newInstance(configMap);
					StorageInterface storage = (StorageInterface) newInstance;
					return storage;
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException| InvocationTargetException  | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		
		System.out.println(isExit("1cn.zvo.fileupload.storage.local.LocalStorage"));
		
		
		
//		try {
//			Object newInstance = storageClass.getDeclaredConstructor(Map.class).newInstance(entry.getValue());
//			StorageInterface storage = (StorageInterface) newInstance;
//			storageList.add(storageClass);
//		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException| InvocationTargetException  | NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		}
	}
	
	
	
	/**
	 * 设置保存
	 * @param key 唯一标识，因为可能会出现多种存储情况，所以每个key可以对应一个存储方式
	 * 传入的是一个json：
	 * {
		    "storage":"cn.zvo.fileupload.storage.local.LocalStorage",
		    "config":{
		        "path":"/mnt/tomcat8/logs/"
		    }
		}
	 * 其中：
	 * storage 是当前使用的是那种存储方式，存的是包名
	 * config 是这种存储方式要设置哪些参数。这里是本地存储，所以要设置path本地存储的路径。其他比如ftp存储，还要设置用户名、密码、host、存储路径等。不同的存储方式设置的参数也是不同的
	 * 
	 */
	public BaseVO save(String key, String json){
		BaseVO vo = new BaseVO();
		
		JSONObject jsonObj = JSONObject.fromObject(json);
		String storage = jsonObj.getString("storage");
		if(storage == null || storage.length() < 1) {
			return BaseVO.failure("请设置storage");
		}
		
		if(jsonObj.get("config") == null) {
			return BaseVO.failure("请设置 config");
		}
		
		//JSONObject config = jsonObj.getJSONObject("config");
		return configStorageInterface.save(key, json);
	}
	
	/**
	 * 传入 config 的字符串配置数据，返回 StorageVO
	 * @param configString json格式的字符串配置
	 * @return
	 */
	public static StorageVO configToStorageVO(String configString) {
		StorageVO vo = new StorageVO();
		
		JSONObject json = JSONObject.fromObject(configString);
		if(json.get("config") == null) {
			vo.setBaseVO(StorageVO.FAILURE, "未发现配置中的 config 属性");
			return vo;
		}
		if(json.get("storage") == null) {
			vo.setBaseVO(StorageVO.FAILURE, "未发现配置中的 storage 属性");
			return vo;
		}
		vo.setJson(json);
		
		//存储的类名
		String storageClassName = json.getString("storage");
		if(!isExit(storageClassName)) {
			vo.setBaseVO(StorageVO.FAILURE, "系统中当前未存在此种存储方式:"+storageClassName);
			return vo;
		}
		
		//存储的config参数
		JSONObject configJson = json.getJSONObject("config");
		//要new storage 需要的map
		Map<String, String> configMap = new HashMap<String, String>();
		// 遍历 JSONObject 对象的键
        for (Object configKeyObj : configJson.keySet()) {
        	String ckey = configKeyObj.toString();
            //System.out.println(ckey);
            configMap.put(ckey, configJson.getString(ckey));
        }
		
        StorageInterface storage = newStorage(storageClassName, configMap);
        vo.setStorage(storage);
        if(storage == null) {
        	vo.setBaseVO(StorageVO.FAILURE, "Storage对象初始化失败");
        }
		return vo;
	}
	
	/**
	 * 根据key获取其 Storage 存储对象
	 * @param key 唯一标识，因为可能会出现多种存储情况，所以每个key可以对应一个存储方式
	 * @return 如果有这种存储并且正常new出来了，那么返回具体存储对象。否则返回null
	 */
	public StorageVO get(String key){
		StorageVO vo = new StorageVO();
		
		BaseVO configStorageVO = configStorageInterface.get(key);
		if(configStorageVO == null) {
			vo.setBaseVO(StorageVO.FAILURE, "未发现配置文件存在");
			return vo;
		}
		if(configStorageVO.getResult() - BaseVO.FAILURE == 0) {
			vo.setBaseVO(StorageVO.FAILURE, configStorageVO.getInfo());
			return vo;
		}
		
		String jsonString = configStorageVO.getInfo();
		return configToStorageVO(jsonString);
	}

	public ConfigStorageInterface getConfigStorageInterface() {
		return Config.configStorageInterface;
	}

	public static void setConfigStorageInterface(ConfigStorageInterface configStorageInterface) {
		Config.configStorageInterface = configStorageInterface;
	}
	
}
