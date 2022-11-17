springboot 项目中进行文件上传  

## 1. 快速使用

#### 1.1 pom.xml 加入

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

#### 1.2 Controller中使用

````
/**
 * 文件上传
 */
@RequestMapping(value="upload.json", method= {RequestMethod.POST})
@ResponseBody
public UploadFileVO uploadImage(@RequestParam("file") MultipartFile multipartFile){
	//将文件上传到 upload/file/ 文件夹中
	return fileUpload.uploadImage("upload/file/", multipartFile);
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

#### 1.3 前端html文件
src/main/resources/static/ 下增加一个 upload.html 的文件，目的是能使用 localhost:8080/upload.html 就能访问到。 其内容为：

````
选择要上传的文件（可以传个图片试试）：<br/>
<input type="file" name="file"/><button onclick="upload();">上传</button>

<script src="http://res.zvo.cn/request/request.js"></script><!-- 文件上传，开源地址 https://github.com/xnx3/request -->
<script>
function upload(){
	var file = document.getElementsByName('file')[0].files[0];	 //要上传的文件
	request.upload('/upload.json', {}, file, function(data){  //执行上传操作
		if(data.result == '1'){
			// 上传成功
		}else{
			// 上传出错，可弹出失败提示 ： data.info
		}
	});
}
</script>
````

#### 1.4 运行起来，测试一下
访问 [http://localhost:8080/upload.html](http://localhost:8080/upload.html) 即可进行测试体验了。   
[另外也可参见 demo_springboot/README.md](../demo_springboot/) 直接将这个demo_springboot项目拉下来，无需任何修改直接运行即可访问使用

## 2. 扩展-配置可上传文件大小及后缀

配置 application.properties (或yml)，加入：  

````
# 文件上传 https://github.com/xnx3/FileUpload
#
# 设置允许上传的文件最大是多大，比如10MB 单位为 KB、MB ， 如果此项不设置，这里默认是3MB
fileupload.maxSize=10MB
# 设置允许上传的后缀名,传入格式如 png|jpg|gif|zip 多个用英文|分割。如果不设置，默认允许像是pdf、word、图片、音频、视频、zip等常用的且安全的文件后缀都可上传
fileupload.allowUploadSuffix=jpg|png|txt|zip
# 设置上传后文件所访问URL的域名，传入如： http://xxxx.com/  注意格式，后面以 / 结尾。非必填，可不设置。这里更多是用于像是CDN加速时，有专门的附件域名的场景使用
fileupload.domain=http://res.zvo.cn/
````

## 3. 扩展-存储方式-使用华为云OBS进行存储

默认是使用的本地存储的方式，但是在一些微服务、以及本身项目需要分布式部署的场景下，需要使用分布式存储支持，服务器本地存储显然是不够的，这里以华为云OBS对象存储的示例来演示  

#### 3.1 pom.xml 额外加入

````
<!-- 加入华为云OBS存储的实现。 （存储到哪，这里artifactId就引入的哪里的 storage.xxx 如果单纯存储到本地，这里直接就不用在引入这个 storage.xxx 了，core中默认带了本地文件存储的实现） -->
<dependency> 
    <groupId>cn.zvo.fileupload</groupId>
    <artifactId>storage.huaweicloudOBS</artifactId>
    <version>1.0</version>
</dependency>
````

#### 3.2 设置配置文件

配置 application.properties (或yml)，加入：

````
#
# 设置当前使用的是哪种存储方式
# 如果此不设置，默认使用的是本地存储的方式。如果设置了，pom.xml 文件中，记得将此存储方式引入进来，不然会报错找不到这个class文件
# 下面便是具体针对华为云obs这种存储方式的配置了
# 华为云的 Access Key Id
fileupload.storage.huaweicloudOBS.accessKeyId=H0TPUBC6YDZxxxxxxxx
# 华为云的 Access Key Secret
fileupload.storage.huaweicloudOBS.accessKeySecret=je56lHuJ62VOhoSXxsfI9InmPAtVY9xxxxxxx
# 区域，传入格式如 "obs.cn-north-4.myhuaweicloud.com" ,详细可参考 https://developer.huaweicloud.com/endpoint?OBS
fileupload.storage.huaweicloudOBS.endpoint=obs.cn-north-4.myhuaweicloud.com
# 桶的名称
fileupload.storage.huaweicloudOBS.obsname=cha-template
````

#### 3.3 启动，测试
如此，就完成了从本地存储转为华为云OBS对象存储的实现。
