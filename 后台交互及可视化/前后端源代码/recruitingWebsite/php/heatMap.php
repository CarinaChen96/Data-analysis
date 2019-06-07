<?php
header("Content-Type: text/html; charset=UTF-8");
include_once 'opDB.class.php';
session_start();//保存用户信息
$con = new opDB();//数据库操作
//从结果集中获取所有的数据

$response = array("status" => '1');

//检查graph_id
$id = $_POST['graph_id'];
if($id!=11){
    $response = array("status" =>0);
}
//最后所需数组
$educations=array();
$requirements=array();


//中间需要的数组
$website=array();
$requirement=array();
$max=array();
$min=array();

//查找所有的学历
$sql = "select distinct KEducation from website_education order by KEducation";
$res = $con->get_result($sql);//类的方法调用用下划线
//存到$educations
if($res){
$educations=$con->deal_result_withoutname($res,'KEducation');
}else{$response = array("status" => '0');}


//查找所有的网站
$sql = "select distinct KWebsite from website_education  ";
$res = $con->get_result($sql);//类的方法调用用下划线
//存到$Website
if($res){
$website=$con->deal_result_withoutname($res,'KWebsite');
}else{$response = array("status" => '0');}



//求网站对应专业的需求；注意对不齐的地方的，有的网站可能没有对应学历
for($i=0;$i<count($website);$i++){
$sql = "select Requirement from website_education where KWebsite='$website[$i]' order by KEducation";
$res = $con->get_result($sql);//类的方法调用用下划线
if($res){
	$requirement=$con->deal_result_withoutname($res,'Requirement');
	array_push($max,get_array_max($requirement));
	array_push($min,get_array_min($requirement));
	array_push($requirements,array("website"=>$website[$i],"requirement"=>$requirement));
}else{
	$response['status']=0;
}
	}
 


$response['educations'] = $educations;
$response['requirements'] = $requirements;

$maxnumber=get_array_max($max);
$minnumber=get_array_min($min);
$response['max']=$maxnumber;
$response['min']=$minnumber;

echo json_encode($response);


//求数组最大值
function get_array_max($array) {
	$maxnumber=0;	
for($i=0;$i<count($array);$i++) {
	if($array["$i"]>$maxnumber){
		$maxnumber=$array["$i"];}
}
		return $maxnumber;
	}

//求数组最小值
function get_array_min($array) {
	$minnumber=$array[0];	
for($i=0;$i<count($array);$i++) {
	if($array["$i"]<$minnumber){
		$minnumber=$array["$i"];}
}
		return $minnumber;
	}



?>