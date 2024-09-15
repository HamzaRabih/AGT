package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Equipe;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

public interface UtilisateurService {
    public Utilisateur creeUtilisateur(Utilisateur utilisateur);

    public List<Utilisateur> findUtilisateurs();

    public String supputilisateurById(Long ids);

    public List<Utilisateur> getUtilisateursBySociete(Long ids);


    public List<Utilisateur> findUtilisateursById(List<Long> id);

    public Utilisateur findUtilisateurById(Long idu);

    public List<Utilisateur> findUtilisateurByIdDepartement(Long idu);

    public List<Utilisateur> findUtilisateurByIdDepartements(List<Long> departementIds) ;

    public String sendMail(Utilisateur utilisateur);

    public List<Utilisateur> findAllSuperieursForUtilisateurByIdUtilisateur(Utilisateur utilisateur) ;

    public void creerNouvelUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurConnecte, RedirectAttributes redirectAttributes) ;
    public void modifierUtilisateur(Utilisateur nouvelleUtilisateur, Utilisateur utilisateurConnecte, RedirectAttributes redirectAttributes) ;
    public void loadSocietieMembers(Utilisateur utilisateur, Model model);

    }
