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
public class HuaweicloudOBSStorage implements StorageInterface {
	public OBSHandler obsHandler;	//禁用，通过getObsUtil() 获取
	public String obsBucketName;		// 当前进行操作桶的名称
	
	/**
	 * 文件上传-华为云OBS
	 * @param key 华为云的 Access Key Id
	 * @param secret 华为云的 Access Key Secret
	 * @param endpoint 华为云连接的地址节点，传入格式如 "obs.cn-north-4.myhuaweicloud.com" ,详细可参考 <a href="https://developer.huaweicloud.com/endpoint?OBS">https://developer.huaweicloud.com/endpoint?OBS</a>
	 * @param obsname 桶的名称
	 */
	public HuaweicloudOBSStorage(String key, String secret, String endpoint, String obsname) {
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
	public UploadFileVO upload(String path, InputStream inputStream) {
		UploadFileVO vo = getObsHander().putFileByStream(obsBucketName, path, inputStream);
		return vo;
	}

	/**
	 * 删除文件
	 * @param path 需要删除的文件路径加名称 例："site/219/index.html"
	 * @return {@link BaseVO}
	 */
	@Override
	public BaseVO delete(String path) {
		DeleteObjectResult result = getObsHander().deleteObject(obsBucketName, path);
		
		//成功
		if(result.getStatusCode() == 200) {
			return BaseVO.success();
		}else {
			return BaseVO.failure("failure, code:"+result.getStatusCode()+", obs requestId:"+result.getRequestId());
		}
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
	public long getSize(String path) {
		if(path == null) {
			return -1;
		}
		
		if((path.lastIndexOf("/") +1 ) == path.length()) {
			//是目录
			return getObsHander().getFolderObjectsSize(obsBucketName, path);
		}else {
			//是文件
			com.obs.services.model.ObjectMetadata metadata = getObsHander().getObsClient().getObjectMetadata(obsBucketName, path);
			if(metadata == null){
				return -1;
			}
			
			return metadata.getContentLength();
		}
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
	public InputStream get(String path) {
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
