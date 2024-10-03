package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.*;
import com.example.tachesapp.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UtilisateurServiceImp implements UtilisateurService {
    @Autowired
    private UtilisateurRepo utilisateurRepo;
    @Autowired
    private EquipeRepo equipeRepository;
    @Autowired
    DepartementRepo departementRepo;
    @Autowired
    DomaineRepo domaineRepo;
    @Autowired
    ReinitDuMotDePasseRepo reinitDuMotDePasseRepo;
    @Autowired
    SocieteRepo societeRepo;
    @Autowired
    TacheRepo tacheRepo;

   JavaMailSender javaMailSender;


    // Constantes
    private static final String MAIL_EXISTE_DEJA = "Ce mail existe déjà. Veuillez saisir une nouvelle adresse mail.";
    private static final String UTILISATEUR_AJOUTE = "Utilisateur ajouté avec succès.";
    private static final String UTILISATEUR_MODIFIE = "L'utilisateur a été modifié avec succès.";



    @Override
    public Utilisateur creeUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepo.save(utilisateur);
    }

    @Override
    public List<Utilisateur> findUtilisateurs() {
        return utilisateurRepo.findAllByOrderBySociete();
    }


    @Override
    public String supputilisateurById(Long idUtilisateur) {
        Utilisateur utilisateur = utilisateurRepo.findById(idUtilisateur).orElse(null);
        if (utilisateur == null) { return null;}

         //verifier si l utilisateur est resspo d une equipe
         Equipe equipe1=equipeRepository.findEquipeByResponsable(utilisateur);
        //cas ou l utilisateur est un responsable d une equipe
        if (equipe1!=null)
        {
           return "vous pouvez pas supprimer ce utilisateur car il est un responsable d une equipe";
        }
        else
        {//cas ou l utilisateur pas un respo
            //verifier si l utilisateur est un membre dans un equipe
            Equipe equipe = utilisateur.getEquipes();
            // Vérifier si l'utilisateur a créé des tâches, a reçu des tâches,est propriétaire d'une Mémo ou a des mémoires
            boolean TachExisteByRecept = tacheRepo.existsByRecepteur(utilisateur);
            boolean existsByUtilisateur = tacheRepo.existsByUtilisateur(utilisateur);
            boolean existsByProprietaire = tacheRepo.existsByProprietaire(utilisateur);

            //cas ou l utilisateur est un membre d une equipe
            if (equipe != null) {
                //cas ou l utilisateur membre a des taches et des memoires
                if (TachExisteByRecept || existsByUtilisateur || existsByProprietaire) {
                    return "Vous ne pouvez pas supprimer cet utilisateur car il a envoyé ou reçu une tâche, possède une mémoire, ou est le propriétaire d'une tâche ou d'une mémoire.";
                }
                else
                {

                    utilisateurRepo.deleteById(idUtilisateur);
                    return "L'utilisateur a été supprimé avec succès.";
                }
            }
            //cas ou l utilisateur ni membre ni respo d une equipe
            else{
                if (TachExisteByRecept || existsByUtilisateur || existsByProprietaire) {
                    return "Vous ne pouvez pas supprimer cet utilisateur car il a envoyé ou reçu une tâche, possède une mémoire, ou est le propriétaire d'une tâche ou d'une mémoire.";
                }
                else
                {
                    utilisateurRepo.deleteById(idUtilisateur);
                    return "L'utilisateur a été supprimé avec succès.";
                }
                }
        }

    }

    public void creerNouvelUtilisateur(Utilisateur utilisateur, Utilisateur utilisateurConnecte, RedirectAttributes redirectAttributes) {
        Boolean existsByMail = utilisateurRepo.existsByMail(utilisateur.getMail());
        if (existsByMail) {
            redirectAttributes.addFlashAttribute("msgError", MAIL_EXISTE_DEJA);
        } else {
            utilisateur.setCreerpar(utilisateurConnecte);
            utilisateurRepo.save(utilisateur);
            redirectAttributes.addFlashAttribute("msg", UTILISATEUR_AJOUTE);
        }
    }

    public void modifierUtilisateur(Utilisateur nouvelleUtilisateur, Utilisateur utilisateurConnecte, RedirectAttributes redirectAttributes) {
        Utilisateur utilisateurAModifie = utilisateurRepo.findByIdutilisateur(nouvelleUtilisateur.getIdutilisateur());
        Boolean existsByMail = utilisateurRepo.existsByMailAndIdutilisateurNot(nouvelleUtilisateur.getMail(), utilisateurAModifie.getIdutilisateur());

        if (existsByMail) {
            redirectAttributes.addFlashAttribute("msgError", MAIL_EXISTE_DEJA);
        } else {
            utilisateurAModifie.setCreerpar(utilisateurConnecte.getCreerpar());
            utilisateurAModifie.setModifierpar(utilisateurConnecte);
            utilisateurAModifie.setNom(nouvelleUtilisateur.getNom());
            utilisateurAModifie.setMail(nouvelleUtilisateur.getMail());
            utilisateurAModifie.setMotdepasse(nouvelleUtilisateur.getMotdepasse());
            utilisateurAModifie.setSociete(nouvelleUtilisateur.getSociete());
            utilisateurAModifie.setDomaine(nouvelleUtilisateur.getDomaine());
            utilisateurAModifie.setDepartement(nouvelleUtilisateur.getDepartement());
            utilisateurAModifie.setPrenom(nouvelleUtilisateur.getPrenom());
            utilisateurAModifie.setActif(nouvelleUtilisateur.isActif());
            utilisateurAModifie.setRole(nouvelleUtilisateur.getRole());

            utilisateurRepo.save(utilisateurAModifie);
            redirectAttributes.addFlashAttribute("msg2", UTILISATEUR_MODIFIE);
        }
    }



    @Override
    public List<Utilisateur> getUtilisateursBySociete(Long ids) {
        return utilisateurRepo.findBySocieteIdsociete(ids);
    }

    @Override
    public List<Utilisateur> findUtilisateursById(List<Long> id) {
        return utilisateurRepo.findByIdutilisateurIn(id);
    }

    @Override
    public Utilisateur findUtilisateurById(Long idu) {
        return utilisateurRepo.findByIdutilisateur(idu);
    }

    @Override
    public List<Utilisateur> findUtilisateurByIdDepartement(Long idu) {
        return utilisateurRepo.findByDepartementIddepartement(idu);
    }


    public List<Utilisateur> findUtilisateurByIdDepartements(List<Long> departementIds) {
        // Récupérez la liste des départements de la société sélectionnée
        List<Departement> departementList = departementRepo.findAllById(departementIds);

        // Créez une liste vide pour stocker les utilisateurs
        List<Utilisateur> utilisateurList = new ArrayList<>();

        // Parcourez la liste des départements
        for (Departement departement : departementList) {
            // Obtenez la liste des utilisateurs du département
            List<Utilisateur> utilisateurDepartementList = utilisateurRepo.findUtilisateursByDepartement(departement);

            // Ajoutez la liste des utilisateurs du département à la liste globale
            utilisateurList.addAll(utilisateurDepartementList);
        }

        // Retournez la liste des utilisateurs
        return utilisateurList;
    }

    @Override
    public String sendMail(Utilisateur utilisateur) {
        try {

            String restLink=genereateResetToken(utilisateur);
            SimpleMailMessage msg=new SimpleMailMessage();
            msg.setFrom("rabih.fst@uhp.ac.ma");
            msg.setTo(utilisateur.getMail());
            msg.setSubject("Mot de passe oubliée");
            msg.setText("Cliquez sur ce lien pour modifier votre mot de passe.");

            javaMailSender.send(msg);
            return "success";
        }catch (Exception e)
        {
            e.printStackTrace();
            return "error";
        }

    }

    //fonction pour le but de recuperer tous les superieur d un utilisateur
    @Override
    public List<Utilisateur> findAllSuperieursForUtilisateurByIdUtilisateur(Utilisateur utilisateur) {
        //initialiser la liste des superieurs
       List<Utilisateur> superieurs=new ArrayList<>();
        //trouver l equipe de l utilisateur
        Equipe equipedUtilisateur=utilisateur.getEquipes();
        if(equipedUtilisateur != null && equipedUtilisateur.getResponsable()!=null) {
            superieurs. add(equipedUtilisateur.getResponsable());
            superieurs.addAll(findAllSuperieursForUtilisateurByIdUtilisateur(equipedUtilisateur.getResponsable()));
        }
        return superieurs;
    }




    public String genereateResetToken(Utilisateur utilisateur)
    {
        UUID uuid=UUID.randomUUID();
        LocalDateTime curentDateTime=LocalDateTime.now();
        LocalDateTime expiryDate=curentDateTime.plusMinutes(15);
        ReinitDuMotDePasse reinitDuMotDePasse=new ReinitDuMotDePasse();
        reinitDuMotDePasse.setUtilisateur(utilisateur);
        reinitDuMotDePasse.setToken(uuid.toString());
        reinitDuMotDePasse.setExpiryDate(expiryDate);

        ReinitDuMotDePasse token=reinitDuMotDePasseRepo.save(reinitDuMotDePasse);

        if(token!=null)
        {
            String endPointUrl="http://localhost:8091/restPassword";
            return endPointUrl+"/"+reinitDuMotDePasse.getToken();
        }
        return null;
    }


    public void loadSocietieMembers(Utilisateur utilisateur, Model model){
        //les utilisteurs de la meme societé (pour le champ proprietaire)
        Societe societe= societeRepo.findAllByUtilisateurs(utilisateur);
        List<Utilisateur> utilisateurList=utilisateurRepo.findUtilisateursBySociete(societe);
        model.addAttribute("utilisateurList2",utilisateurList);
    }
}
