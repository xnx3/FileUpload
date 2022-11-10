package cn.zvo.file.framework.springboot;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.xnx3.Lang;
import cn.zvo.file.vo.UploadFileVO;

/**
 * 文件、图片、附件的上传等操作
 * @author 管雷鸣
 */
public class FileUtil extends cn.zvo.file.FileUtil {

	/**
	 * SpringMVC 上传文件，配置允许上传的文件后缀再 systemConfig.xml 的attachmentFile.allowUploadSuffix.suffix节点
	 * @param filePath 上传后的文件所在目录、路径，如 "jar/file/"
	 * @param multipartFile SpringMVC接收的 {@link MultipartFile},若是有上传文件，会自动转化为{@link MultipartFile}保存
	 * @return {@link UploadFileVO} 若成功，则上传了文件并且上传成功
	 */
	public UploadFileVO uploadFile(String filePath, MultipartFile multipartFile) {
		UploadFileVO vo = new UploadFileVO();
		
		if(multipartFile == null){
			vo.setBaseVO(UploadFileVO.FAILURE, "请选择要上传的文件");
			return vo;
		}
		
		InputStream inputStream = null;
		try {
			inputStream = multipartFile.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(inputStream == null){
			vo.setBaseVO(UploadFileVO.FAILURE, "请选择要上传的文件");
			return vo;
		}
		
		//获取上传的文件的后缀
		String fileSuffix = null;
		fileSuffix = Lang.findFileSuffix(multipartFile.getOriginalFilename());
		
		if(!allowUploadSuffix(fileSuffix)){
			vo.setBaseVO(UploadFileVO.FAILURE, "该后缀文件不允许上传");
			return vo;
		}
		
		vo = uploadFile(filePath, inputStream, fileSuffix);
		return vo;
	}
	
	/**
	 * SpringMVC 上传图片文件，配置允许上传的文件后缀再 systemConfig.xml 的attachmentFile.allowUploadSuffix.suffix节点
	 * @param filePath 上传后的文件所在目录、路径，如 "jar/file/"
	 * @param multipartFile SpringMVC接收的 {@link MultipartFile},若是有上传图片文件，会自动转化为{@link MultipartFile}保存
	 * @param maxWidth 上传图片的最大宽度，若超过这个宽度，会对图片进行等比缩放为当前宽度。若传入0.则不启用此功能
	 * @return {@link UploadFileVO} 若成功，则上传了文件并且上传成功
	 */
	public UploadFileVO uploadImageByMultipartFile(String filePath, MultipartFile multipartFile, int maxWidth) {
		UploadFileVO vo = new UploadFileVO();
		
		if(multipartFile == null){
			vo.setBaseVO(UploadFileVO.FAILURE, "请选择要上传的文件");
			return vo;
		}
		
		InputStream inputStream = null;
		try {
			inputStream = multipartFile.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(inputStream == null){
			vo.setBaseVO(UploadFileVO.FAILURE, "请选择要上传的文件");
			return vo;
		}
		
		//获取上传的文件的后缀
		String fileSuffix = null;
		fileSuffix = Lang.findFileSuffix(multipartFile.getOriginalFilename());
		
		if(!allowUploadSuffix(fileSuffix)){
			vo.setBaseVO(UploadFileVO.FAILURE, "该文件后缀不允许上传");
			return vo;
		}
		
		vo = uploadImage(filePath, inputStream, fileSuffix, maxWidth);
		return vo;
	}
	
	/**
	 * SpringMVC 上传图片文件，配置允许上传的文件后缀再 systemConfig.xml 的attachmentFile.allowUploadSuffix.suffix节点
	 * @param filePath 上传后的文件所在目录、路径，如 "jar/file/"
	 * @param multipartFile SpringMVC接收的 {@link MultipartFile},若是有上传图片文件，会自动转化为{@link MultipartFile}保存
	 * @return {@link UploadFileVO} 若成功，则上传了文件并且上传成功
	 */
	public UploadFileVO uploadImageByMultipartFile(String filePath, MultipartFile multipartFile) {
		return uploadImageByMultipartFile(filePath, multipartFile, 0);
	}
	
	
	/**
	 * SpringMVC 上传图片文件，配置允许上传的文件后缀再 systemConfig.xml 的AttachmentFile节点
	 * @param filePath 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param request SpringMVC接收的 {@link MultipartFile},若是有上传图片文件，会自动转化为{@link MultipartFile}保存
	 * @param formFileName form表单上传的单个图片文件，表单里上传文件的文件名
	 * @param maxWidth 上传图片的最大宽度，若超过这个宽度，会对图片进行等比缩放为当前宽度。
	 * @return {@link UploadFileVO} 若成功，则上传了文件并且上传成功
	 */
	public UploadFileVO uploadImage(String filePath,HttpServletRequest request,String formFileName, int maxWidth) {
		UploadFileVO uploadFileVO = new UploadFileVO();
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			List<MultipartFile> imageList = multipartRequest.getFiles(formFileName);
			if(imageList.size()>0 && !imageList.get(0).isEmpty()){
				MultipartFile multi = imageList.get(0);
				uploadFileVO = uploadImageByMultipartFile(filePath, multi, maxWidth);
			}else{
				uploadFileVO.setResult(UploadFileVO.NOTFILE);
				uploadFileVO.setInfo("请选择要上传的文件");
			}
		}else{
			uploadFileVO.setResult(UploadFileVO.NOTFILE);
			uploadFileVO.setInfo("请选择要上传的文件");
		}
		return uploadFileVO;
	}
	
	/**
	 * SpringMVC 上传文件，配置允许上传的文件后缀再 systemConfig.xml 的AttachmentFile节点
	 * @param filePath 上传后的文件所在的目录、路径，如 "jar/file/"
	 * @param request SpringMVC接收的 {@link MultipartFile},若是有上传文件，会自动转化为{@link MultipartFile}保存
	 * @param formFileName form表单上传的单个文件，表单里上传文件的文件名
	 * @return {@link UploadFileVO} 若成功，则上传了文件并且上传成功
	 */
	public UploadFileVO uploadFile(String filePath,HttpServletRequest request,String formFileName) {
		UploadFileVO uploadFileVO = new UploadFileVO();
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			List<MultipartFile> list = multipartRequest.getFiles(formFileName);
			if(list.size()>0 && !list.get(0).isEmpty()){
				MultipartFile multi = list.get(0);
				uploadFileVO = uploadFile(filePath, multi);
			}else{
				uploadFileVO.setResult(UploadFileVO.NOTFILE);
				uploadFileVO.setInfo("请选择要上传的文件");
			}
		}else{
			uploadFileVO.setResult(UploadFileVO.NOTFILE);
			uploadFileVO.setInfo("请选择要上传的文件");
		}
		return uploadFileVO;
	}
	
}
