package cn.zvo.fileupload.config;

import cn.zvo.fileupload.config.vo.ConfigVO;


public class ConfigDemo {
	public static void main(String[] args) {
		
		ConfigVO vo = Config.getAllStorage();
		System.out.println(vo);
	}
}
