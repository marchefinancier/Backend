package com.example.simulateurmarche.controllers;


import com.example.simulateurmarche.Iservices.ActtionServiceImpl;
import com.example.simulateurmarche.entities.Action;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@AllArgsConstructor
@RequestMapping("/FinnancialInstrument")
public class ActionController {
    @Autowired


    private ActtionServiceImpl actionService;



    @PostMapping("/ajouterAction")
        Action ajouterAction (@RequestBody Action action){
        return actionService.AjouterAction(action);
    }
    @GetMapping("/AfficherAction")
    List<Action> afficherAction (){
        return actionService.AfiiferAction();
    }

    @DeleteMapping("/delete/{id}")
    void deleteAction(@PathVariable("id") Integer id_action){
        actionService.DeleteAction(id_action);

    }
    @PutMapping("/updateAction")
    Action updateAction(@RequestBody Action action){
        return actionService.UpdateAction(action);
    }

    @GetMapping("/{id}/statistiques")
    public ResponseEntity<Map<String, Object>> calculerStatistiquesOrdres(@PathVariable Integer id) {
        Action action = actionService.findById(id);
        if (action == null) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Action non trouvée"), HttpStatus.NOT_FOUND);
        }

        Map<String, Object> statistiques = actionService.calculerStatistiquesOrdres(action);
        return new ResponseEntity<>(statistiques, HttpStatus.OK);
    }
}
