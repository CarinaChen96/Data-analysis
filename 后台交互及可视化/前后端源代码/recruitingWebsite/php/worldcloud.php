<?php
header("Content-Type: text/html; charset=UTF-8");
include_once 'opDB.class.php';
session_start();//保存用户信息
$con = new opDB();//数据库操作
//从结果集中获取所有的数据

$response = array("status" => '1');

//检查graph_id
$id = $_POST['graph_id'];
if($id!=7){
    $response = array("status" =>0);
}

//最后需要数据的数组们
$skill=array();

//查找所有的行业
$sql = "select  KSkill,Amount from skill  ";
$res = $con->get_result($sql);//类的方法调用用下划线
//存到$skill
while($row = mysqli_fetch_assoc($res)){
   array_push( $skill,array("name"=>$row['KSkill'],"amount"=>$row['Amount']));
}

$response["skill"]=$skill;
echo json_encode($response);
?>