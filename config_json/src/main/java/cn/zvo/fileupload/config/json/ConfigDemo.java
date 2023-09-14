package cn.zvo.fileupload.config.json;

import cn.zvo.fileupload.config.json.vo.ConfigVO;


public class ConfigDemo {
	public static void main(String[] args) {
		
		ConfigVO vo = Config.getAllStorage();
		System.out.println(vo);
	}
}
