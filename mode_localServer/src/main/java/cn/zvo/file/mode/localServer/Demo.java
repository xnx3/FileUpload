package cn.zvo.file.mode.localServer;

import com.xnx3.FileUtil;
import com.xnx3.net.HttpUtil;
import cn.zvo.file.vo.UploadFileVO;

public class Demo {
	public static void main(String[] args) {
		
		cn.zvo.file.FileUtil file = new cn.zvo.file.FileUtil();
		//StorageModeInterface storage = new HuaweiyunOBSMode(null, null, null, null, null);
		
		//设置允许上传的后缀名
		file.setAllowUploadSuffix("jpg|png|txt1|zip");
		//设置允许上传的文件大小
		file.setMaxFileSize("10MB");
		
		String s = FileUtil.read("G:\\git\\FileUtil\\mode_localServer\\src\\main\\java\\cn\\zvo\\file\\mode\\localServer\\LocalServerMode.java");
		
		UploadFileVO vo = file.uploadStringFile("1.jsp", s);
		System.out.println(vo);
	}
}
