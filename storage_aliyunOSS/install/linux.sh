# linux系统中，加入 aliyunOSS服务的jar

# fileupload-storage-aliyunOSS.jar
cd /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/fileupload-storage-aliyunOSS-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/fileupload-storage-aliyunOSS-1.0.1.jar

# aliyun-sdk-oss-2.8.2.jar
cd /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/aliyun-sdk-oss-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/aliyun-sdk-oss-2.8.2.jar

# aliyun-java-sdk-core-3.0.7.jar
cd /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/aliyun-java-sdk-core-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/aliyun-java-sdk-core-3.0.7.jar

# aliyun-java-sdk-sts-2.1.6.jar
cd /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/aliyun-java-sdk-sts-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/aliyun-java-sdk-sts-2.1.6.jar

# httpclient-4.4.1.jar
cd /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/httpclient-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/httpclient-4.4.1.jar

# httpcore-4.4.1.jar
cd /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/httpcore-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/httpcore-4.4.1.jar

# jdom-1.1.jar
cd /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/
rm -rf /mnt/tomcat8/webapps/ROOT/WEB-INF/lib/jdom-*.jar
wget https://gitee.com/mail_osc/FileUpload/raw/main/storage_aliyunOSS/install/lib/jdom-1.1.jar
