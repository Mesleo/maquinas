<?php
// Script para obtener todas las ubicaciones de una máquina
include_once './ConnectDB.php';

$db = ConnectDB::getInstance();


$response['estado'] = [];
$response['ubicaciones_maquina'] = [];

$machine = null;
$query = "SELECT 
            u.id AS id,
            u.nombre AS ubicacion,
            us.nombre AS usuario,
            mu.created AS fecha,
            mu.latitud,
            mu.longitud
        FROM 
            ubicacion u
        LEFT JOIN maquina_ubicacion AS mu
            ON u.id = mu.id_ubicacion
        LEFT JOIN maquina AS m
            ON mu.id_maquina = m.id
        LEFT JOIN usuario AS us
            ON mu.id_usuario = us.id
        WHERE
            m.id = ? AND 
            mu.trash = 0
        ORDER BY
            mu.created DESC";

if(isset($_GET['idMachine'])){
    $machine = $_GET['idMachine'];
}

$response['ubicaciones_maquina'] = $db->queryOneParamArray($query, $machine);
if(count($response['ubicaciones_maquina']) > 0){
    $response['estado'] = 1;
}else{
    $response['estado'] = 2;
}

print json_encode($response);

?>