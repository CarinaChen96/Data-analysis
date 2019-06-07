/**
 * Created by 123 on 2017/7/4.
 */
(function(){
	var dom = document.getElementById("barCategory");
	var myChart = echarts.init(dom);
	var app = {};
	option = null;
	app.title = '堆叠条形图';
	//可以选中不同行业进行对比

	/*************模拟数据*********************/
//	var mydata = {
//	  "industry":['通信类','金融类','政府部门','医疗单位','信息类','建筑类','新闻类'],
//	  "jobs":[
//	  {"job":"需求分析师","requirement":"320,302,301,334,390,330,220"},
//	  {"job":"开发工程师","requirement":"370,342,301,334,390,330,300"},
//	  {"job":"测试工程师","requirement":"420,302,311,334,390,330,390"},
//	  {"job":"运营维护人员","requirement":"220,302,301,334,390,330,320"},
//	  {"job":"产品经理","requirement":"367,302,311,334,390,360,380"}
//	  ]
//	};

	/***********初始化数据**************************/
	function InitData(){

		var data_info ={"graph_id":6};
		/***********与后台交互传输数据***********/
		$.ajax({
			type: "POST",
	        url: "php/barCategory.php",
		    data: data_info,
	        success: function (data) {
	        	
	        	handleData(data);
	        },
	        error: function (data) {
	        	alert("error");
	        }
		});
//      handleData(data);
		function handleData(data){
//			console.log(data);
			data = JSON.parse(data);
			if(data.status==0){
				alert("行业岗位需求连接有误");
			}else{
				var jobs =[],industries=[],requirements=[];
				var industries = data.industry;
				
				
				for(var i = 0;i<data.jobs.length;i++){
					jobs.push(data.jobs[i]['job']);
//					var arr = data.jobs[i].requirement.split(",");
					var arr = data.jobs[i].requirement;//如果给的是数组就用 这个
					var temp = {"job":data.jobs[i]['job'],"require":arr};
					requirements.push(temp);
				}
	
			}

			initChart(jobs,industries,requirements);
		}
	}
	function initChart(jobs,industries,requirements){
		option = {
			tooltip : {
				trigger: 'axis',
				axisPointer : {            // 坐标轴指示器，坐标轴触发有效
					type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
				}
			},
			legend: {
				data: jobs
			},
			grid: {
				left: '3%',
				right: '4%',
				bottom: '3%',
				containLabel: true
			},
			xAxis:  {
				type: 'value'
			},
			yAxis: {
				type: 'category',
				data: industries
			},
			/*********岗位对应行业的需求映射到坐标中*******************/
			series: requirements.map(function (requirement) {

						return {
							name: requirement.job,
							type: 'bar',
							stack: '总量',
							label: {
								normal: {
//									show: true,
									position: 'insideRight'
								}
							},
							data: requirement.require

						};
					})

		};
		if (option && typeof option === "object") {
			myChart.setOption(option, true);
		}
	}
	InitData();

})();
