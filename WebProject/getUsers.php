<?php
// Script para obtener todos los usuarios
include_once './ConnectDB.php';

$db = ConnectDB::getInstance();

$query = "SELECT 
              u.nombre AS nombre
            FROM 
                usuario AS u
            WHERE
                u.rol = 1
            ORDER BY u.nombre ASC";

// Lo del rol es para crear usuarios administradores y tener otros privilegios en la aplicación

$response['usuarios'] = [];
$response['usuarios'] = $db->querySingle($query);

echo json_encode($response);

?>