var codeAjax=(function($){
	
	var ainame={};
	
	$.ajax({url: "/selectAiName",data: {},type: "GET",dataType: "json"})
	.done(function(json) {
		if(json.result){
			for(var i in json.data){
				ainame[json.data[i].columnCode.toLowerCase()]=json.data[i].valueName;
			}
		}
	})
	
	var getAiName=function(code){
		return ainame[code.toLowerCase()];
	}
	
	return {
		getAiName:getAiName
	}
})($);