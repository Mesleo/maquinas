<?php
// Script para obtener el nombre de una máquina
include_once './ConnectDB.php';

$db = ConnectDB::getInstance();
$response['machine'] = [];
$machine = null;

$query = "SELECT 
              nombre
            FROM 
                machine 
            WHERE id = ?";

if($_SERVER['REQUEST_METHOD'] == 'GET') {
    if(isset($_GET['idMach'])) {
        $machine = trim($_GET['idMach']);
        $response['machine'] = $db->queryOneParamArray($query, $machine);
    }
}

echo json_encode($response);

?>
