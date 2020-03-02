var historyPage=(function($){
	
	var table;
	var tableLock=false;
	var page=0;
	var pageLimit=100;
	var searchParam={
		siteCode:"0",
		edgeCode:"0",
		sDate:"",
		eDate:"",
		deviceCode:"0",
	}
	
	var pageLoad=function(){
		nav.setTitle("엣지 로그내역");
		nav.showBack();
		
		tableLock=false;
		page=0;
		pageLimit=100;
		searchParam={
			siteCode:"0",
			edgeCode:"0",
			sDate:"",
			eDate:"",
			deviceCode:"0",
		}
		
		edgeComponent.init();
		regEvent();
		
		table = FooTable.init('.table',{
			"columns": [
				{"name":"Site","title":"Site","style":{"width":80,"maxWidth":80}},
				{"name":"Edge","title":"Edge","style":{"width":80,"maxWidth":80}},
				{"name":"deviceCode","title":"deviceCode"},
				{"name":"ALARM","title":"ALARM"},
				{"name":"TYPE","title":"TYPE","breakpoints":"xs sm","style":{"maxWidth":200,"overflow":"hidden","textOverflow":"ellipsis","wordBreak":"keep-all","whiteSpace":"nowrap"}},
				{"name":"Critical","title":"Critical"},
				{"name":"Value","title":"Value"},
				{"name":"Date","title":"Date"}
			],
			"rows": [
			]
		});
		
		table.$el.bind({
			"after.ft.paging":function(e,ft,pager){
			},
			"before.ft.paging":function(e,ft,pager){
				console.log(pager);
				loadingPage(pager);
			}
//			"postinit.ft.table":function(e,ft,pager){
//				
//			},
//			"preinit.ft.paging":function(e,ft,pager){
//			},
//			"init.ft.paging":function(e,ft,pager){
//			}
		});
		
		table.$el.find("li[data-page=last]").hide();
		loadingPage({total:0,page:0});
	}
	
	
	var regEvent=function(){
		$(".sDate").datepicker({
			dateFormat: 'yy-mm-dd' //Input Display Format 변경
            ,showOtherMonths: true //빈 공간에 현재월의 앞뒤월의 날짜를 표시
            ,showMonthAfterYear:true //년도 먼저 나오고, 뒤에 월 표시
            ,changeYear: true //콤보박스에서 년 선택 가능
            ,changeMonth: true //콤보박스에서 월 선택 가능                
//            ,showOn: "both" //button:버튼을 표시하고,버튼을 눌러야만 달력 표시 ^ both:버튼을 표시하고,버튼을 누르거나 input을 클릭하면 달력 표시  
//            ,buttonImage: "http://jqueryui.com/resources/demos/datepicker/images/calendar.gif" //버튼 이미지 경로
//            ,buttonImageOnly: true //기본 버튼의 회색 부분을 없애고, 이미지만 보이게 함
//            ,buttonText: "선택" //버튼에 마우스 갖다 댔을 때 표시되는 텍스트                
            ,yearSuffix: "년" //달력의 년도 부분 뒤에 붙는 텍스트
            ,monthNamesShort: ['1','2','3','4','5','6','7','8','9','10','11','12'] //달력의 월 부분 텍스트
            ,monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'] //달력의 월 부분 Tooltip 텍스트
            ,dayNamesMin: ['일','월','화','수','목','금','토'] //달력의 요일 부분 텍스트
            ,dayNames: ['일요일','월요일','화요일','수요일','목요일','금요일','토요일'] //달력의 요일 부분 Tooltip 텍스트
            ,minDate: "-1M" //최소 선택일자(-1D:하루전, -1M:한달전, -1Y:일년전)
            ,maxDate: "+1M" //최대 선택일자(+1D:하루후, -1M:한달후, -1Y:일년후)     
		});
		$(".eDate").datepicker({
			dateFormat: 'yy-mm-dd' //Input Display Format 변경
            ,showOtherMonths: true //빈 공간에 현재월의 앞뒤월의 날짜를 표시
            ,showMonthAfterYear:true //년도 먼저 나오고, 뒤에 월 표시
            ,changeYear: true //콤보박스에서 년 선택 가능
            ,changeMonth: true //콤보박스에서 월 선택 가능                
//            ,showOn: "both" //button:버튼을 표시하고,버튼을 눌러야만 달력 표시 ^ both:버튼을 표시하고,버튼을 누르거나 input을 클릭하면 달력 표시  
//            ,buttonImage: "http://jqueryui.com/resources/demos/datepicker/images/calendar.gif" //버튼 이미지 경로
//            ,buttonImageOnly: true //기본 버튼의 회색 부분을 없애고, 이미지만 보이게 함
//            ,buttonText: "선택" //버튼에 마우스 갖다 댔을 때 표시되는 텍스트                
            ,yearSuffix: "년" //달력의 년도 부분 뒤에 붙는 텍스트
            ,monthNamesShort: ['1','2','3','4','5','6','7','8','9','10','11','12'] //달력의 월 부분 텍스트
            ,monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'] //달력의 월 부분 Tooltip 텍스트
            ,dayNamesMin: ['일','월','화','수','목','금','토'] //달력의 요일 부분 텍스트
            ,dayNames: ['일요일','월요일','화요일','수요일','목요일','금요일','토요일'] //달력의 요일 부분 Tooltip 텍스트
            ,minDate: "-1M" //최소 선택일자(-1D:하루전, -1M:한달전, -1Y:일년전)
            ,maxDate: "+1M" //최대 선택일자(+1D:하루후, -1M:한달후, -1Y:일년후)     
		});
		
		
		$(".search").click(function(){
			var siteCode = $(".site-select").val();
			var edgeCode = $(".edge-select").val();
			var sDate = $(".sDate").val();
			var eDate = $(".eDate").val();
			var deviceCode = $(".devicecode-select").val();
			
			var pattern = /[0-9]{4}-[0-9]{2}-[0-9]{2}/;
			
			if(sDate!="")
			{
				if(!pattern.test(sDate)) {
				    alert("시작 날짜가 날짜형식이 아닙니다.");
				    return;
				}
			}
			if(eDate!="")
			{
				if(!pattern.test(eDate)) {
				    alert("종료 날짜가 날짜형식이 아닙니다.");
				    return;
				}
			}
			
			searchParam.siteCode=siteCode;
			searchParam.edgeCode=edgeCode;
			searchParam.deviceCode=deviceCode;
			searchParam.sDate=sDate;
			searchParam.eDate=eDate;
			// 초기화
			tableLock=false;
			page=0;
			pageLimit=100;
			table.rows.load([]);
			loadingPage({total:0,page:0});
		});
	}
	
	var loadingPage=function(pager){
		if((pager.total-5)<=pager.page && !tableLock){
			tableLock=true;
			searchParam.page=page;
			searchParam.limit=pageLimit;
			$.ajax({
			    url: "/selectEventLog", // 클라이언트가 요청을 보낼 서버의 URL 주소
			    data: searchParam,                // HTTP 요청과 함께 서버로 보낼 데이터
			    type: "GET",                             // HTTP 요청 방식(GET, POST)
			    dataType: "json"                         // 서버에서 보내줄 데이터의 타입
			})
			// HTTP 요청이 성공하면 요청한 데이터가 done() 메소드로 전달됨.
			.done(function(json) {
				var rows=[];
				if(json.data.list){
					for(var i in json.data.list){
						rows.push({
							"Site":json.data.list[i].siteCode,
							"Edge":json.data.list[i].edgeCode,
							"deviceCode":json.data.list[i].devCode,
							"ALARM":json.data.list[i].alarmName,
							"TYPE":json.data.list[i].eventType,
							"Critical":json.data.list[i].critical,
							"Value":json.data.list[i].value,
							"Date":json.data.list[i].regdate,
						});
						
					}
				}
				table.rows.load(rows,true);
				if(json.data.list.length>0){
					page+=pageLimit;
					tableLock=false;
				}
			});
		}


	}
	
	var getTable = function(){
		return table;
	}
	
	return {
		pageLoad:pageLoad,
		getTable:getTable
	}
})($);