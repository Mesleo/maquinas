<?php
// src/AppBundle/Entity/MaquinaUbicacion.php
namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints\DateTime;

/**
 * @ORM\Table(name="maquina_ubicacion")
 * @ORM\Entity(repositoryClass="AppBundle\Repository\MaquinaUbicacionRepository")
 */
class MaquinaUbicacion
{

    /**
     * @ORM\Id
     * @ORM\ManyToOne(targetEntity="Maquina", cascade="persist")
     * @ORM\JoinColumn(name="id_maquina", referencedColumnName="id")
     */
    protected $maquina;

    /**
     * @ORM\Id
     * @ORM\ManyToOne(targetEntity="Ubicacion", cascade="persist")
     * @ORM\JoinColumn(name="id_ubicacion", referencedColumnName="id")
     */
    protected $ubicacion;

    /**
     * @ORM\Id
     * @ORM\ManyToOne(targetEntity="Usuario", cascade="persist")
     * @ORM\JoinColumn(name="id_usuario", referencedColumnName="id")
     */
    protected $usuario;

    /**
     * @var string
     *
     * @ORM\Column(name="latitud", type="string", nullable=true)
     */
    protected $latitud;

    /**
     * @var string
     *
     * @ORM\Column(name="longitud", type="string", nullable=true)
     */
    protected $longitud;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="created", type="datetime", nullable=true)
     */
    protected $created;

    /**
     * @var boolean
     *
     * @ORM\Column(name="trash", type="boolean", nullable=false)
     */
    private $trash = false;


    /**
     * Set created
     *
     * @param \DateTime $created
     *
     * @return MaquinaUbicacion
     */
    public function setCreated($created)
    {
        $this->created = $created;

        return $this;
    }

    /**
     * Get created
     *
     * @return \DateTime
     */
    public function getCreated()
    {
        return $this->created;
    }

    /**
     * Set trash
     *
     * @param boolean $trash
     *
     * @return MaquinaUbicacion
     */
    public function setTrash($trash)
    {
        $this->trash = $trash;

        return $this;
    }

    /**
     * Get trash
     *
     * @return boolean
     */
    public function getTrash()
    {
        return $this->trash;
    }

    /**
     * Set maquina
     *
     * @param \AppBundle\Entity\Maquina $maquina
     *
     * @return MaquinaUbicacion
     */
    public function setMaquina(\AppBundle\Entity\Maquina $maquina)
    {
        $this->maquina = $maquina;

        return $this;
    }

    /**
     * Get maquina
     *
     * @return \AppBundle\Entity\Maquina
     */
    public function getMaquina()
    {
        return $this->maquina;
    }

    /**
     * Set ubicacion
     *
     * @param \AppBundle\Entity\Ubicacion $ubicacion
     *
     * @return MaquinaUbicacion
     */
    public function setUbicacion(\AppBundle\Entity\Ubicacion $ubicacion)
    {
        $this->ubicacion = $ubicacion;

        return $this;
    }

    /**
     * Get ubicacion
     *
     * @return \AppBundle\Entity\Ubicacion
     */
    public function getUbicacion()
    {
        return $this->ubicacion;
    }

    /**
     * Set usuario
     *
     * @param \AppBundle\Entity\Usuario $usuario
     *
     * @return MaquinaUbicacion
     */
    public function setUsuario(\AppBundle\Entity\Usuario $usuario)
    {
        $this->usuario = $usuario;

        return $this;
    }

    /**
     * Get usuario
     *
     * @return \AppBundle\Entity\Usuario
     */
    public function getUsuario()
    {
        return $this->usuario;
    }

    /**
     * Set user
     *
     * @param \AppBundle\Entity\Usuario $user
     *
     * @return MaquinaUbicacion
     */
    public function setUser(\AppBundle\Entity\Usuario $user = null)
    {
        $this->user = $user;

        return $this;
    }

    /**
     * Get user
     *
     * @return \AppBundle\Entity\Usuario
     */
    public function getUser()
    {
        return $this->user;
    }

    /**
     * Set machine
     *
     * @param \AppBundle\Entity\Maquina $machine
     *
     * @return MaquinaUbicacion
     */
    public function setMachine(\AppBundle\Entity\Maquina $machine = null)
    {
        $this->machine = $machine;

        return $this;
    }

    /**
     * Get machine
     *
     * @return \AppBundle\Entity\Maquina
     */
    public function getMachine()
    {
        return $this->machine;
    }

    /**
     * Set latitud
     *
     * @param string $latitud
     *
     * @return MaquinaUbicacion
     */
    public function setLatitud($latitud)
    {
        $this->latitud = $latitud;

        return $this;
    }

    /**
     * Get latitud
     *
     * @return string
     */
    public function getLatitud()
    {
        return $this->latitud;
    }

    /**
     * Set longitud
     *
     * @param string $longitud
     *
     * @return MaquinaUbicacion
     */
    public function setLongitud($longitud)
    {
        $this->longitud = $longitud;

        return $this;
    }

    /**
     * Get longitud
     *
     * @return string
     */
    public function getLongitud()
    {
        return $this->longitud;
    }
}
