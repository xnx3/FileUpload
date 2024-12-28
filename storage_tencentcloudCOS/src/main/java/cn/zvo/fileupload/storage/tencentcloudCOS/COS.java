package cn.zvo.fileupload.storage.tencentcloudCOS;

import cn.zvo.fileupload.storage.tencentcloudCOS.cosbean.PutResult;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.*;
import com.xnx3.Lang;
import com.xnx3.media.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tencent Cloud COS 上传成功后，返回值
 * <br/> 复制并修改自 https://gitee.com/mail_osc/xnx3
 * @author
 */
public class COS {
	public String region = "";
	public String secretId = "";
	public String secretKey = "";
	public String bucketName = "";

	/**
	 * 处理过的COS外网域名,如 http://xnx3.cos.ap-guangzhou.myqcloud.com/
	 * <br/>(文件上传成功时会加上此域名拼接出文件的访问完整URL。位于Bucket概览－COS域名)
	 */
	private String url = "";

	/**
	 * 创建COS操作对象
	 * @param region COS服务的地域region。如：ap-guangzhou
	 * @param secretId 访问COS的Secret Id。
	 * @param secretKey 访问COS的Secret Key。
	 * @param bucketName 要操作的Bucket名称
	 * @param url Bucket的COS域名，绑定的域名，末尾要加/  如： http://www.example.com/
	 *             <br/>&nbsp;&nbsp;&nbsp;&nbsp;如果是公共读这里会用到。如果是私有读写这里用不到，可以传个空字符串""
	 */
	public COS(String region, String secretId, String secretKey, String bucketName, String url) {
		this.region = region;
		this.secretId = secretId;
		this.secretKey = secretKey;
		this.bucketName = bucketName;
	}

	private COSClient cosClient;

	/**
	 * 获取 COSClient 对象
	 * @return {@link COSClient}
	 */
	public COSClient getCOSClient(){
		if(cosClient == null){
			COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
			ClientConfig clientConfig = new ClientConfig(new com.qcloud.cos.region.Region(region)); // endpoint 是地域，如 ap-guangzhou
			cosClient = new COSClient(cred, clientConfig);
		}
		return cosClient;
	}

	/**
	 * 创建文件夹
	 * @param folderName 要创建的文件夹名字，如要创建xnx3文件夹，则传入"xnx3/"。也可以传入"x/n/" 代表建立x文件夹同时其下再建立n文件夹
	 */
	public void createFolder(String folderName) {
		// 既然是目录，那就是以/结束，判断此是否是以／结束的，若不是，末尾自动加上
		if (folderName.lastIndexOf("/") < (folderName.length() - 1)) {
			folderName += "/";
		}

		ObjectMetadata metadata = new ObjectMetadata();
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName, emptyContent, metadata);
		getCOSClient().putObject(putObjectRequest);
	}


	/**
	 * 上传文件
	 * @param filePath 上传后的文件所在COS的目录、路径，如 "jar/file/"
	 * @param fileName 上传的文件名，如“xnx3.jar”；主要拿里面的后缀名。也可以直接传入文件的后缀名如“.jar”
	 * @param inputStream {@link InputStream}
	 * @return {@link PutResult} 若失败，返回null
	 */
	public PutResult put(String filePath, String fileName, InputStream inputStream) {
		String fileSuffix = Lang.subString(fileName, ".", null, 3); // 获得文件后缀，以便重命名
		String name = Lang.uuid() + "." + fileSuffix;
		String path = filePath + name;
		ObjectMetadata metadata = new ObjectMetadata();
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, path, inputStream, metadata);
		PutObjectResult pr = getCOSClient().putObject(putObjectRequest);
		return new PutResult(name, path, url + path);
	}


	/**
	 * 删除文件
	 * @param filePath 文件所在COS的绝对路径，如 "jar/file/xnx3.jpg"
	 */
	public void deleteObject(String filePath) {
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, filePath);
		getCOSClient().deleteObject(deleteObjectRequest);
	}

	/**
	 * 上传文件。上传后的文件名固定
	 * @param path 上传到哪里，包含上传后的文件名，如"image/head/123.jpg"
	 * @param inputStream 文件
	 * @return {@link PutResult} 若失败，返回null
	 */
	public PutResult put(String path, InputStream inputStream) {
		ObjectMetadata metadata = new ObjectMetadata();
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, path, inputStream, metadata);
		PutObjectResult pr = getCOSClient().putObject(putObjectRequest);
		String name = Lang.subString(path, "/", null, 3);
		return new PutResult(name, path, url + path);
	}


	/**
	 * 上传本地文件
	 * @param filePath 上传后的文件所在COS的目录、路径，如 "jar/file/"
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
	 * 上传图片，将网上的图片复制到COS里 如果获取不到后缀，默认用 jpg
	 * @param filePath 上传图片的COS地址，如 image/124  后面会自动拼接上图片的后缀名，上传成功后为image/124.png
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
	 * @param path 上传后的文件所在COS的目录＋文件名，如 "jar/file/xnx3.html"
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
	 * @param path 上传后的文件所在COS的目录＋文件名，如 "jar/file/xnx3.html"
	 * @param text 文件内容
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
	public long getFolderSize(String filePath) {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketName);
		listObjectsRequest.setPrefix(filePath);
		listObjectsRequest.setMaxKeys(1000);

		boolean have = true; // 是否有下一页
		String nextMarker = null;
		long size = 0; // 总字节大小，单位：B
		while (have) {
			if (nextMarker != null) {
				listObjectsRequest.setMarker(nextMarker);
			}
			ObjectListing listO = getCOSClient().listObjects(listObjectsRequest);

			for (COSObjectSummary objectSummary : listO.getObjectSummaries()) {
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
	public List<COSObjectSummary> getFolderObjectList(String filePath) {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
		listObjectsRequest.setBucketName(bucketName);
		listObjectsRequest.setPrefix(filePath);
		listObjectsRequest.setMaxKeys(1000);
		List<COSObjectSummary> list = new ArrayList<>();

		ObjectListing objectListing;
		do {
			objectListing = getCOSClient().listObjects(listObjectsRequest);
			list.addAll(objectListing.getObjectSummaries());
			listObjectsRequest.setMarker(objectListing.getNextMarker());
		} while (objectListing.isTruncated());

		return list;
	}
}
