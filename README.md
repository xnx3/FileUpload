# 文件操作，如文件上传

## 快速使用
pom.xml 中加入：

````
<!-- 文件上传相关的核心支持，是必须有的 -->
<dependency> 
	<groupId>cn.zvo.file</groupId>
	<artifactId>core</artifactId>
	<version>1.0</version>
</dependency>
<!-- 本机存储的方式来存放上传的文件（使用不同的存储方式，这里引入的mode.xxx也不同） -->
<dependency> 
	<groupId>cn.zvo.file</groupId>
	<artifactId>mode.localServer</artifactId>
	<version>1.0</version>
</dependency>
<!-- 在 SpringBoot 框架中的快速使用。 （在不同的框架中使用，这里引入的framework.xxx也不同） -->
<dependency> 
	<groupId>cn.zvo.file</groupId>
	<artifactId>framework.springboot</artifactId>
	<version>1.0</version>
</dependency> 
````