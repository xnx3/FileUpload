package cn.zvo.fileupload.storage.tencentcloudCOS;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.qcloud.cos.model.*;
import com.xnx3.BaseVO;
import com.xnx3.Log;
import com.xnx3.StringUtil;
import cn.zvo.fileupload.StorageInterface;
import cn.zvo.fileupload.bean.SubFileBean;
import cn.zvo.fileupload.vo.StorageConfigVO;
import cn.zvo.fileupload.vo.UploadFileVO;
import cn.zvo.fileupload.vo.bean.Param;

/**
 * 文件上传之 腾讯云 COS
 * @author 管雷鸣
 *
 */
public class TencentcloudCOSStorage implements StorageInterface {
	private COS COS;

	/**
	 * 文件上传-腾讯云COS
	 * @param secretId 访问COS的secret ID。
	 * @param secretKey 访问COS的Secret Key。
	 * @param region COS服务的地域Region。如：ap-guangzhou
	 * @param bucketname 要操作的Bucket名称
	 */
	public TencentcloudCOSStorage(String secretId, String secretKey, String region, String bucketname) {
		init(secretId, secretKey, region, bucketname);
	}

	/**
	 * 文件上传-腾讯云COS 。传入的map的key 有：
	 * 	<ul>
	 *  	<li>secretId: 腾讯云的 Secret Id</li>
	 *  	<li>secretKey 访问COS的Secret Key。</li>
	 *  	<li>region COS服务的Region。如：ap-guangzhou/li>
	 * 		<li>bucketname 要操作的Bucket名称</li>
	 *  </ul>
	 */
	public TencentcloudCOSStorage(Map<String, String> map) {
		String secretKey = map.get("secretKey");
		String secretId = map.get("secretId");
		String region = map.get("region");
		String bucketname = map.get("bucketname");

		init(secretId, secretKey, region, bucketname);
	}

	/**
	 * 文件上传-腾讯云COS
	 * @param secretId 访问COS的secret ID。
	 * @param secretKey 访问COS的Secret Key。
	 * @param region COS服务的地域Region。如：ap-guangzhou
	 * @param bucketname 要操作的Bucket名称
	 */
	private void init(String secretId, String secretKey, String region, String bucketname) {
		this.COS = new COS(region, secretId, secretKey, bucketname, "");
	}

	/**
	 * 获取腾讯云COS操作
	 * @return {@link COS}
	 */
	public COS getCOS() {
		return COS;
	}

    /**
     * 通过流进行上传文件
     * @param path 上传文件路径和名称 例："site/1.txt"
     * @param inputStream 需要上传文件的输入流
     * @return {@link UploadFileVO} result 1: 成功；0 失败。
     */
	@Override
	public UploadFileVO upload(String path, InputStream inputStream) {
		ObjectMetadata metadata = new ObjectMetadata();
		UploadFileVO vo = new UploadFileVO();

		try {
			// 调用存储服务的上传方法
			PutObjectResult putResult = getCOS().getCOSClient().putObject(new PutObjectRequest(getCOS().bucketName, path, inputStream, metadata));
			Log.info(putResult.toString());

			// 判断上传是否成功
			if (putResult != null && putResult.getETag() != null) {
				String fileName = StringUtil.subString(path, "/", null, 3);
				vo.setName(fileName);
				vo.setPath(path);
			} else {
				vo.setBaseVO(BaseVO.FAILURE, "文件上传失败");
				return vo;
			}
		} catch (Exception e) {
			vo.setBaseVO(BaseVO.FAILURE, "文件上传失败");
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
			getCOS().deleteObject(path);
			return BaseVO.success();
		} catch (Exception e) {
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
		String bucketname = getCOS().bucketName;
		getCOS().getCOSClient().copyObject(bucketname, originalFilePath, bucketname, newFilePath);
		//getObsHander().copyObject(obsBucketName, originalFilePath, obsBucketName, newFilePath);
	}

	@Override
	public List<SubFileBean> getSubFileList(String path) {
		List<SubFileBean> list = new ArrayList<SubFileBean>();
		if(path == null || path.length() == 0){
			return list;
		}

        List<COSObjectSummary> resultList = getCOS().getFolderObjectList(path);
        for (int i = 0; i < resultList.size(); i++) {
            COSObjectSummary item = resultList.get(i);

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
			return getCOS().getFolderSize(path);
		}else {
			//是文件
            COSObject ossObject = getCOS().getCOSClient().getObject(getCOS().bucketName, path);
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
			getCOS().createFolder(path);
			return BaseVO.success();
		} catch (Exception e) {
			return BaseVO.failure(" 创建文件夹失败");
		}
	}

	@Override
	public InputStream get(String path) {
		try {
            COSObject ossObject = getCOS().getCOSClient().getObject(getCOS().bucketName, path);
            return ossObject.getObjectContent();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public StorageConfigVO config() {
		StorageConfigVO vo = new StorageConfigVO();
		vo.setName("腾讯云COS");
		vo.setDescription("腾讯云对象存储COS");
		vo.getParamList().add(new Param("secretId", "Secret Id", "腾讯云的 Secret Id", true, ""));
		vo.getParamList().add(new Param("secretKey", "Secret Key", "腾讯云的Secret Key", true, ""));
		vo.getParamList().add(new Param("region", "region", "COS服务的Region。如：ap-guangzhou", true, ""));
		vo.getParamList().add(new Param("bucketname", "桶名称", "COS服务的Bucket桶名称", true, ""));

		return vo;
	}

}
