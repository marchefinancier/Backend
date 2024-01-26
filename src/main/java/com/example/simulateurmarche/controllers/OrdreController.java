package com.example.simulateurmarche.controllers;

import com.example.simulateurmarche.Iservices.OrdreServiceImpl;
import com.example.simulateurmarche.entities.Ordre;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
@RequestMapping("/Ordre")
public class OrdreController {
    @Autowired
    private OrdreServiceImpl ordreService;

    @PostMapping("/ajouterOrdre")
    Ordre ajouterOrdre(@RequestBody Ordre ordre){
        return ordreService.AjouterOrdre(ordre);
    }


    @DeleteMapping("/deletOrdre/{id_ordre}")
    void DeleteOrdre(@PathVariable ("id_ordre") Integer id_ordre){
        ordreService.DeleteOrdre(id_ordre);
    }

    @PutMapping("/ModifierOrdre")
    Ordre UpdateOrdre(@RequestBody Ordre ordre){
        return ordreService.UpdateOrdre(ordre);
    }

    @PutMapping("/assignerOrderToAction/{id_ordre}/{id_action}")
     public Ordre Assigner(@PathVariable("id_ordre") Integer id_ordre,@PathVariable("id_action") Integer id_action){
        return ordreService.AffecterOrdreToRequest(id_ordre,id_action) ;
    }


}
