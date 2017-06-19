<?php
// Script que inserta una nueva ubicación para la máquina pasada por parámetro
include_once './ConnectDB.php';

$db = ConnectDB::getInstance();

$ubicacion = null;
$machine = null;
$user = null;
$response = [];

if($_SERVER['REQUEST_METHOD'] == 'POST') {

    $body = json_decode(file_get_contents("php://input"), true);

    $ubicacion = $body['ubication'];
    $machine = $body['idMachine'];
    $user = $body['idUser'];

    $query = "INSERT INTO ubicacion (nombre) VALUES (?)";

    $query2 = "INSERT INTO 
                maquina_ubicacion 
                    (
                    id_maquina, 
                    id_ubicacion, 
                    id_usuario
                ) 
                VALUES (?, ?, ?)";

    if ($ubicacion != null && $machine != null) {
        $response['ubicacion'] = $db->queryOneParamArray($query, $ubicacion);
        $idUbicacion = $db->lastInsertId();
        $response['maquina_ubicacion'] = $db->queryThreeParamsArray($query2, $machine, $idUbicacion, $user);
        $response['estado'] = "Inserccion correcta";
    }else{
        $response['estado'] = "Inserccion erronea";
    }

    echo json_encode($response);
}

?>