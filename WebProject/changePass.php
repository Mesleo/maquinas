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
    $newPass = $body['new_password'];
    $newPass2 = $body['new_password2'];

    $query = "SELECT 
                    u.id AS id,
                    u.email AS email,
                    u.nombre AS nombre,
                    u.password
                FROM 
                    usuario u
                WHERE
                    u.nombre = ?";

    if ($newPass === $newPass2) {
        if(strlen($newPass) > 3 && strlen($newPass) < 11) {
            if ($name != null && $pass != null) {
                $user = $db->queryOneParam($query, $name);
                if ($user) {
                    $newPass = password_hash($newPass, PASSWORD_BCRYPT);
                    if (password_verify($pass, $user[3])) {
                        $query = "UPDATE  
                                      usuario 
                                  SET  
                                      password =  ? 
                                  WHERE  
                                      usuario.id = ?;";
                        $response['estado'] = 0;
                        $response['usuario'] = $db->queryTwoParams($query, $newPass, $user[0]);
                    } else {
                        // La contraseña actual no es correcta
                        $response['estado'] = 1;
                    }
                } else {
                    // No existe un usuario con ese nombre
                    $response['estado'] = 2;
                }
            }
        } else {
            // La contraseña debe tener entre 4 y 10 caracteres
            $response['estado'] = 3;
        }
    }else{
        // La constraseña no coinciden
        $response['estado'] = 4;
    }

    echo json_encode($response);
}

?>
