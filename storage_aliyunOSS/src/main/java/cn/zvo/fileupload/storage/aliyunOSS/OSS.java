package cn.zvo.fileupload.storage.aliyunOSS;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectResult;
import com.xnx3.Lang;
import com.xnx3.media.ImageUtil;

import cn.zvo.fileupload.storage.aliyunOSS.ossbean.PutResult;

/**
 * aliyun OSS
 * <br/> 复制于 https://gitee.com/mail_osc/xnx3
 * @author 管雷鸣
 */
public class OSS {
	public String endpoint = "";
	public String accessKeyId = "";
	public String secretAccessKey = "";
	public String bucketName = "";
	
	 /**
     * 处理过的OSS外网域名,如 http://xnx3.oss-cn-qingdao.aliyuncs.com/
     * <br/>(文件上传成功时会加上此域名拼接出文件的访问完整URL。位于Bucket概览－OSS域名)
     */
	public String url = "";
	
	/**
	 * 创建OSS操作对象
	 * @param endpoint OSS服务的Endpoint。如：oss-cn-hongkong.aliyuncs.com
	 * @param accessKeyId 访问OSS的Access Key ID。
	 * @param secretAccessKey 访问OSS的Secret Access Key。
	 * @param bucketName 要操作的Bucket名称
	 * @param url Bucket的OSS域名，绑定的域名，末尾要加/  如： http://www.baidu.com/
	 * 				<br/>&nbsp;&nbsp;&nbsp;&nbsp;如果是公共读这里会用到。如果是私有读写这里用不到，可以传个空字符串""
	 */
	public OSS(String endpoint, String accessKeyId, String secretAccessKey, String bucketName, String url) {
		this.endpoint = endpoint;
		this.accessKeyId = accessKeyId;
		this.secretAccessKey = secretAccessKey;
		this.bucketName = bucketName;
	}
	
	
    private OSSClient ossClient;
	
	/**
	 * 获取 OSSClient 对象
	 * @return {@link OSSClient}
	 */
	public OSSClient getOSSClient(){
		if(ossClient == null){
			ossClient = new OSSClient(endpoint, accessKeyId, secretAccessKey);
		}
		return ossClient;
	}
	
	/**
	 * 创建文件夹
	 * @param folderName 要创建的文件夹名字，如要创建xnx3文件夹，则传入"xnx3/"。也可以传入"x/n/" 代表建立x文件夹同时其下再建立n文件夹
	 */
	public void createFolder(String folderName){
		//既然是目录，那就是以/结束，判断此是否是以／结束的，若不是，末尾自动加上
		if(folderName.lastIndexOf("/")<(folderName.length()-1)){
			folderName+="/";
		}
		
		getOSSClient().putObject(bucketName, folderName, new ByteArrayInputStream(new byte[0]));
	}
	
	/**
	 * 上传文件
	 * @param filePath 上传后的文件所在OSS的目录、路径，如 "jar/file/"
	 * @param fileName 上传的文件名，如“xnx3.jar”；主要拿里面的后缀名。也可以直接传入文件的后缀名如“.jar”
	 * @param inputStream {@link InputStream}
	 * @return {@link PutResult} 若失败，返回null
	 */
	public PutResult put(String filePath,String fileName,InputStream inputStream){
		String fileSuffix=com.xnx3.Lang.subString(fileName, ".", null, 3);	//获得文件后缀，以便重命名
        String name=Lang.uuid()+"."+fileSuffix;
        String path = filePath+name;
        PutObjectResult pr = getOSSClient().putObject(bucketName, path, inputStream);
		return new PutResult(name, path,url+path);
	}
	
	/**
	 * 删除文件
	 * @param filePath 文件所在OSS的绝对路径，如 "jar/file/xnx3.jpg"
	 */
	public void deleteObject(String filePath){
		getOSSClient().deleteObject(bucketName, filePath);
	}
	
	/**
	 * 上传文件。上传后的文件名固定
	 * @param path 上传到哪里，包含上传后的文件名，如"image/head/123.jpg"
	 * @param inputStream 文件
	 * @return {@link PutResult}
	 */
	public PutResult put(String path,InputStream inputStream){
		PutObjectResult pr = getOSSClient().putObject(bucketName, path, inputStream);
		String name = Lang.subString(path, "/", null, 3);
		return new PutResult(name, path,url+path);
	}
	
	/**
	 * 上传本地文件
	 * @param filePath 上传后的文件所在OSS的目录、路径，如 "jar/file/"
	 * @param localPath 本地要上传的文件的绝对路径，如 "/jar_file/iw.jar"
	 * @return {@link PutResult} 若失败，返回null
	 */
	public PutResult put(String filePath, String localPath){
		File file = new File(localPath);
		InputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return put(filePath, localPath, input);
	}
	
	/**
	 * 上传图片，将网上的图片复制到OSS里 如果获取不到后缀，默认用 jpg
	 * @param filePath 上传图片的OSS地址，如 image/124  后面会自动拼接上图片的后缀名，上传成功后为image/124.png
	 * @param imageUrl 网上图片的地址
	 * @return {@link PutResult}
	 */
	public PutResult putImageByUrl(String filePath, String imageUrl){
		if(imageUrl == null){
			return null;
		}
		String suffix = Lang.findFileSuffix(imageUrl);	//取图片后缀名
		BufferedImage bufferedImage = ImageUtil.getBufferedImageByUrl(imageUrl);
		if(suffix == null){
			suffix = "jpg";
		}
		
    	return put(filePath+"."+suffix, ImageUtil.bufferedImageToInputStream(bufferedImage, suffix));
	}
	
	
	/**
	 * 以字符串创建文件
	 * @param path 上传后的文件所在OSS的目录＋文件名，如 "jar/file/xnx3.html"
	 * @param text 文件内容
	 * @param encode 文件编码，如：UTF-8 
	 * @return {@link PutResult} 若失败，返回null
	 */
	public PutResult putStringFile(String path, String text, String encode){
		try {
			return put(path, new ByteArrayInputStream(text.getBytes(encode)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 以字符串创建文件，创建的文件编码为UTF-8
	 * @param path 上传后的文件所在OSS的目录＋文件名，如 "jar/file/xnx3.html"
	 * @param text 文件内容
	 * @param encode 文件编码，如：UTF-8 
	 * @return {@link PutResult} 若失败，返回null
	 */
	public PutResult putStringFile(String path, String text){
		return putStringFile(path, text, "UTF-8");
	}
	
	/**
	 * 查看某个路径下的文件所占用的资源的大小
	 * @param filePath 要查看文件的路径，如 file/image/
	 * @return 单位：B
	 */
	public long getFolderSize(String filePath){
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
		listObjectsRequest.setPrefix(filePath); 
		listObjectsRequest.setMaxKeys(1000);
		
		boolean have = true;		//是否有下一页
		String nextMarker = null;
		int size = 0;		//总字节大小，单位：B
		while(have){
			if(nextMarker != null){
				listObjectsRequest.setMarker(nextMarker);
			}
			ObjectListing listO = getOSSClient().listObjects(listObjectsRequest);
			
		    for (OSSObjectSummary objectSummary : listO.getObjectSummaries()) {
		        size += objectSummary.getSize();  
		    }
		    
		    have = listO.isTruncated();
		    nextMarker = listO.getNextMarker();
		}
		return size;
	}
	
	/**
	 * 获取 指定目录下的所有文件对象
	 * @param filePath 要查看文件的路径，如 file/image/
	 * @return {@link List}
	 */
	public List<OSSObjectSummary> getFolderObjectList(String filePath){
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
		listObjectsRequest.setPrefix(filePath); 
		listObjectsRequest.setMaxKeys(1000);
		List<OSSObjectSummary> list = new ArrayList<OSSObjectSummary>();
		
		boolean have = true;		//是否有下一页
		String nextMarker = null;
		while(have){
			if(nextMarker != null){
				listObjectsRequest.setMarker(nextMarker);
			}
			ObjectListing listO = getOSSClient().listObjects(listObjectsRequest);
			
		    for (OSSObjectSummary objectSummary : listO.getObjectSummaries()) {
		    	list.add(objectSummary);
		    }
		    
		    have = listO.isTruncated();
		    nextMarker = listO.getNextMarker();
		}
		return list;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
//		
//		OSSUtils o = new OSSUtils("oss-cn-hongkong.aliyuncs.com", ".....", "...", "...", "http://www.baidu.com/");
//		
//		System.out.println(o.putStringFile("ceshi.txt", "哈哈"));
//		putStringFile("test.txt", "test");
	}
}
