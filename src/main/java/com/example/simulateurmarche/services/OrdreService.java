package com.example.simulateurmarche.services;

import com.example.simulateurmarche.entities.Ordre;

import java.util.List;

public interface OrdreService  {

    Ordre AjouterOrdre(Ordre ordre);

    void DeleteOrdre(Integer id_ordre);
    Ordre UpdateOrdre (Ordre ordre);
    Ordre AffecterOrdreToRequest(Integer id_ordre,Integer id_action);
}
