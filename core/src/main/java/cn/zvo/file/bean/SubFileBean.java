package cn.zvo.file.bean;

/**
 * 子文件信息
 * @author 管雷鸣
 *
 */
public class SubFileBean {
	public String path;		//文件路径，相对路径，如 site/219/index.html
	public long size;			//文件大小，单位B
	public long lastModified;	//上次修改日期，单位是毫秒
	public boolean folder;		//是否是文件夹？如果是，则是true

	public SubFileBean() {
		size = 0;
		folder = false;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if(path == null) {
			this.path = null;
		}
		
		path = path.replaceAll("//", "/"); //obs场景下会多出一个来，所以进行减去
		this.path = path;
	}
	
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isFolder() {
		return folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	@Override
	public String toString() {
		return "SubFileBean [path=" + path + ", size=" + size + ", lastModified=" + lastModified + ", folder=" + folder
				+ "]";
	}
	
	
}
