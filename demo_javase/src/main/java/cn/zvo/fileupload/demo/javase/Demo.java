package cn.zvo.fileupload.demo.javase;

import cn.zvo.fileupload.FileUpload;
import cn.zvo.fileupload.storage.LocalStorage;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * 普通的Java项目进行文件上传DEMO示例
 * @author 管雷鸣
 *
 */
public class Demo {
	public static void main(String[] args) {
		
		FileUpload fileUpload = new FileUpload();
		
		/*
		 * 设置使用本地存储的方式。并将上传的文件存储到 D盘的fileupload文件夹下
		 * 如果不使用 fileUpload.setStorage(...) 设置存储方式，那默认使用的便是本地存储，文件存储到当前项目的根路径下
		 */
		LocalStorage localStorage = new LocalStorage();
		localStorage.setLocalFilePath("D:/fileupload/");
		fileUpload.setStorage(localStorage);
		
		//设置允许上传的后缀名
		fileUpload.setAllowUploadSuffix("jpg|png|txt|zip");
		//设置允许上传的文件大小
		fileUpload.setMaxFileSize("10MB");
		
		UploadFileVO vo = fileUpload.uploadStringFile("abc/1.txt", "123456");
		System.out.println(vo);
	}
}