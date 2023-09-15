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
		
		if(fileupload.config.paramTemplate == null){
			fileupload.config.paramTemplate = document.getElementById("storage_param").innerHTML
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
	},
	
	/*快速使用*/
	quick:{
		//显示出来的html页面内容
		html: `
			<style>
				/* input输入框前面的文字 */
				.storage_label{ width:5rem; display: inline-block; }
				/* input输入框所在的div */
				#storage_param .storage_param_input_div{  padding-top: 12px; }
				/* input输入框 */
				#storage_param .storage_param_input_div input{ }
				/* input是否必填 - 非必填 */
				#storage_param div .storage_param_false{ display:none; }
				/* input输入框下面跟随的填写说明 */
				#storage_param .storage_param_info{ margin-left: 85px;color: gray;font-size: 10px;}
				/* input是否必填 - 必填 */
				.storage_param_true{ color:red; }
				/* 提交按钮所在的div */
				.storage_submit_div{ text-align: center; }
				/* 提交按钮 */
				.storage_submit_div button{ padding: 5px; padding-left: 15px; padding-right: 15px; }
			</style>
			<div id="from">
				<div>
					<label class="storage_label">存储方式：</label>
					<select id="storageSelect" name="storage_select" onchange="fileupload.config.renderStorageParam(this.value);">
						<!-- 这里面的值是动态赋予的,这里先写死模拟 storageList.id -->
						<option value="cn.zvo.fileupload.storage.local.LocalStorage">本地存储</option>
						<option value="cn.zvo.fileupload.storage.sftp.SftpStorage">SFTP</option>
					</select>
				</div>
				
				<div id="storage_param">
					<div class="storage_param_input_div">
						<label class="storage_label" title="{description}">{name}:</label>
						<input type="text" name="{id}" value="{defaultValue}" placeholder="" title="{description}" class="storage_param_{id}" />
						<span class="storage_param_{require}">*</span>
					</div>
					<div class="storage_param_info">{description}</div>
				</div>
				
				<div class="storage_submit_div"><button onclick="fileupload.config.quick.submit();">提交</button></div>
			</div>
		`,
		//提交按钮
		submit:function(){
			var data = from.getJsonData('from');
			console.log(JSON.stringify(data, null, 4));
			alert(data);
		},
		//加载
		load:function(){
			//接口请求，获取 FileUpload 的所有配置参数
			request.get('http://leimingyun.e3.luyouxia.net:14751/config.json',{}, function(data){
				//初始化配置信息
				
				msg.popups({
					text:fileupload.config.quick.html,
					width: '360px',
					top:'26%'
				});
				fileupload.config.initConfigData(data);
				
				//这里默认让它显示第一个存储方式
				var firstStorageId = data.storageList[0].id;
				fileupload.config.renderStorageParam(firstStorageId);
			});
		}
	}
}