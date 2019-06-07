<?php
header("Content-Type: text/html; charset=UTF-8");
include_once 'opDB.class.php';
//session_start();//保存用户信息
$con = new opDB();//数据库操作
$response = array("status" => '1');

//检查city是否为空
$city=array();
//$city=array("北京","上海");
$city= $_POST['cities'];//最后别忘了加回来啊

if($city){
  }else{$response = array("status" => '0');}

//检查city是否都在数据库中出现
for($i=0;$i<count($city);$i++){
	$sql = "select *
			from city_job
			where KCity='$city[$i]'
			";
	$res = $con->excute_dql($sql);//类的方法调用用下划线
	if($res!=1){
	$response = array("status" => '0');
	}
}



//最后需要数据的数组们
	$data=array();


//求城市对应的薪资和房价
for($i=0;$i<count($city);$i++){
	$sql = "select avg(Wage)as AVGWage,avg(HousePrice)as AVGHousePrice
			from city_job
			where KCity='$city[$i]'
			";
	$res = $con->get_result($sql);
	while($row = mysqli_fetch_assoc($res)){
    array_push($data,array("city"=>$city[$i],"wage"=>ROUND($row['AVGWage'],0),"rent"=>ROUND($row['AVGHousePrice'],0)));
   }
}
$response['area']=$data;
echo json_encode($response);




?>