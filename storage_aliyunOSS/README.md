将文件上传存储到阿里云OSS中

## 1. 使用方式
#### 1.1 pom.xml 中加入：

````
<!-- 文件上传相关的核心支持，是必须有的  -->
<dependency> 
    <groupId>cn.zvo.fileupload</groupId>
    <artifactId>core</artifactId>
    <version>1.0</version>
</dependency>
<!-- 加入OSS存储相关实现 -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>storage.aliyunOSS</artifactId>
	<version>1.0</version>
</dependency>
<!-- 如果你在springboot使用，可引入 framework.springboot 模块，快速再springboot中使用 -->
````

#### 1.2 代码中使用

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

## 2. 快速体验
#### 2.1 git拉下本项目
#### 2.2 修改阿里云OSS相关参数
打开 ```` cn.zvo.fileupload.storage.aliyunOSS.Demo.java ````  
设置阿里云OSS的相关参数  

````
/**** 定义存储位置，存储到阿里云OSS中 ****/
String key = "LTAIzIuZhxxxxxx";	//阿里云的 Access Key Id
String secret = "cbtB8llV24aScFBoQWXBt4xxxxxx4Z";	//阿里云的 Access Key Secret
String endpoint = "oss-cn-beijing.aliyuncs.com";	//OSS服务的Endpoint。如：oss-cn-hongkong.aliyuncs.com
String bucketname = "httpsshiyong";	//oss桶的名称
AliyunOSSStorage storage = new AliyunOSSStorage(key, secret, endpoint, bucketname);
````

#### 2.3 运行 
设置好参数后运行，即可看到控制台输出结果：

````
UploadFileVO [fileName=1.txt, path=abc/1.txt, url=https://cha-template.obs.cn-north-4.myhuaweicloud.com/abc/1.txt, getResult()=1, getInfo()=success]
````

## 3. 在SpringBoot项目中使用

参见 [demo_springboot/](../demo_springboot/) 