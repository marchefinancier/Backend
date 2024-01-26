package com.example.simulateurmarche.Iservices;

import com.example.simulateurmarche.entities.Action;
import com.example.simulateurmarche.entities.Ordre;

import java.util.List;

import com.example.simulateurmarche.repositories.IActionRepository;
import com.example.simulateurmarche.repositories.IOrdreRepository;
import com.example.simulateurmarche.services.OrdreService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@AllArgsConstructor

public class OrdreServiceImpl implements OrdreService {

    @Autowired
    private IOrdreRepository oRepository;

    @Autowired
    private IActionRepository actionRepository;


    @Override
    public Ordre AjouterOrdre(Ordre ordre) {
       return oRepository.save(ordre);
    }



    @Override
    public void DeleteOrdre(Integer id_ordre) {
        oRepository.deleteById(id_ordre);

    }

    @Override
    public Ordre UpdateOrdre(Ordre ordre) {
        return oRepository.save(ordre);
    }

    @Override
    public Ordre AffecterOrdreToRequest(Integer id_ordre, Integer id_action) {
        Ordre ordre=oRepository.findById(id_ordre).orElse(null);
        Action action=actionRepository.findById(id_action).orElse(null);
            ordre.setAction(action);
            return oRepository.save(ordre);



        }


}
