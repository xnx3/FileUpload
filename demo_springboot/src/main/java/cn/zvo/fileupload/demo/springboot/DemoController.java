package cn.zvo.fileupload.demo.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.xnx3.FileUtil;
import com.xnx3.StringUtil;

import cn.zvo.fileupload.framework.springboot.FileUpload;
import cn.zvo.fileupload.vo.UploadFileVO;

@Controller
@RequestMapping("/")
public class DemoController{
	
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
		
		//将图片上传到 file/images/ 路径下
		UploadFileVO vo = fileUpload.uploadImage("file/images/", multipartFile);
		return vo;
	}
	
}
