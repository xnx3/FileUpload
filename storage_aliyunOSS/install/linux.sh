# linux系统中，加入 aliyunOSS服务的jar

# fileupload-storage-aliyunOSS.jar
cd /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/fileupload-storage-aliyunOSS-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/fileupload-storage-aliyunOSS-1.0.jar

# aliyun-sdk-oss-2.8.2.jar
cd /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/aliyun-sdk-oss-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/aliyun-sdk-oss-2.8.2.jar

# aliyun-java-sdk-core-3.0.7.jar
cd /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/aliyun-java-sdk-core-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/aliyun-java-sdk-core-3.0.7.jar
