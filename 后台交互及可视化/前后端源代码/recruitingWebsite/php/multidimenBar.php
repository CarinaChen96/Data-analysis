<?php
header("Content-Type: text/html; charset=UTF-8");
include_once 'opDB.class.php';
//session_start();//保存用户信息
$con = new opDB();//数据库操作
//从结果集中获取所有的数据

$response = array("status" => '1');

//检查graph_id
$id = $_POST['graph_id'];
if($id!=9){
    $response = array("status" =>0);
}

//最后所需数组
$experience=array();
$industry=array();
$wage=array();

//中间用到的数组
$pl=array();
$exp=array();


//找到最大工资
$sql = "select distinct max(Wage)as MAXWage
		from industry_job";
$res = $con->get_result($sql);//类的方法调用用下划线
$row = mysqli_fetch_assoc($res);
$MAXWage=$row['MAXWage'];


//查找所有的行业
$sql = "select distinct KIndustry from industry_job order by KIndustry ";
$res = $con->get_result($sql);//类的方法调用用下划线
//存到$industry
if($res){
    $industry=$con->deal_result_withoutname($res,'KIndustry');
}else{$response = array("status" => '0');}


//查找所有的经验
$sql = "select distinct KExperience from industry_job";
$res = $con->get_result($sql);//类的方法调用用下划线
//存到$exp
if($res){
    $exp=$con->deal_result_withoutname($res,'KExperience');
}else{$response = array("status" => '0');}


//行业对应经验的工资,注意对不齐的地方的，有的行业可能没有对应经验
for($i=0;$i<count($exp);$i++){
	$sql = "select AVGWage from (
				select KExperience,KIndustry,avg(Wage)as AVGWage
				from industry_job
				group by KExperience,KIndustry
			) as AVGRWageTable
			where KExperience='$exp[$i]'
			order by KIndustry
	 		";
	$res = $con->get_result($sql);//类的方法调用用下划线
	if($res){
		$wage=$con->deal_result_withoutname($res,'AVGWage');
		for($j=0;$j<count($wage);$j++){
			$wage[$j]=ROUND($wage[$j],0);
			array_push($pl,($MAXWage-$wage[$j]));
		}
		array_push($experience,array("ex"=>$exp[$i],"wage"=>$wage,"pl"=>$pl));
		$pl=array();
	}else{
		$response['status']=0;
	}
}

$response['experience'] = $experience;
$response['industry'] = $industry;


echo json_encode($response);

?>