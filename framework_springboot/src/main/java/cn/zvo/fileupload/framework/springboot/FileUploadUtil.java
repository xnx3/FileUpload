package cn.zvo.fileupload.framework.springboot;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import com.xnx3.Log;
import com.xnx3.ScanClassUtil;
import cn.zvo.fileupload.StorageInterface;
import cn.zvo.fileupload.bean.SubFileBean;
import cn.zvo.fileupload.storage.local.LocalStorage;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * 文件上传
 * @author 管雷鸣
 */
@EnableConfigurationProperties(ApplicationConfig.class)
@Configuration
public class FileUploadUtil implements CommandLineRunner{
	public static FileUpload fileupload;
    @Resource
    private ApplicationConfig config;
	
    /**
     * springboot启动成功后自动执行初始化
     */
//    @PostConstruct
	public void init() {
    	Log.debug("load fileupload config by application.properties/yml : "+this.config);
    	loadConfig(this.config);
	}
	
    /**
     * 加载配置 {@link ApplicationConfig} 文件，通过其属性来决定使用何种配置。
     * <br>这个其实就相当于用java代码来动态决定配置
     * @param config
     */
    public static void loadConfig(ApplicationConfig config) {
    	if(config == null) {
    		return;
    	}
    	if(fileupload == null) {
    		fileupload = new FileUpload();
    	}
    	
		Log.debug(config.toString());
		if(config.getAllowUploadSuffix() != null && config.getAllowUploadSuffix().trim().length() > 0) {
			fileupload.setAllowUploadSuffix(config.getAllowUploadSuffix());
		}
		if(config.getDomain() != null && config.getDomain().trim().length() > 0) {
			fileupload.setDomain(config.getDomain());
		}
		if(config.getMaxSize() != null && config.getMaxSize().trim().length() > 0) {
			fileupload.setMaxFileSize(config.getMaxSize());
		}
		
		if(config.getStorage() != null) {
			for (Map.Entry<String, Map<String, String>> entry : config.getStorage().entrySet()) {
				//拼接，取该插件在哪个包
				String storagePackage = "cn.zvo.fileupload.storage."+entry.getKey();
				
				List<Class<?>> classList = ScanClassUtil.getClasses(storagePackage);
				if(classList.size() == 0) {
					System.err.println("====================");
					System.err.println(" 【【【 ERROR 】】】    ");
					System.err.println(" fileupload 未发现 "+storagePackage +" 这个包存在，请确认pom.xml是否加入了这个 storage 支持模块");
					System.err.println("====================");
					continue;
				}
				
				//搜索继承StorageInterface接口的
				List<Class<?>> storageClassList = ScanClassUtil.searchByInterfaceName(classList, "cn.zvo.fileupload.StorageInterface");
				for (int i = 0; i < storageClassList.size(); i++) {
					Class storageClass = storageClassList.get(i);
					Log.debug("fileupload storage : "+storageClass.getName());
					try {
						Object newInstance = storageClass.getDeclaredConstructor(Map.class).newInstance(entry.getValue());
						StorageInterface storage = (StorageInterface) newInstance;
						fileupload.setStorage(storage);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException| InvocationTargetException  | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}
    }
    
	/**
	 * 判断当前文件附件存储使用的是哪种存储方式
	 * @param storageClassName 存储方式的实现类的名字，如默认带的本地存储为 {@link LocalStorage} ，这里如果要判断是否是使用的本地存储，可传入 "LocalStorage"
	 * @return 是否使用
	 * 			<ul>
	 * 				<li>true ： 是此种模式</li>
	 * 				<li>false ： 不是此种模式</li>
	 * 			</ul>
	 */
	public static boolean isStorage(String storageClassName){
		return fileupload.isStorage(storageClassName);
	}
	
	/**
	 * 判断当前文件附件存储使用的是哪种存储方式
	 * @param storageClass 存储方式的实现类，如默认带的本地存储为 {@link LocalStorage} ，这里如果要判断是否是使用的本地存储，可传入 LocalStorage.class
	 * @return 是否使用
	 * 			<ul>
	 * 				<li>true ： 是此种模式</li>
	 * 				<li>false ： 不是此种模式</li>
	 * 			</ul>
	 */
	public static boolean isStorage(Class storageClass){
		return fileupload.isStorage(storageClass);
	}
    
	/**
	 * 上传本地文件
	 * @param path 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param localPath 本地要上传的文件的绝对路径，如 "/jar_file/iw.jar"
	 * @return {@link PutResult} 若失败，返回null
	 */
	public static UploadFileVO upload(String path, String localPath){
		return fileupload.upload(path, localPath);
	}
	
	/**
	 * 上传本地文件。上传的文件名会被自动重命名为uuid+后缀
	 * @param path 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param localFile 本地要上传的文件
	 * @return {@link PutResult} 若失败，返回null
	 */
	public static UploadFileVO upload(String path, File localFile){
		return fileupload.upload(path, localFile);
	}
	
	/**
	 * 上传文件。上传后的文件名固定
	 * @param path 上传到哪里，包含上传后的文件名，如"image/head/123.jpg" 
	 * 			<p>注意，这里是跟着上传的文件名的，文件名叫什么，就保存成什么</p>
	 * @param inputStream 文件
	 * @return {@link UploadFileVO}
	 */
	public static UploadFileVO upload(String path,InputStream inputStream){
		return fileupload.upload(path, inputStream);
	}
	

	/**
	 * 上传文件
	 * @param path 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param inputStream 要上传的文件的数据流
	 * @param fileSuffix 上传的文件的后缀名
	 * @return {@link UploadFileVO}
	 */
	public static UploadFileVO upload(String path, InputStream inputStream, String fileSuffix) {
		return fileupload.upload(path, inputStream, fileSuffix);
	}
	

	/**
	 * 上传文件
	 * @param path 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param fileName 上传的文件名，如“xnx3.jar”；主要拿里面的后缀名。也可以直接传入文件的后缀名如“.jar。新的文件名会是自动生成的 uuid+后缀”
	 * @param inputStream {@link InputStream}
	 * @return {@link PutResult} 若失败，返回null
	 */
	public static UploadFileVO upload(String path,String fileName,InputStream inputStream){
		return fileupload.upload(path, fileName, inputStream);
	}
	
	/**
	 * SpringMVC 上传文件，配置允许上传的文件后缀再 systemConfig.xml 的AttachmentFile节点
	 * @param path 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param request SpringMVC接收的 {@link MultipartFile},若是有上传文件，会自动转化为{@link MultipartFile}保存
	 * @param formFileName form表单上传的单个文件，表单里上传文件的文件名
	 * @return {@link UploadFileVO} 若成功，则上传了文件并且上传成功
	 */
	public static UploadFileVO upload(String path,HttpServletRequest request,String formFileName) {
		return fileupload.upload(path, request, formFileName);
	}

	/**
	 * 上传文件
	 * @param path 上传后的文件所在目录、路径，如 "jar/file/"
	 * @param multipartFile SpringMVC接收的 {@link MultipartFile},若是有上传文件，会自动转化为{@link MultipartFile}保存
	 * @return {@link UploadFileVO} 若成功，则上传了文件并且上传成功
	 */
	public static UploadFileVO upload(String path, MultipartFile multipartFile) {
		return fileupload.upload(path, multipartFile);
	}

	/**
	 * 给出文本内容，写出文件
	 * @param path 写出的路径,上传后的文件所在的目录＋文件名，如 "jar/file/xnx3.html"
	 * @param text 文本内容
	 * @param encode 编码格式，可传入 {@link FileUpload#GBK}、{@link FileUpload#UTF8}
	 * @return  {@link UploadFileVO}
	 */
	public static UploadFileVO uploadString(String path, String text, String encode){
		return fileupload.uploadString(path, text, encode);
	}
	
	/**
	 * 给出文本内容，写出文件。写出UTF－8编码
	 * @param path 写出的路径,上传后的文件所在的目录＋文件名，如 "jar/file/xnx3.html"
	 * @param text 文本内容
	 */
	public static UploadFileVO uploadString(String path, String text){
		return uploadString(path, text, com.xnx3.FileUtil.UTF8);
	}
	
	
	/**
	 * 上传图片文件
	 * @param path 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param inputStream 图片的数据流
	 * @param fileSuffix 图片的后缀名
	 * @param maxWidth 上传图片的最大宽度，若超过这个宽度，会对图片进行等比缩放为当前宽度
	 * @return {@link UploadFileVO}
	 */
	public static UploadFileVO uploadImage(String path, InputStream inputStream, String fileSuffix, int maxWidth) {
		return fileupload.uploadImage(path, inputStream, fileSuffix, maxWidth);
	}
	
	/**
	 * 上传图片，将网上的图片复制到自己这里。（如果网上图片的URL获取不到后缀，默认用 jpg）
	 * @param path 上传后的文件所在的目录、路径。 传入格式如： file/images/  会自动给上传的图片重命名保存
	 * @param imageUrl 网上图片的地址
	 * @return {@link UploadFileVO}
	 */
	public static UploadFileVO uploadImage(String path, String imageUrl){
		return fileupload.uploadImage(path, imageUrl);
	}
	
	/**
	 * 上传图片文件。
	 * <br/>文件上传上去后，会自动对其进行使用uuid对其命名，将保存的文件信息返回
	 * @param path 上传后的文件所在目录、路径，如 "jar/file/"
	 * 			<br/><b>注意，这里传入的是路径，不带文件名</b>
	 * @param multipartFile SpringMVC接收的 {@link MultipartFile},若是有上传图片文件，会自动转化为{@link MultipartFile}保存
	 * @param maxWidth 上传图片的最大宽度，若超过这个宽度，会对图片进行等比缩放为当前宽度。若传入0.则不启用此功能
	 * @return {@link UploadFileVO} 若成功，则上传了文件并且上传成功
	 */
	public static UploadFileVO uploadImage(String path, MultipartFile multipartFile, int maxWidth) {
		return fileupload.uploadImage(path, multipartFile, maxWidth);
	}
	
	/**
	 * 上传图片文件
	 * <br/>文件上传上去后，会自动对其进行使用uuid对其命名，将保存的文件信息返回
	 * @param path 上传后的文件所在目录、路径，如 "jar/file/"
	 * 			<br/><b>注意，这里传入的是路径，不带文件名</b>
	 * @param multipartFile SpringMVC接收的 {@link MultipartFile},若是有上传图片文件，会自动转化为{@link MultipartFile}保存
	 * @return {@link UploadFileVO} 若成功，则上传了文件并且上传成功
	 */
	public static UploadFileVO uploadImage(String path, MultipartFile multipartFile) {
		return uploadImage(path, multipartFile, 0);
	}
	
	/**
	 * 上传图片文件
	 * <br/>文件上传上去后，会自动对其进行使用uuid对其命名，将保存的文件信息返回
	 * @param path 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param request SpringMVC接收的 {@link MultipartFile},若是有上传图片文件，会自动转化为{@link MultipartFile}保存
	 * @param formFileName form表单上传的单个图片文件，表单里上传文件的文件名
	 * @param maxWidth 上传图片的最大宽度，若超过这个宽度，会对图片进行等比缩放为当前宽度。
	 * @return {@link UploadFileVO} 若成功，则上传了文件并且上传成功
	 */
	public static UploadFileVO uploadImage(String path,HttpServletRequest request,String formFileName, int maxWidth) {
		return fileupload.uploadImage(path, request, formFileName, maxWidth);
	}
	
	/**
	 * 传入一个路径，取得文件数据
	 * @param path 要获取的文件的路径，如  site/123/index.html
	 * @return 返回文件数据。若找不到，或出错，则返回 null
	 */
	public static InputStream getInputStream(String path){
		return fileupload.getInputStream(path);
	}
	
	/**
	 * 传入一个路径，得到其源代码(文本)
	 * @param path 要获取的文本内容的路径，如  site/123/index.html
	 * @return 返回其文本内容。若找不到，或出错，则返回 null
	 */
	public static String getText(String path){
		return fileupload.getText(path);
	}
	
	/**
	 * 删除文件
	 * @param path 文件所在的路径，如 "jar/file/xnx3.jpg"
	 */
	public static void delete(String path){
		fileupload.delete(path);
	}
	
	/**
	 * 文件下载操作，通过浏览器打开某个网址实现文件下载
	 * @param path 要下载的文件，传入如 /site/219/abc.zip 
	 * @param response {@link HttpServletResponse}
	 */
	public static void download(String path, HttpServletResponse response){
		fileupload.download(path, response);
	}
	
	/**
	 * 复制文件
	 * @param originalFilePath 原本文件所在的路径(相对路径，非绝对路径，操作的是当前附件文件目录下)
	 * @param newFilePath 复制的文件所在的路径，所放的路径。(相对路径，非绝对路径，操作的是当前附件文件目录下)
	 */
	public static void copy(String originalFilePath, String newFilePath){
		fileupload.copy(originalFilePath, newFilePath);
	}
	
	
	/**
	 * 获取某个目录（文件夹）占用空间的大小
	 * @param path 要计算的目录(文件夹)，如 jar/file/
	 * @return 计算出来的大小。单位：字节，B。  千分之一KB
	 */
	public static long getDirectorySize(String path){
		return fileupload.getDirectorySize(path);
	}
	
	/**
	 * 获取某个文件的大小，这个是文件，如果传入文件夹，是不起作用的，会返回-1，文件未发现
	 * @param path 要获取的是哪个文件。传入如 site/219/1.html
	 * @return 单位是 B， * 1000 = KB 。 如果返回-1，则是文件未发现，文件不存在
	 */
	public static long getFileSize(String path){
		return fileupload.getFileSize(path);
	}
	
	/**
	 * 获取某个目录下的子文件列表。获取的只是目录下第一级的子文件，并非是在这个目录下无论目录深度是多少都列出来
	 * @param path 要获取的是哪个目录的子文件。传入如 site/219/
	 * @return 该目录下一级子文件（如果有文件夹，也包含文件夹）列表。如果size为0，则是没有子文件或文件夹。无论什么情况不会反null
	 */
	public static List<SubFileBean> getSubFileList(String path){
		return fileupload.getSubFileList(path);
	}

	/**
	 * 创建文件夹
	 * @param path 要创建的文件路径，传入如 site/219/test/ 则是创建 test 文件夹
	 */
	public static void createFolder(String path) {
		fileupload.createFolder(path);
	}

	@Override
	public void run(String... args) throws Exception {
		Log.debug("load fileupload config by application.properties / yml : "+this.config);
    	loadConfig(this.config);
	}

}
