<?php
include_once './ConnectDB.php';


$db = ConnectDB::getInstance();

$name = null;
$pass = null;
$response = [];
$usuario = [];

if($_SERVER['REQUEST_METHOD'] == 'POST') {

    $body = json_decode(file_get_contents("php://input"), true);

    $name = $body['name'];
    $pass = $body['password'];
    $query = "SELECT 
                    u.id AS id,
                    u.email AS email,
                    u.nombre AS nombre,
                    u.password
                FROM 
                    usuario u
                WHERE
                    u.nombre = ?";

    if ($name != null && $pass != null) {
        $user = $db->queryOneParam($query, $name);
        if($user){
            if(password_verify($pass, $user[3])){
                $response['estado'] = 0;
                $usuario = $user;
                unset($usuario["3"]);
                unset($usuario["password"]);
                $response['usuario'] = $usuario;
            }else{
                $response['estado'] = 2;
            }
        }else{
            $response['estado'] = 1;
        }
    }

    echo json_encode($response);
}

?>
