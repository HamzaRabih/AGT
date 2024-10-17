package com.example.tachesapp.Service;

import com.example.tachesapp.Model.Tache;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface MemoireService {
    public void createMemoire(Tache tache, RedirectAttributes redirectAttributes);
    public void updateMemoire(Tache tache, RedirectAttributes redirectAttributes);
    public void validateMemo(Long id);
}
