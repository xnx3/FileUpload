package cn.zvo.fileupload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import com.xnx3.BaseVO;
import com.xnx3.Lang;
import com.xnx3.Log;
import com.xnx3.StringUtil;
import com.xnx3.UrlUtil;

import cn.zvo.fileupload.bean.SubFileBean;
import cn.zvo.fileupload.storage.local.LocalStorage;
import cn.zvo.fileupload.vo.*;
import com.xnx3.media.ImageUtil;

/**
 * 文件上传，附件的操作，如云存储、或服务器本地文件
 * 如果是localFile ，则需要设置 AttachmentFile.netUrl
 * @author 管雷鸣
 */
public class FileUpload{
	public final static String UTF8="UTF-8";
	public final static String GBK="GBK";
	/**
	 * 默认的允许上传的文件后缀，如果没有使用 {@link #setAllowUploadSuffix(String)} 设置允许上传的后缀，那默认就是使用这里的。凡是这里的，都允许上传
	 */
	public final static String DEFAULT_ALLOW_UPLOAD_SUFFIXS = "png|jpg|jpeg|gif|bmp|flv|swf|mkv|avi|rm|rmvb|mpeg|mpg|ogg|ogv|mov|wmv|mp4|webm|mp3|wav|mid|rar|zip|tar|gz|7z|bz2|cab|iso|doc|docx|xls|xlsx|ppt|pptx|pdf|txt|md|xml";
	
	//允许上传的文件最大是多大，比如3MB 单位使 KB、MB
	private String maxFileSize;	 
	//最大上传限制，单位：KB，在getMaxFileSizeKB()获取
	private int maxFileSizeKB = -1;							
	
	//允许上传的后缀名数组，存储如 jpg 、 gif、zip
	public static String[] allowUploadSuffixs;
	
	//实际执行的存储动作。不可直接使用，需使用 getStorage() 获取
	private StorageInterface storage;					
	
	//文件URL访问域名，格式如 http://res.zvo.cn/ 注意格式使协议开头，/结尾。 例如上传了一个文件到 image/head.jpg ，那这个文件的URL为 netUrl+"image/head.jpg"
	public String domain = null;
	
	/**
	 * 获取上传后文件所访问URL的域名
	 * @return 返回如 http://res.weiunity.com/   若找不到，则返回null
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * 设置上传后文件所访问URL的域名
	 * @param domain 当前正在使用的附件的域名，传入如： http://xxxx.com/  注意格式，后面以 / 结尾
	 */
	public void setDomain(String domain){
		this.domain = domain;
	}
	
	/**
	 * 获取当前使用的存储模式，进行存储。
	 * @return 如果在数据库表 system 表加载成功之前调用此方法，会返回null，当然，这个空指针几乎可忽略。实际使用中不会有这种情况
	 */
	public StorageInterface getstorage(){
		if(this.storage == null){
			//赋予默认本地存储模式
			LocalStorage localStorage = new LocalStorage();
			this.storage = localStorage;
			Log.info("use default storage mode : local server");
		}
		return storage;
	}
	
	/**
	 * 设置当前使用的存储模式。如果设置了此处，那么数据库中 ATTACHMENT_FILE_MODE 字段设置的存储方式将会失效，不会起任何作用。以此接口的设置为准
	 * @param storage 实现 {@link StorageInterface}接口
	 */
	public void setStorage(StorageInterface storage) {
		this.storage = storage;
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
	public boolean isStorage(String storageClassName){
		//向前兼容，兼容 wm 2.25 (http://wm.zvo.cn)及以前版本的设置
		if(storageClassName.equalsIgnoreCase("localFile")) {
			storageClassName = "LocalServerMode";
		}else if(storageClassName.equalsIgnoreCase("huaWeiYunOBS")) {
			storageClassName = "HuaweiyunOBSMode";
		}else if(storageClassName.equalsIgnoreCase("aliyunOSS")) {
			storageClassName = "AliyunOSSMode";
		}
		
		//取得当前实现的文件的名字，例如本地存储的命名为 LocalServerMode.java ,那这里会取到 LocalServerMode
		String currentModeFileName = this.getstorage().getClass().getSimpleName();
		if(currentModeFileName.equalsIgnoreCase(storageClassName)) {
			return true;
		}
		
		return false;
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
	public boolean isStorage(Class storageClass){
		String name = storageClass.getSimpleName();
		return isStorage(name);
	}
	
	
	/**
	 * 获取当前允许上传的文件的最大大小
	 * @return 如 3MB 、 400KB 等
	 */
	public String getMaxFileSize(){
		if(this.maxFileSize == null) {
			this.maxFileSize = "3MB";
		}
		return maxFileSize;
	}
	
	/**
	 * 获取当前限制的上传文件最大的大小限制。单位是KB
	 * @return 单位KB
	 */
	public long getMaxFileSizeKB(){
		if(maxFileSizeKB == -1){
			//未初始化，那么进行初始化
			maxFileSize = getMaxFileSize();
			
			if(maxFileSize.indexOf("KB") > 0){
				//使用KB单位
				maxFileSizeKB = Lang.stringToInt(maxFileSize.replace("KB", "").trim(), 0);
				if(maxFileSizeKB == 0){
					Log.error("maxFileSize 异常,当前获取到的为0");
				}
			}else if (maxFileSize.indexOf("MB") > 0) {
				//使用MB
				maxFileSizeKB = Lang.stringToInt(maxFileSize.replace("MB", "").trim(), 0) * 1024;
				if(maxFileSizeKB == 0){
					Log.error("maxFileSize 异常,当前获取到的为0");
				}
			}else if (maxFileSize.indexOf("GB") > 0) {
				//使用 GB
				maxFileSizeKB = Lang.stringToInt(maxFileSize.replace("GB", "").trim(), 0) * 1024 * 1024;
				if(maxFileSizeKB == 0){
					Log.error("maxFileSize 异常,当前获取到的为0");
				}
			}else{
				//没有找到合适单位，报错
				Log.error("maxFileSize exception, not find unit，your are KB ? MB ? GB ? Please use one of them");
			}
		}
		return maxFileSizeKB;
	}

	/**
	 * 判断要上传的文件是否超出大小限制，若超出大小限制，返回出错原因
	 * @param file 要上传的文件，判断其大小是否超过系统指定的最大限制
	 * @return 若超出大小，则返回result:Failure ，info为出错原因
	 */
	public BaseVO verifyFileMaxLength(File file){
		BaseVO vo = new BaseVO();
		if(file != null){
			//文件的KB长度
			int lengthKB = (int) Math.ceil(file.length()/1024);
			vo = verifyFileMaxLength(lengthKB);
		}
		return vo;
	}
	
	/**
	 * 判断要上传的文件是否超出大小限制，若超出大小限制，返回出错原因
	 * @param lengthKB 要上传的文件的大小，判断其大小是否超过系统指定的最大限制，单位是KB (1024B=1KB)
	 * @return 若超出大小，则返回result:Failure ，info为出错原因
	 */
	public BaseVO verifyFileMaxLength(long lengthKB){
		BaseVO vo = new BaseVO();
		if(getMaxFileSizeKB() > 0 && lengthKB > getMaxFileSizeKB()){
			vo.setBaseVO(BaseVO.FAILURE, "文件大小超出限制！上传大小在 "+maxFileSize+" 以内");
			return vo;
		}
		return vo;
	}
	
	
	/**
	 * 设置允许上传的文件最大是多大，比如3MB 单位为 KB、MB
	 * @param maxSize 传入入  3MB  单位有 KB、MB
	 */
	public void setMaxFileSize(String maxSize) {
		if(maxSize == null) {
			return;
		}
		this.maxFileSize = maxSize;
	}
	

	/**
	 * 设置允许可上传的后缀名。
	 * @param allowUploadSuffix 传入格式入 png|jpg|gif|zip 多个用英文|分割
	 */
	public void setAllowUploadSuffix(String allowUploadSuffix) {
		if(allowUploadSuffix == null) {
			return;
		}
		
		String ss[] = allowUploadSuffix.split("\\|");
		//过滤一遍，空跟无效的
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < ss.length; i++) {
			String s = ss[i].trim();
			if(s != null && s.length() > 0){
				list.add(s);
			}
		}
		
		//初始化创建数组
		allowUploadSuffixs = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			allowUploadSuffixs[i] = list.get(i);
		}
	}
	
	/**
	 * 判断当前后缀名是否在可允许上传的后缀中
	 * @param path 上传文件要保存到的路径。传入如 site/219/index.html 当然，你也可以直接传入具体后缀，如 html
	 * @return true：可上传，允许上传，后缀在指定的后缀列表中
	 */
	public boolean isAllowUpload(String path) {
		if(path == null || path.trim().length() ==0) {
			return false;
		}
		
		String suffix;
		if(path.indexOf(".") > -1) {
			//发现路径，需要取后缀
			suffix = Lang.findFileSuffix(path);
		}else {
			//未发现路径，传入的就是后缀
			suffix = path;
		}
		
		//判断是否设置允许上传什么后缀
		if(allowUploadSuffixs == null || allowUploadSuffixs.length == 0){
			//还未设置，那默认使用 DEFAULT_ALLOW_UPLOAD_SUFFIXS 
			setAllowUploadSuffix(DEFAULT_ALLOW_UPLOAD_SUFFIXS);
//			Log.error("请先使用 fileUpload.setAllowUploadSuffix(\"jpg|png|txt|zip\"); 方法设置哪些后缀允许上传");
			//return false;
		}
		
		//进行判断，判断传入的suffix是否在允许上传的后缀里面
		for (int j = 0; j < allowUploadSuffixs.length; j++) {
			if(allowUploadSuffixs[j].equalsIgnoreCase(suffix)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 上传本地文件
	 * @param path 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param localPath 本地要上传的文件的绝对路径，如 "/jar_file/iw.jar"
	 * @return {@link PutResult} 若失败，返回null
	 */
	public UploadFileVO upload(String path, String localPath){
		File localFile = new File(localPath);
		return upload(path, localFile);
	}
	
	/**
	 * 上传本地文件。上传的文件名会被自动重命名为uuid+后缀
	 * @param path 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param localFile 本地要上传的文件
	 * @return {@link PutResult} 若失败，返回null
	 */
	public UploadFileVO upload(String path, File localFile){
		UploadFileVO vo = new UploadFileVO();
		
		BaseVO baseVO = verifyFileMaxLength(localFile);
		if(baseVO.getResult() - BaseVO.FAILURE == 0){
			vo.setBaseVO(baseVO);
			return vo;
		}
		
		//将本地文件转化为流
		try {
			InputStream localInput = new FileInputStream(localFile);
			return upload(path, localInput);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			vo.setBaseVO(UploadFileVO.FAILURE, "上传出错，要上传的文件不存在！");
			return vo;
		}
	}
	
	/**
	 * 上传文件。上传后的文件名固定
	 * @param path 上传到哪里，包含上传后的文件名，如"image/head/123.jpg" 
	 * 			<p>注意，这里是跟着上传的文件名的，文件名叫什么，就保存成什么</p>
	 * @param inputStream 文件
	 * @return {@link UploadFileVO}
	 */
	public UploadFileVO upload(String path,InputStream inputStream){
		UploadFileVO vo = new UploadFileVO();
		
		/** 判断存储出去的后缀是否合规 **/
		if(!isAllowUpload(path)){
			vo.setBaseVO(UploadFileVO.FAILURE, "该后缀不允许被上传");
			return vo;
		}
		
		/** 判断文件大小是否超出最大限制的大小 **/
//		int lengthKB = 0;
		long length_B = 0;
		try {
			length_B = inputStream.available();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BaseVO baseVO = verifyFileMaxLength((long) Math.ceil(length_B/1024));
		if(baseVO.getResult() - BaseVO.FAILURE == 0){
			vo.setBaseVO(baseVO);
			return vo;
		}
		vo.setSize(length_B);
		
		//执行上传
		vo = getstorage().upload(path, inputStream);
		if(vo.getSize() < 1) {
			vo.setSize(length_B);
		}
		
		//设置网络下载地址
		String domain = getDomain();
		if(domain != null) {
			vo.setUrl(domain+vo.getPath());
		}
		//提取文件名
		vo.setName(UrlUtil.getFileName("http://zvo.cn/"+path));
		
		return vo;
	}
	

	/**
	 * 上传文件
	 * @param path 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param inputStream 要上传的文件的数据流
	 * @param fileSuffix 上传的文件的后缀名
	 * @return {@link UploadFileVO}
	 */
	public UploadFileVO upload(String path, InputStream inputStream, String fileSuffix) {
		UploadFileVO vo = new UploadFileVO();
		
		if(!isAllowUpload(fileSuffix)){
			vo.setBaseVO(UploadFileVO.FAILURE, "此后缀名不在可上传文件列表中");
			return vo;
		}
		
		if(inputStream == null){
			vo.setBaseVO(UploadFileVO.FAILURE, "上传文件不存在，请选择要上传的文件");
			return vo;
		}
		
		return upload(path, "."+fileSuffix, inputStream);
	}
	

	/**
	 * 上传文件
	 * @param path 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param fileName 上传的文件名，如“xnx3.jar”；主要拿里面的后缀名。也可以直接传入文件的后缀名如“.jar。新的文件名会是自动生成的 uuid+后缀”
	 * @param inputStream {@link InputStream}
	 * @return {@link PutResult} 若失败，返回null
	 */
	public UploadFileVO upload(String path,String fileName,InputStream inputStream){
		UploadFileVO vo = new UploadFileVO();
		
		//进行文件后缀校验
		if(fileName == null || fileName.indexOf(".") == -1){
			vo.setBaseVO(UploadFileVO.FAILURE, "上传的文件名(后缀)校验失败！传入的为："+fileName+"，允许传入的值如：a.jpg或.jpg");
			return vo;
		}
		
		String fileSuffix = StringUtil.subString(fileName, ".", null, 3);	//获得文件后缀，以便重命名
		String name=Lang.uuid()+"."+fileSuffix;
		return upload(path+name, inputStream);
	}
	

	/**
	 * 给出文本内容，写出文件
	 * @param path 写出的路径,上传后的文件所在的目录＋文件名，如 "jar/file/xnx3.html"
	 * @param text 文本内容
	 * @param encode 编码格式，可传入 {@link FileUpload#GBK}、{@link FileUpload#UTF8}
	 * @return  {@link UploadFileVO}
	 */
	public UploadFileVO uploadString(String path, String text, String encode){
		try {
			InputStream inputStream = StringUtil.stringToInputStream(text, encode);
			return upload(path, inputStream);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			UploadFileVO vo = new UploadFileVO();
			vo.setBaseVO(UploadFileVO.FAILURE, e.getMessage());
			return vo;
		}
	}
	
	/**
	 * 给出文本内容，写出文件。写出UTF－8编码
	 * @param path 写出的路径,上传后的文件所在的目录＋文件名，如 "jar/file/xnx3.html"
	 * @param text 文本内容
	 */
	public UploadFileVO uploadString(String path, String text){
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
	public UploadFileVO uploadImage(String path, InputStream inputStream, String fileSuffix, int maxWidth) {
		UploadFileVO vo = new UploadFileVO();

		if(!isAllowUpload(fileSuffix)){
			vo.setBaseVO(UploadFileVO.FAILURE, "此后缀名不在可上传文件列表中");
			return vo;
		}
		
		if(inputStream == null){
			vo.setBaseVO(UploadFileVO.FAILURE, "请选择要上传的文件");
			return vo;
		}
		
		//判断其是否进行图像压缩
		if(maxWidth > 0){
			inputStream = ImageUtil.proportionZoom(inputStream, maxWidth, fileSuffix);
		}
		
		return upload(path, "."+fileSuffix, inputStream);
	}
	
	/**
	 * 上传图片，将网上的图片复制到自己这里。（如果网上图片的URL获取不到后缀，默认用 jpg）
	 * @param path 上传后的文件所在的目录、路径。 传入格式如： file/images/  会自动给上传的图片重命名保存
	 * @param imageUrl 网上图片的地址
	 * @return {@link UploadFileVO}
	 */
	public UploadFileVO uploadImage(String path, String imageUrl){
		if(imageUrl == null){
			return null;
		}
		String suffix = Lang.findFileSuffix(imageUrl);	//取图片后缀名
		BufferedImage bufferedImage = ImageUtil.getBufferedImageByUrl(imageUrl);
		if(suffix == null){
			suffix = "jpg";
		}
		
		return upload(path+Lang.uuid()+"."+suffix, ImageUtil.bufferedImageToInputStream(bufferedImage, suffix));
	}
	
	/**
	 * 传入一个路径，得到其源代码(文本)
	 * @param path 要获取的文本内容的路径，如  site/123/index.html
	 * @return 返回其文本内容。若找不到，或出错，则返回 null
	 */
	public String getText(String path){
		InputStream is = getstorage().get(path);
		if(is == null) {
			return null;
		}
		
		try {
			return StringUtil.inputStreamToString(is, com.xnx3.FileUtil.UTF8);
		} catch (IOException e) {
			e.printStackTrace();
			Log.debug(e.getMessage());
			return null;
		}
	}
	

	/**
	 * 传入一个路径，取得文件数据
	 * @param path 要获取的文件的路径，如  site/123/index.html
	 * @return 返回文件数据。若找不到，或出错，则返回 null
	 */
	public InputStream getInputStream(String path){
		InputStream is = getstorage().get(path);
		return is;
	}
	
	/**
	 * 删除文件
	 * @param path 文件所在的路径，如 "jar/file/xnx3.jpg"
	 */
	public void delete(String path){
		getstorage().delete(path);
	}
	
	/**
	 * 复制文件
	 * @param originalFilePath 原本文件所在的路径(相对路径，非绝对路径，操作的是当前附件文件目录下)
	 * @param newFilePath 复制的文件所在的路径，所放的路径。(相对路径，非绝对路径，操作的是当前附件文件目录下)
	 */
	public void copy(String originalFilePath, String newFilePath){
		getstorage().copyFile(originalFilePath, newFilePath);
	}
	
	
	/**
	 * 获取某个目录（文件夹）占用空间的大小
	 * @param path 要计算的目录(文件夹)，如 jar/file/
	 * @return 计算出来的大小。单位：字节，B。  千分之一KB
	 */
	public long getDirectorySize(String path){
		return getstorage().getSize(path);
	}
	
	/**
	 * 获取某个文件的大小，这个是文件，如果传入文件夹，是不起作用的，会返回-1，文件未发现
	 * @param path 要获取的是哪个文件。传入如 site/219/1.html
	 * @return 单位是 B， * 1000 = KB 。 如果返回-1，则是文件未发现，文件不存在
	 */
	public long getFileSize(String path){
		return getstorage().getSize(path);
	}
	
	/**
	 * 获取某个目录下的子文件列表。获取的只是目录下第一级的子文件，并非是在这个目录下无论目录深度是多少都列出来
	 * @param path 要获取的是哪个目录的子文件。传入如 site/219/
	 * @return 该目录下一级子文件（如果有文件夹，也包含文件夹）列表。如果size为0，则是没有子文件或文件夹。无论什么情况不会反null
	 */
	public List<SubFileBean> getSubFileList(String path){
		return getstorage().getSubFileList(path);
	}
	

	/**
	 * 创建文件夹
	 * @param path 要创建的文件路径，传入如 site/219/test/ 则是创建 test 文件夹
	 */
	public void createFolder(String path) {
		getstorage().createFolder(path);
	}
	
}
