package com.example.tachesapp.Controleur;

import com.example.tachesapp.Dao.UtilisateurRepo;
import com.example.tachesapp.Model.Utilisateur;
import com.example.tachesapp.Service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SecuriteControleur {

    @Autowired
    UtilisateurRepo utilisateurRepo;
    @Autowired
    UtilisateurService utilisateurService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) {model.addAttribute("error", "Adresse email ou mot de passe invalide");}
        return "/pages/login";
    }

    @GetMapping("/403")
    public String Error() {
        return "/pages/403";
    }

}
