<?php
header("Content-Type: text/html; charset=UTF-8");
include_once 'opDB.class.php';
$con = new opDB();//数据库操作
//从结果集中获取所有的数据
$response = array("status" => '1');

//检查graph_id
//$id = $_POST['graph_id'];
$id = 1;
if($id!=1){
    $response = array("status" =>$id);
}

//需要的数组
$geoCoordMap=array();
$demands=array();

//查找城市和地理位置
$sql = "select distinct KCity,Latitude,Longitude
		from city_job
		group by KCity,Latitude,Longitude";
$res = $con->get_result($sql);//类的方法调用用下划线
while($row = mysqli_fetch_assoc($res)){
	$geoCoordMap[$row['KCity']]=array($row['Latitude'],$row['Longitude']);
}


//查找城市和平均需求
$sql = "select distinct KCity,avg(Requirement) as AVGRequirement
		from city_job
		group by KCity";
$res = $con->get_result($sql);//类的方法调用用下划线
while($row = mysqli_fetch_assoc($res)){
	array_push($demands,array("name"=>$row["KCity"],"value"=>ROUND($row["AVGRequirement"],0)));
}



$response["demands"]=$demands;
$response["geoCoordMap"]=$geoCoordMap;
echo json_encode($response);


?>