package cn.zvo.fileupload;

import java.io.InputStream;
import java.util.List;
import com.xnx3.BaseVO;
import cn.zvo.fileupload.bean.SubFileBean;
import cn.zvo.fileupload.storage.local.LocalStorage;
import cn.zvo.fileupload.vo.StorageConfigVO;
import cn.zvo.fileupload.vo.UploadFileVO;

/**
 * 存储接口。比如阿里云、华为云、服务器本地存储，都要实现这个接口
 * @author 管雷鸣
 *
 */
public interface StorageInterface {

	/**
	 * 上传文件
	 * @param path 上传到哪里，这里是相对路径。要包含上传后的文件名，如 site/123/index.html
	 * @param inputStream 文件
	 * @return {@link UploadFileVO}
	 */
	public UploadFileVO upload(String path,InputStream inputStream);
	
	/**
	 * 通过路径，得到其文件数据 {@link InputStream} 
	 * <br/> 注意，像是一些稍大的文件，比如几百MB的，不建议通过此方式取
	 * @param path 要获取的文件的路径，如  site/123/index.html
	 * @return 返回文件数据。若找不到，或出错，则返回 null
	 */
	public InputStream get(String path);
	
	/**
	 * 删除文件
	 * @param path 要删除的文件的路径，如 site/123/index.html
	 * @return 执行成功，则 {@link BaseVO#getResult()} 为 {@link BaseVO#SUCCESS} 。注意，如果删除文件时，文件不存在，那接口实现时也要返回成功，因为使用者执行了这个方法后，最终结果是文件确实没了
	 */
	public BaseVO delete(String path);

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
	 * 获取某个文件或文件夹的大小
	 * <br/>如果你本身项目中用不到，这里可以直接返回个0，无需具体实现
	 * <br/>如果开发对接别的存储方式实现这个接口时，判断是否是目录，可以用 <pre>path.lastIndexOf("/") +1 ) == path.length()</pre>
	 * @param path 要获取的是哪个文件/文件夹。传入方式有两种：
	 * 	<ul>
	 * 		<li>如果获取的是文件夹占用的空间大小，传入的则是如 site/123/ 以/结尾，表示获取这个文件夹(目录)所占用的空间大小</li>
	 * 		<li>如果获取的是具体某个文件占用的空间大小，传入的则是如 site/219/index.html 传入的是具体文件路径</li>
	 * </ul>
	 * @return 单位是 B， * 1000 = KB 。 如果返回-1，则是文件未发现，文件/文件夹 不存在
	 */
	public long getSize(String path);
	
	/**
	 * 创建文件夹
	 * <br/>如果你本身项目中用不到，这里可以直接返回成功 {@link BaseVO#success()} 无需具体实现
	 * @param path 要创建的文件路径，传入如 site/219/ 则是创建 test 文件夹
	 */
	public BaseVO createFolder(String path);
	
	/**
	 * 实现该 {@link StorageInterface} 的存储，其需要进行哪些参数配置，比如本地存储 {@link LocalStorage} 需要配置 path 存储路径参数
	 * @return 需要进行配置的参数
	 */
	public StorageConfigVO config();
	
}
