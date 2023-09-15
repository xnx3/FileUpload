package cn.zvo.fileupload.config;

import com.xnx3.BaseVO;
import com.xnx3.FileUtil;
import com.xnx3.Log;
import cn.zvo.fileupload.FileUpload;
import cn.zvo.fileupload.StorageInterface;
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
		
		//创建 Config 对象
		Config config = new Config();
		
		//假设这个用户的唯一标识符是 user_123
		String key = "user_123";
		
		//设置 json 配置存放的方式，比如用户a选择使用华为云存储，华为云ak相关的是啥，进行的持久化存储相关，也就是保存、取得这个。 这里用于演示所以使用一个简单的以文件方式进行存储的
		config.setConfigStorageInterface(new ConfigStorageInterface() {
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
		
		//将用户abc自己定义的存储方式进行持久化保存
		String jsonString = "{\"storage\":\"cn.zvo.fileupload.storage.local.LocalStorage\",\"config\":{\"path\":\"/mnt/tomcat8/logs/\"}}";
		BaseVO saveVO = config.save("user_abc", jsonString);
		Log.info("json格式的配置文件保存结果："+saveVO.toString());
		
		//取出用户abc自己定义的存储方式配置
		StorageVO storageVO = config.get("user_abc");
		//用户自定义的存储方式数据,也就是json格式的数据
		JSONObject json = storageVO.getJson();
		Log.info("用户自定义的存储方式数据 ："+json.toString());
		//存储对象，可以用来执行相关存储、读取操作,它提供了基本能力，但绝大多数时候是配合 FileUpload 来使用的
		StorageInterface storage = storageVO.getStorage();
		//获取 fileupload ，可以直接用来操作文件。当然如果你是在springboot中进行使用，您可以手动创建 cn.zvo.fileupload.framework.springboot.FileUpload ，然后在设置它的 storage
		FileUpload fileUpload = storageVO.getFileupload();
		
		//上传一个txt文件，内容为123
		UploadFileVO uploadFileVO = fileUpload.uploadString("a/b/1.txt", "123", true);
		Log.info(uploadFileVO.toString());
	}
	
}
