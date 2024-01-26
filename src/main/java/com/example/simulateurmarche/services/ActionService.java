package com.example.simulateurmarche.services;

import com.example.simulateurmarche.entities.Action;

import java.util.List;
import java.util.Map;

public interface ActionService {
    Action AjouterAction(Action action);
    List<Action> AfiiferAction ();
    void DeleteAction(Integer id_action);
    Action UpdateAction(Action action);

    Action findById(Integer id);

    Map<String, Object> calculerStatistiquesOrdres(Action action);
}
