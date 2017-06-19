<?php
// src/AppBundle/Entity/Maquina.php
namespace AppBundle\Entity;


use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints\DateTime;

/**
 * @ORM\Table(name="maquina")
 * @ORM\Entity(repositoryClass="AppBundle\Repository\MaquinaRepository")
 */
class Maquina
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
     * @ORM\Column(name="matricula", type="string")
     */
    protected $matricula;

    /**
     * @var boolean
     *
     * @ORM\Column(name="operativa", type="boolean", nullable=false)
     */
    protected $operativa = true;

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
     * @return Maquina
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
     * Set matricula
     *
     * @param string $matricula
     *
     * @return Maquina
     */
    public function setMatricula($matricula)
    {
        $this->matricula = $matricula;

        return $this;
    }

    /**
     * Get matricula
     *
     * @return string
     */
    public function getMatricula()
    {
        return $this->matricula;
    }

    /**
     * Set operativa
     *
     * @param boolean $operativa
     *
     * @return Maquina
     */
    public function setOperativa($operativa)
    {
        $this->operativa = $operativa;

        return $this;
    }

    /**
     * Get operativa
     *
     * @return boolean
     */
    public function getOperativa()
    {
        return $this->operativa;
    }

    /**
     * Set created
     *
     * @param \DateTime $created
     *
     * @return Maquina
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
     * @return Maquina
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
     * @return Maquina
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
