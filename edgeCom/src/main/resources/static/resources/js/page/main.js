var mainPage=(function($){
	
	var popupZindex=1;
	var spinner=null;
	var pageLoad=function(){
//		nav.setTitle("엣지 대쉬보드");
		nav.hideBack();
		
		myAjax.selectEdgeList(function(res){
			nav.setTitle("엣지 대쉬보드("+res.data[0].edgeName+")");
		});
		
		if(!$("#p_value").attr("role")){
			spinner = $("#p_value").spinner();
			$("#p_value").change(function(){
				console.log($(this).val());
				myAjax.insertPvalue($(this).val(),function(res){});
			});
			$('.ui-spinner-button').click(function() {
			   $(this).siblings('input').change();
			});
		}
		
		
		if($("#b_value").html()==""){
			$("#b_value").append("<p class='on' style='margin: 0;text-align: center;color: #fff;font-weight: bold;'>ON</p><p class='off' style='display: none;margin: 0;text-align: center;color: #fff;font-weight: bold;'>OFF</p>");
			$("#b_value").css("cursor","pointer");
			$("#b_value").click(function(){
				var on =$(this).find(".on");
				var off =$(this).find(".off");
				var value=0;
				if(on.is(":visible")){
					off.show();
					on.hide();
					$(this).css("background","#f00");
				}else{
					off.hide();
					on.show();
					$(this).css("background","#4dac3c");
					value=1;
				}
				myAjax.insertBvalue(value,function(res){});
			});
			$("#b_value").click();
		}
		
		
		
		myAjax.selectPlcList(function(res){
			if(res.result){
				if(res.data.ctrlYn=='Y')
					$("div.bvalve").show();
				if(res.data.ctrlYn=='Y')
					$("div.pvalve").show();
				
				$("#p_value").val(res.data.pvalve);
				
				if(res.data.bvalve==0){
					$("#b_value").find(".on").hide();
					$("#b_value").find(".off").show();
					$("#b_value").css("background","#f00");
				}else{
					$("#b_value").find(".on").show();
					$("#b_value").find(".off").hide();
					$("#b_value").css("background","#4dac3c");
				}
				
				if(res.data.status==0){
					$("div.box.status2").text("Normal");
					$("div.box.status2").css("background","black");
				}
				else if(res.data.status==1){
					$("div.box.status2").text("Warning");
					$("div.box.status2").css("background","#ffbc00");
				}
				else if(res.data.status==2){
					$("div.box.status2").text("Danger");
					$("div.box.status2").css("background","#ff0000");
				}
				
				var code={};
				for(var e in res.data.code){
					code[res.data.code[e].columnCode.toLowerCase()]=res.data.code[e].valueName
				}
				var keyMap=Object.keys(res.data.map);
				var div="";
				for(var key in keyMap){
					if(keyMap[key]=="milisecond" || keyMap[key]=="regdate")
						continue;
					div += "<div onclick='mainPage.openPopup(this)' style='display: inline-block;' columncode="+keyMap[key]+">"+
					"<span>"+code[keyMap[key]]+"</span>"+
					"<div class='box'>"+res.data.map[keyMap[key]]+"</div>"+
					"</div>";
					
				}
				$(".status").html(div);
				
			}
			$(".box").click(function(){
			});
		});
		google.charts.load('current', {'packages':['corechart','line','gauge']});
	}
	
	var openPopup=function(event){
//		if(kind=="plc"){
			var columncode = $(event).attr("columncode");
			if($("#popup-container .popup."+columncode).length<=0){
				var popup='<div class="popup '+columncode+'" style="display:none">'+
					'<div class="inner">'+
						'<input class="cancel" type="button" value="X"/>'+
						'<p class="title"></p>'+
						'<div class="content">'+
							'<div class="dim"><img class="loading" src="/resources/loading.svg"/> </div>'+
							'<div id="linechart" style="width: 800px; height: 300px"></div>'+
						'</div>'+
						'<div class="bottom"></div>'+
					'</div>'+
				'</div>';
				$("#popup-container").append(popup);
				$("#popup-container .popup."+columncode+" .cancel").click(function(){
					$(this).parent().parent().hide();
					var chartIntervalId = $("#popup-container .popup."+columncode).attr("interval-id");
					clearInterval(Number(chartIntervalId));
				});
			}
			
			$("#popup-container .popup."+columncode+" .title").text($(event).find("span").text());
			$("#popup-container .popup."+columncode+" .dim").show();
			$("#popup-container .popup."+columncode).show();
			$("#popup-container .popup."+columncode).draggable({      // 드래그  
		        cursor:"move",      // 드래그 시 커서모양 
		        stack:".post",      // .post 클래스끼리의 스택 기능 
		        opacity:0.8         // 드래그 시 투명도 
		    });
			$("#popup-container .popup."+columncode).on("mousedown",function(){
				$(this).css("z-index",popupZindex++);
			});
			
			var chartIntervalId = setInterval(function(){
				$.ajax({
				    url: "/plcHistory?columnCode="+columncode,
				    type: "GET", 
				    dataType: "json"
				})
				// HTTP 요청이 성공하면 요청한 데이터가 done() 메소드로 전달됨.
				.done(function(json) {
					if(json.result){
						console.log(json.data);
						var dataArray=[
							["일자","값"]
						];
						for(var i in json.data){
							var data = json.data[i].date.split("-");
							dataArray.push([new Date(data[0],data[1],data[2],json.data[i].hour,json.data[i].minute,json.data[i].second),json.data[i].value])
						}
						initPopupChart(dataArray,columncode);
					}
				});
			},1000)
			$("#popup-container .popup."+columncode).attr("interval-id",chartIntervalId);
			
//		}
//		else if(kind=="rcu"){
//		}
		
	}
	var initPopupChart=function(dataArray,columncode){
		$("#popup-container .popup."+columncode+" .dim").hide();
		var options2 = {
				legend: {position: 'top'},
				vAxis: {	
					textStyle:{
						color:"#757575",
						fontSize:"12"
					}
				},
				hAxis: { 
					textStyle:{
						color:"#757575",
						fontSize:"12"
					}
//					format: 'yyyy-MM-dd HH시',
				},
				width: 800,
				height: 300
			};
			console.log(dataArray);
			var data2 = google.visualization.arrayToDataTable(dataArray);
			var chart2 = new google.visualization.LineChart($("#popup-container .popup."+columncode+" #linechart")[0]);
			chart2.draw(data2, options2);
	}
	
	setInterval(function(){
		if(location.hash.substring(1, location.hash.length).replace(/ /gi, '%20').indexOf("history")>=0){
			return;
		}
		pageLoad();
	},1000);
	return {
		pageLoad:pageLoad,
		openPopup:openPopup
	}
})($);