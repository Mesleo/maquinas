<?php
// src/AppBundle/Entity/Usuario.php
namespace AppBundle\Entity;

use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints\DateTime;

/**
 * @ORM\Table(name="usuario")
 * @ORM\Entity(repositoryClass="AppBundle\Repository\UsuarioRepository")
 */
class Usuario
{
    /**
     * @var integer
     *
     * @ORM\Column(name="id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    protected $id;

    /**
     * @var string
     *
     * @ORM\Column(name="nombre", type="string", nullable=false,  unique=true)
     */
    protected $nombre;

    /**
     * @var string
     *
     * @ORM\Column(name="email", type="string", nullable=false,  unique=true)
     */
    protected $email;

    /**
     * @var string
     *
     * @ORM\Column(name="password", type="string", nullable=false)
     */
    protected $password;

    /**
     * @var integer
     *
     * @ORM\Column(name="rol", type="integer", nullable=false)
     */
    private $rol;

    /**
     * @var boolean
     *
     * @ORM\Column(name="trash", type="boolean", nullable=false)
     */
    protected $trash = false;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="created_at", type="datetime", nullable=true)
     */
    protected $created;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="updated_at", type="datetime", nullable=true)
     */
    protected $updated;

    public function __construct()
    {
        $this->machineUbication = new ArrayCollection();
    }

    /**
     * Get id
     *
     * @return integer
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * Set nombre
     *
     * @param string $nombre
     *
     * @return Usuario
     */
    public function setNombre($nombre)
    {
        $this->nombre = $nombre;

        return $this;
    }

    /**
     * Get nombre
     *
     * @return string
     */
    public function getNombre()
    {
        return $this->nombre;
    }

    /**
     * Set email
     *
     * @param string $email
     *
     * @return Usuario
     */
    public function setEmail($email)
    {
        $this->email = $email;

        return $this;
    }

    /**
     * Get email
     *
     * @return string
     */
    public function getEmail()
    {
        return $this->email;
    }

    /**
     * Set password
     *
     * @param string $password
     *
     * @return Usuario
     */
    public function setPassword($password)
    {
        $this->password = $password;

        return $this;
    }

    /**
     * Get password
     *
     * @return string
     */
    public function getPassword()
    {
        return $this->password;
    }

    /**
     * Set rol
     *
     * @param integer $rol
     *
     * @return Usuario
     */
    public function setRol($rol)
    {
        $this->rol = $rol;

        return $this;
    }

    /**
     * Get rol
     *
     * @return integer
     */
    public function getRol()
    {
        return $this->rol;
    }

    /**
     * Set trash
     *
     * @param boolean $trash
     *
     * @return Usuario
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
     * Set created
     *
     * @param \DateTime $created
     *
     * @return Usuario
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
     * Set updated
     *
     * @param \DateTime $updated
     *
     * @return Usuario
     */
    public function setUpdated($updated)
    {
        $this->updated = $updated;

        return $this;
    }

    /**
     * Get updated
     *
     * @return \DateTime
     */
    public function getUpdated()
    {
        return $this->updated;
    }

    /**
     * Add machineUbication
     *
     * @param \AppBundle\Entity\MaquinaUbicacion $machineUbication
     *
     * @return Usuario
     */
    public function addMachineUbication(\AppBundle\Entity\MaquinaUbicacion $machineUbication)
    {
        $this->machineUbication[] = $machineUbication;

        return $this;
    }

    /**
     * Remove machineUbication
     *
     * @param \AppBundle\Entity\MaquinaUbicacion $machineUbication
     */
    public function removeMachineUbication(\AppBundle\Entity\MaquinaUbicacion $machineUbication)
    {
        $this->machineUbication->removeElement($machineUbication);
    }

    /**
     * Get machineUbication
     *
     * @return \Doctrine\Common\Collections\Collection
     */
    public function getMachineUbication()
    {
        return $this->machineUbication;
    }
}
