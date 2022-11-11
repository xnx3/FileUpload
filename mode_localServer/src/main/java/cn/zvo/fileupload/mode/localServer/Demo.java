package cn.zvo.fileupload.mode.localServer;

import com.xnx3.FileUtil;
import cn.zvo.fileupload.vo.UploadFileVO;

public class Demo {
	public static void main(String[] args) {
		
		cn.zvo.fileupload.FileUtil file = new cn.zvo.fileupload.FileUtil();
		//StorageModeInterface storage = new HuaweiyunOBSMode(null, null, null, null, null);
		
		//设置允许上传的后缀名
		file.setAllowUploadSuffix("jpg|png|txt1|zip");
		//设置允许上传的文件大小
		file.setMaxFileSize("10MB");
		
		UploadFileVO vo = file.uploadStringFile("1.jsp", "123");
		System.out.println(vo);
	}
}
