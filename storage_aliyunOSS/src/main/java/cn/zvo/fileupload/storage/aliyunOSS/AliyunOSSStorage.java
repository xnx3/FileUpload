package cn.zvo.fileupload.storage.aliyunOSS;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.PutObjectResult;
import com.xnx3.BaseVO;
import com.xnx3.Log;
import com.xnx3.StringUtil;
import cn.zvo.fileupload.StorageInterface;
import cn.zvo.fileupload.bean.SubFileBean;
import cn.zvo.fileupload.vo.StorageConfigVO;
import cn.zvo.fileupload.vo.UploadFileVO;
import cn.zvo.fileupload.vo.bean.Param;

/**
 * 文件上传之 华为云 OBS 
 * @author 管雷鸣
 *
 */
public class AliyunOSSStorage implements StorageInterface {
	private OSS oss;
	
	/**
	 * 文件上传-阿里云OBS
	 * @param accessKeyId 访问OSS的Access Key ID。
	 * @param secretAccessKey 访问OSS的Secret Access Key。
	 * @param endpoint OSS服务的Endpoint。如：oss-cn-hongkong.aliyuncs.com
	 * @param bucketname 要操作的Bucket名称
	 */
	public AliyunOSSStorage(String accessKeyId, String secretAccessKey, String endpoint, String bucketname) {
		init(accessKeyId, secretAccessKey, endpoint, bucketname);
	}

	/**
	 * 文件上传-阿里云OSS 。传入的map的key 有：
	 * 	<ul>
	 *  	<li>accessKeyId: 阿里云的 Access Key Id</li>
	 *  	<li>secretAccessKey 访问OSS的Secret Access Key。</li>
	 *  	<li>endpoint OSS服务的Endpoint。如：oss-cn-hongkong.aliyuncs.com</li>
	 * 		<li>bucketname 要操作的Bucket名称</li>
	 *  </ul>
	 */
	public AliyunOSSStorage(Map<String, String> map) {
		String secretAccessKey = map.get("secretAccessKey");
		String accessKeyId = map.get("accessKeyId");
		String endpoint = map.get("endpoint");
		String bucketname = map.get("bucketname");
		
		init(accessKeyId, secretAccessKey, endpoint, bucketname);
	}
	
	/**
	 * 文件上传-阿里云OBS
	 * @param accessKeyId 访问OSS的Access Key ID。
	 * @param secretAccessKey 访问OSS的Secret Access Key。
	 * @param endpoint OSS服务的Endpoint。如：oss-cn-hongkong.aliyuncs.com
	 * @param bucketname 要操作的Bucket名称
	 */
	private void init(String accessKeyId, String secretAccessKey, String endpoint, String bucketname) {
		this.oss = new OSS(endpoint, accessKeyId, secretAccessKey, bucketname, "");
	}
	
	/**
	 * 获取阿里云OSS操作
	 * @return {@link OSS}
	 */
	public OSS getOss() {
		return oss;
	}

	/**
	 * 通过流进行上传文件
	 * @param path 上传文件路径和名称 例："site/1.txt"
	 * @param inputStream 需要上传文件的输入流
	 * @return {@link UploadFileVO} result 1: 成功；0 失败。
	 */
	@Override
	public UploadFileVO upload(String path, InputStream inputStream) {
		UploadFileVO vo = new UploadFileVO();
		try {
			// 调用上传方法
			PutObjectResult result = getOss().getOSSClient().putObject(getOss().bucketName, path, inputStream);
			Log.info(result.toString());

			// 检查上传是否成功
			if (result != null && result.getETag() != null) {
				String fileName = StringUtil.subString(path, "/", null, 3);
				vo.setName(fileName);
				vo.setPath(path);
			} else {
				vo.setBaseVO(BaseVO.FAILURE,"上传文件失败");
				return vo;
			}
		} catch (Exception e) {
			Log.info("文件上传失败，错误提示为: " + e.getMessage());
			vo.setBaseVO(BaseVO.FAILURE,"上传文件失败");
			return vo;
		}
		return vo;
	}

	/**
	 * 删除文件
	 * @param path 需要删除的文件路径加名称 例："site/219/index.html"
	 * @return {@link BaseVO}
	 */
	@Override
	public BaseVO delete(String path) {
		try {
			// 调用删除方法
			getOss().getOSSClient().deleteObject(getOss().bucketName, path);
			return BaseVO.success();  // 成功
		} catch (Exception e) {
			Log.info("文件删除失败，错误提示为: " + e.getMessage());
			return BaseVO.failure("文件删除失败");
		}
	}
	
	/**
	 * 文件复制
	 * @param originalFilePath 源文件的路径和文件名 例："site/2010/example.txt"
	 * @param newFilePath 目标文件的路径和文件名 例："site/2010/example_bak.txt"
	 */
	@Override
	public void copyFile(String originalFilePath, String newFilePath) {
		String bucketname = getOss().bucketName;
		getOss().getOSSClient().copyObject(bucketname, originalFilePath, bucketname, newFilePath);
		//getObsHander().copyObject(obsBucketName, originalFilePath, obsBucketName, newFilePath);
	}

	@Override
	public List<SubFileBean> getSubFileList(String path) {
		List<SubFileBean> list = new ArrayList<SubFileBean>();
		if(path == null || path.length() == 0){
			return list;
		}
		
		List<OSSObjectSummary> resultList = getOss().getFolderObjectList(path);
		for (int i = 0; i < resultList.size(); i++) {
			OSSObjectSummary item = resultList.get(i);
			
			SubFileBean bean = new SubFileBean();
			bean.setPath(item.getKey());
			bean.setSize(item.getSize());
			bean.setLastModified(item.getLastModified().getTime());
			bean.setFolder((item.getKey().lastIndexOf("/") +1 ) == item.getKey().length());
			
			list.add(bean);
		}
		
		return list;
	}

	@Override
	public long getSize(String path) {
		if(path == null) {
			return -1;
		}
		
		if((path.lastIndexOf("/") +1 ) == path.length()) {
			//是目录
			return getOss().getFolderSize(path);
		}else {
			//是文件
			OSSObject ossObject = getOss().getOSSClient().getObject(getOss().bucketName, path);
			if(ossObject == null || ossObject.getObjectContent() == null) {
				return -1;
			}
			
			try {
				return ossObject.getObjectContent().available();
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
		}
	}

	@Override
	public BaseVO createFolder(String path) {
		try {
			getOss().createFolder(path);
			return BaseVO.success();  // 成功
		} catch (Exception e) {
			Log.info("文件夹创建失败，错误提示为: " + e.getMessage());
			return BaseVO.failure("文件夹创建失败");
		}
	}

	@Override
	public InputStream get(String path) {
		try {
			OSSObject ossObject = getOss().getOSSClient().getObject(getOss().bucketName, path);
			return ossObject.getObjectContent();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public StorageConfigVO config() {
		StorageConfigVO vo = new StorageConfigVO();
		vo.setName("阿里云OSS");
		vo.setDescription("阿里云对象存储OSS");
		vo.getParamList().add(new Param("accessKeyId", "Access Key Id", "阿里云的 Access Key Id", true, ""));
		vo.getParamList().add(new Param("secretAccessKey", "Secret Access Key", "阿里云的Secret Access Key", true, ""));
		vo.getParamList().add(new Param("endpoint", "endpoint", "OSS服务的Endpoint。如：oss-cn-hongkong.aliyuncs.com", true, ""));
		vo.getParamList().add(new Param("bucketname", "桶名称", "OSS服务的Bucket桶名称", true, ""));
		
		return vo;
	}
	
}
