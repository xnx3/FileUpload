# 文件上传

![](http://res.zvo.cn/fileupload/framework.png?t=20221112)

## 快速使用
#### 1. pom.xml 中加入：

如果你只是单纯本地用，用不到像是华为云OBS存储了、Springboot框架的，那你可以只使用 ```` <artifactId>core</artifactId> ```` 这一个核心实现即可

````
<!-- 文件上传相关的核心支持 https://github.com/xnx3/FileUpload -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>core</artifactId>
	<version>1.0</version>
</dependency>
````

#### 2. 代码中调用

````
FileUpload fileUpload = new FileUpload();	//创建
UploadFileVO vo = fileUpload.uploadString("abc/1.txt", "123456"); //这里上传一个文本文件，文本文件的内容是 123456 ，将他保存到 abc 目录下的 1.txt 文件
System.out.println(vo); //输出到控制台，看看结果
````

#### 3. 更多设置方式

````
FileUpload fileUpload = new FileUpload();
//设置只允许上传jpg、png、gif、txt后缀的文件
fileUpload.setAllowUploadSuffix("jpg|png|gif|txt");
//设置允许上传的文件大小,最大不能超过10MB
fileUpload.setMaxFileSize("10MB");
//设置存储到哪。不设置默认使用LocalStorage本地存储。比如这里可以设置使用华为云OBS存储 
fileUpload.setStorage(...);
//设置上传后文件所访问URL的域名，当文件上传完成后，会通过 UploadFileVO.url 返回文件访问的URL
fileUpload.setDomain("http://res.zvo.cn/");
//这里上传一个文本文件，文本文件的内容是 123456 ，将他保存到 abc 目录下的 1.txt 文件
UploadFileVO vo = fileUpload.uploadString("abc/1.txt", "123456");
//输出到控制台，看看结果
System.out.println(vo);
````

#### 4. Demo示例
[demo_javase/README.md](demo_javase/)

## 存储方式
默认使用的是本地存储。可以切换成采用华为云OBS对象存储、私有化部署FastDFS等来进行存储。  
比如使用华为云OBS对象存储，则pom.xml 中额外加入：

````
<!-- 加入华为云OBS存储的实现。 （存储到哪，这里artifactId就引入的哪里的 storage.xxx 如果单纯存储到本地，这里直接就不用在引入这个 storage.xxx 了，core中默认带了本地文件存储的实现） -->
<dependency> 
    <groupId>cn.zvo.fileupload</groupId>
    <artifactId>storage.huaweicloudOBS</artifactId>
    <version>1.0</version>
</dependency>
````

代码中的变动,仅仅只是针对 FileUpload ，增加一行 setStorage 设置： 

````
fileUpload.setStorage(new HuaweicloudOBSStorage(key, secret, endpoint, obsname));	//设置使用obs存储
````

如此，便将本地存储切换为了使用华为云OBS存储。  
[点此查看使用华为云OBS对象存储的详细说明 storage_huaweicloudOBS/README.md](storage_huaweicloudOBS/)

## SpringBoot框架中使用
在springboot项目中使用时，pom.xml 中再加入以下：

````
<!-- 在 SpringBoot 框架中的快速使用。 （在不同的框架中使用，这里artifactId引入的framework.xxx也不同） -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>framework.springboot</artifactId>
	<version>1.0</version>
</dependency> 
````

##### 代码中的变动：

正常是用 ```` cn.zvo.fileupload.FileUpload ````  
而SpringBoot框架中，则使用 ```` cn.zvo.fileupload.framework.springboot.FileUpload ```` 
仅此区别。  
具体SpringBoot的使用，可参考： [framework_springboot/README.md](framework_springboot/)

## 交流及参与贡献
作者：管雷鸣
微信：xnx3com
QQ交流群：:579544729