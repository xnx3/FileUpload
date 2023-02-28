上传的 storage 存储源扩展，可以参考此对接七牛云Kodo对象存储、FastDFS、腾讯云COS对象存储等。

# 步骤
这里以对接华为云OBS对象存储为例  
### 1. 新建项目
新建maven项目 storage_huaweicloudOBS  
**注意**  
1.1 项目名前缀要以 storage_ 开头  
1.2 pom.xml 中除了引入必须的SDK外，不要引入不必要的依赖，避免臃肿。 比如华为云OBS的： [https://gitee.com/HuaweiCloudDeveloper/file-upload/blob/master/pom.xml](https://gitee.com/HuaweiCloudDeveloper/file-upload/blob/master/pom.xml)

### 2. 新建包
新建包，因为是对接华为云OBS的，项目名为 storage_huaweicloudOBS ，那么包名就以 huaweicloudOBS 来命名，包名为：  

````
cn.zvo.fileupload.storage.huaweicloudOBS
````

**注意**  
1. 包名的 ````cn.zvo.fileupload.storage.```` 是固定的，不要进行改动，这个是固定一定要这么写的。  
1. 而包名的 ````huaweicloudOBS```` 也是固定的，跟项目名能对应起来  

### 3. 新建类
##### 3.1 类的命名
命名为 ````HuaweicloudOBSStorage````  
注意名字也是跟项目名对应的，进行大驼峰命名.  

##### 3.2 类要实现接口
类要实现接口 ````cn.zvo.fileupload.StorageInterface````  对接口中的方法进行实现。 具体的每个方法都是什么作用，可以查看相关说明： [https://github.com/xnx3/FileUpload/blob/main/core/src/main/java/cn/zvo/fileupload/StorageInterface.java](https://github.com/xnx3/FileUpload/blob/main/core/src/main/java/cn/zvo/fileupload/StorageInterface.java)  

##### 3.3 类要有构造方法
要有一个构造方法，构造方法需要传入Map，具体代码如下

````

/**
 * 文件上传-华为云OBS
 * @param map 传入一个 Map<String, String> 其中map要定义这么几个参数：
 * 			<ul>
 * 				<li>map.put("accessKeyId", "华为云的 Access Key Id");</li>
 * 				<li>map.put("accessKeySecret", "华为云的 Access Key Secret");</li>
 * 				<li>map.put("endpoint", "区域，传入格式如 obs.cn-north-4.myhuaweicloud.com");  //详细可参考 <a href="https://developer.huaweicloud.com/endpoint?OBS">https://developer.huaweicloud.com/endpoint?OBS</a></li>
 * 				<li>map.put("obsname", "桶的名称")</li>
 * 			</ul>
 */
public HuaweicloudOBSStorage(Map<String, String> map) {
	String accessKeySecret = map.get("accessKeySecret");
	String accessKeyId = map.get("accessKeyId");
	...
}
````

**注意**  
构造方法必须要传入 ````Map<String, String> map```` 用来接收自定义设置的参数  
具体接收的参数，参数的命名，扩展者自己定义即可。尽量跟对方存储的平台参数名保持一致  
可以在构造方法中，拿到用户自定义的参数后进行相关初始化操作  

##### 3.4 类中的一些三方组件使用

相关实现如果人家SDK都封装好了，那就直接用SDK的。如果SDK还没有，需要掉接口的，网络请求这块可使用 cn.zvo.http.Http 这个，其使用说明参见 [https://github.com/xnx3/http.java](https://github.com/xnx3/http.java)

### 4. application.properties 中配置
在 SpringBoot 框架中使用，可通过设置 application.peroperties 中的配置项，来实现传入上面步骤 3.3 中的初始化参数。按照上面第2步所示， ，在 application.peroperties 中的配置便是如下：  

````
# 华为云的 Access Key Id
fileupload.storage.huaweicloudOBS.accessKeyId=H0TPUBC6YDZxxxxxxxx
# 华为云的 Access Key Secret
fileupload.storage.huaweicloudOBS.accessKeySecret=je56lHuJ62VOhoSXxsfI9InmPAtVY9xxxxxxx
# 区域，传入格式如 "obs.cn-north-4.myhuaweicloud.com" ,详细可参考 https://developer.huaweicloud.com/endpoint?OBS
fileupload.storage.huaweicloudOBS.endpoint=obs.cn-north-4.myhuaweicloud.com
# 桶的名称
fileupload.storage.huaweicloudOBS.obsname=cha-template
````
**注意**
1. ````fileupload.storage.```` 是固定的，
1. ````huaweicloudOBS```` 是项目在包 ````cn.zvo.fileupload.storage.huaweicloudOBS```` 中，去掉前面固定的 ````cn.zvo.fileupload.storage.```` 即可得到
1. ````accessKeyId、accessKeySecret、endpoint、obsname```` 是在上面步骤 3.3 中自定义传入map的参数名

