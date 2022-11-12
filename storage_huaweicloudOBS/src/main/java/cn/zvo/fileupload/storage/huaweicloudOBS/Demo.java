package cn.zvo.fileupload.storage.huaweicloudOBS;

import cn.zvo.fileupload.FileUpload;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * 使用示例
 * @author 管雷鸣
 *
 */
public class Demo {
	
	public static void main(String[] args) {
		
		/**** 定义存储位置，存储到华为云OBS中 ****/
		String key = "H0TPUBC6YDZWRxxxxxxx";	//华为云的 Access Key Id
		String secret = "je56lHbJ62VOhoSXcsfI9InmPAtVY9xxxxxxxxxx";	//华为云的 Access Key Secret
		String endpoint = "obs.cn-north-4.myhuaweicloud.com";	//华为云连接的地址节点,传入格式如 "obs.cn-north-4.myhuaweicloud.com" ,详细可参考 https://developer.huaweicloud.com/endpoint?OBS
		String obsname = "cha-template";	//obs桶的名称
		HuaweicloudOBSStorage obsStorage = new HuaweicloudOBSStorage(key, secret, endpoint, obsname);

		/**** 创建文件上传工具对象 ****/
		FileUpload fileUpload = new FileUpload();
		fileUpload.setStorage(obsStorage);	//设置使用obs存储
		fileUpload.setMaxFileSize("10MB");	//设置最大上传大小为10MB，不设置默认是3MB
		fileUpload.setAllowUploadSuffix("jpg|zip|txt");	//设置允许上传的后缀名，不设置默认是一堆图片、压缩包、文档、音视频等常见后缀
		//fileUpload.setDomain("http://cdn.yourdomain.com/");  //还可设置OBS绑定的域名或CDN加速域名，不设置默认返回的是obs桶自带域名的文件url

		/**** 上传，这里上传一个文本文件，内容是 123456 ，将他保存到 桶 abc 目录下的 1.txt 文件 ****/
		UploadFileVO vo = fileUpload.uploadStringFile("abc/1.txt", "123456");
		System.out.println(vo);	//打印结果
	}
	
}
