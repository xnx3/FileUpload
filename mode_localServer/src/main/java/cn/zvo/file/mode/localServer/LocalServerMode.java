package cn.zvo.file.mode.localServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import com.xnx3.BaseVO;
import com.xnx3.FileUtil;
import com.xnx3.Log;
import com.xnx3.StringUtil;
import cn.zvo.file.StorageModeInterface;
import cn.zvo.file.bean.SubFileBean;
import cn.zvo.file.vo.UploadFileVO;

/**
 * 附件上传之 服务器本身存储，服务器本地存储，附件存储到服务器硬盘上
 * @author 管雷鸣
 *
 */
public class LocalServerMode implements StorageModeInterface{
	
	//附件保存在当前服务器上，保存的路径是哪个? 如果不设置，默认是保存到当前项目的根路径下
	private String localFilePath;
	
	public String getLocalFilePath() {
		if(localFilePath == null) {
			String path = new LocalServerMode().getClass().getResource("/").getPath();
			localFilePath = path.replace("WEB-INF/classes/", "");
		}
		return localFilePath;
	}
	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	@Override
	public UploadFileVO uploadFile(String path, InputStream inputStream) {
		UploadFileVO vo = new UploadFileVO();
		
		directoryInit(path);
		File file = new File(this.getLocalFilePath()+path);
		OutputStream os;
		try {
			os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			inputStream.close();
			
			vo.setFileName(file.getName());
			vo.setInfo("success");
			vo.setPath(path);
		} catch (IOException e) {
			vo.setBaseVO(BaseVO.FAILURE, e.getMessage());
			e.printStackTrace();
		}
		
		return vo;
	}

	@Override
	public BaseVO deleteObject(String filePath) {
		try {
			FileUtil.deleteFile(this.getLocalFilePath()+filePath);
			return BaseVO.success();
		} catch (Exception e) {
			e.printStackTrace();
			return BaseVO.failure(e.getMessage());
		}
	}

	@Override
	public long getDirectorySize(String path) {
		directoryInit(path);
		return sizeOfDirectory(new File(this.getLocalFilePath()+path));
	}

	@Override
	public void copyObject(String originalFilePath, String newFilePath) {
		directoryInit(newFilePath);
		FileUtil.copyFile(this.getLocalFilePath() + originalFilePath, this.getLocalFilePath() + newFilePath);
	}
	
	
	/**
	 * 目录检测，检测是否存在。若不存在，则自动创建目录。适用于使用本地磁盘进行存储，在本身tomcat中创建目录.有一下两种情况:
	 * <ul>
	 * 		<li>在线上的tomcat项目中，创建的目录是在 tomcat/webapps/ROOT/ 目录下</li>
	 * 		<li>在开发环境Eclipse中，创建的目录是在 target/classes/ 目录下</li>
	 * </ul>
	 * @param path 要检测的目录，相对路径，如 jar/file/  创建到file文件，末尾一定加/     或者jar/file/a.jar创建到file文件夹
	 */
	public void directoryInit(String path){
		if(path == null){
			return;
		}
		
		//windows取的路径是\，所以要将\替换为/
		if(path.indexOf("\\") > 1){
			path = StringUtil.replaceAll(path, "\\\\", "/");
		}
		
		if(path.length() - path.lastIndexOf("/") > 1){
			//path最后是带了具体文件名的，把具体文件名过滤掉，只留文件/结尾
			path = path.substring(0, path.lastIndexOf("/")+1);
		}
		
		//如果目录或文件不存在，再进行创建目录的判断
		if(!FileUtil.exists(path)){
			String[] ps = path.split("/");
			
			String xiangdui = "";
			//length-1，/最后面应该就是文件名了，所以要忽略最后一个
			for (int i = 0; i < ps.length; i++) {
				if(ps[i].length() > 0){
					xiangdui = xiangdui + ps[i]+"/";
					if(!FileUtil.exists(this.getLocalFilePath()+xiangdui)){
						File file = new File(this.getLocalFilePath()+xiangdui);
						file.mkdir();
					}
				}
			}
		}
	}

	@Override
	public List<SubFileBean> getSubFileList(String path) {
		List<SubFileBean> list = new ArrayList<SubFileBean>();
		if(path == null || path.length() == 0){
			return list;
		}
		
		File file = new File(this.getLocalFilePath()+path);
		if(!file.exists()){
			//文件夹不存在，也返回空
			return list;
		}
		
		File[] subFiles = file.listFiles();
		for (int i = 0; i < subFiles.length; i++) {
			File subFile = subFiles[i];
			SubFileBean bean = new SubFileBean();
			bean.setPath(subFile.getPath().replace(this.localFilePath+path, ""));
			bean.setSize(subFile.length());
			bean.setLastModified(subFile.lastModified());
			bean.setFolder(subFile.isDirectory());
			list.add(bean);
		}
		
		return list;
	}

	@Override
	public long getFileSize(String path) {
		File file = new File(this.getLocalFilePath()+path);
		if(!file.exists()){
			//文件不存在
			return -1;
		}
		return file.length();
	}

	@Override
	public BaseVO createFolder(String path) {
		directoryInit(path);
		return BaseVO.success();
	}
	
	@Override
	public InputStream getFile(String path) {
		File file = new File(this.getLocalFilePath()+path);
		if(!file.exists()) {
			return null;
		}
		
		try {
			InputStream in = new FileInputStream(file);
			return in;
		} catch (FileNotFoundException e) {
			Log.debug(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**** 下面的复制与 common-fileupload ，因为用的不多，就不额外多引入jar包了 ****/

    /**
     * Counts the size of a directory recursively (sum of the length of all files).
     * 
     * @param directory
     *            directory to inspect, must not be {@code null}
     * @return size of directory in bytes, 0 if directory is security restricted, a negative number when the real total
     *         is greater than {@link Long#MAX_VALUE}.
     * @throws NullPointerException
     *             if the directory is {@code null}
     */
    public static long sizeOfDirectory(File directory) {
        checkDirectory(directory);

        final File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            return 0L;
        }
        long size = 0;

        for (final File file : files) {
            try {
                if (!isSymlink(file)) {
                    size += sizeOf(file);
                    if (size < 0) {
                        break;
                    }
                }
            } catch (IOException ioe) {
                // Ignore exceptions caught when asking if a File is a symlink.
            }
        }

        return size;
    }


    /**
     * Checks that the given {@code File} exists and is a directory.
     * 
     * @param directory The {@code File} to check.
     * @throws IllegalArgumentException if the given {@code File} does not exist or is not a directory.
     */
    private static void checkDirectory(File directory) {
        if (!directory.exists()) {
            throw new IllegalArgumentException(directory + " does not exist");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " is not a directory");
        }
    }

    /**
     * Determines whether the specified file is a Symbolic Link rather than an actual file.
     * <p>
     * Will not return true if there is a Symbolic Link anywhere in the path,
     * only if the specific file is.
     * <p>
     * <b>Note:</b> the current implementation always returns {@code false} if the system
     * is detected as Windows using {@link FilenameUtils#isSystemWindows()}
     * 
     * @param file the file to check
     * @return true if the file is a Symbolic Link
     * @throws IOException if an IO error occurs while checking the file
     * @since 2.0
     */
    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        
        /**
         * The Windows separator character.
         */
        char WINDOWS_SEPARATOR = '\\';
        /**
         * The system separator character.
         */
        char SYSTEM_SEPARATOR = File.separatorChar;
        if (SYSTEM_SEPARATOR == WINDOWS_SEPARATOR) {
            return false;
        }
        
        File fileInCanonicalDir = null;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }
        
        if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
            return false;
        } else {
            return true;
        }
    }


    //-----------------------------------------------------------------------
    /**
     * Returns the size of the specified file or directory. If the provided 
     * {@link File} is a regular file, then the file's length is returned.
     * If the argument is a directory, then the size of the directory is
     * calculated recursively. If a directory or subdirectory is security 
     * restricted, its size will not be included.
     * 
     * @param file the regular file or directory to return the size 
     *        of (must not be {@code null}).
     * 
     * @return the length of the file, or recursive size of the directory, 
     *         provided (in bytes).
     * 
     * @throws NullPointerException if the file is {@code null}
     * @throws IllegalArgumentException if the file does not exist.
     *         
     * @since 2.0
     */
    public static long sizeOf(File file) {

        if (!file.exists()) {
            String message = file + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (file.isDirectory()) {
            return sizeOfDirectory(file);
        } else {
            return file.length();
        }

    }

}
