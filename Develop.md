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
包名的 ````cn.zvo.fileupload.storage.```` 是固定的，不要进行改动，这个是固定一定要这么写的。  
而包名的 ````huaweicloudOBS```` 也是固定的，跟项目名能对应起来  

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
	 * @param accessKeyId 华为云的 Access Key Id
	 * @param accessKeySecret 华为云的 Access Key Secret
	 * @param endpoint 区域，传入格式如 "obs.cn-north-4.myhuaweicloud.com" ,详细可参考 <a href="https://developer.huaweicloud.com/endpoint?OBS">https://developer.huaweicloud.com/endpoint?OBS</a>
	 * @param obsname 桶的名称
	 */
	public HuaweicloudOBSStorage(Map<String, String> map) {
		String accessKeySecret = map.get("accessKeySecret");
		String accessKeyId = map.get("accessKeyId");
		String endpoint = map.get("endpoint");
		String obsname = map.get("obsname");
		
		init(accessKeyId, accessKeySecret, endpoint, obsname);
	}
public ServiceInterfaceImplement(Map<String, String> config) {
	//可以使用 config.get('username') 获取 application.peroperties 中设置的 translate.service.huawei.username 的值
}
````





### 2. 

扩展时，有以下几点需要注意：
1. 将扩展的翻译服务对接的实现，都要放到 cn.zvo.translate.service 这个包下。比如对接华为云翻译，那就建立一个 cn.zvo.translate.service 包，在这个包下建立一个名为 ServiceInterfaceImplement.java 的类
2. ServiceInterfaceImplement 要实现 cn.zvo.translate.core.service.interfaces.ServiceInterface 接口
3. 在跟翻译服务对接时，网络请求这块使用 cn.zvo.http.Http 这个，其使用说明参见 [https://github.com/xnx3/http.java](https://github.com/xnx3/http.java),  这样不至于引入很多杂七杂八的支持包进去。当然如果单纯就只是你自己用，你可以直接吧对方SDK，通过修改 pom.xml 中加入，来引入一堆的三方jar包。  
4. 要有一个构造方法，构造方法需要传入Map，具体代码如下
````
public ServiceInterfaceImplement(Map<String, String> config) {
	//可以使用 config.get('username') 获取 application.peroperties 中设置的 translate.service.huawei.username 的值
}
````
5. application.peroperties 中的配置项，按照上面所示的 translate.service.huawei.username ，其中:  
	1. translate.service 是固定的，
	1. huawei 是在 cn.zvo.translate.service 包下所建立的针对华为云翻译所建立的包名
	1. username 是自己定义的一个参数名，这里叫username，那么在 ServiceInterfaceImplement 的构造方法中获取时，也要用 config.get("username") 来取
  
这里已内置了两个翻译服务的对接示例，一个是google翻译、一个是华为云翻译，可以参考华为云翻译的实现 cn.zvo.translate.service.huawei.ServiceInterfaceImplement.java