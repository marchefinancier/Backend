package com.example.simulateurmarche.services;

import com.example.simulateurmarche.Iservices.IFormationService;
import com.example.simulateurmarche.entities.Formation;
import com.example.simulateurmarche.entities.Status;
import com.example.simulateurmarche.entities.User;
import com.example.simulateurmarche.repositories.FormationRepository;
import com.example.simulateurmarche.repositories.UserRepository;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j

public class FormationService implements IFormationService {
    private FormationRepository formationRepository;
    @Autowired
    UserRepository userrep;
    @Override
    public Formation addFormation(Formation formation, long userid) {

        User u =userrep.findById(userid).get();
        formation.setUser(u);

            return formationRepository.save(formation);


    }


    @Override
    public Formation editFormation(Formation formation) {
        Formation f = formationRepository.findById(formation.getFormation_Id()).get();
        f.setTitre(formation.getTitre());
        f.setDateDebut(formation.getDateDebut());
        f.setDateFin(formation.getDateFin());
        f.setEtat(f.getEtat());
        f.setDuree(formation.getDuree());
        f.setTypeFormation(formation.getTypeFormation());
        f.setDescriptionFormation(formation.getDescriptionFormation());

        return  formationRepository.save(f);

    }

    @Override
    public List<Formation> selectAllFormation() {
        return formationRepository.findAll();
    }

    @Override
    public Formation SelectFormationById(int formationId) {
        return formationRepository.findById(formationId).get();
    }

    @Override
    public List<Formation> SelectFormationByuser(int userid) {
        return formationRepository.findFormationsByUserIdNative(userid);
    }


    @Override
    public void deleteFormationById(int formationId) {
        formationRepository.deleteById(formationId);

    }

    @Override
    public void deleteAllFormations() {
        formationRepository.deleteAll();

    }
    /// formation image
    @Override
    public Formation addformationImage(Formation formation, MultipartFile image , long id ) {

        User u = userrep.findById(id).get() ;
        try {
            String imageName = image.getOriginalFilename();
            formation.setFormationimage(imageName);
            InputStream inputStream = image.getInputStream();
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);

            // Create a new file using the destination path
            File destinationFile = new File("src\\main\\resources\\images\\"+formation.getFormationimage());


            // Write the bytes to the new file
            FileUtils.writeByteArrayToFile(destinationFile, bytes);
            formation.setUser(u);
            return formationRepository.save(formation);
        }catch (IOException e){log.error("Io problem"+e.getMessage());
         return  null ;

        }
    }

        public Formation updateFormationWithImage(Formation updatedFormation,int formationid, MultipartFile newImage) {
            // Retrieve the existing Formation from the database
            Formation existingFormation = formationRepository.findById(formationid).get();
 User u =userrep.findById(existingFormation.getUser().getId()).get();

            // Update Formation fields with the new values
            existingFormation.setTitre(updatedFormation.getTitre());
            existingFormation.setDateDebut(updatedFormation.getDateDebut());
            existingFormation.setDateFin(updatedFormation.getDateFin());
            existingFormation.setEtat(updatedFormation.getEtat());
            existingFormation.setUser(u);
            existingFormation.setDuree(updatedFormation.getDuree());
            existingFormation.setTypeFormation(updatedFormation.getTypeFormation());
            existingFormation.setDescriptionFormation(updatedFormation.getDescriptionFormation());

            // Update Formation image if a new image is provided
            if (newImage != null && !newImage.isEmpty()) {
                try {
                    String imageName = newImage.getOriginalFilename();
                    if(imageName!=existingFormation.getFormationimage()){
                        existingFormation.setFormationimage(imageName);
                        InputStream inputStream = newImage.getInputStream();
                        byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);

                        // Create a new file using the destination path
                        File destinationFile = new File("src\\main\\resources\\images\\"+imageName);
                        // Write the bytes to the new file
                        FileUtils.writeByteArrayToFile(destinationFile, bytes);
                        return formationRepository.save(existingFormation);
                    }




                }catch (IOException e){log.error("Io problem"+e.getMessage());
                    return  null ;

                }
            }

            // Save the updated Formation in the repository
            return formationRepository.save(existingFormation);

    }

    public List<String> getAllFormationImages() {
        List<Formation> formations = formationRepository.findAll();
        List<String> allFormationImages = new ArrayList<>();

        for (Formation formation : formations) {
            String image = formation.getFormationimage(); // Assuming formationimage holds the image URL/path
            if (image != null && !image.isEmpty()) {
                allFormationImages.add(image);
            }
        }

        return allFormationImages;
    }

}