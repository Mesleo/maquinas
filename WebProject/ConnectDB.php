<?php

class ConnectDB
{
    private static $instance;
    private static $db;

    private function __construct(){
        try {
            self::$db = new PDO('127.0.0.1;
                dbname=maquinas',
                'usuario',
                'password',
                array(
                    PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8",
                    PDO::MYSQL_ATTR_USE_BUFFERED_QUERY => true,
                    PDO::ATTR_EMULATE_PREPARES => false
                ));
        } catch (PDOException $e) {
            print "<p class='error'>Error conectando con la base de datos! : " . $e->getMessage() . "</p>";
            die();
        }
    }

    public static function getInstance(){
        if (!isset(self::$instance)) {
            $myclass = __CLASS__;
            self::$instance = new $myclass;
        }
        return self::$instance;
    }

    /**
     * Consulta con un parámetro que devuelve una tupla
     *
     * @param $user
     * @return $q
     */
    public function queryOneParam($query, $param){
        $q = self::$db -> prepare($query);
        $q -> bindParam(1, $param);
        $q -> execute();
        return $q->fetch();
    }

    /**
     * Consulta con un parámetro que devuelve un array
     *
     * @param $param
     * @return array
     */
    public function queryOneParamArray($query, $param){
        $q = self::$db -> prepare($query);
        $q -> bindParam(1, $param);
        $q -> execute();
        return $q->fetchAll();
    }

    /**
     * Consulta con dos parámetros que devuelve una tupla
     *
     * @param $user
     * @return $q
     */
    public function queryTwoParams($query, $param1, $param2){
        $q = self::$db -> prepare($query);
        $q -> bindParam(1, $param1);
        $q -> bindParam(2, $param2);
        $q -> execute();
        return $q->fetch();
    }

    /**
     * Consulta con dos parámetros que devuelve un array
     *
     * @param $param
     * @return array
     */
    public function queryThreeParamsArray($query, $param1, $param2, $param3){
        $q = self::$db -> prepare($query);
        $q -> bindParam(1, $param1);
        $q -> bindParam(2, $param2);
        $q -> bindParam(3, $param3);
        $q -> execute();
        return $q->fetchAll();
    }

    /**
     * Obtengo todas las máquinas disponibles
     * @param $query
     * @return array
     */
    public function getMachines($query){
        $a = 0;
        $result = [];
        foreach ( self::$db->query($query) as $row){
            $matricula = "";
            if($row['matricula'] != null){
                $matricula = $row['matricula'];
            }
            $result[$a] = [];
            $result[$a] = [
                'id' => $row['id'],
                'maquina'  => $row['maquina'],
                'matricula'  => $matricula
                ];
            $a++;
        }
        return $result;
    }

    /**
     * Consulta simple que devuelve un array
     * @param $query
     * @return array
     */
    public function querySingle($query){
        $q = self::$db -> prepare($query);
        $q -> execute();
        return $q->fetchAll();
    }

    /**
     * Obtengo el último id insertado
     * @return string
     */
    public function lastInsertId(){
        return self::$db->lastInsertId();
    }
}
