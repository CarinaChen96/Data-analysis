/**
 * Created by 123 on 2017/7/4.
 */
(function(){
    var grapDom = document.getElementById("graph");
    var grapChart = echarts.init(grapDom);
    var app = {};
    
    option = null;

//  var mydata=  {"nodes":[
//      {"x":100,"y":100,"id":1,"label":"软件开发","size":100,"color":"#f00"},
//      {"x":150,"y":100,"id":2,"label":"数据分析","size":10,"color":"#f0f"},
//      {"x":100,"y":150,"id":3,"label":"运营维护","size":50,"color":"#ff0"},
//      {"x":10,"y":100,"id":4,"label":"计算机","size":90,"color":"#230"},
//      {"x":50,"y":100,"id":5,"label":"软件","size":120,"color":"#f09"},
//      {"x":100,"y":50,"id":6,"label":"信息","size":40,"color":"#f37"},
//      {"x":44,"y":100,"id":7,"label":"新闻","size":5,"color":"#130"},
//      {"x":49,"y":160,"id":8,"label":"控制","size":15,"color":"#f01"},
//      {"x":10,"y":45,"id":9,"label":"数学","size":2,"color":"#f20"}
//  ],
//      "edges":[
//      {"sourceID":1,"targetID":4,"width":13},
//      {"sourceID":1,"targetID":5,"width":0.5},
//      {"sourceID":1,"targetID":6,"width":1},
//      {"sourceID":2,"targetID":9,"width":2},
//      {"sourceID":2,"targetID":4,"width":11},
//      {"sourceID":2,"targetID":5,"width":0.2},
//      {"sourceID":2,"targetID":6,"width":0.1},
//      {"sourceID":3,"targetID":4,"width":0.7},
//      {"sourceID":3,"targetID":5,"width":11},
//      {"sourceID":3,"targetID":6,"width":0.8},
//      {"sourceID":3,"targetID":7,"width":2}
//  ]
//  };
    /***********初始化数据**************************/
    function InitData(data){

        var data_info ={"graph_id":11};
        /***********与后台交互传输数据***********/
       	$.ajax({
         type: "POST",
         url: "php/word2Vector.php",
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
//            console.log(data);
            if(data.status==0){
                alert("连接有误");
            }else{
              
               var nodes = data.nodes;
               var edges = data.edges;
               initChart(nodes,edges);

            }
//          initChart(jobs,websites,wages);

        }

    }
    function initChart(nodes,edges){
    	grapChart.hideLoading();
        grapChart.setOption(option = {
           title: {
	            text: '行业专业相关性分析',
	            top:20,
//	            left: 'left',
	            textStyle: {
	            	fontSize:24,
//	                color: '#fff'
	                
	            }
	        },
            animationDurationUpdate: 1500,
            animationEasingUpdate: 'quinticInOut',
            series : [
                {
                    type: 'graph',
                    layout: 'none',
                    progressiveThreshold: 700,
                    data: nodes.map(function (node) {
                        return {
                            x: node.x,
                            y: node.y,
                            id: node.id,
                            name: node.label,
                            symbolSize: node.size,
                            itemStyle: {
                                normal: {
                                    color: node.color
                                }
                            }
                        };
                    }),
                    links: edges.map(function (edge) {
                        return {
                            source: edge.sourceID,
                            target: edge.targetID,
                            lineStyle: {
                                normal: {
                                    width: edge.width,
                                   
                                    curveness: 0.2
                                }
                            }
                        };
                    }),
                    label: {
                        emphasis: {
                            position: 'right',
                            show: true
                        }
                    },
                    roam: true,
                    focusNodeAdjacency: true,

                    lineStyle: {
                        normal: {
                            width: 1.5,
                            curveness: 0.3,
                            opacity: 0.7
                        }
                    }
                }
            ]
        }, true);
    }
    InitData();
})();
