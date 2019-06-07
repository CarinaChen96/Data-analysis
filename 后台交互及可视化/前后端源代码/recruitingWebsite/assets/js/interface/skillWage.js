(function(){
			initData();
			
		function initData(){
			$.ajax({
				type:"post",
				url:"php/skillWage.php",
				data:{"grap_id":5},
				async:true,
				success:function(data){
					handleData(data);
				}
			});
			function handleData(data){
				data = JSON.parse(data);
				
				
				var skillWageReq = [];
				var skills=[];
				var avgW = [];
				if(data.status!=0){
					skills = data.skills;
					console.log(data);
					for(var i=0;i<data.skills.length;i++){
						skillWageReq.push([data.skills[i],data.wages[i],data.Requirements[i]]);
						avgW.push(data.avg);
					}
//					console.log(skillWageReq);
//					console.log(skills);
					initChart(skillWageReq,skills,avgW)
				}
			}
		}
			
			
			function initChart(skillWageReq,skills,avgW){
				var dom = document.getElementById("Cityrelation");
				var myChart = echarts.init(dom);
				var app = {};
				option = null;
					// See https://github.com/ecomfe/echarts-stat
				var myRegression = ecStat.regression('logarithmic', avgW);
//				console.log(myRegression);
				myRegression.points.sort(function(a, b) {
				    return a[1] - b[1];
				});
				
				option = {
				    
				    title: {
				        text: '掌握技能对薪资的影响以及和技能需求',
				        top:'20',
				        left: 'center'
				    },
//				    tooltip: {
//				        trigger: 'axis',
//				        axisPointer: {
//				            type: 'cross'
//				        }
//				    },
				 tooltip: {
		            	formatter: function(params) {
//		            		console.log(params);
		              		var result = '<div>';
//							result += '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params.color + '"></span>';
							result+='<span>技能点：'+params.data[0]+'</span>';
//							
							result+='<br><span>薪资：'+params.data[1]+'元</span>';
							result+='<br><span>需求：每万人'+Math.ceil(params.data[2]/3)+'</span>';

		
		
							return result;
		            	}
		            },
				    grid: {
				        top: 80,
				        bottom: 90
				    },
				    xAxis: {
				        type: 'category',
		        		data:skills,
				        splitLine: {
				            lineStyle: {
				                type: 'dashed'
				            }
				        },
				    },
				    yAxis: {
				        type: 'value',
				        splitLine: {
				            lineStyle: {
				                type: 'dashed'
				            }
				        },
				    },
				    series: [{
				        name: '技能薪资需求',
				        type: 'scatter',
				        symbolSize: function(data) {
//				        	console.log(data);
				            return Math.sqrt(data[2]);
				        },
				       
				        label: {
				            emphasis: {
				                show: true,
				                formatter: function(param) {
//				                	console.log(param);
				                    return param.data[0];
				                },
				                position: 'top'
				            }
				        },
				        itemStyle: {
				            normal: {
				                shadowBlur: 10,
				                shadowColor: 'rgba(120, 36, 50, 0.5)',
				                shadowOffsetY: 5,
				                color: new echarts.graphic.RadialGradient(0.4, 0.3, 1, [{
				                    offset: 0,
				                    color: 'rgb(251, 118, 123)'
				                }, {
				                    offset: 1,
				                    color: 'rgb(204, 46, 72)'
				                }])
				            }
				        },
				        data: skillWageReq
				    }, {
				        name: '行业平均值',
				        type: 'line',
				        lineStyle: {
				            normal: {
				                color: '#2f4554'
				            }
				        },
				        smooth: true,
				        showSymbol: false,
				        data: avgW
				        
				    }]
				};;
				if (option && typeof option === "object") {
				    myChart.setOption(option, true);
				}
			}
			
})();
