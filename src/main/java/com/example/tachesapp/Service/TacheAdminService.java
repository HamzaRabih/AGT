package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Tache;
import com.example.tachesapp.Model.Utilisateur;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

public interface TacheAdminService {
    List<Tache> getTachesParentsByidUtilisateur(Long idUtilisateur);

    List<Utilisateur> getPropritairByIdUtilisateur(Long idUtilisateur);

    List<Utilisateur> getDestinatairByIdUtilisateur(Long idUtilisateur);

    void deleteTache(Long id, RedirectAttributes redirectAttributes);

    void GestionTacheAdmin(Model model, Authentication authentication);

    void updateTacheAdmin(Tache tache, RedirectAttributes redirectAttributes, Authentication authentication);

    ResponseEntity<List<Tache>> getAllTasks(Authentication authentication);

    void loadReceivers(Utilisateur utilisateur, Model model);
}
