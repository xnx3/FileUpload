if(typeof(fileupload) == 'undefined'){
	fileupload = {};
}
fileupload.config = {
	paramTemplate:null,
	templateReplace:function(item){
		var paramHtml = fileupload.config.paramTemplate;
		return paramHtml.replace(/\{id\}/g, item.id)
					.replace(/\{name\}/g, item.name)
					.replace(/\{value\}/g, item.value)
					.replace(/\{require\}/g, item.require)
					.replace(/\{description\}/g, item.description)
					.replace(/\{defaultValue\}/g, item.defaultValue)
					;
	},
	configData:null, //通过 config.json接口获取到的数据
	
	//初始化，赋予配置信息
	initConfigData:function(configData){
		fileupload.config.configData = configData;
		
		if(this.paramTemplate == null){
			this.paramTemplate = document.getElementById("storage_param").innerHTML
		}
	},
	
	storageSelectAddOption:function(value, text){
		var select = document.getElementById("storageSelect");
		var option = document.createElement("option");
		option.value = value;
		option.text = text;
		select.appendChild(option);
	},
	
	/**
	 * 渲染显示具体某个存储方式的输入
	 * storageId 传入存储方式，显示这个存储方式的输入方式
	 */
	renderStorageParam:function(storageId){
		if(typeof(fileupload.config.configData) == 'undefined'){
			msg.failure('请等待接口加载config配置数据');
			return;
		}
		
		//寻找 storage
		var storage;
		for(var index in fileupload.config.configData.storageList){
			if(storageId == fileupload.config.configData.storageList[index].id){
				storage = fileupload.config.configData.storageList[index];
				break;
			}
		}
		
		var html = '';
		for(var index in storage.paramList){
			html = html + fileupload.config.templateReplace(storage.paramList[index]);
		}
		document.getElementById("storage_param").innerHTML = html;
	}
	
}