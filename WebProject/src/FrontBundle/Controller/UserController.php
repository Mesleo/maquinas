<?php

namespace FrontBundle\Controller;

use AppBundle\Form\UsuarioChangePasswordType;
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
 * Class UserController
 * @package FrontBundle\Controller
 * @Route("/usuario")
 */
class UserController extends Controller
{

    private $em;
    private $params;
    private $info;

    /**
     * @Route("/", name="form_change_pass")
     * @param Request $request
     */
    public function changePassAction(Request $request){

        $this->initialize();
        $session = $request->getSession();

        if($request->request->has('name') && $request->request->has('pass')
            && $request->request->has('new_pass') && $request->request->has('confirm_pass')){
            $name = trim($request->request->get('name'));
            $pass = trim($request->request->get('pass'));
            $newPass = trim($request->request->get('new_pass'));
            $confirmPass = trim($request->request->get('confirm_pass'));
            var_dump($name);
            $userFind = $this->em->getRepository('AppBundle:Usuario')
                ->findOneBy([
                   'nombre' => $name
                ]);
            if($userFind) {
                if (password_verify($pass, $userFind->getPassword())) {
                    if($newPass === $confirmPass){
                        $userFind->setPassword(password_hash($newPass, PASSWORD_DEFAULT));
                        $this->em->persist($userFind);
                        $this->em->flush();
                        $this->info = "Contraseña cambiada correctamente";
                        return $this->redirectToRoute("form_login",
                            array(
                                "info" => $this->info
                            ));
                    }else{
                        $this->info = 'Las contraseñas no coinciden';
                    }
                } else {
                    $this->info = 'Contraseña antigua errónea';
                }
            }else{
                $this->info = 'No existe ese usuario';
            }
        }

        return $this->render("FrontBundle:Forms:change_pass.html.twig",
            array(
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
