## 简介说明
通过json方式来定义使用哪种存储、存储的参数，进而实例化存储对象，进行存储操作。  
这里是单纯前端的DEMO，将常规的请求、选择存储方式、填写、提交等全部进行了封装整理为 file-config.js ，以便快速进行使用。    
 ( 详细说明参考：  https://gitee.com/mail_osc/FileUpload/tree/main/config )

## 效果图
![](//cdn.weiunity.com/site/7464/news/42f4525c306a41cc8be73c89baceed8b.png)

## 代码使用

````
<script src="https://res.zvo.cn/request/request.js"></script> <!-- 原生js实现网络接口请求，不依赖任何三方框架 https://gitee.com/mail_osc/request -->
<script src="https://res.zvo.cn/msg/msg.js"></script> <!-- 原生的消息提醒 https://gitee.com/mail_osc/msg -->
<script src="https://res.zvo.cn/from.js/from.js"></script> <!--  -->
<script src="./fileupload-config.js"></script>
<script>
//采用快速使用的方式，进行简单的接口定义
fileupload.config.quick.use({
	key:"123",	//key便是要获取的哪个唯一标识的存储方式。每个唯一标识都有自己的一套自定义存储方式
	configUrl:"http://res.zvo.cn/fileupload/config/config.json", //get方式获取fileupload config 的 json配置。这里只是演示，所以这个接口无论传什么key都是返回固定的内容，正常使用时这里配置的是接口的url，如 ：http://xxxxx.com/getConfig.json
	submitUrl:"http://xxxxx.com/save.json",	//提交保存的url，会进行post提交
});
</script>
````