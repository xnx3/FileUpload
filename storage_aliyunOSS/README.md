将文件上传存储到阿里云OSS中


# 1. 快速体验
### 1.1 git拉下本项目

### 1.2 修改Demo中相关参数
打开 ```` cn.zvo.fileupload.storage.aliyunOSS.Demo.java ````  

````
/**** 定义存储位置，存储到阿里云OSS中 ****/
String key = "LTAIzIuZhxxxxxx";	//阿里云的 Access Key Id
String secret = "cbtB8llV24aScFBoQWXBt4xxxxxx4Z";	//阿里云的 Access Key Secret
String endpoint = "oss-cn-beijing.aliyuncs.com";	//OSS服务的Endpoint。如：oss-cn-hongkong.aliyuncs.com
String bucketname = "httpsshiyong";	//oss桶的名称
AliyunOSSStorage storage = new AliyunOSSStorage(key, secret, endpoint, bucketname);
````

### 1.3 运行 
设置好参数后运行，即可看到控制台输出结果：

````
UploadFileVO [fileName=1.txt, path=abc/1.txt, url=https://cha-template.obs.cn-north-4.myhuaweicloud.com/abc/1.txt, getResult()=1, getInfo()=success]
````

# 2. 在普通Java项目中加入
### 2.1 pom.xml 中加入：

````
<!-- 文件上传相关的核心支持，是必须有的  -->
<dependency> 
    <groupId>cn.zvo.fileupload</groupId>
    <artifactId>fileupload-core</artifactId>
    <version>1.1</version>
</dependency>
<!-- 加入阿里云OSS存储相关实现 -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>fileupload-storage-aliyunOSS</artifactId>
	<version>1.0</version>
</dependency>
<!-- 如果你在springboot使用，可引入 framework.springboot 模块，快速再springboot中使用 -->
````

### 2.2 代码中使用

````
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
//fileUpload.setDomain("http://cdn.yourdomain.com/");  //还可设置OSS绑定的域名或CDN加速域名

/**** 上传，这里上传一个文本文件，内容是 123456 ，将他保存到 桶 abc 目录下的 1.txt 文件 ****/
UploadFileVO vo = fileUpload.uploadString("abc/1.txt", "123456");
System.out.println(vo);	//打印结果
````

[点此查看 cn.zvo.fileupload.storage.aliyunOSS.Demo.java 文件](src/main/java/cn/zvo/fileupload/storage/aliyunOSS/Demo.java)

# 3. 在SpringBoot项目中使用

### 3.1 pom.xml 中加入：

````
<!-- 文件上传相关的核心支持，是必须有的  -->
<dependency> 
    <groupId>cn.zvo.fileupload</groupId>
    <<artifactId>fileupload-core</artifactId>
    <version>1.1</version>
</dependency>
<!-- 加入阿里云OSS存储相关实现 -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>fileupload-storage-aliyunOSS</artifactId>
	<version>1.0</version>
</dependency>
<!-- 在 SpringBoot 框架中的快速使用。 （在不同的框架中使用，这里引入的framework.xxx也不同） -->
<dependency> 
    <groupId>cn.zvo.fileupload</groupId>
    <artifactId>fileupload-framework-springboot</artifactId>
    <version>1.1</version>
</dependency> 
````

### 3.2 参数配置

配置 application.properties (或yml)，加入：  

````
# 文件上传 https://github.com/xnx3/FileUpload
#
# 设置允许上传的文件最大是多大，比如10MB 单位为 KB、MB ， 如果此项不设置，这里默认是3MB
fileupload.maxSize=10MB
# 设置允许上传的后缀名,传入格式如 png|jpg|gif|zip 多个用英文|分割。如果不设置，默认允许像是pdf、word、图片、音频、视频、zip等常用的且安全的文件后缀都可上传
fileupload.allowUploadSuffix=jpg|png|txt|zip
# 设置上传后文件所访问URL的域名，传入如： http://xxxx.com/  注意格式，后面以 / 结尾。这里结合CDN加速一起使用效果更佳
fileupload.domain=http://res.zvo.cn/
#
# 设置当前使用的是哪种存储方式
# 如果此不设置，默认使用的是本地存储的方式。如果设置了，pom.xml 文件中，记得将此存储方式引入进来，不然会报错找不到这个class文件
# 下面便是具体针对阿里云oss这种存储方式的配置了
# 阿里云的 Access Key Id
fileupload.storage.aliyunOSS.accessKeyId=H0TPUBC6YDZxxxxxxxx
# 阿里云的 Access Key Secret
fileupload.storage.aliyunOSS.secretAccessKey=je56lHuJ62VOhoSXxsfI9InmPAtVY9xxxxxxx
# OSS服务的Endpoint。如：oss-cn-hongkong.aliyuncs.com
fileupload.storage.aliyunOSS.endpoint=oss-cn-hongkong.aliyuncs.com
# oss桶的名称
fileupload.storage.aliyunOSS.bucketname=cha-template
````

### 3.3 Java代码

建立一个Controller，其中加入：

````
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
````

### 3.4 前端html代码
src/main/resources/static/ 下增加一个 upload.html 的文件，目的是能使用 localhost:8080/upload.html 就能访问到。 其内容为：

````
选择要上传的文件（可以传个图片试试）：<br/>
<input type="file" name="file"/><button onclick="upload();">上传</button>

<script src="http://res.zvo.cn/request/request.js"></script><!-- 文件上传，开源地址 https://github.com/xnx3/request -->
<script>
function upload(){
	var file = document.getElementsByName('file')[0].files[0];	 //要上传的文件
	request.upload('/upload.json', {}, file, function(data){  //执行上传操作
		console.log(data);
		if(data.result == '1'){
			// 上传成功
		}else{
			// 上传出错，可弹出失败提示 ： data.info
			alert(data.info);
		}
	});
}
</script>
````

#### 1.4 运行起来，测试一下
访问 [http://localhost:8080/upload.html](http://localhost:8080/upload.html) 即可进行测试体验了。   
[另外也可参见 demo_springboot/README.md](../demo_springboot/) 直接将这个demo_springboot项目拉下来，无需任何修改直接运行即可访问使用
