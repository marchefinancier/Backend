package com.example.simulateurmarche.Iservices;

import com.example.simulateurmarche.entities.Action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.simulateurmarche.entities.Ordre;


import com.example.simulateurmarche.repositories.IActionRepository;
import com.example.simulateurmarche.services.ActionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j

public class ActtionServiceImpl implements ActionService {
    @Autowired
    private IActionRepository actionRepository;

    @Override
    public Action AjouterAction(Action action) {
        return actionRepository.save(action);
    }

    @Override
    public List<Action> AfiiferAction() {
        return actionRepository.findAll();
    }

    @Override
    public void  DeleteAction(Integer id_action) {actionRepository.deleteById(id_action);
    }


    @Override
    public Action UpdateAction(Action action) {
        return actionRepository.save(action);
    }

    @Override
    public Action findById(Integer id) {
        return actionRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> calculerStatistiquesOrdres(Action action) {
        Map<String, Object> statistiques = new HashMap<>();

        if (action.getList() == null || action.getList().isEmpty()) {
            statistiques.put("error", "Aucun ordre associé à cette action.");
            return statistiques;
        }

        int nombreOrdres = action.getList().size();
        int qteTotale = 0;
        int qteMin = Integer.MAX_VALUE;
        int qteMax = Integer.MIN_VALUE;

        int nombreTypeDifferent = 0;

        // Utilisation d'une structure de données (par exemple, une Map) pour stocker le nombre d'occurrences de chaque type
        // Ceci suppose que le type est une chaîne de caractères, vous devrez peut-être ajuster selon le type réel.
        Map<String, Integer> occurrencesType = new HashMap<>();

        for (Ordre ordre : action.getList()) {
            qteTotale += ordre.getQte();
            if (ordre.getQte() < qteMin) {
                qteMin = ordre.getQte();
            }
            if (ordre.getQte() > qteMax) {
                qteMax = ordre.getQte();
            }

            // Calcul du nombre de types différents
            if (!occurrencesType.containsKey(ordre.getType())) {
                occurrencesType.put(ordre.getType(), 1);
                nombreTypeDifferent++;
            } else {
                occurrencesType.put(ordre.getType(), occurrencesType.get(ordre.getType()) + 1);
            }
        }

        statistiques.put("nomAction", action.getNom());
        statistiques.put("nombreOrdres", nombreOrdres);
        statistiques.put("qteTotale", qteTotale);
        statistiques.put("qteMin", qteMin);
        statistiques.put("qteMax", qteMax);
        statistiques.put("nombreTypeDifferent", nombreTypeDifferent);
        statistiques.put("occurrencesType", occurrencesType);

        return statistiques;


    }
}
