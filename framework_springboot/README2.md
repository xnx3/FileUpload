springboot 项目中进行文件上传  

## 使用方式

#### 1. pom.xml 加入

````
<!-- 文件上传相关的核心支持，是必须有的 -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>core</artifactId>
	<version>1.0</version>
</dependency>
<!-- 在 SpringBoot 框架中的快速使用。 （在不同的框架中使用，这里引入的framework.xxx也不同） -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>framework.springboot</artifactId>
	<version>1.0</version>
</dependency> 
````

#### 2. Controller中使用

````
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
	//将图片上传到 upload/images/ 文件夹中
	UploadFileVO vo = fileUpload.uploadImage("upload/images/", multipartFile);
	return vo;
}
````


## 使用示例
[参见 demo_springboot/README.md](../demo_springboot/)