package cn.zvo.fileupload.demo.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import cn.zvo.fileupload.framework.springboot.ApplicationConfig;
import cn.zvo.fileupload.framework.springboot.FileUpload;
import cn.zvo.fileupload.framework.springboot.FileUploadUtil;
import cn.zvo.fileupload.storage.local.LocalStorage;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * 文件上传的demo演示
 * @author 管雷鸣
 */
@Controller
@RequestMapping("/")
public class DemoController{
	
	
	@RequestMapping(value="test")
	@ResponseBody
	public UploadFileVO test(){
		return FileUploadUtil.uploadString("a/b.txt", "abcde");
	}
	
	/**
	 * 图片上传
	 * @author 管雷鸣
	 */
	@RequestMapping(value="upload.json", method= {RequestMethod.POST})
	@ResponseBody
	public UploadFileVO uploadImage(@RequestParam("file") MultipartFile multipartFile){
		//注意，springboot框架中用的是 cn.zvo.fileupload.framework.springboot.FileUpload
		FileUpload fileUpload = new FileUpload();
		//设置允许上传的后缀名
		fileUpload.setAllowUploadSuffix("jpg|png|gif");
		//设置允许上传的文件大小
		fileUpload.setMaxFileSize("2MB");
		
		/*
		 * 设置使用本地存储的方式。并将上传的文件存储到 static 目录下
		 * 如果不使用 fileUpload.setStorage(...) 设置存储方式，那默认使用的便是本地存储，文件存储到当前项目的根路径下
		 */
		LocalStorage localStorage = new LocalStorage();
		localStorage.setLocalFilePath(localStorage.getLocalFilePath()+"static/");
		fileUpload.setStorage(localStorage);
		
		//将图片上传到 upload/images/ 文件夹中
		UploadFileVO vo = fileUpload.uploadImage("upload/images/", multipartFile);
		return vo;
	}
	
}
