(function(){
	var multiBarDom = document.getElementById("MultiBar");
	var multiBarChart = echarts.init(multiBarDom);
	var app = {};
	option= null;
	
//	app.title = '正负条形图';
	/*************************模拟数据****************************/
//  var mydata={"experience":[
//  	{"ex":"应届生","wage":[38, 50, 33, 72,89],"pl":[62, 50, 67, 28,11]},
//		{"ex":"1-3年","wage":[61, 41, 42, 30,32],"pl":[39, 59, 58, 70,68]},
//		{"ex":"3-5年","wage":[71, 51, 32, 40,42],"pl":[29, 49, 68, 60,58]},
//  	{"ex":"5年以上","wage":[61, 61, 82, 60,12],"pl":[39, 39, 18, 40,88]}],
// 		 "industry":['Java开发', '安卓工程师', 'C语言', '后台',"前端"]
//	};
    var placeHoledStyle = {
        normal:{
            barBorderColor:'rgba(0,0,0,0)',
            color:'rgba(0,0,0,0)'
        },
        emphasis:{
            barBorderColor:'rgba(0,0,0,0)',
            color:'rgba(0,0,0,0)'
        }
    };
    var dataStyle = {
        normal: {
            label : {
                show: true,
                position: 'insideLeft',
                formatter: '{c}'
            }
        }
    };
    /***********初始化数据**************************/
    function InitData(){

        var data_info ={"graph_id":9};
        /***********与后台交互传输数据***********/
		$.ajax({
			 type: "POST",
			 url: "php/multidimenBar.php",
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

			data = JSON.parse(data);
			console.log(data);
            if(data.status==0){
                alert("连接有误");
            }else{
                var experiences =[],industries=[];
                var industries = data.industry;
               var temp1 = data.experience[0];
               var tempa = data.experience[6];
               var temp10 =data.experience[1];
               console.log(temp10);
               var templ =data.experience[2]
               data.experience[6]=temp10;
               data.experience[0]=tempa;
               data.experience[1]=templ;
               data.experience[2]=temp1;
                for(var i = 0;i<data.experience.length;i++){
                   // var wages=data.experience[i].wage;//如果给的是数组就用这个
                    //var arr = data.jobs[i].requirement.split(",");
//					console.log(data.experience[i]);
					
                    //var temp = {"job":data.jobs[i]['job'],"require":arr};
					var temp1={"name":data.experience[i]['ex'],
                        "type":'bar',
                        "stack": '总量',
                        "itemStyle" : dataStyle,
                        "data":data.experience[i]["wage"]};
					var temp2={"name":data.experience[i]['ex'],
                        "type":'bar',
                        "stack": '总量',
                        "itemStyle" : placeHoledStyle,
                        "data":data.experience[i]["pl"]};
					// var temp={"data":temp1, "placeHold":temp2};
                    experiences.push(temp1);
                    experiences.push(temp2);

                }

            }
            initChart(experiences,industries);
        }
    }

    function initChart(experiences,industries){

		option= {
			title: {
				text: '行业经验分析',
				top:10,
	            left: 'center',
			},
			tooltip : {
				trigger: 'axis',
				axisPointer : {            // 坐标轴指示器，坐标轴触发有效
					type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
				}
				// formatter : '{b}<br/>{a0}:{c0}%<br/>{a2}:{c2}%<br/>{a4}:{c4}%<br/>{a6}:{c6}%'
			},
			legend: {
				y: 55,
				itemGap : document.getElementById('MultiBar').offsetWidth / 19,
				data:experiences
			},
			toolbox: {
				show : true,
				feature : {
					mark : {show: true},
					dataView : {show: true, readOnly: false},
					restore : {show: true},
					saveAsImage : {show: true}
				}
			},
			label: {
						normal: {
										show: false,
										position: 'insideRight'
						}
			},
			grid: {
				y: 80,
				y2: 30
			},
			xAxis : [
				{
					type : 'value',
					position: 'top',
					axisTick:{show:false},
					splitLine: {show: false},
					axisLabel: {show: false}
				}
			],
			yAxis : [
				{
					type : 'category',
					splitLine: {show: false},
					data : industries
				}
			],
            /********************不同工作经验下各行业的薪资水平************************/
			series : experiences.map(function (experience) {
				return experience;
			})

		};
        multiBarChart.setOption(option);
        if (option && typeof option=== "object") {
            multiBarChart.setOption(option, true);
        }
    }

    InitData();
})();
