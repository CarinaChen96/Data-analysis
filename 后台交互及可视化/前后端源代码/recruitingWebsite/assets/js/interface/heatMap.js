/**
 * Created by 123 on 2017/7/4.
 */
(function(){
    var heatMapDom = document.getElementById("heatMap");
    var heatMapChart = echarts.init(heatMapDom);
    var app = {};
    option = null;
    app.title = '笛卡尔坐标系上的热力图';

    /***********************模拟数据**************************/
//  var mydata= {
//      "educations":['不限', '大专', '本科', '硕士', '博士'],
//      "wages":[{"website":"58","wage":[5,1,0,0,2]},
//          {"website":"大街网","wage":[2,1,5,0,4]},
//          {"website":"拉勾网","wage":[3,0,2,4,7]},
//          {"website":"赶集网","wage":[0,1,2,5,4]}
//      ]};
    /***********初始化数据**************************/
    function InitData(){

            var data_info ={"graph_id":11};
            /***********与后台交互传输数据***********/
           $.ajax({
             type: "POST",
             url: "php/heatMap.php",
             data: data_info,
             success: function (data) {

            	 handleData(data);
             },
             error: function (data) {
             alert("error");
             }
            });
//          handleData(data);
            function handleData(data){

                  data = JSON.parse(data);
//                console.log(data);
                if(data.status==0){
                    alert("连接有误");
                }else{
                    var educations =[],websites=[],wages=[];
                    var educations = data.educations;
                    var max = data.max

                    for(var i = 0;i<data.requirements.length;i++){
                        websites.push(data.requirements[i]['website']);
                        //var arr = data.wages[i].wage.split(",");//如果给的是字符串就用 这个
                        var arr = data.requirements[i].requirement;
                        for(var j=0;j<arr.length;j++) {
                            var temp=[i,j,arr[j]];
                            wages.push(temp);
                        }
                    }
				 	initChart(educations,websites,wages,max,100);
                }
               

            }

        }
    /*********************网站--岗位薪资水平映射（平均工资）**********************/
    function initChart(educations,websites,wages,max,min){

         wages = wages.map(function (item) {
                return [item[1], item[0], item[2] || '-'];
         });
         console.log(wages);
        option = {
        	 title:{
	            text: '不同网站对学历的需求分析',
	            top:20,
	            left: 'center',
	            textStyle: {
	            	fontSize:24,
//	                color: '#fff'
	                
	            }
	        },
            tooltip: {
                position: 'top'
            },
            animation: false,
            grid: {
                height: '50%',
                y: '10%'
            },
            xAxis: {
                type: 'category',
                data: educations,
                splitArea: {
                    show: true
                }
            },
            yAxis: {
                type: 'category',
                data: websites,
                splitArea: {
                    show: true
                }
            },
            visualMap: {
                min: min,
                max: max,
                calculable: true,
                orient: 'horizontal',
                left: 'center',
                bottom: '15%'
            },
            tooltip:{
            	formatter: function(params) {
            		
				    var result = '<div>';
//				    params.forEach(function (item) {
//				        result += '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + item.color + '"></span>';
//				    });
					result += '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params.color + '"></span>';
					result+='<spanstyle="padding-left:25px;">'+params.name+'</span>';
					result+='<br><span>人数:'+params.value[2]+'</span>';
//					console.log(params);

					return result;
				}
            },
            series: [{
                name: '薪资水平',
                type: 'heatmap',
                data: wages,
                label: {
                    normal: {
                        show: true,
                        textStyle: {
//			            	fontSize:24,
			                color: '#111'
			                
			            }
                    }
                },
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                  
                }
            }]
        };
        if (option && typeof option === "object") {
            heatMapChart.setOption(option, true);
        }
    }

    InitData();
})();
