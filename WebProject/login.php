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
                    sessions,
                    u.nombre AS nombre,
                    u.password
                FROM 
                    usuario u
                WHERE
                    u.nombre = ?";

    if ($name != null && $pass != null) {
        $user = $db->queryOneParam($query, $name);
        if($user){
            if(password_verify($pass, $user[4])){
                if($user[2] != null){
                    $sess = $user[2]+1;
                }else{
                    $sess = 1;
                }
                // Se incrementa el número de sesiones del usuario logeado
                $query = "UPDATE  usuario SET  last_access =  NOW(), sessions = ? WHERE  id = ?;";
                $ok = $db->queryTwoParams($query, $sess, $user[0]);
                $response['estado'] = 0;
                $usuario = $user;
                unset($usuario["4"]);
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
