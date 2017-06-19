<?php
// Script que pone una ubicación como eliminada
include_once './ConnectDB.php';

$db = ConnectDB::getInstance();

$ubicacion = null;
$response = [];

if($_SERVER['REQUEST_METHOD'] == 'POST') {

    $body = json_decode(file_get_contents("php://input"), true);

    $ubicacion = $body['ubication'];
    $ubicacion = $_POST['ubication'];

    $query = "UPDATE maquina_ubicacion SET trash = true WHERE id_ubicacion = ?";

    if ($ubicacion != null) {
        $response['ubicacion'] = $db->queryOneParamArray($query, $ubicacion);
        $response['estado'] = "Borrado correcto";
    }

    echo json_encode($response);
}

?>