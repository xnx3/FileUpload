package cn.zvo.fileupload.storage.sftp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jcraft.jsch.SftpException;
import com.xnx3.BaseVO;
import com.xnx3.Lang;
import com.xnx3.Log;
import com.xnx3.UrlUtil;

import cn.zvo.fileupload.StorageInterface;
import cn.zvo.fileupload.bean.SubFileBean;
import cn.zvo.fileupload.storage.sftp.bean.PathBean;
import cn.zvo.fileupload.vo.StorageConfigVO;
import cn.zvo.fileupload.vo.UploadFileVO;
import cn.zvo.fileupload.vo.bean.Param;

/**
 * 文件上传之 华为云 OBS 
 * @author 管雷鸣
 *
 */
public class SftpStorage implements StorageInterface {
	public SFTPUtil sftpUtil;
	public String directory;	//SFTP的默认目录，比如root账号，默认操作的目录是在 /root/  严格注意格式，前后有 /
	
	/**
	 * 文件上传-sftp。传入的map的key 有：
	 * 	<ul>
	 *  	<li>host: 主机，格式如 127.0.0.1</li>
	 *  	<li>username: 用户名，如 root</li>
	 *  	<li>password: 密码</li>
	 * 		<li>port: 端口号，如 22 ，如果不传默认就是22</li>
	 *  	<li>directory:  操作的目录，比如 /root/ 如果不传入，默认是 / 也就是根目录。但要注意该账号要对此目录有读写权限！貌似除了root账号能有根目录权限外，其他别的账号可能操作不了根目录 </li>
	 *  </ul>
	 */
	public SftpStorage(Map<String, String> map) {
		String host = map.get("host");
		String username = map.get("username");
		String password = map.get("password");
		String port = map.get("port");
		this.directory = map.get("directory");
		if(this.directory == null || this.directory.length() == 0 || this.directory.equalsIgnoreCase("null")) {
			Log.info("提示 directory参数未设置，这也是SFTP的默认目录，比如root账号，默认操作的目录是在 /root/  严格注意格式，前后有 /");
			this.directory = "/";
		}
		
		this.sftpUtil = new SFTPUtil();
		this.sftpUtil.setHost(host);
		this.sftpUtil.setUsername(username);
		this.sftpUtil.setPassword(password);
		if(port != null && port.length() > 0) {
			this.sftpUtil.setPort(Lang.stringToInt(port, 22));
		}
	}

	public BaseVO openConnectCheck(){
		if(this.sftpUtil.getSftp() == null || !this.sftpUtil.getSftp().isConnected()) {
			//链接没打开，那么打开链接
			
			try {
				this.sftpUtil.connect();
			} catch (Exception e) {
				e.printStackTrace();
				return BaseVO.failure(e.getMessage());
			}
			
		}
		return BaseVO.success();
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
		
		//检测打开连接
		BaseVO openVO = openConnectCheck();
		if(openVO.getResult()-BaseVO.FAILURE == 0) {
			vo.setBaseVO(openVO);
			return vo;
		}
		
		PathBean pathBean = getPath(path);
		
		//切换到要上传的目录
		try {
			this.sftpUtil.getSftp().cd(this.directory + pathBean.getPath()); //切换到指定上传目录
		} catch (SftpException e) {
			e.printStackTrace();
			vo.setResult(UploadFileVO.FAILURE);
			vo.setInfo("异常，很可能是无权操作此目录:"+pathBean.getPath()+", 错误:"+e.getCause().toString());
			return vo;
		}
		
		
		vo.setName(pathBean.getFileName());
		vo.setPath(path);
//		vo.setSize(inputStream.available());
		
		//上传
		try {
			this.sftpUtil.getSftp().put(inputStream, pathBean.getFileName());
		} catch (SftpException e) {
			e.printStackTrace();
			vo.setResult(UploadFileVO.FAILURE);
			vo.setInfo(e.getMessage());
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
		String fileName = UrlUtil.getFileName(path);
		String sftpPath = UrlUtil.getPath(path);
		
		//检测打开连接
		openConnectCheck();
		
		PathBean pathBean = getPath(path);
		
		//切换到要上传的目录
		try {
			this.sftpUtil.getSftp().cd(this.directory + pathBean.getPath()); //切换到指定上传目录
		} catch (SftpException e) {
			e.printStackTrace();
			return BaseVO.failure("异常，很可能是无权操作此目录:"+this.directory + pathBean.getPath()+", 错误:"+e.getCause().toString());
		}
		
		
		try {
			this.sftpUtil.getSftp().rm(sftpPath);
			return BaseVO.success();
		} catch (SftpException e) {
			e.printStackTrace();
			return BaseVO.failure(e.getMessage());
		}
	}
	
	/**
	 * 文件复制
	 * @param originalFilePath 源文件的路径和文件名 例："site/2010/example.txt"
	 * @param newFilePath 目标文件的路径和文件名 例："site/2010/example_bak.txt"
	 */
	@Override
	public void copyFile(String originalFilePath, String newFilePath) {
//		String bucketname = getOss().bucketName;
//		getOss().getOSSClient().copyObject(bucketname, originalFilePath, bucketname, newFilePath);
	}

	@Override
	public List<SubFileBean> getSubFileList(String path) {
		List<SubFileBean> list = new ArrayList<SubFileBean>();
		if(path == null || path.length() == 0){
			return list;
		}
		
//		List<OSSObjectSummary> resultList = getOss().getFolderObjectList(path);
//		for (int i = 0; i < resultList.size(); i++) {
//			OSSObjectSummary item = resultList.get(i);
//			
//			SubFileBean bean = new SubFileBean();
//			bean.setPath(item.getKey());
//			bean.setSize(item.getSize());
//			bean.setLastModified(item.getLastModified().getTime());
//			bean.setFolder((item.getKey().lastIndexOf("/") +1 ) == item.getKey().length());
//			
//			list.add(bean);
//		}
		
		return list;
	}

	@Override
	public long getSize(String path) {
		if(path == null) {
			return -1;
		}
		
//		if((path.lastIndexOf("/") +1 ) == path.length()) {
//			//是目录
//			return getOss().getFolderSize(path);
//		}else {
//			//是文件
//			OSSObject ossObject = getOss().getOSSClient().getObject(getOss().bucketName, path);
//			if(ossObject == null || ossObject.getObjectContent() == null) {
//				return -1;
//			}
//			
//			try {
//				return ossObject.getObjectContent().available();
//			} catch (IOException e) {
//				e.printStackTrace();
//				return -1;
//			}
//		}
		
		return 0;
	}

	@Override
	public BaseVO createFolder(String path) {
		
		return BaseVO.success();
	}

	@Override
	public InputStream get(String path) {
		return null;
	}
	
	/**
	 * 将传入的path，进行转换
	 * @param inputPath
	 * @return
	 */
	public PathBean getPath(String inputPath) {
		String d = "http://translate.zvo.cn/";
		String path = d+inputPath;
		
		String fileName = UrlUtil.getFileName(path);
		String sftpPath = UrlUtil.getPath(path).replace(d, "");
	
		PathBean bean = new PathBean();
		bean.setFileName(fileName);
		bean.setPath(sftpPath);
		return bean;
	}

	@Override
	public StorageConfigVO config() {
		StorageConfigVO vo = new StorageConfigVO();
		vo.setName("SFTP");
		vo.setDescription("将文件存储到指定的SFTP空间中");
		vo.getParamList().add(new Param("host", "主机", "格式如 127.0.0.1", true, ""));
		vo.getParamList().add(new Param("username", "用户名", "用户名，如 root", true, ""));
		vo.getParamList().add(new Param("password", "密码", "登陆密码，如果未设置密码可留空", false, ""));
		vo.getParamList().add(new Param("port", "端口号", "如 22", false, "22"));
		vo.getParamList().add(new Param("directory", "上传目录", "操作的目录，比如 /root/  如果不传入，默认是 / 也就是根目录。但要注意该账号要对此目录有读写权限！貌似除了root账号能有根目录权限外，其他别的账号可能操作不了根目录", false, "/"));
		
		return vo;
	}
}
