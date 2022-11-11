package cn.zvo.fileupload.framework.springboot;

import com.xnx3.net.HttpUtil;
import cn.zvo.fileupload.vo.UploadFileVO;

public class Demo {
	public static void main(String[] args) {
		
		cn.zvo.fileupload.FileUpload file = new cn.zvo.fileupload.FileUpload();
		
		//StorageModeInterface storage = new HuaweiyunOBSMode(null, null, null, null, null);
		
		//设置允许上传的后缀名
		file.setAllowUploadSuffix("jpg|png|txt1|zip");
		//设置允许上传的文件大小
		file.setMaxFileSize("10MB");
		//设置文件上传后，返回的 
//		file.setNetUrl("http://11.11.11.11/");
		
		HttpUtil http = new HttpUtil();
		String text = http.get("http://www.wang.market/").getContent();
		
		UploadFileVO vo = file.uploadStringFile("1.jsp", text);
		System.out.println(vo);
	}
}
