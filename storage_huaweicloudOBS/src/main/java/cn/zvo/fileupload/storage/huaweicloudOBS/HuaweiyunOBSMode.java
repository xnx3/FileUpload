package cn.zvo.fileupload.storage.huaweicloudOBS;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.obs.services.model.DeleteObjectResult;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;
import com.xnx3.BaseVO;
import cn.zvo.fileupload.StorageInterface;
import cn.zvo.fileupload.bean.SubFileBean;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * 文件上传之 华为云 OBS 
 * @author 管雷鸣
 *
 */
public class HuaweiyunOBSMode implements StorageInterface {
	public OBSHandler obsHandler;	//禁用，通过getObsUtil() 获取
	public String obsBucketName;		// 当前进行操作桶的名称
	
	/**
	 * 文件上传-华为云OBS
	 * @param key 华为云的 Access Key Id
	 * @param secret 华为云的 Access Key Secret
	 * @param endpoint 华为云连接的地址节点
	 * @param obsname 桶的名称
	 */
	public HuaweiyunOBSMode(String key, String secret, String endpoint, String obsname) {
		obsHandler = new OBSHandler(key,secret,endpoint);
		// 如果设置过CDN的路径测设置为CDN路径，没有设置则为桶原生的访问路径
//		obsHandler.setUrlForCDN(netUrl);
		// 在数据库中读取进行操作的桶的明恒
		obsHandler.setObsBucketName(obsname);
		// 对桶名称进行当前类内缓存
		obsBucketName = obsHandler.getObsBucketName();
	}
	
	/**
	 * 获取华为云OBS的操作类
	 * @return 当前华为云OBS的操作类型
	 */
	public OBSHandler getObsHander() {
		return obsHandler;
	}
	
	/**
	 * 通过流进行上传文件
	 * @param path 上传文件路径和名称 例："site/1.txt"
	 * @param inputStream 需要上传文件的输入流
	 * @return {@link com.xnx3.j2ee.vo.UploadFileVO} result 1: 成功；0 失败。
	 */
	@Override
	public UploadFileVO uploadFile(String path, InputStream inputStream) {
		return getObsHander().putFileByStream(obsBucketName, path, inputStream);
	}

	/**
	 * 删除文件
	 * @param filePath 需要删除的文件路径加名称 例："site/1.sh"
	 * @return {@link BaseVO}
	 */
	@Override
	public BaseVO deleteFile(String filePath) {
		DeleteObjectResult result = getObsHander().deleteObject(obsBucketName, filePath);
		
		//成功
		if(result.getStatusCode() == 200) {
			return BaseVO.success();
		}else {
			return BaseVO.failure("failure, code:"+result.getStatusCode()+", obs requestId:"+result.getRequestId());
		}
	}
	
	/**
	 * 获得指定路径下的对象个数
	 * @param path 指定查询的文件夹路径 例：“site/”
	 * @return 
	 */
	@Override
	public long getDirectorySize(String path) {
		return getObsHander().getFolderObjectsSize(obsBucketName, path);
	}
	
	/**
	 * OBS内对象复制
	 * @param originalFilePath 源文件的路径和文件名 例："site/2010/example.txt"
	 * @param newFilePath 目标文件的路径和文件名 例："site/2010/example_bak.txt"
	 */
	@Override
	public void copyFile(String originalFilePath, String newFilePath) {
		getObsHander().copyObject(obsBucketName, originalFilePath, obsBucketName, newFilePath);
	}

	@Override
	public List<SubFileBean> getSubFileList(String path) {
		List<SubFileBean> list = new ArrayList<SubFileBean>();
		if(path == null || path.length() == 0){
			return list;
		}
		
		ListObjectsRequest request = new ListObjectsRequest(obsBucketName);
		request.setPrefix(path);
		request.setMaxKeys(100);
		ObjectListing result;
		do{
			result = getObsHander().getObsClient().listObjects(request);
			for(ObsObject obsObject : result.getObjects()){
				SubFileBean bean = new SubFileBean();
				bean.setPath(obsObject.getObjectKey());
				bean.setSize(obsObject.getMetadata().getContentLength());
				bean.setLastModified(obsObject.getMetadata().getLastModified().getTime());
//				obsObject.getMetadata().
				//判断是否是目录。SDK没有直接判断是否是目录的方法，采用这种方式。
				if(path != null && bean.getSize() < 1) {
					if(bean.getPath().lastIndexOf("/") == bean.getPath().length()-1) {
						bean.setFolder(true);
					}
				}
				list.add(bean);
			}
			request.setMarker(result.getNextMarker());
		}while(result.isTruncated());
		
		return list;
	}

	@Override
	public long getFileSize(String path) {
		com.obs.services.model.ObjectMetadata metadata = getObsHander().getObsClient().getObjectMetadata(obsBucketName, path);
		if(metadata == null){
			return 0;
		}
		
		return metadata.getContentLength();
	}

	@Override
	public BaseVO createFolder(String path) {
		PutObjectResult result = getObsHander().mkdirFolder(obsBucketName, path);
		UploadFileVO uploadFileVO = getObsHander().getUploadFileVO(result);
	
		BaseVO vo = new BaseVO();
		vo.setBaseVO(uploadFileVO.getResult(), uploadFileVO.getInfo());
		return vo;
	}

	@Override
	public InputStream getFile(String path) {
		String content = null;
		byte[] bytes = null;
		try {
			bytes = getObsHander().getFileByteArray(obsBucketName, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(bytes == null){
			return null;
		}
		
		InputStream input = new ByteArrayInputStream(bytes);
		return input;
	}
	
}
