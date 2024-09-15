package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Equipe;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

public interface EquipeService {

    public Equipe saveEquie(Equipe e);

    public List<Equipe> findAllEquipes();
    public void suppEquipe(Long id);

    public Equipe findEquipByID(Long ide);

    void deleteEquipe(Long id);

    public List<Long> findAllIdsReso();
    public void handleEquipeModification(Equipe equipe, String nomequipe, Long idResponsableEquipe, List<Long> idUtilisateurs,
                                         RedirectAttributes redirectAttributes, Utilisateur utilisateurConnecte, Long idsoc);
    public void handleEquipeCreation(Equipe equipe, String nomequipe, Long idResponsableEquipe,
                                     List<Long> idUtilisateurs, boolean isResponsableOfAnotherTeam,
                                     RedirectAttributes redirectAttributes, Utilisateur utilisateurConnecte, Long idsoc);


    }
