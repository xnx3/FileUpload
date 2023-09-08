普通的Java项目中进行文件上传

## 1. 使用方式
#### 1.1 pom.xml 中加入：

````
<!-- 文件上传相关的核心支持，是必须有的 -->
<dependency> 
    <groupId>cn.zvo.fileupload</groupId>
    <artifactId>fileupload-core</artifactId>
    <version>1.2</version>
</dependency>
````

#### 1.2 代码中使用

##### 1.2.1 快速使用
````
FileUpload fileUpload = new FileUpload();
//这里上传一个文本文件，文本文件的内容是 123456 ，将他保存到 abc 目录下的 1.txt 文件
UploadFileVO vo = fileUpload.uploadString("abc/1.txt", "123456");
//输出到控制台，看看结果
System.out.println(vo);
````

##### 1.2.2 更多使用方式
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


## 快速体验
#### 1. git拉下本项目
#### 2. 运行
运行 cn.zvo.fileupload.demo.javase  
即可看到效果

## 文件所在：  
* 静态页面 /upload.html 位于 [src/main/resources/static/upload.html](src/main/resources/static/upload.html)  
* 后端controller位于 [src/main/java/cn/zvo/fileupload/demo/springboot/DemoController.java](src/main/java/cn/zvo/fileupload/demo/springboot/DemoController.java)  



