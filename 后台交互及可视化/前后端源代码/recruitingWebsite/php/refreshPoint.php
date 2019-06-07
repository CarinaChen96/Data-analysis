<?php
header("Content-Type: text/html; charset=UTF-8");
include_once 'opDB.class.php';
//session_start();//保存用户信息
$con = new opDB();//数据库操作
//从结果集中获取所有的数据
//session_start();
$response = array("status" => '1');

//检查city是否为空
$city = $_POST['city'];
//$city="北京";
if($city){
    }else{$response = array("status" =>0);}

//检查是否有这个城市
$sql = "select KCity from city_job where KCity='$city' ";
$res = $con->excute_dql($sql);//类的方法调用用下划线
if($res!=1){
	$response = array("status" => '0');
}

//最后需要的数组
$data=array();

//查找所有的行业
$sql = "select KJob,Wage 
		from city_job
		where KCity='$city'";
$res = $con->get_result($sql);//类的方法调用用下划线
while($row = mysqli_fetch_assoc($res)){
    array_push( $data,array("works"=>$row["KJob"],"wage"=>$row["Wage"]));
   }


$response['points'] = $data;
echo json_encode($response);






?>