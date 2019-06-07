/**
 * Created by 123 on 2017/7/4.
 */
(function(){
	var polarHeatMapDom = document.getElementById("polarHeatMap");
	var polarHeatMapChart = echarts.init(polarHeatMapDom);
	var app = {};
	var option = null;

	/****************模拟数据******************/
//  var mydata={
//      "scale_data":[{"scale":"0人","data":[5,7,1,17,10,1,1]},
//          {"scale":"10人","data":[5,5,1,10,3,1,3]},
//          {"scale":"20人","data":[5,4,2,13,0,1,4]},
//          {"scale":"50人","data":[5,6,2,14,0,3,5]},
//          {"scale":"80人","data":[10,7,3,15,1,3,6]},
//          {"scale":"100人","data":[12,7,5,16,1,5,7]},
//          {"scale":"200人","data":[12,7,5,14,1,5,8]},
//          {"scale":"500人","data":[15,7,6,12,1,6,9]},
//          {"scale":"1000人","data":[18,7,8,10,1,8,10]},
//          {"scale":"2000人","data":[18,7,9,7,8,10,11]},
//          {"scale":"5000人","data":[20,15,10,7,1,12,12]},
//          {"scale":"5000以上","data":[21,17,11,5,10,15,15]}],
//      "welfareType":['五险一金', '餐补', '宿补', '奖金', '带薪假期', '周末双休', '节日福利']
//  };
    /***********初始化数据**************************/
    function InitData(){
        var data_info ={"graph_id":4};
        /***********与后台交互传输数据***********/
		$.ajax({
			 type: "POST",
			 url: "php/polarHeatMap.php",
			 data: data_info,
			 success: function (data) {
	
				 handleData(data);
			 },
			 error: function (data) {
			 	alert("error");
			 }
		 });
		 function handleData(data){
//      	console.log(data);
            data = JSON.parse(data);
//          console.log(data);
            if(data.status==0){
                alert("福利status=0");
            }else{
                var scale =[],weight=[],welfareCat=[];
                welfareCat = data.welfareType;
                for(var i=0;i<data.scale_data.length;i++) {
                    scale.push(data.scale_data[i]['scale']);
                    for(var j=0;j<data.scale_data[i]['data'].length;j++) {
                        weight.push([j,i,data.scale_data[i]['data'][j]]);
                    }
                }

            }
//          console.log(welfareCat);
//          console.log(weight);
            initChart(scale,weight,welfareCat);
        }

       
    }
    function initChart(scale,weight,welfareCat) {
//  	console.log(weight);
        function renderItem(params, api) {
        	
//      	console.log(params);
            var values = [api.value(0), api.value(1)];
//          console.log(values);
            var coord = api.coord(values);

            var size = api.size([1, 1], values);
//          console.log(size);
//          console.log(coord);
            return {
                type: 'sector',
                shape: {
                    cx: params.coordSys.cx,//相对父坐标的横坐标
                    cy: params.coordSys.cy,
                    r0: coord[2] - size[0] / 2,//外半径
                    r: coord[2] + size[0] / 2,
                    startAngle: coord[3] -size[1] / 2+54,//开始弧度
                    endAngle: coord[3] +size[1] / 2+54
                },
                style: api.style({
                    fill: api.visual('color')
                })
            };
        }

        /****************************有该项福利的公司所占比重*******************************/
        var maxValue = echarts.util.reduce(weight, function (max, item) {
            return Math.max(max, item[2]);
        }, -Infinity);

        option = {
        	 title:{
	            text: '不同规模的公司福利情况分析',
	            top:5,
	            left: 'center',
	            textStyle: {
	            	fontSize:24,
//	                color: '#000'
	                
	            }
	        },

            polar: {},
            tooltip: {
            	formatter: function(params) {
            		console.log(params);
              		var result = '<div>';
					result += '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params.color + '"></span>';
					result+='<span style="padding-left:25px;">公司规模：'+scale[params.data[1]]+'</span>';
					if(params.data[2]>1000){
						result+='<br><span>几乎每家公司都会提供'+params.data[2]/1000+"种以上"+welfareCat[params.data[0]]+'相关福利</span>';

					}else{
						result+='<br><span>每1000家公司:有'+params.data[2]+'家公司提供'+welfareCat[params.data[0]]+'</span>';

					}

					return result;
            	}
            },
            visualMap: {
                type: 'continuous',
                min: 0,
//              top:100,
                max: maxValue,
                top: 'middle',
                dimension: 2,
                calculable: false
            },
            angleAxis: {
                type: 'category',
                data: scale,
//              startAngle:0,
                clockwise:false,
//              top:180,
                boundaryGap: false,
                splitLine: {
                    show: false,
                    lineStyle: {
                        color: '#ddd',
                        type: 'dashed'
                    }
                },
                axisLabel:{show:false},
                axisLine: {
                    show: false
                }
            },
            radiusAxis: {
                type: 'category',
                data:welfareCat ,
                z: 100
            },
            label: {
                    normal: {
                        show: true
                      
                    }
                },
            series: [{
                name: '福利：',
                type: 'custom',
               	label: {
                    normal: {
                        show: false
                    }
                },
                coordinateSystem: 'polar',
                itemStyle: {
                    normal: {
                        color: '#d14a61'
                    }
                },
                renderItem: renderItem,
                data: weight
            }]
        };
        if (option && typeof option === "object") {
            polarHeatMapChart.setOption(option, true);
        }
    };
    InitData();
})();
