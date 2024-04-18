package cn.zvo.fileupload.storage.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPFile;

import com.xnx3.BaseVO;
import com.xnx3.Lang;
import com.xnx3.Log;
import com.xnx3.UrlUtil;

import cn.zvo.fileupload.StorageInterface;
import cn.zvo.fileupload.bean.SubFileBean;
import cn.zvo.fileupload.storage.ftp.bean.PathBean;
import cn.zvo.fileupload.vo.StorageConfigVO;
import cn.zvo.fileupload.vo.UploadFileVO;
import cn.zvo.fileupload.vo.bean.Param;

/**
 * 文件上传之 华为云 OBS 
 * @author 管雷鸣
 *
 */
public class FtpStorage implements StorageInterface {
	public FTPUtil ftpUtil;
	public String directory;	//FTP的默认目录，比如root账号，默认操作的目录是在 /root/  严格注意格式，前后有 /
	
	/**
	 * 文件上传-ftp。传入的map的key 有：
	 * 	<ul>
	 *  	<li>host: 主机，格式如 127.0.0.1</li>
	 *  	<li>username: 用户名，如 root</li>
	 *  	<li>password: 密码</li>
	 * 		<li>port: 端口号，如 22 ，如果不传默认就是22</li>
	 *  	<li>directory:  操作的目录，比如 /root/ 如果不传入，默认是 / 也就是根目录。但要注意该账号要对此目录有读写权限！貌似除了root账号能有根目录权限外，其他别的账号可能操作不了根目录 </li>
	 *  </ul>
	 */
	public FtpStorage(Map<String, String> map) {
		String host = map.get("host");
		String username = map.get("username");
		String password = map.get("password");
		String port = map.get("port");
		this.directory = map.get("directory");
		if(this.directory == null || this.directory.length() == 0 || this.directory.equalsIgnoreCase("null")) {
			Log.info("提示 directory参数未设置，这也是FTP的默认目录，比如root账号，默认操作的目录是在 /root/  严格注意格式，前后有 /");
			this.directory = "/";
		}
		
		if(host != null && host.length() > 0) {
			this.ftpUtil = new FTPUtil(host, Lang.stringToInt(port, 21), username, password);
		}
	}
	
	public BaseVO openConnectCheck(){
		int num = 0;	//尝试链接次数
		while(!this.ftpUtil.ftpClient.isConnected() && num++ < 3) {
			//如果未建立链接，那么要打开链接
			if (this.ftpUtil.connect(this.ftpUtil.hostname, this.ftpUtil.port, this.ftpUtil.username, this.ftpUtil.password)) {
				//打开成功
				return BaseVO.success();
			}else {
				//Log.debug("FTP未能打开链接。当前第"+num+"次尝试");
			}
		}
		if(!this.ftpUtil.ftpClient.isConnected()) {
			return BaseVO.failure("FTP未能打开连接");
		}else {
			return BaseVO.success();
		}
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
		try {
			Log.info("this.directory:"+this.directory+", pathBean.getPath():"+pathBean.getPath()+", this.ftpUtil.currentPath:"+this.ftpUtil.currentPath+", this.ftpUtil.ftpClient.printWorkingDirectory()："+this.ftpUtil.ftpClient.printWorkingDirectory());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//		boolean b = this.ftpUtil.upload(this.directory + pathBean.getPath(), inputStream, pathBean.getFileName());
		
		String ftpPath = this.directory + pathBean.getPath();	//ftp中要操作的目录
		if(!this.ftpUtil.currentPath.equals(ftpPath)) {
			//跟上次不在一个目录下，要先进入这个目录
			try {
				//这个会出现目录重复问题
				//String serverAbsPath = this.ftpUtil.ftpClient.printWorkingDirectory()+ftpPath;
				String serverAbsPath = ftpPath;
				serverAbsPath = serverAbsPath.replaceAll("//", "/");	//去掉 //
				
				// 使用 listDirectories 方法获取目录列表
				FTPFile[] ftpFiles = this.ftpUtil.ftpClient.listDirectories(serverAbsPath);

	            // 如果目录列表不为空，说明文件夹存在
	            if (ftpFiles != null && ftpFiles.length > 0) {
	               //文件夹存在
	            }else {
	            	//不存在，创建文件夹
	            	
	            	String[] directoryNames = serverAbsPath.split("/");
	            	String currentPath = "/";
	                for (String directoryName : directoryNames) {
	                	if(directoryName.length() < 1) {
	                		continue;
	                	}
	                	currentPath = currentPath + directoryName+"/";
	                	//判断目录如果不存在，则创建，存在，则进入
	                    if (!this.ftpUtil.ftpClient.changeWorkingDirectory(currentPath)) {
//	                    	this.ftpUtil.ftpClient.makeDirectory(directoryName);
	                    	if (!this.ftpUtil.ftpClient.makeDirectory(currentPath)) {
	    		                //System.out.println("FTP 文件夹创建失败 ： "+serverAbsPath);
	    		                vo.setBaseVO(BaseVO.FAILURE, "FTP 文件夹创建失败 ： "+currentPath);
	    						return vo;
	    					}
	                    }
	                }
	                
					if (!this.ftpUtil.ftpClient.changeWorkingDirectory(serverAbsPath)) {
		                //System.out.println("FTP 文件夹创建失败 ： "+serverAbsPath);
		                vo.setBaseVO(BaseVO.FAILURE, "FTP 文件夹进入失败 ： "+serverAbsPath);
						return vo;
					}
	            }
				
				if (!this.ftpUtil.ftpClient.changeWorkingDirectory(serverAbsPath)) {
					Log.debug("FTP目录 "+ftpPath+" 进入返回false。可能是服务器权限有什么限定，多数情况下不影响文件上传。当然最终还要看文件是否传上去了。服务器中实际目录："+serverAbsPath);
					vo.setBaseVO(BaseVO.FAILURE, "FTP目录 "+ftpPath+" 进入返回false。可能是服务器权限有什么限定，多数情况下不影响文件上传。当然最终还要看文件是否传上去了。服务器中实际目录："+serverAbsPath);
					return vo;
				} else {
					Log.debug("进入FTP目录："+ftpPath+", 服务器中实际目录："+serverAbsPath);
				}

				this.ftpUtil.currentPath = ftpPath;
			} catch (IOException e) {
				e.printStackTrace();
				vo.setBaseVO(BaseVO.FAILURE, e.getMessage());
				return vo;
			}
		}

		//上传
		try {
			if (this.ftpUtil.ftpClient.storeFile(pathBean.getFileName(), inputStream)) {
				vo.setBaseVO(BaseVO.SUCCESS, "success");
			}else {
				vo.setBaseVO(BaseVO.FAILURE, "上传失败");
			}
		} catch (IOException e) {
			vo.setBaseVO(BaseVO.FAILURE, e.getMessage());
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
		
		
		Log.info("待实现。。。");
		return BaseVO.failure("待实现..");
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
		vo.setName("FTP");
		vo.setDescription("将文件存储到指定的FTP空间中");
		vo.getParamList().add(new Param("host", "主机", "格式如 127.0.0.1", true, ""));
		vo.getParamList().add(new Param("username", "用户名", "用户名，如 root", true, ""));
		vo.getParamList().add(new Param("password", "密码", "登陆密码，如果未设置密码可留空", false, ""));
		vo.getParamList().add(new Param("port", "端口号", "如 21", false, "21"));
		vo.getParamList().add(new Param("directory", "上传目录", "操作的目录，比如 /root/  如果不传入，默认是 / 也就是根目录。但要注意该账号要对此目录有读写权限！貌似除了root账号能有根目录权限外，其他别的账号可能操作不了根目录", false, "/"));
		
		return vo;
	}
}
