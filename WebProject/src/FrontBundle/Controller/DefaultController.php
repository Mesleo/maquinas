<?php

namespace FrontBundle\Controller;

use AppBundle\Form\UsuarioType;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Security;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Symfony\Component\Validator\Constraints\Date;

class DefaultController extends Controller
{

    private $em;
    private $params;
    private $info;

    /**
     * @Route("/", name="form_login")
     * @param Request $request
     */
    public function loginAction(Request $request){

        $this->initialize();
        $session = $request->getSession();
        $user = new \AppBundle\Entity\Usuario();
        $form=$this->createForm(UsuarioType::class, $user);
        $form->handleRequest($request);
        if($request->request->has('info')){
            $this->info = $request->request->has('info');
        }

        if($form->isSubmitted() && $form->isValid()){
            $name = $user->getNombre();
            $userFind = $this->em->getRepository('AppBundle:Usuario')
                ->findOneBy([
                   'nombre' => $name
                ]);
            if($userFind) {
                if (password_verify($user->getPassword(), $userFind->getPassword())) {
                    $session->set('user', $userFind);
                    $this->params['machines'] = $this->em->getRepository('AppBundle:Maquina')
                        ->findAll([],[
                            'nombre' => 'ASC'
                        ]);
                    return $this->render("FrontBundle:Pages:machines_ubications.html.twig",
                        array(
                            "user" => $userFind,
                            "machines" => $this->params['machines']
                        ));
                } else {
                    $this->info = 'Contraseña incorrecta';
                }
            }else{
                $this->info = 'No existe ese usuario';
            }
        }

        return $this->render("FrontBundle:Forms:login.html.twig",
            array(
                "form" => $form->createView(),
                "info" => $this->info
            ));
    }


    private function initialize()
    {
        $this->params = [];
        $this->info = "";
        $this->em = $this->getDoctrine()->getManager();
    }
}
