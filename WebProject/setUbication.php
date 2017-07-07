<?php
// Script que inserta una nueva ubicación para la máquina pasada por parámetro
include_once './ConnectDB.php';

$db = ConnectDB::getInstance();

$ubicacion = null;
$machine = null;
$user = null;
$latitude = null;
$longitude = null;
$response = [];

if($_SERVER['REQUEST_METHOD'] == 'POST') {

    $body = json_decode(file_get_contents("php://input"), true);

    $ubicacion = $body['ubication'];
    $machine = $body['idMachine'];
    $user = $body['idUser'];
    $latitude = $body['latitud'];
    $longitude = $body['longitud'];

    $query = "SELECT id, nombre
                FROM ubicacion
                WHERE nombre =  ?
                GROUP BY nombre";

    $ubic = $db->queryOneParam($query, $ubicacion);

    if($ubic != null){// Si ya hay una ubicación con el mismo nombre...
        $idUbicacion = $ubic['id'];
    }else{// Sino se crea una nueva...
        $query2 = "INSERT INTO ubicacion (nombre) VALUES (?)";
        $response['ubicacion'] = $db->queryOneParamArray($query2, $ubicacion);
        $idUbicacion = $db->lastInsertId();
    }

    $query3 = "INSERT INTO
                maquina_ubicacion
                    (
                    id_maquina,
                    id_ubicacion,
                    id_usuario,
                    latitud,
                    longitud
                )
                VALUES (?, ?, ?, ?, ?)";

    if ($ubicacion != null && $machine != null) {
        $response['maquina_ubicacion'] = $db->queryFiveParamsArray($query3, $machine, $idUbicacion, $user, $latitude, $longitude);
        $response['estado'] = "Inserccion correcta";
    }else{
        $response['estado'] = "Inserccion erronea";
    }

    echo json_encode($response);
}

?>