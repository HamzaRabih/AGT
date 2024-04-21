package com.example.tachesapp.Controleur;


import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController  implements ErrorController {

   // Pour renvoyer la page 403.html en cas d'erreur 404.
   @RequestMapping("/error")
    public String handleError() {
        // Redirige vers la page d'erreur personnalisée (404.html)
        return "/pages/403";
    }
    public String getErrorPath() {
        return "/error";
    }

}
