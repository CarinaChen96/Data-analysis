<?php
header("Content-Type: text/html; charset=UTF-8");
include_once 'opDB.class.php';
session_start();//保存用户信息
$con = new opDB();//数据库操作
//从结果集中获取所有的数据

$response = array("status" => '1');

//检查graph_id
//$id = $_POST['graph_id'];
//if($id!=7){
//  $response = array("status" =>0);
//}

//最后需要数据的数组们
$skills=array();
$wages=array();
$Requirements=array();
//查找所有的重点技能的薪资与需求
$sql = "select  skill,salary,Requirement from skill_salary  ";
$res = $con->get_result($sql);//类的方法调用用下划线
//存到$skill
while($row = mysqli_fetch_assoc($res)){
	array_push( $skills,$row['skill']);
	array_push( $wages,$row['salary']);
	array_push( $Requirements,$row['Requirement']);
}
$avg=0;
for($i=0;$i<count($wages);$i++){
	$avg=$avg+$wages[$i];
}
$avg=ROUND($avg/count($wages),0);


$response["skills"]=$skills;
$response["wages"]=$wages;
$response["Requirements"]=$Requirements;
$response["avg"]=$avg;
echo json_encode($response);
?>