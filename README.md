# 文件上传

![](http://res.zvo.cn/fileupload/framework.png?t=20221112)


## pom.xml 中加入：

````
<!-- 文件上传相关的核心支持，是必须有的 -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>core</artifactId>
	<version>1.0</version>
</dependency>
<!-- 加入华为云OBS存储的实现。 （存储到哪，这里artifactId就引入的哪里的 storage.xxx ） -->
<dependency> 
    <groupId>cn.zvo.fileupload</groupId>
    <artifactId>storage.huaweicloudOBS</artifactId>
    <version>1.0</version>
</dependency>
<!-- 在 SpringBoot 框架中的快速使用。 （在不同的框架中使用，这里artifactId引入的framework.xxx也不同） -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>framework.springboot</artifactId>
	<version>1.0</version>
</dependency> 
````

如果你只是单纯本地用，用不到像是华为云OBS存储了、Springboot框架的，那你可以只使用 <artifactId>core</artifactId> 这一个核心实现即可。其他两个都无需引入。