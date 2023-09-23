package cn.zvo.fileupload.storage.ftp;

import java.util.HashMap;
import java.util.Map;

import cn.zvo.fileupload.FileUpload;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * 使用示例
 * @author 管雷鸣
 */
public class Demo {
	
	public static void main(String[] args) {
		
		/**** 定义存储位置，存储到SFTP中 ****/
		Map<String, String> config = new HashMap<String, String>();
		config.put("host", "192.168.31.241");
		config.put("username", "root");
		config.put("password", "leimingyun");
		FtpStorage storage = new FtpStorage(config);
		
		/**** 创建文件上传工具对象 ****/
		FileUpload fileUpload = new FileUpload();
		fileUpload.setStorage(storage);	//设置使用oss存储
		fileUpload.setMaxFileSize("10MB");	//设置最大上传大小为10MB，不设置默认是3MB
		fileUpload.setAllowUploadSuffix("jpg|zip|txt|html");	//设置允许上传的后缀名，不设置默认是一堆图片、压缩包、文档、音视频等常见后缀
//		fileUpload.setDomain("http://cdn.yourdomain.com/");  //还可设置OSS绑定的域名或CDN加速域名

		/**** 上传，这里上传一个文本文件，内容是 123456 ，将他保存到 桶 abc 目录下的 1.txt 文件 ****/
		UploadFileVO vo = fileUpload.uploadString("test.html", "123456");
		System.out.println(vo);	//打印结果
	}
	
}
