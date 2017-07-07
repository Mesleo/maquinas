<?php
include_once './ConnectDB.php';

$db = ConnectDB::getInstance();

$query = "SELECT 
              m.id AS id, 
              m.nombre AS maquina, 
              m.matricula AS matricula
            FROM 
                maquina AS m
            ORDER BY m.nombre ASC";

$response['maquinas'] = [];

if($_SERVER['REQUEST_METHOD'] == 'GET') {
    $response['maquinas'] = $db->querySingle($query);
}

echo json_encode($response);

?>