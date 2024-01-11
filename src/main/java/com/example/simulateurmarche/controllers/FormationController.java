package com.example.simulateurmarche.controllers;


import com.example.simulateurmarche.Iservices.IFormationService;
import com.example.simulateurmarche.entities.Formation;
import com.example.simulateurmarche.entities.User;
import com.example.simulateurmarche.repositories.FormationRepository;
import com.example.simulateurmarche.services.FormationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/Formation")
public class FormationController {
    //private FormationService iFormationService;
     @Autowired
    FormationService formationService;
     @Autowired
    FormationRepository formationRepository;
    @PostMapping("/ajouterFormation/{userid}")
    public ResponseEntity<Formation> addFormation(@RequestBody Formation formation, @PathVariable("userid") long iduser){
        return ResponseEntity.ok(formationService.addFormation(formation,iduser));
    }
    @PostMapping(value = "/ajouterFormationImage/{userid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Formation> addFormationImage(
            @RequestParam("formation") String formationJson,
            @RequestPart("image") MultipartFile image,
            @PathVariable("userid") long iduser
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        Formation formation;
        try {
            formation = objectMapper.readValue(formationJson, Formation.class);
        } catch (JsonProcessingException e) {
            // Handle JSON parsing exception
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(formationService.addformationImage(formation, image, iduser));
    }
    @PutMapping(value="/updateWithImage/{formationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Formation> updateFormationWithImage(
            @PathVariable("formationId") int formationId,
            @RequestPart("updatedFormation") String updatedFormationJson,
            @RequestPart("newImage") MultipartFile newImage) {

        ObjectMapper objectMapper = new ObjectMapper();
        Formation updatedFormation;

        try {
            updatedFormation = objectMapper.readValue(updatedFormationJson, Formation.class);
        } catch (JsonProcessingException e) {
            // Handle JSON parsing exception
            return ResponseEntity.badRequest().build();
        }

        Formation updatedFormationWithImage = formationService.updateFormationWithImage( updatedFormation, formationId,newImage);

        if (updatedFormationWithImage != null) {
            return ResponseEntity.ok(updatedFormationWithImage);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/uploadImage/{formationId}")

    public ResponseEntity<String> uploadImage(
            @PathVariable int formationId,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {

        try {
            String imageName = imageFile.getOriginalFilename();

            Optional<Formation> optionalFormation = formationRepository.findById(formationId);
            if (optionalFormation.isPresent()) {
                Formation formation = optionalFormation.get();
                formation.setFormationimage(imageName);

                Path imagePath = Paths.get("src/main/resources/images", imageName);
                Files.write(imagePath, imageFile.getBytes());

                formationRepository.save(formation);



                return ResponseEntity.ok("Image uploaded successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Formation not found.");
            }
        } catch (IOException e) {
            // Handle the exception appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
        }
    }
    @PutMapping("/modifierFormation")
    public ResponseEntity<String> editFormation(@RequestBody Formation formation){

        formationService.editFormation(formation);
        return ResponseEntity.ok("Edited successfully.");
    }
    @GetMapping("/afficherAll")
    public List<Formation> afficherAll(){
        return formationService.selectAllFormation();
    }
    @GetMapping("/afficherFormationById/{formationId}")
    public Formation afficherFormationById(@PathVariable int formationId ) {

        return formationService.SelectFormationById(formationId);
    }
    @GetMapping("/afficherFormationByOwnerId/{userid}")
    public List<Formation> afficherFormationByOwnerId(@PathVariable("userid") int userid) {

        return formationService.SelectFormationByuser(userid);
    }
    @DeleteMapping("/SupprimerFormationById/{formationId}")
    public  void supprimerFormationById(@PathVariable int formationId){
        formationService.deleteFormationById(formationId);
    }
    @DeleteMapping("/SupprimerAll")
    public ResponseEntity<String> SupprimerAll() {
        formationService.deleteAllFormations();
        return ResponseEntity.ok("Deleted successfully.");
    }
    @GetMapping("/all-formation-images")
    public ResponseEntity<List<String>> getAllFormationImages() {
        List<String> allFormationImages = formationService.getAllFormationImages();

        if (allFormationImages.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(allFormationImages, HttpStatus.OK);
    }
    @GetMapping("/getImageByFormationId/{formationId}")
    public ResponseEntity<byte[]> getImageByFormationId(@PathVariable int formationId) {
        Optional<Formation> optionalFormation = formationRepository.findById(formationId);

        if (optionalFormation.isPresent()) {
            Formation formation = optionalFormation.get();
            String imageName = formation.getFormationimage();

            if (imageName != null) {
                try {
                    Path imagePath = Paths.get("src/main/resources/images", imageName);
                    byte[] imageBytes = Files.readAllBytes(imagePath);

                    // Assuming the content type is image/png for this example
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.IMAGE_PNG );
                    headers.setContentType(MediaType.IMAGE_JPEG);
                    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}
