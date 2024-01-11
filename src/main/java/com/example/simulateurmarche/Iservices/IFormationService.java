package com.example.simulateurmarche.Iservices;

import com.example.simulateurmarche.entities.Formation;
import com.example.simulateurmarche.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFormationService {
    Formation addFormation (Formation formation, long userid);
    Formation editFormation (Formation formation);
    List<Formation> selectAllFormation();
    Formation SelectFormationById(int formationId);
   List <Formation> SelectFormationByuser(int userid);
    public List<String> getAllFormationImages() ;


    void deleteFormationById(int formationId);
    void deleteAllFormations();


    /// ajouter avec image
    Formation addformationImage (Formation formation , MultipartFile image , long id ) ;
    public Formation updateFormationWithImage(Formation updatedFormation,int idformation, MultipartFile newImage);
}
