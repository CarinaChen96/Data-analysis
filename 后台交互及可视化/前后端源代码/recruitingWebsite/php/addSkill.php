<?php
header("Content-Type: text/html; charset=UTF-8");
include_once 'opDB.class.php';

$con = new opDB();//数据库操作
$response = array("status" => '1');

//检查city是否为空
$skill=array();

$skill= $_POST['skills'];//最后别忘了加回来啊


if($skill){
}else{$response = array("status" => '0');}

print("<pre>"); // 格式化输出数组 
print_r($skill); 
print("</pre>"); 

for($i=0;$i<count($skill);$i++){

    $KSkill=$skill[$i]['name'];
    $industry=$skill[$i]['industry'];
    $num=$skill[$i]['amount'];

	$sql = "INSERT INTO skill(KSkill,KIndustry,Amount) VALUES ('$KSkill','$industry',$num)";
	echo $sql;

    $res = $con->excute_dml($sql);//类的方法调用用下划线

	if($res!=1 ){
		$response = array("status" => '0');
    }
	
}	
	echo json_encode($response);

?>

<!--{"skills":[{"skill"="勤奋","industry"="安卓","num"=8}
		{"skill"="勤奋","industry"="安卓","num"=8}
		{"skill"="勤奋","industry"="安卓","num"=8}
	]}-->