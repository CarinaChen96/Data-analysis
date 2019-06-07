<?php
header("Content-Type: text/html; charset=UTF-8");
include_once 'opDB.class.php';
session_start();//保存用户信息
$con = new opDB();//数据库操作

$response = array("status" => '1');

//检查graph_id
$id = $_POST['graph_id'];
if($id!=11){
    $response = array("status" =>0);
}

//最后需要数据的数组们
$x=array();
$y=array();
$id=array();//ID从1开始
$label=array();
$size=array();
$color=array();


//把值存到数组

//求各行业的需求值
$sql = "select KIndustry,Avg(Requirement)as AVGRequirement
		from industry_profession
		group by KIndustry
		order by KIndustry";
	$res = $con->get_result($sql);//类的方法调用用下划线
	$indexID=0;//所有数组从1开始避免混乱
while($row = mysqli_fetch_assoc($res)){
	$indexID++;
	$id[$indexID]=$indexID;
	$label[$indexID]=$row['KIndustry'];
	$size[$indexID]=ROUND($row['AVGRequirement'],0);//得到数据定义为AVGRequirement
	}

//一个行业和专业的分界值
$indexforEdges=$indexID;

	
//求各专业的需求值
$sql = "select KProfession,Avg(Requirement)as AVGRequirement
		from industry_profession
		group by KProfession
		order by KProfession";
$res = $con->get_result($sql);//类的方法调用用下划线

while($row = mysqli_fetch_assoc($res)){
	$indexID++;	
	$id[$indexID]=$indexID;
	$label[$indexID]=$row['KProfession'];
	$size[$indexID]=$row['AVGRequirement'];//得到数据定义为AVGRequirement
	
}

//求最大用于确认半径
$max=get_array_max($size);
$min=get_array_min($size);

if($max-$min!=0){
$facor=40/($max-$min);}
else{$facor=1;}
$indexID+=1;

//用于产生最后的坐标值
$rowmax=3;
$linemax=2;
$unit=ceil(sqrt($indexID/($rowmax*$linemax)));
$value=400/$unit;



//形成nodes 数组
$node=array();
//红色：c90e04蓝色：#0273e5
$red=13176324;
$blue=160741;
$randomnumber=0;
for($i=1;$i<$indexID;$i++){
	$size[$i]=$facor*($size[$i]-$min)+10;
	$ynumber=$value*ROUND($id[$i]/($unit*$rowmax),0);
		$xnumber=$value*ROUND($id[$i]%($unit*$rowmax),0);
	$x=rand($xnumber,($xnumber+$value));
	$y=rand($ynumber,($ynumber+$value));
	
	if($i<$indexforEdges+1){
//		$randomnumber=dechex(ROUND(rand(100,127)*$red/100,0));
//996ae2,#6ad6e2
		$color="#e26a75";
	}else{
		$randomnumber=dechex(rand(5532048,7001826));
		$color="#".$randomnumber;
//$color="#FFFFFF";
	}
	array_push($node,array("x"=>$x,"y"=>$y,"id"=>$id[$i],"label"=>$label[$i],"size"=>$size[$i],"color"=>$color));
}
$response['nodes']=$node;

//形成edge数组
$edge=array();
for($i=1;$i<$indexforEdges+1;$i++){
	for($j=1;$j<$indexID;$j++){
		$sql = "select Correlation
		from industry_profession
		where KIndustry='$label[$i]'&& KProfession='$label[$j]'";
		$res = $con->get_result($sql);//类的方法调用用下划线
		if($row = mysqli_fetch_assoc($res)){
			array_push($edge,array("sourceID"=>$i,"targetID"=>$j,"width"=>($row['Correlation']*150-0.6)));
		}
	}
	
}
$response['edges']=$edge;

echo json_encode($response);


//数组操作，获取数组最大值
function get_array_max($array) {
	$maxnumber=0;	
for($i=1;$i<count($array);$i++) {
	if($array[$i]>$maxnumber){
		$maxnumber=$array[$i];}
}
		return $maxnumber;
	}


//数组操作，获取数组最小值
function get_array_min($array) {
	$minnumber=0;	
for($i=1;$i<count($array);$i++) {
	if($array[$i]<$minnumber){
		$minnumber=$array[$i];}
}
		return $minnumber;
	}
?>