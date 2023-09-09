通过json方式来定义使用哪种存储、存储的参数，进而实例化存储对象，进行存储操作。  
例如：  

````
{
    "storage":"cn.zvo.fileupload.storage.local.LocalStorage",
    "config":{
        "path":"/mnt/tomcat8/logs/"
    }
}
````
其中：
* **storage** 是当前使用的是那种存储方式，存的是包名
* **config** 是这种存储方式要设置哪些参数。这里是本地存储，所以要设置path本地存储的路径。其他比如ftp存储，还要设置用户名、密码、host、存储路径等。不同的存储方式设置的参数也是不同的