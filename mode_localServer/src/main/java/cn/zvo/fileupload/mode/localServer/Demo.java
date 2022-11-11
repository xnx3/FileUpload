package cn.zvo.fileupload.mode.localServer;

import cn.zvo.fileupload.FileUpload;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * Demo示例
 * @author 管雷鸣
 *
 */
public class Demo {
	public static void main(String[] args) {
		
		FileUpload file = new FileUpload();
		//设置允许上传的后缀名
		file.setAllowUploadSuffix("jpg|png|txt|zip");
		//设置允许上传的文件大小
		file.setMaxFileSize("10MB");
		//进行上传
		UploadFileVO vo = file.uploadStringFile("1.txt", "123");
		System.out.println(vo);
	}
}
