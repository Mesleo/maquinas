<?php

namespace AppBundle\Repository;

/**
 * UbicacionRepository
 *
 * This class was generated by the Doctrine ORM. Add your own custom
 * repository methods below.
 */
class UbicacionRepository extends \Doctrine\ORM\EntityRepository
{

    /**
     * Gets all ubications of a machine
     * @param $machine
     * @return array
     */
    public function getUbicationsMachine($machine){

        $conn = $this->getEntityManager()->getConnection();

        $query = "SELECT 
                        u.id AS id,
                        u.nombre AS ubicacion,
                        us.nombre AS usuario,
                        mu.created + INTERVAL 2 HOUR AS fecha,
                        mu.id_maquina AS maquina,
                        mu.latitud,
                        mu.longitud
                    FROM 
                        ubicacion u
                    LEFT JOIN maquina_ubicacion AS mu
                        ON u.id = mu.id_ubicacion
                    LEFT JOIN maquina AS m
                        ON mu.id_maquina = m.id
                    LEFT JOIN usuario AS us
                        ON mu.id_usuario = us.id
                    WHERE
                        m.id = :machine AND 
                        mu.trash = 0
                    ORDER BY
                        mu.created DESC";

        $stmt = $conn->prepare($query);
        $stmt->bindValue(':machine', $machine);
        $stmt->execute();
        return $stmt->fetchAll();
    }

}
