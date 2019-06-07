<?php
header("Content-Type: text/html; charset=UTF-8");
include_once 'opDB.class.php';
//session_start();//保存用户信息
$con = new opDB();//数据库操作
//从结果集中获取所有的数据
//session_start();
$response = array("status" => '1');

//检查graph_id
$id = $_POST['graph_id'];
if($id!=4){
    $response = array("status" =>0);
}

//最后需要数据的数组们
$welfare=array();
$scale_data=array();
$data=array();
$number=array();
//中间用到的数组
$scale=array();


//查找所有的福利
$sql = "select distinct KWelfareName from welfare_scale order by KWelfareName ";
$res = $con->get_result($sql);//类的方法调用用下划线

//存到$welfare
if($res){
    $welfare=$con->deal_result_withoutname($res,'KWelfareName');
}else{$response = array("status" => '4');}

//查找所有的规模
$sql = "select distinct KScale from welfare_scale  ";
$res = $con->get_result($sql);//类的方法调用用下划线
//存到$Scale
if($res){
    $scale=$con->deal_result_withoutname($res,'KScale');
}else{$response = array("status" => '0');}

//求规模对应福利的福利指数（补全规模对应空福利）
for($i=0;$i<count($scale);$i++){
    $sql = "select KWelfareName,KScale,WelfareData,number
			from welfare_scale
			where KScale='$scale[$i]'
			order by KWelfareName
 			";
    $res = $con->get_result($sql);//类的方法调用用下划线
    if($res){
    	while($row = mysqli_fetch_assoc($res)){
        array_push($data,$row['WelfareData']);
		array_push($number,$row['number']);
//		$number=$con->deal_result_withoutname($res,'number');
		}
			
		for($j=0;$j<count($data);$j++){
			$data[$j]=ROUND(($data[$j]/$number[$j])*1000,0);
		}
		
        array_push($scale_data,array("scale"=>$scale[$i],"data"=>$data));
		$data=array();
		$number=array();
    }else{
        $response['status']=0;
    }
}

$response['scale_data'] = $scale_data;
$response['welfareType'] = $welfare;
echo json_encode($response);



?>