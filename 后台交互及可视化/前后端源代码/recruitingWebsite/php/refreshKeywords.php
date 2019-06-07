<?php
header("Content-Type: text/html; charset=UTF-8");
include_once 'opDB.class.php';

$con = new opDB();//数据库操作
$response = array("status" => '1');


//$jobs="all";
//$city=array("北京","上海");
$jobs= $_POST['jobs'];//最后别忘了加回来啊
//检查是否为空
	if($jobs){
	  }else{$response = array("status" => '6');}
	  
//最后要用的数组
$skills=array();
//最后要用的数据
$max=0;

	  
//根据jobs做出相应的数据处理
if($jobs=="综合"){
	  	
	$sql = "select *
			from(
			select distinct KSkill,sum(Amount)as SUMAmount
			from skill
			group by KSkill) as abc
			order by SUMAmount DESC
			";
	$res = $con->get_result($sql);
	while($row = mysqli_fetch_assoc($res)){
		if($row['SUMAmount']>$max){
			$max=$row['SUMAmount'];
		}
    array_push($skills,array("name"=>$row['KSkill'],"amount"=>$row['SUMAmount']));
	}
}else{
	
	$sql = "select distinct KSkill,Amount
			from skill
			where KIndustry	='$jobs'
			order by Amount DESC
			";
	$res = $con->get_result($sql);
	while($row = mysqli_fetch_assoc($res)){
		if($row['Amount']>$max){
			$max=$row['Amount'];
		}
    array_push($skills,array("name"=>$row['KSkill'],"amount"=>$row['Amount']));
	}
	
}

$response['skills']=$skills;
$response['max']=$max;

echo json_encode($response);


?>