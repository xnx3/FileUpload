# 文件操作，如文件上传

## 场景：普通Java项目
也就是普通javase的项目使用

#### 1. pom.xml 中加入：

````
<!-- 文件上传相关的核心支持，是必须有的 -->
<dependency> 
	<groupId>cn.zvo.fileupload</groupId>
	<artifactId>core</artifactId>
	<version>1.0</version>
</dependency>
````

## 场景：SpringBoot 项目中使用

#### 1. pom.xml 中加入：

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

#### 2. 使用
