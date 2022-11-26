package cn.zvo.fileupload.storage.aliyunOSS;

import cn.zvo.fileupload.FileUpload;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * 使用示例
 * @author 管雷鸣
 */
public class Demo {
	
	public static void main(String[] args) {
		
		/**** 定义存储位置，存储到阿里云OSS中 ****/
		String key = "LTAIzIuZhxxxxxx";	//阿里云的 Access Key Id
		String secret = "cbtB8llV24aScFBoQWXBt4xxxxxx4Z";	//阿里云的 Access Key Secret
		String endpoint = "oss-cn-beijing.aliyuncs.com";	//OSS服务的Endpoint。如：oss-cn-hongkong.aliyuncs.com
		String bucketname = "httpsshiyong";	//oss桶的名称
		AliyunOSSStorage storage = new AliyunOSSStorage(key, secret, endpoint, bucketname);
		
		/**** 创建文件上传工具对象 ****/
		FileUpload fileUpload = new FileUpload();
		fileUpload.setStorage(storage);	//设置使用oss存储
		fileUpload.setMaxFileSize("10MB");	//设置最大上传大小为10MB，不设置默认是3MB
		fileUpload.setAllowUploadSuffix("jpg|zip|txt");	//设置允许上传的后缀名，不设置默认是一堆图片、压缩包、文档、音视频等常见后缀
//		fileUpload.setDomain("http://cdn.yourdomain.com/");  //还可设置OSS绑定的域名或CDN加速域名

		/**** 上传，这里上传一个文本文件，内容是 123456 ，将他保存到 桶 abc 目录下的 1.txt 文件 ****/
		UploadFileVO vo = fileUpload.uploadString("abc/1.txt", "123456");
		System.out.println(vo);	//打印结果
	}
	
}
