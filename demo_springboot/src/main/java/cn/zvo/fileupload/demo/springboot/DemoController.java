package cn.zvo.fileupload.demo.springboot;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.xnx3.UrlUtil;
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
	
	/**
	 * 直接访问这个，进行随便上传一个文本文件进行测试
	 */
	@RequestMapping(value="upload.do")
	@ResponseBody
	public UploadFileVO upload(){
		return FileUploadUtil.uploadString("a/b.txt", "abcde");
	}
	
	/**
	 * 文件上传
	 */
	@RequestMapping(value="upload.json", method= {RequestMethod.POST})
	@ResponseBody
	public UploadFileVO uploadImage(@RequestParam("file") MultipartFile multipartFile){
		//将文件上传到 upload/file/ 文件夹中
		return FileUploadUtil.uploadImage("upload/file/", multipartFile);
	}
	
	/**
	 * 文件下载
	 * @param path 要下载的文件，传入如 upload/file/123.zip
	 */
	@RequestMapping(value="download")
	public void download(String path, HttpServletResponse response){
		FileUploadUtil.download(path, response);
	}
	
	
}
