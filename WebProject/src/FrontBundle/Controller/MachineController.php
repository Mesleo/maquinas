<?php

namespace FrontBundle\Controller;

use AppBundle\Entity\MaquinaUbicacion;
use AppBundle\Entity\Ubicacion;
use AppBundle\Form\UsuarioType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Security;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Symfony\Component\Validator\Constraints\Date;

/**
 * Class MachineController
 * @package FrontBundle\Controller
 * @Route("/maquinas")
 */
class MachineController extends Controller
{

    private $em;
    private $params;
    private $info;

    /**
     * @Route("/", name="show_ubications")
     * @param Request $request
     */
    public function machinesUbicationsAction(Request $request){

        $session = $request->getSession();
        if($session->get('user')) {
            $this->initialize();
            $info = null;
            $machine = null;
            $this->params['user'] = $session->get('user');

            if ($request->query->has('info')) {
                $this->params['info'] = $request->query->get('info');
            }
            $this->params['machines'] = $this->em->getRepository('AppBundle:Maquina')
                ->findAll([], [
                    'nombre' => 'ASC'
                ]);
            if ($request->request->has('machine')) {
                $machine = trim($request->request->get('machine'));
                $session->set('machine', $request->request->get('machine'));
            } else if ($session->get('machine') != null) {
                $machine = $session->get('machine');
            }
            $this->params['ubications'] = $this->em->getRepository('AppBundle:Ubicacion')
                ->getUbicationsMachine($machine);

            return $this->render("FrontBundle:Pages:machines_ubications.html.twig",
                $this->params);
        }else{
            return $this->render('FrontBundle:Error:access_denied.html.twig');
        }
    }

    /**
     * @Route("/annadir", name="ubication_add")
     * @param Request $request
     */
    public function addUbicationAction(Request $request){

        $session = $request->getSession();
        if($session->get('user')) {
            $this->initialize();
            $ubication = null;
            if($request->request->has('btnPress') && trim($request->request->get('btnPress')) == 1
                && is_numeric($request->request->get('mach'))){
                $ubication = $this->em->getRepository('AppBundle:Ubicacion')
                    ->findOneBy([
                            'nombre' => 'Nave'
                        ]
                    );
                if ($ubication == null) {
                    $ubication = new Ubicacion();
                    $ubication->setNombre('Nave');
                    $this->em->persist($ubication);
                    $this->em->flush();
                }
            }else if ($request->request->has('new-ubication') && $request->request->has('mach')
                && is_numeric($request->request->get('mach'))) {
                $ubication = $this->em->getRepository('AppBundle:Ubicacion')
                    ->findOneBy([
                            'nombre' => trim($request->request->get('new-ubication'))
                        ]
                    );

                if ($ubication == null) {
                    $ubication = new Ubicacion();
                    $ubication->setNombre(trim($request->request->get('new-ubication')));
                    $this->em->persist($ubication);
                    $this->em->flush();
                }
            }

            if($request->request->has('btnPress') && is_numeric($request->request->get('mach'))){
                $coordinates = [];
                if($request->request->has('latitude') && $request->request->has('longitude')){
                    $coordinates['latitude'] = trim($request->request->get('latitude'));
                    $coordinates['longitude'] = trim($request->request->get('longitude'));
                }
                $this->insertNewUbicationMachine($request, $ubication, $session, $coordinates);
            }else{
                $this->info = "Error, no existe esa máquina";
            }


            return $this->redirectToRoute("show_ubications", [
                'info' => $this->info
            ]);
        }else{
            return $this->render('FrontBundle:Error:access_denied.html.twig');
        }
    }

    /**
     * @Route("/borrar", name="ubication_del")
     * @param Request $request
     */
    public function delUbicationAction(Request $request){

        $session = $request->getSession();
        if($session->get('user')) {
            $this->initialize();
            $info = "No se ha eliminado la ubicación";
            if ($request->request->has('id-ubi') && $request->request->has('id-mach')
                && $request->request->has('date-ubi')
            ) {
                $ubi = $request->request->get('id-ubi');
                $mach = $request->request->get('id-mach');
                $date = \DateTime::createFromFormat('Y-m-d H:i:s', $request->request->get('date-ubi'));
                $info = "Se ha eliminado la ubicación exitosamente";
                $machineUbication = $this->em->getRepository('AppBundle:MaquinaUbicacion')
                    ->findOneBy([
                        'maquina' => $mach,
                        'ubicacion' => $ubi,
                        'created' => $date->modify('-2 hour')
                    ]);

                $machineUbication->setTrash(1);
                $this->em->persist($machineUbication);
                $this->em->flush();
            }
            return $this->redirectToRoute('show_ubications', [
                'info' => $info
            ]);
        }else{
            return $this->render('FrontBundle:Error:access_denied.html.twig');
        }
    }

    /**
     * @Route("/salir", name="clear_session")
     */
    public function clearSessionAction(Request $request){
        $session = $request->getSession();
        $session->clear();
        return $this->redirectToRoute('form_login');
    }

    private function initialize()
    {
        $this->params = [];
        $this->info = "";
        $this->em = $this->getDoctrine()->getManager();
    }

    /**
     * @param Request $request
     * @param $ubication
     * @param $session
     * @param $info
     */
    private function insertNewUbicationMachine(Request $request, $ubication, $session, $coords)
    {
        $machine = $this->em->getRepository('AppBundle:Maquina')
            ->findOneById($request->request->get('mach'));

        $this->em->getRepository('AppBundle:MaquinaUbicacion')
            ->insertMachineUbication($machine, $ubication, $session->get('user'), $coords);

        $this->info = "Ubicación cambiada";
    }
}
