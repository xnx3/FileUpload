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
import cn.zvo.fileupload.bean.SubFileBean;
import cn.zvo.fileupload.storage.LocalStorage;
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
	
	//允许上传的文件最大是多大，比如3MB 单位使 KB、MB
	private String maxFileSize;	 
	//最大上传限制，单位：KB，在getMaxFileSizeKB()获取
	private int maxFileSizeKB = -1;							
	
	//允许上传的后缀名数组，存储如 jpg 、 gif、zip
	public static String[] allowUploadSuffixs;
	
	//实际执行的存储动作。不可直接使用，需使用 getStorage() 获取
	private StorageInterface storage;					
	
	//文件URL访问域名，格式如 http://res.zvo.cn/ 注意格式使协议开头，/结尾。 例如上传了一个文件到 image/head.jpg ，那这个文件的URL为 netUrl+"image/head.jpg"
	public String netUrl = null;
	
	/**
	 * 获取附件访问的url地址
	 * @return 返回如 http://res.weiunity.com/   若找不到，则返回null
	 */
	public String getNetUrl() {
		return netUrl;
	}

	/**
	 * 设置当前的netUrl
	 * @param url 当前正在使用的附件url前缀，传入如： http://xxxx.com/  注意格式，后面以 / 结尾
	 */
	public void setNetUrl(String url){
		url = netUrl;
	}
	/**
	 * 设置当前使用的存储模式。如果设置了此处，那么数据库中 ATTACHMENT_FILE_MODE 字段设置的存储方式将会失效，不会起任何作用。以此接口的设置为准
	 * @param storage 实现 {@link StorageInterface}接口
	 */
	public void setStorage(StorageInterface storage) {
		this.storage = storage;
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
	 * @param fileSuffix 要判断的上传的文件的后缀名
	 * @return true：可上传，允许上传，后缀在指定的后缀列表中
	 */
	public boolean allowUploadSuffix(String fileSuffix){
		if(allowUploadSuffixs == null){
			Log.error("请先使用 setAllowUploadSuffix() 方法设置哪些后缀允许上传");
			return false;
		}
		
		//进行判断，判断传入的suffix是否在允许上传的后缀里面
		for (int j = 0; j < allowUploadSuffixs.length; j++) {
			if(allowUploadSuffixs[j].equalsIgnoreCase(fileSuffix)){
				return true;
			}
		}
		return false;
	}
	
	
	public void isAllowUpload(String path) {
		
	}
	
	
	/**
	 * 判断当前文件附件存储使用的是哪种模式，存储到什么位置
	 * @param mode 存储的代码，可直接传入如 {@link #MODE_ALIYUN_OSS}
	 * @return 是否使用
	 * 			<ul>
	 * 				<li>true ： 是此种模式</li>
	 * 				<li>false ： 不是此种模式</li>
	 * 			</ul>
	 */
	public boolean isMode(String mode){
		
		//向前兼容，兼容 wm 2.25及以前版本的设置
		if(mode.equalsIgnoreCase("localFile")) {
			mode = "LocalServerMode";
		}else if(mode.equalsIgnoreCase("huaWeiYunOBS")) {
			mode = "HuaweiyunOBSMode";
		}else if(mode.equalsIgnoreCase("aliyunOSS")) {
			mode = "AliyunOSSMode";
		}
		
		
		//取得当前实现的文件的名字，例如本地存储的命名为 LocalServerMode.java ,那这里会取到 LocalServerMode
		String currentModeFileName = this.getstorage().getClass().getSimpleName();
		if(currentModeFileName.equalsIgnoreCase(mode)) {
			return true;
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		FileUpload file = new FileUpload();
		
		System.out.println(file.getstorage().getClass().getSimpleName());
		
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
	 * @param lengthKB 要上传的文件的大小，判断其大小是否超过系统指定的最大限制，单位是KB
	 * @return 若超出大小，则返回result:Failure ，info为出错原因
	 */
	public BaseVO verifyFileMaxLength(int lengthKB){
		BaseVO vo = new BaseVO();
		if(getMaxFileSizeKB() > 0 && lengthKB > getMaxFileSizeKB()){
			vo.setBaseVO(BaseVO.FAILURE, "文件大小超出限制！上传大小在 "+maxFileSize+" 以内");
			return vo;
		}
		return vo;
	}
	
	/**
	 * 给出文本内容，写出文件
	 * @param filePath 写出的路径,上传后的文件所在的目录＋文件名，如 "jar/file/xnx3.html"
	 * @param text 文本内容
	 * @param encode 编码格式，可传入 {@link FileUpload#GBK}、{@link FileUpload#UTF8}
	 * @return  {@link UploadFileVO}
	 */
	public UploadFileVO uploadStringFile(String filePath, String text, String encode){
		try {
			InputStream inputStream = StringUtil.stringToInputStream(text, encode);
			return uploadFile(filePath, inputStream);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			UploadFileVO vo = new UploadFileVO();
			vo.setBaseVO(UploadFileVO.FAILURE, e.getMessage());
			return vo;
		}
	}
	
	/**
	 * 给出文本内容，写出文件。写出UTF－8编码
	 * @param filePath 写出的路径,上传后的文件所在的目录＋文件名，如 "jar/file/xnx3.html"
	 * @param text 文本内容
	 */
	public UploadFileVO uploadStringFile(String filePath, String text){
		return uploadStringFile(filePath, text, com.xnx3.FileUtil.UTF8);
	}
	
	
	/**
	 * 上传本地文件
	 * @param filePath 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param localPath 本地要上传的文件的绝对路径，如 "/jar_file/iw.jar"
	 * @return {@link PutResult} 若失败，返回null
	 */
	public UploadFileVO uploadFile(String filePath, String localPath){
		File localFile = new File(localPath);
		return uploadFile(filePath, localFile);
	}
	
	/**
	 * 上传本地文件。上传的文件名会被自动重命名为uuid+后缀
	 * @param filePath 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param localFile 本地要上传的文件
	 * @return {@link PutResult} 若失败，返回null
	 */
	public UploadFileVO uploadFile(String filePath, File localFile){
		UploadFileVO vo = new UploadFileVO();
		
		BaseVO baseVO = verifyFileMaxLength(localFile);
		if(baseVO.getResult() - BaseVO.FAILURE == 0){
			vo.setBaseVO(baseVO);
			return vo;
		}
		
		//将本地文件转化为流
		try {
			InputStream localInput = new FileInputStream(localFile);
			return uploadFile(filePath, localInput);
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
	public UploadFileVO uploadFile(String path,InputStream inputStream){
		UploadFileVO vo = new UploadFileVO();
		
		/** 判断存储出去的后缀是否合规 **/
		String fileSuffix = null; //获取上传的文件的后缀
		fileSuffix = Lang.findFileSuffix(path);
		if(!allowUploadSuffix(fileSuffix)){
			vo.setBaseVO(UploadFileVO.FAILURE, "该后缀["+fileSuffix+"]不允许被上传");
			return vo;
		}
		
		/** 判断文件大小是否超出最大限制的大小 **/
		int lengthKB = 0;
		try {
			lengthKB = (int) Math.ceil(inputStream.available()/1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BaseVO baseVO = verifyFileMaxLength(lengthKB);
		if(baseVO.getResult() - BaseVO.FAILURE == 0){
			vo.setBaseVO(baseVO);
			return vo;
		}
		vo.setSize(lengthKB);
		
		//执行上传
		vo = getstorage().uploadFile(path, inputStream);
		if(vo.getSize() < 1) {
			vo.setSize(lengthKB);
		}
		vo.setUrl(getNetUrl()+vo.getPath());	//设置网络下载地址
		return vo;
	}
	

	/**
	 * 上传图片，将网上的图片复制到自己这里。（如果网上图片的URL获取不到后缀，默认用 jpg）
	 * @param filePath 上传后的文件所在的目录、路径。 传入格式如： file/images/  会自动给上传的图片重命名保存
	 * @param imageUrl 网上图片的地址
	 * @return {@link UploadFileVO}
	 */
	public UploadFileVO uploadImage(String filePath, String imageUrl){
		if(imageUrl == null){
			return null;
		}
		String suffix = Lang.findFileSuffix(imageUrl);	//取图片后缀名
		BufferedImage bufferedImage = ImageUtil.getBufferedImageByUrl(imageUrl);
		if(suffix == null){
			suffix = "jpg";
		}
		
		return uploadFile(filePath+Lang.uuid()+"."+suffix, ImageUtil.bufferedImageToInputStream(bufferedImage, suffix));
	}
	
	/**
	 * 传入一个路径，得到其源代码(文本)
	 * @param path 要获取的文本内容的路径，如  site/123/index.html
	 * @return 返回其文本内容。若找不到，或出错，则返回 null
	 */
	public String getText(String path){
		InputStream is = getstorage().getFile(path);
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
	 * 删除文件
	 * @param filePath 文件所在的路径，如 "jar/file/xnx3.jpg"
	 */
	public void deleteFile(String filePath){
		getstorage().deleteFile(filePath);
	}
	
	/**
	 * 复制文件
	 * @param originalFilePath 原本文件所在的路径(相对路径，非绝对路径，操作的是当前附件文件目录下)
	 * @param newFilePath 复制的文件所在的路径，所放的路径。(相对路径，非绝对路径，操作的是当前附件文件目录下)
	 */
	public void copyFile(String originalFilePath, String newFilePath){
		getstorage().copyFile(originalFilePath, newFilePath);
	}
	
	/**
	 * 上传文件
	 * @param filePath 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param inputStream 要上传的文件的数据流
	 * @param fileSuffix 上传的文件的后缀名
	 * @return {@link UploadFileVO}
	 */
	public UploadFileVO uploadFile(String filePath, InputStream inputStream, String fileSuffix) {
		UploadFileVO vo = new UploadFileVO();
		
		if(!allowUploadSuffix(fileSuffix)){
			vo.setBaseVO(UploadFileVO.FAILURE, "此后缀名不在可上传文件列表中");
			return vo;
		}
		
		if(inputStream == null){
			vo.setBaseVO(UploadFileVO.FAILURE, "上传文件不存在，请选择要上传的文件");
			return vo;
		}
		
		return uploadFile(filePath, "."+fileSuffix, inputStream);
	}
	
	/**
	 * 上传图片文件
	 * @param filePath 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param inputStream 图片的数据流
	 * @param fileSuffix 图片的后缀名
	 * @param maxWidth 上传图片的最大宽度，若超过这个宽度，会对图片进行等比缩放为当前宽度
	 * @return {@link UploadFileVO}
	 */
	public UploadFileVO uploadImage(String filePath, InputStream inputStream, String fileSuffix, int maxWidth) {
		UploadFileVO vo = new UploadFileVO();

		if(!allowUploadSuffix(fileSuffix)){
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
		
		return uploadFile(filePath, "."+fileSuffix, inputStream);
	}
	
	/**
	 * 上传文件
	 * @param filePath 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param fileName 上传的文件名，如“xnx3.jar”；主要拿里面的后缀名。也可以直接传入文件的后缀名如“.jar。新的文件名会是自动生成的 uuid+后缀”
	 * @param inputStream {@link InputStream}
	 * @return {@link PutResult} 若失败，返回null
	 */
	public UploadFileVO uploadFile(String filePath,String fileName,InputStream inputStream){
		UploadFileVO vo = new UploadFileVO();
		
		//进行文件后缀校验
		if(fileName == null || fileName.indexOf(".") == -1){
			vo.setBaseVO(UploadFileVO.FAILURE, "上传的文件名(后缀)校验失败！传入的为："+fileName+"，允许传入的值如：a.jpg或.jpg");
			return vo;
		}
		
		String fileSuffix = StringUtil.subString(fileName, ".", null, 3);	//获得文件后缀，以便重命名
		String name=Lang.uuid()+"."+fileSuffix;
		String path = filePath+name;
		return uploadFile(path, inputStream);
	}
	
	/**
	 * 获取某个目录（文件夹）占用空间的大小
	 * @param path 要计算的目录(文件夹)，如 jar/file/
	 * @return 计算出来的大小。单位：字节，B。  千分之一KB
	 */
	public long getDirectorySize(String path){
		return getstorage().getDirectorySize(path);
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
	 * 获取某个文件的大小，这个是文件，如果传入文件夹，是不起作用的，会返回-1，文件未发现
	 * @param path 要获取的是哪个文件。传入如 site/219/1.html
	 * @return 单位是 B， * 1000 = KB 。 如果返回-1，则是文件未发现，文件不存在
	 */
	public long getFileSize(String path){
		return getstorage().getFileSize(path);
	}
	
	/**
	 * 创建文件夹
	 * @param path 要创建的文件路径，传入如 site/219/test/ 则是创建 test 文件夹
	 */
	public void createFolder(String path) {
		getstorage().createFolder(path);
	}
	
}
