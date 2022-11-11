package cn.zvo.fileupload;

import java.io.InputStream;
import java.util.List;
import com.xnx3.BaseVO;
import cn.zvo.fileupload.bean.SubFileBean;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * 存储接口。比如阿里云、华为云、服务器本地存储，都要实现这个接口
 * @author 管雷鸣
 *
 */
public interface StorageInterface {

	/**
	 * 上传文件。上传后的文件名固定
	 * @param path 上传到哪里，包含上传后的文件名，如"image/head/123.jpg"
	 * @param inputStream 文件
	 * @return {@link UploadFileVO}
	 */
	public UploadFileVO uploadFile(String path,InputStream inputStream);
	
	/**
	 * 传入一个路径，得到其 {@link InputStream} 
	 * <br/> 注意，像是一些稍大的文件，比如几百MB的，不建议通过此方式取
	 * @param path 要获取的文本内容的路径，如  site/123/index.html
	 * @return 返回文件数据。若找不到，或出错，则返回 null
	 */
	public InputStream getFile(String path);
	
	/**
	 * 删除文件
	 * @param filePath 文件所在的路径，如 "jar/file/xnx3.jpg"
	 * @return 执行成功，则 {@link BaseVO#getResult()} 为 {@link BaseVO#SUCCESS} 。注意，如果删除文件时，文件不存在，那接口实现时也要返回成功，因为使用者执行了这个方法后，最终结果是文件确实没了
	 */
	public BaseVO deleteFile(String filePath);
	
	/**
	 * 获取某个目录（文件夹）占用空间的大小。如果你本身项目中用不到，这里可以直接返回个0，无需具体实现
	 * @param path 要计算的目录(文件夹)，如 jar/file/
	 * @return 计算出来的大小。单位：字节，B。  ( 1000B = 1KB )
	 */
	public long getDirectorySize(String path);
	
	/**
	 * 复制文件 。如果你本身项目中用不到，这里可以无需具体实现
	 * @param originalFilePath 原本文件所在的路径(相对路径，非绝对路径，操作的是当前附件文件目录下)
	 * @param newFilePath 复制的文件所在的路径，所放的路径。(相对路径，非绝对路径，操作的是当前附件文件目录下)
	 */
	public void copyFile(String originalFilePath, String newFilePath);
	
	/**
	 * 获取某个目录下的子文件列表。获取的只是目录下第一级的子文件，并非是在这个目录下无论目录深度是多少都列出来。
	 * <br/>。如果你本身项目中用不到，这里可以直接返回个0，无需具体实现
	 * @param path 要获取的是哪个目录的子文件。传入如 site/219/
	 * @return 该目录下一级子文件（如果有文件夹，也包含文件夹）列表。如果size为0，则是没有子文件或文件夹。无论什么情况不会反null. 另外SubFileBean.path 返回的是文件名，不能带路径。返回的比如是 a.jpg ,不能返回 images/a.jpg (云存储中返回的是这样的，需要过滤) 
	 */
	public List<SubFileBean> getSubFileList(String path);
	
	/**
	 * 获取某个文件的大小，这个是文件，如果传入文件夹，是不起作用的，会返回-1，文件未发现。
	 * <br/>。如果你本身项目中用不到，这里可以直接返回个0，无需具体实现
	 * @param path 要获取的是哪个文件。传入如 site/219/1.html
	 * @return 单位是 B， * 1000 = KB 。 如果返回-1，则是文件未发现，文件不存在
	 */
	public long getFileSize(String path);
	
	/**
	 * 创建文件夹
	 * @param path 要创建的文件路径，传入如 site/219/test/ 则是创建 test 文件夹
	 */
	public BaseVO createFolder(String path);
}
