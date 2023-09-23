package cn.zvo.fileupload.storage.ftp;

import org.apache.commons.net.ftp.FTPClient;

import com.xnx3.BaseVO;
import com.xnx3.FileUtil;
import com.xnx3.Log;
import com.xnx3.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
/**
 * FTP工具类
 * 需要使用  commons-net v3.3
* @author 周瑞宝,管雷鸣整理
* @date 2022/07/09
*/
public class FTPUtil {
	FTPClient ftpClient;
	public String hostname;	//ip或域名地址
	public int port = 21;		//端口默认21
	public String username;	//用户名
	public String password;	//密码
	
	public String currentPath;	//当前所在的FTP的目录，这个目录并不是实际的在服务器的目录，而是upload 中传入的filePath
	
	/**
	* 
	*/
	public FTPUtil(String hostname, int port, String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		
		ftpClient = new FTPClient();
		currentPath = "-1";
	}

	/**
	 * 上传
	 * @param filePath 要将文件上传到ftp的哪个路径下,传入格式如 /web/root/ 
	 * @param inputStream 要上传文件的输入流
	 * @param fileName	设置上传之后的文件名 传入如:  a.html
	 * @return
	 */
//	public BaseVO upload(String filePath, InputStream inputStream, String fileName) {
//		
//	}
//	
//	public boolean upload(String filePath, String text, String fileName) {
//		InputStream fileInputStream = null;
//		try {
//			fileInputStream = StringUtil.stringToInputStream(text, FileUtil.UTF8);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			Log.debug("stringToInputStream error.");
//			return false;
//		}
//		return upload(filePath, fileInputStream, fileName);
//	 }
	/**
	 * 断开连接
	 *
	 * @param ftpClient
	 * @throws Exception
	 */
	public static void disconnect(FTPClient ftpClient) {
		if (ftpClient.isConnected()) {
			try {
				ftpClient.disconnect();
				Log.info("已关闭连接");
			} catch (IOException e) {
				Log.info("没有关闭连接");
				e.printStackTrace();
			}
		}
	}
	/**
	 * 测试是否能连接
	 *
	 * @param ftpClient
	 * @param hostname  ip或域名地址
	 * @param port	  端口
	 * @param username  用户名
	 * @param password  密码
	 * @return 返回真则能连接
	 */
	public boolean connect(String hostname, int port, String username, String password) {
		boolean flag = false;
		try {
			//ftp初始化的一些参数
			ftpClient.connect(hostname, port);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftpClient.setControlEncoding("UTF-8");
			if (ftpClient.login(username, password)) {
				Log.info("连接ftp成功");
				//ConsoleUtil.info(JSONArray.fromObject(ftpClient.listFiles()).toString());
				flag = true;
			} else {
				Log.info("连接ftp失败，可能用户名或密码错误");
				try {
					disconnect(ftpClient);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			Log.info("连接失败，可能ip或端口错误");
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 上传文件
	 *
	 * @param ftpClient
	 * @param  path		全路径。如home/public/a.txt
	 * @param fileInputStream 要上传的文件流
	 * @return
	 */
	public static boolean storeFile(FTPClient ftpClient, String path, InputStream fileInputStream) {
		boolean flag = false;
		try {
			if (ftpClient.storeFile(path, fileInputStream)) {
				flag = true;
			}
		} catch (IOException e) {
			Log.info("上传失败");
			e.printStackTrace();
		}
		return flag;
	}
	

//	public static void main(String[] args) {
//		int port = 21;
////		String hostname = "ftp.cntd12n.99aiji.net";
////		String username = "zrb5517";
////		String password = "7YLQDG4SHMQ9";
//		
//		String hostname = "192.168.31.237";
//		String username = "test";
//		String password = "ewbHbiSMAKZmWw3L";
//		
//		FTPUtil ftpTools = new FTPUtil(hostname, port, username, password);
////		boolean result = ftpTools.upload("/web/root/", "12345", "demo.html");
//		boolean a = ftpTools.upload("/www/wwwroot/test/", "12345", "ha21.html");
//		System.out.println(a);
//	}
	
	
}
