Java两行代码实现文件上传。2分钟对接使用，从此无论本地存储、分布式存储、对象存储……都是完全一样的代码调用！  它赋予你各种存储随便切换随便用的能力，而无需动项目代码。  

![](http://res.zvo.cn/fileupload/framework.png?t=20230223)

## 快速使用
#### 1. pom.xml 中加入：

如果你只是单纯本地用，用不到像是华为云OBS存储了、Springboot框架的，那你可以只使用 ```` <artifactId>fileupload-core</artifactId> ```` 这一个核心实现即可

````
<!-- 文件上传相关的核心支持 https://github.com/xnx3/FileUpload -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>fileupload-core</artifactId>
	<version>1.2</version>
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
    <artifactId>fileupload-storage-huaweicloudOBS</artifactId>
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
<!-- 在 SpringBoot 框架中的快速使用。 （在不同的框架中使用，这里artifactId引入的fileupload-framework-xxx也不同） -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>fileupload-framework-springboot</artifactId>
	<version>1.3.1</version>
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


## 开源项目

致力于开源基础化信息建设，如有需要，可直接拿去使用。这里列出了我部分开源项目：

| 项目| star数量 | 简介 |  
| --- | --- | --- |
|[wangmarket CMS](https://gitee.com/mail_osc/wangmarket) | ![](https://gitee.com/mail_osc/wangmarket/badge/star.svg?theme=white) | [私有部署自己的SAAS建站系统](https://gitee.com/mail_osc/wangmarket)  |
|[obs-datax-plugins](https://gitee.com/HuaweiCloudDeveloper/obs-datax-plugins) | ![](https://gitee.com/HuaweiCloudDeveloper/obs-datax-plugins/badge/star.svg?theme=white) | [Datax 的 华为云OBS 插件](https://gitee.com/HuaweiCloudDeveloper/obs-datax-plugins) |
| [templatespider](https://gitee.com/mail_osc/templatespider) | ![](https://gitee.com/mail_osc/templatespider/badge/star.svg?theme=white) | [扒网站工具，所见网站皆可为我所用](https://gitee.com/mail_osc/templatespider) |
|[FileUpload](https://gitee.com/mail_osc/FileUpload)| ![](https://gitee.com/mail_osc/FileUpload/badge/star.svg?theme=white ) | [文件上传，各种存储任意切换](https://gitee.com/mail_osc/FileUpload) |
| [cms client](https://gitee.com/HuaweiCloudDeveloper/huaweicloud-obs-website-wangmarket-cms) | ![](https://gitee.com/HuaweiCloudDeveloper/huaweicloud-obs-website-wangmarket-cms/badge/star.svg?theme=white) | [云服务深度结合无服务器建站](https://gitee.com/HuaweiCloudDeveloper/huaweicloud-obs-website-wangmarket-cms)  |
| [kefu.js](https://gitee.com/mail_osc/kefu.js) | ![](https://gitee.com/mail_osc/kefu.js/badge/star.svg?theme=white ) | https://gitee.com/mail_osc/kefu.js | [在线聊天的前端框架](https://gitee.com/mail_osc/kefu.js)  | 
| [msg.js](https://gitee.com/mail_osc) | ![](https://gitee.com/mail_osc/msg/badge/star.svg?theme=white ) | [轻量级js消息提醒组件](https://gitee.com/mail_osc)  | 
| [translate.js](https://gitee.com/mail_osc/translate) | ![](https://gitee.com/mail_osc/translate/badge/star.svg?theme=white )  | [三行js实现 html 全自动翻译](https://gitee.com/mail_osc/translate)  | 
| [WriteCode](https://gitee.com/mail_osc/writecode) | ![](https://gitee.com/mail_osc/writecode/badge/star.svg?theme=white ) | [代码生成器，自动写代码](https://gitee.com/mail_osc/writecode)  | 
| [log](https://gitee.com/mail_osc/log) | ![](https://gitee.com/mail_osc/log/badge/star.svg?theme=white ) | [Java日志存储及读取](https://gitee.com/mail_osc/log) | 
| [layui translate](https://gitee.com/mail_osc/translate_layui) |  ![](https://gitee.com/mail_osc/translate_layui/badge/star.svg?theme=white ) | [Layui的国际化支持组件](https://gitee.com/mail_osc/translate_layui) |
| [http.java](https://gitee.com/mail_osc/http.java) |  ![](https://gitee.com/mail_osc/http.java/badge/star.svg?theme=white ) | [Java8轻量级http请求类](https://gitee.com/mail_osc/http.java) |
| [xnx3](https://gitee.com/mail_osc/xnx3) |  ![](https://gitee.com/mail_osc/xnx3/badge/star.svg?theme=white ) | [Java版按键精灵，游戏辅助开发](https://gitee.com/mail_osc/xnx3) |  
| [websocket.js](https://gitee.com/mail_osc/websocket.js)  | ![](https://gitee.com/mail_osc/websocket.js/badge/star.svg?theme=white ) | [js的WebSocket框架封装](https://gitee.com/mail_osc/websocket.js) |
| [email.java](https://gitee.com/mail_osc/email.java) | ![](https://gitee.com/mail_osc/email.java/badge/star.svg?theme=white ) | [邮件发送](https://gitee.com/mail_osc/email.java) | 
| [notification.js](https://gitee.com/mail_osc/notification.js) | ![](https://gitee.com/mail_osc/notification.js/badge/star.svg?theme=white ) | [浏览器通知提醒工具类](https://gitee.com/mail_osc/notification.js) | 
| [pinyin.js](https://gitee.com/mail_osc/pinyin.js) | ![](https://gitee.com/mail_osc/pinyin.js/badge/star.svg?theme=white ) | [JS中文转拼音工具类](https://gitee.com/mail_osc/pinyin.js) |
| [xnx3_weixin](https://gitee.com/mail_osc/xnx3_weixin) | ![](https://gitee.com/mail_osc/xnx3_weixin/badge/star.svg?theme=white ) | [Java 微信常用工具类](https://gitee.com/mail_osc/xnx3_weixin) |
| [xunxian](https://gitee.com/mail_osc/xunxian) | ![](https://gitee.com/mail_osc/xunxian/badge/star.svg?theme=white ) | [QQ寻仙的游戏辅助软件](https://gitee.com/mail_osc/xunxian) | 
| [wangmarket_shop](https://gitee.com/leimingyun/wangmarket_shop) | ![](https://gitee.com/leimingyun/wangmarket_shop/badge/star.svg?theme=white ) | [私有化部署自己的 SAAS 商城](https://gitee.com/leimingyun/wangmarket_shop) |
| [wm](https://gitee.com/leimingyun/wm) | ![](https://gitee.com/leimingyun/wm/badge/star.svg?theme=white ) | [Java开发框架及规章约束](https://gitee.com/leimingyun/wm) |
| [yunkefu](https://gitee.com/leimingyun/yunkefu) | ![](https://gitee.com/leimingyun/yunkefu/badge/star.svg?theme=white ) | [私有化部署自己的SAAS客服系统](https://gitee.com/leimingyun/yunkefu) |
| [javadoc](https://gitee.com/leimingyun/javadoc) | ![](https://gitee.com/leimingyun/javadoc/badge/star.svg?theme=white) | [根据标准的 JavaDoc 生成接口文档 ](https://gitee.com/leimingyun/javadoc) |
| [elasticsearch util](https://gitee.com/leimingyun/elasticsearch) | ![](https://gitee.com/leimingyun/elasticsearch/badge/star.svg?theme=white ) | [用sql方式使用Elasticsearch](https://gitee.com/leimingyun/elasticsearch) |
| [AutoPublish](https://gitee.com/leimingyun/sftp-ssh-autopublish) | ![](https://gitee.com/leimingyun/sftp-ssh-autopublish/badge/star.svg?theme=white ) | [Java应用全自动部署及更新](https://gitee.com/leimingyun/sftp-ssh-autopublish) |
| [aichat](https://gitee.com/leimingyun/aichat) | ![](https://gitee.com/leimingyun/aichat/badge/star.svg?theme=white ) | [智能聊天机器人](https://gitee.com/leimingyun/aichat) | 
| [yunbackups](https://gitee.com/leimingyun/yunbackups) | ![](https://gitee.com/leimingyun/yunbackups/badge/star.svg?theme=white ) | [自动备份文件到云存储及FTP等](https://gitee.com/leimingyun/yunbackups) |
| [chatbot](https://gitee.com/leimingyun/chatbot) | ![](https://gitee.com/leimingyun/chatbot/badge/star.svg?theme=white) | [智能客服机器人](https://gitee.com/leimingyun/chatbot) |
| [java print](https://gitee.com/leimingyun/printJframe) | ![](https://gitee.com/leimingyun/printJframe/badge/star.svg?theme=white ) | [Java打印及预览的工具类](https://gitee.com/leimingyun/printJframe) |
…………