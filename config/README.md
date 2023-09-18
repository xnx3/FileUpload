## 简介说明
通过json方式来定义使用哪种存储、存储的参数，进而实例化存储对象，进行存储操作。  

## 使用场景
比如我有一个建站平台，可以开通多个网站。每个网站都可以设置它自己的存储方式、以及生成网站html静态文件的保存方式，那么可以使用本项目的能力，有网站用户自行决定采用哪种存储方式。而多种多样的存储方式，无需建站平台本身进行代码的判断、适配等繁琐的工作！

## 示例描述
例如：  

````
{
    "storage":"cn.zvo.fileupload.storage.local.LocalStorage",
    "config":{
        "path":"/mnt/tomcat8/logs/"
    }
}
````
其中：
* **storage** 是当前使用的是那种存储方式，存的是包名
* **config** 是这种存储方式要设置哪些参数。这里是本地存储，所以要设置path本地存储的路径。其他比如ftp存储，还要设置用户名、密码、host、存储路径等。不同的存储方式设置的参数也是不同的

## 支持的存储方式
#### 本地存储
````
{
    "storage":"cn.zvo.fileupload.storage.local.LocalStorage",
    "config":{
        "path":"/mnt/tomcat8/logs/"
    }
}
````

#### 华为云OBS存储

````
{
    "storage":"cn.zvo.fileupload.storage.huaweicloudOBS.HuaweicloudOBSStorage",
    "config":{
        "accessKeyId":"H0TPUBC6YDZxxxxxxxx",
        "accessKeySecret":"je56lHuJ62VOhoSXxsfI9InmPAtVY9xxxxxxx",
        "obsname":"testname",
        "endpoint":"obs.cn-north-4.myhuaweicloud.com"
    }
}
````
其中 config 中的参数说明，参考华为云开发者组织仓库中FileUpload的 [2.2 参数配置](https://gitee.com/HuaweiCloudDeveloper/file-upload#22-%E5%8F%82%E6%95%B0%E9%85%8D%E7%BD%AE)

#### 阿里云OSS存储
````
{
    "storage":"cn.zvo.fileupload.storage.aliyunOSS.AliyunOSSStorage",
    "config":{
        "accessKeyId":"H0TPUBC6YDZxxxxxxxx",
        "secretAccessKey":"je56lHuJ62VOhoSXxsfI9InmPAtVY9xxxxxxx",
        "bucketname":"testname",
        "endpoint":"oss-cn-hongkong.aliyuncs.com"
    }
}
````
其中 config 中的参数说明，参考FileUpload的 [2.2 参数配置](https://gitee.com/mail_osc/FileUpload/tree/main/storage_aliyunOSS#22-%E4%BB%A3%E7%A0%81%E4%B8%AD%E4%BD%BF%E7%94%A8)

#### 七牛云Kodo存储
````
{
    "storage":"cn.zvo.fileupload.storage.qiniuKodo.QiniuKodoStorage",
    "config":{
        "accessKeyId":"H0TPUBC6YDZxxxxxxxx",
        "accessKeySecret":"je56lHuJ62VOhoSXxsfI9InmPAtVY9xxxxxxx",
        "bucketName":"testname",
        "domain":"www.xxxx.com"
    }
}
````
其中 config 中的参数说明，参考FileUpload的 [2.2 参数配置](https://github.com/xnx3/fileupload-storage-qiniucloudKodo#22-%E5%8F%82%E6%95%B0%E9%85%8D%E7%BD%AE)

#### SFTP方式存储

````
{
    "storage":"cn.zvo.fileupload.storage.sftp.SftpStorage",
    "config":{
        "host":"12.12.12.12",
        "username":"root",
        "password":"123456",
        "port":"22",
        "directory":"/root/"
    }
}
````
其中 config 中的参数说明，参考： 
[https://github.com/xnx3/FileUpload/tree/main/storage_sftp](https://github.com/xnx3/FileUpload/tree/main/storage_sftp)


## 代码示例

````
package cn.zvo.fileupload.config;

import com.xnx3.BaseVO;
import com.xnx3.FileUtil;
import com.xnx3.Log;
import cn.zvo.fileupload.FileUpload;
import cn.zvo.fileupload.config.vo.StorageVO;
import cn.zvo.fileupload.vo.UploadFileVO;
import net.sf.json.JSONObject;

/**
 * 使用demo
 * @author 管雷鸣
 *
 */
public class Demo {
	
	public static void main(String[] args) {
		
		//设置 json 配置存放的方式，比如用户a选择使用华为云存储，华为云ak相关的是啥，进行的持久化存储相关，也就是保存、取得这个。 这里用于演示所以使用一个简单的以文件方式进行存储的
		Config.setConfigStorageInterface(new ConfigStorageInterface() {
			public BaseVO save(String key, String json) {
				boolean isSuccess = FileUtil.write("G:\\git\\FileUpload\\config_json\\target\\"+key, json);
				if(isSuccess) {
					return BaseVO.success();
				}else {
					return BaseVO.failure("保存失败");
				}
			}
			public BaseVO get(String key) {
				String text = FileUtil.read("G:\\git\\FileUpload\\config_json\\target\\"+key);
				return BaseVO.success(text);
			}
		});
		
		//假设这个用户的唯一标识符是 user_123
		String key = "user_123";
		
		//将用户abc自己定义的存储方式进行持久化保存
		String jsonString = "{\"storage\":\"cn.zvo.fileupload.storage.local.LocalStorage\",\"config\":{\"path\":\"/mnt/tomcat8/logs/\"}}";
		BaseVO saveVO = Config.save(key, jsonString);
		Log.info("json格式的配置文件保存结果："+saveVO.toString());
		
		//取出用户abc自己定义的存储方式配置
		StorageVO storageVO = Config.get(key);
		//用户自定义的存储方式数据,也就是json格式的数据
		JSONObject json = storageVO.getJson();
		Log.info("用户自定义的存储方式数据 ："+json.toString());
		//获取 fileupload ，可以直接用来操作文件。当然如果你是在springboot中进行使用，您可以手动创建 cn.zvo.fileupload.framework.springboot.FileUpload ，然后在设置它的 storage
		FileUpload fileUpload = storageVO.getFileupload();
		
		//上传一个txt文件，内容为123
		UploadFileVO uploadFileVO = fileUpload.uploadString("a/b/1.txt", "123", true);
		Log.info(uploadFileVO.toString());
	}
	
}

````

这个demo文件是在 [https://gitee.com/mail_osc/FileUpload/blob/main/config_json/src/main/java/cn/zvo/fileupload/config/json/Demo.java](https://gitee.com/mail_osc/FileUpload/blob/main/config_json/src/main/java/cn/zvo/fileupload/config/json/Demo.java)  
控制台打印：

````
fileupload storage : cn.zvo.fileupload.storage.local.LocalStorage 	 cn.zvo.fileupload.config.json.Config.<clinit>() 41 Line
json格式的配置文件保存结果：BaseVO [result=1, info=SUCCESS] 	 cn.zvo.fileupload.config.json.Demo.main() 42 Line
用户自定义的存储方式数据 ：{"storage":"cn.zvo.fileupload.storage.local.LocalStorage","config":{"path":"/mnt/tomcat8/logs/"}} 	 cn.zvo.fileupload.config.json.Demo.main() 48 Line
UploadFileVO [name=1.txt, path=a/b/1.txt, url=null, size=3, getResult()=1, getInfo()=success] 	 cn.zvo.fileupload.config.json.Demo.main() 56 Line

````
