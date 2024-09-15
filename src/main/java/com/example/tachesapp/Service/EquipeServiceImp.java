package com.example.tachesapp.Service;

import com.example.tachesapp.Dao.EquipeRepo;
import com.example.tachesapp.Dao.HistoriqueAffRepo;
import com.example.tachesapp.Dao.SocieteRepo;
import com.example.tachesapp.Model.Equipe;
import com.example.tachesapp.Model.Societe;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Service
public class EquipeServiceImp implements EquipeService {
    @Autowired
    private EquipeRepo equipeRepo;
    @Autowired
    SocieteRepo societeRepo;
    @Autowired
    UtilisateurService utilisateurService;

    @Override
    public Equipe saveEquie(Equipe e) {
        return equipeRepo.save(e);
    }


    @Override
    public List<Equipe> findAllEquipes() {
        return equipeRepo.findAll();
    }

    @Override
    public void suppEquipe(Long id) {
        equipeRepo.deleteByIdequipe(id);
    }

    @Override
    public Equipe findEquipByID(Long ide) {
        return equipeRepo.findEquipeByIdequipe(ide);
    }

    @Override
    public void deleteEquipe(Long id) {
        equipeRepo.deleteEquipeByIdequipe(id);
    }

    @Override
    public List<Long> findAllIdsReso() {
        List<Long> respoIds = new ArrayList<>();

        List<Equipe> equipeList = equipeRepo.findAll();

        for (Equipe equipe : equipeList) {
            if (equipe.getResponsable() != null) {
                respoIds.add(equipe.getResponsable().getIdutilisateur());
            }
        }
        return respoIds;
    }


    public void handleEquipeCreation(Equipe equipe, String nomequipe, Long idResponsableEquipe,
                                      List<Long> idUtilisateurs, boolean isResponsableOfAnotherTeam,
                                      RedirectAttributes redirectAttributes, Utilisateur utilisateurConnecte, Long idsoc) {

        Societe societe=societeRepo.findByIdsociete(idsoc);

        Utilisateur responsable = utilisateurService.findUtilisateurById(idResponsableEquipe);
        //bool pour verifier si le nom d'equipe est existe
        Boolean existsByNomeqpAndSociete=equipeRepo.existsByNomequipeAndResponsableSocieteAndIdequipeNot(equipe.getNomequipe(), societe,equipe.getIdequipe());

        //La condition "idResponsableEquipe != -1" est utilisée pour gérer le cas où une équipe n'a pas de responsable (responsable == null).
        // Cela permet d'éviter le message "L'utilisateur est déjà responsable d'une équipe" dans le scénario où deux responsables ou plus sont nuls.
        if (idResponsableEquipe != -1) {

            if (isResponsableOfAnotherTeam) {
                redirectAttributes.addFlashAttribute("msg", "L'utilisateur est déjà Responsable d'une équipe");
            }
            else
            {// Cas de la création

                if (existsByNomeqpAndSociete) {  // si le DOMAINE  est existe déjà
                    redirectAttributes.addFlashAttribute("msgError", "Ce nom d'équipe existe déjà dans la société.");
                }
                else
                {//sinon
                    equipe.setCreerpar(utilisateurConnecte);
                    setEquipeDetails(equipe, nomequipe, idResponsableEquipe, idUtilisateurs);
                    Equipe equipe1 = saveEquie(equipe);
                    handleEquipeSaveResult(equipe1, redirectAttributes,"create");
                }
            }
        } else {

            if (existsByNomeqpAndSociete) {  // si le DOMAINE  est existe déjà
                redirectAttributes.addFlashAttribute("msgError", "Ce nom d'équipe existe déjà dans la société.");
            }
            else
            {//sinon
                equipe.setCreerpar(utilisateurConnecte);
                setEquipeDetails(equipe, nomequipe, idResponsableEquipe, idUtilisateurs);
                Equipe equipe1 = saveEquie(equipe);
                handleEquipeSaveResult(equipe1, redirectAttributes,"create");
            }
        }
    }

    public void handleEquipeModification(Equipe equipe, String nomequipe, Long idResponsableEquipe, List<Long> idUtilisateurs,
                                          RedirectAttributes redirectAttributes, Utilisateur utilisateurConnecte, Long idsoc) {
        // Cas de la mise à jour
        Equipe equipeExist = equipeRepo.findById(equipe.getIdequipe()).orElse(null);
        Societe societe = societeRepo.findByIdsociete(idsoc);

        // Vérifier si le nom d'équipe existe déjà dans la même société existsByMailAndIdutilisateurNot
        Boolean existsByNomeqpAndSociete = equipeRepo.existsByNomequipeAndResponsableSocieteAndIdequipeNot(equipe.getNomequipe(), societe,equipe.getIdequipe());

        if (equipeExist == null) {
            equipeExist.setCreerpar(equipeExist.getCreerpar());
            redirectAttributes.addFlashAttribute("msgError", "Équipe non trouvée.");
        }

        // Vérifier si le nom d'équipe change
        boolean isNomEquipeChanged = !equipe.getNomequipe().equals(equipeExist.getNomequipe());

        if (idUtilisateurs == null || idUtilisateurs.isEmpty()) {
            if (existsByNomeqpAndSociete && isNomEquipeChanged) {
                redirectAttributes.addFlashAttribute("msgError", "Ce nom d'équipe existe déjà dans la société.");
            } else {
                equipeExist.setModifierpar(utilisateurConnecte);
                setEquipeDetails(equipeExist, nomequipe, idResponsableEquipe, idUtilisateurs);
                saveEquie(equipeExist);
                redirectAttributes.addFlashAttribute("msg", "Attention, vous avez créé une équipe sans membres, veuillez choisir des membres pour l'équipe.");
            }
        } else {
            if (existsByNomeqpAndSociete && isNomEquipeChanged) {
                redirectAttributes.addFlashAttribute("msgError", "Ce nom d'équipe existe déjà dans la société.");
            } else {
                equipeExist.setModifierpar(utilisateurConnecte);
                setEquipeDetails(equipeExist, nomequipe, idResponsableEquipe, idUtilisateurs);
                Equipe equipe1 = saveEquie(equipeExist);
                handleEquipeSaveResult(equipe1, redirectAttributes, "update");
            }
        }

    }


    public void setEquipeDetails(Equipe equipe, String nomequipe, Long idResponsableEquipe, List<Long> idUtilisateurs) {
        Utilisateur responsableEquipe = utilisateurService.findUtilisateurById(idResponsableEquipe);
        equipe.setResponsable(responsableEquipe);
        equipe.setNomequipe(nomequipe);
        if (idUtilisateurs != null && !idUtilisateurs.isEmpty()) {
            List<Utilisateur> membresEquipe = utilisateurService.findUtilisateursById(idUtilisateurs);
            equipe.setMembres(membresEquipe);
        }
    }

    public void handleEquipeSaveResult(Equipe equipe1, RedirectAttributes redirectAttributes,String cas) {
        if (equipe1 != null) {
            if (cas == "create") {
                redirectAttributes.addFlashAttribute("msg", "Équipe créée avec succès.");
            }else
            {
                redirectAttributes.addFlashAttribute("msg2", "Équipe modifiée avec succès");
            }
        } else {
            redirectAttributes.addFlashAttribute("msg1", "Échec de l'opération");
        }
    }



}
