var edgeComponent=(function($){

    var init=function(callback){
    	
        // $.when(myAjax.selectSiteList(),myAjax.selectEdgeList(),myAjax.selectDeviceCodeList()).done(function(result1,result2,result3){
        //     console.log(result1);
        //     console.log(result2);
        //     console.log(result3);
        // })
        var requests=[myAjax.selectSiteList(),myAjax.selectEdgeList(),myAjax.selectDeviceCodeList()];
        $.when.apply($, requests).done(function () {
            console.log(arguments); //it is an array like object which can be looped
            $.each(arguments, function (i, data) {
                if(data=="")
                    return;
                var res=data[0];
                switch(i){
                    // siteList
                    case 0:
                        if(res.result){
                            var div="<select class='site-select'>";
                            div+="<option value='0'>전체</option>";
                            for(var i in res.data){
                                var siteCode = res.data[i].siteCode;
                                var siteName = res.data[i].siteName;
                                div+="<option value='"+siteCode+"'>"+siteName+"</option>";
                            }
                            div+="</select>";
                            $("site-select").replaceWith(div);
                        }
                        break;
                    // edgeList
                    case 1:
                        if(res.result){
                            var div="<select class='edge-select'>";
                            div+="<option value='0'>전체</option>";
                            for(var i in res.data){
                                var edgeCode = res.data[i].edgeCode;
                                var edgeName = res.data[i].edgeName;
                                div+="<option value='"+edgeCode+"'>"+edgeName+"</option>";
                            }
                            div+="</select>";
                            $("edge-select").replaceWith(div);
                        }
                        break;
                    // device code list
                    case 2:
                        if(res.result){
                            var div="<select class='devicecode-select'>";
                            div+="<option value='0'>전체</option>";
                            for(var i in res.data){
                                var devCodeName = res.data[i].devCodeName;
                                div+="<option value='"+devCodeName+"'>"+devCodeName+"</option>";
                            }
                            div+="</select>";
                            $("devicecode-select").replaceWith(div);
                        }
                        break;
                }
            });
            if(callback)
            	callback();
        });
    }

    return{
        init:init
    }
})($);
