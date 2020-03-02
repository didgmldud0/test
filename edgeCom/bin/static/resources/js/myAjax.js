var myAjax=(function($){
	
	var commonAjax=function(option){
		$.ajax(option)
		// HTTP 요청이 성공하면 요청한 데이터가 done() 메소드로 전달됨.
		.done(function(json) {
			option.callback(json);
		})
		// HTTP 요청이 실패하면 오류와 상태에 관한 정보가 fail() 메소드로 전달됨.
		.fail(function(xhr, status, errorThrown) {
		})
		// HTTP 요청이 성공하거나 실패하는 것에 상관없이 언제나 always() 메소드가 실행됨.
		.always(function(xhr, status) {
		});
	}
	
	// 회원가입
	var ajaxJoin=function(data,callback){
		return $.ajax({
		    url: "/api.register_frm.php",
		    data: data,
		    type: "POST",
			dataType: "json"
		});
	}
	
	return {
		ajaxJoin:ajaxJoin
	}
})($);