package com.example.simulateurmarche.Iservices;


import com.example.simulateurmarche.DTO.CountType;
import com.example.simulateurmarche.entities.ERole;
import com.example.simulateurmarche.entities.Formation;
import com.example.simulateurmarche.entities.User;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface IUserService {

    // CRUD
    List<User> getUsers();
    User saveUser(User user);
    void deleteUser(Long id);
    User updateUser(User user);

    // service avancés
    User addRoleToUser(String username, ERole roleName);
    User getUser(String username);
    User getUserByMail(String mail);
    User activateUser(User user);
    List<User> retrieveUserByState(boolean stateUser);
    List<User> retrieveUserByAddress(String adressUser);
    User updatePassword(String emailUser, String newPassword);

    public boolean forgotpass(String emailuser);
    User desactivateUser(User user1);
    public List<User> searchh(String s);
    public boolean ifEmailExist(String email);

    public List<User> getdisable();
    public User findById(Long id) ;

    // pdf
    public ByteArrayInputStream userExport(List<User> users);

    // statistique
    List<CountType> statistque();
    public     List<CountType> statistqueAge();

    public List<CountType> statistque1();


    public boolean sendResetPasswordCode(String email) ;
    public User verifyUserPassResetCode(String email);
    public User BlocUser(long id);
    public User unblockuser(long id);

    public Formation participateFormation(int fid , long uid );

    int getpartitipationperuser(long iduser);
}
