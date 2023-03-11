# linux系统中，加入 aliyunOSS服务的jar

# fileupload-storage-aliyunOSS.jar
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/fileupload-storage-aliyunOSS-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/fileupload-storage-aliyunOSS-1.0.jar

# aliyun-sdk-oss-2.8.2.jar
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/aliyun-sdk-oss-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/aliyun-sdk-oss-2.8.2.jar
