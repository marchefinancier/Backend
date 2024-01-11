package com.example.simulateurmarche.controllers;

import com.example.simulateurmarche.DTO.CountType;
import com.example.simulateurmarche.entities.ERole;
import com.example.simulateurmarche.entities.Formation;
import com.example.simulateurmarche.entities.Role;
import com.example.simulateurmarche.entities.User;
import com.example.simulateurmarche.repositories.FormationRepository;
import com.example.simulateurmarche.repositories.UserRepository;
import com.example.simulateurmarche.services.RoleService;
import com.example.simulateurmarche.services.UserService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.util.List;
@Slf4j
@CrossOrigin(origins = "http://localhost:4200") // Replace with your Angular app's origin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    UserRepository userRepo;
    @Autowired
    FormationRepository formrepo;


    @GetMapping("/allUsers")
    public List<User> retrieveAllUsers() {
        List<User> List = userService.getUsers();
        return List;

    }

    @PostMapping("/addUser")
    public User addUser(@RequestBody User user) {
        return userService.saveUser(user);

    }
    @PutMapping("/blockuser/{id}")
    public  User BlockUser(@PathVariable("id") long id ){
        return userService.BlocUser(id);
    }
    @PutMapping("/unblockuser/{id}")
    public  User unblockuser(@PathVariable("id") long id ){
        return userService.unblockuser(id);
    }

    @PutMapping("/modifUser")
    public User updateUser(@RequestBody User p) {
        return userService.updateUser(p);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<User> getUserbyId(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updateUser = userService.findById(id);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        updateUser.setPassword(hashedPassword);
        updateUser.setName(user.getName());
        updateUser.setPrenom(user.getPrenom());
        updateUser.setTel(user.getTel());
        updateUser.setAddress(user.getAddress());
        updateUser.setUsername(user.getUsername());
        updateUser.setDatenaissance(user.getDatenaissance());
        updateUser.setEmail(user.getEmail());


        User updateBoite = userService.updateUser(updateUser);
        return ResponseEntity.ok(updateBoite);


    }

    @DeleteMapping("/deleteUser/{id}")
    public void DeleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }

    // partie service avancés

    //  ajout role to user

    @PostMapping("/add/{user}/{role}")
    public ResponseEntity<User> assignusertorole(@PathVariable Long user, @PathVariable Long role) {
        User user1 = userService.findById(user);

        Role role1 = roleService.findID(role);

        // User updatedDonation = user1;
        user1.getRoles().add(role1);
        userRepo.save(user1);
        return ResponseEntity.ok(user1);
    }


    @PutMapping("/add/{user}/{role}")
    public User addRoletoUser(@PathVariable String user, @PathVariable ERole role) {
        return userService.addRoleToUser(user, role);
    }


    //RECHERCHE
    @GetMapping("/retrieveUserByState/{state}")
    @ResponseBody
    public List<User> retrieveUserByState(@PathVariable("state") boolean stateUser) {
        return userService.retrieveUserByState(stateUser);
    }

    @GetMapping("/userbyusername")
    @ResponseBody
    public List<User> retrieveuserbynom(@RequestParam String username) {
        return (List<User>) userService.getUser(username);
    }

    @GetMapping("/userbyemail")
    @ResponseBody
    public User retrieveuserbyemail(@RequestParam String email) {
        return userService.getUserByMail(email);
    }

    // Activation User
    @PutMapping("/activateUser")
    public User activateUser(@RequestBody User user) {
        return userService.activateUser(user);
    }

    // desactiver User


    // Modifier Password
    @PutMapping("/updatepassword/{emailUser}/{password}")
    User updatePassword(@PathVariable("emailUser") String emailUser, @PathVariable("password") String newPassword
    ) {
        return userService.updatePassword(emailUser, newPassword);
    }

    @PutMapping("/updatepassword")
    public ResponseEntity<?> processResetPassword(HttpServletRequest request) {
        String mail = request.getParameter("email");
        String password = request.getParameter("password");

        User user = userService.getUserByMail(mail);

        if (user == null) {

            String mesg = "Invalid mail";

            return ResponseEntity.ok(mesg);

        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            userService.updatePassword(user.getEmail(), passwordEncoder.encode(password));
            String mesg = "You have successfully changed your password.";
            return ResponseEntity.ok(mesg);

        }

    }

    @GetMapping("/tri")
    public List<User> findAll()
    {
        return userService.findAllByOrderBOrderByRolesDesc();
    }
    // PDF
    @GetMapping("/exportpdf")
    public ResponseEntity<InputStreamResource> exportPdf() {
        List<User> users = (List<User>) userService.getUsers();
        ByteArrayInputStream bais = userService.userExport(users);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline;filename=user.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(bais));
    }

    // statistique User

    @GetMapping("/stat")
    public List<CountType> statistque() {
        return userService.statistque();
    }

    @GetMapping("/stati")
    public List<CountType> statistque1(){
        return userService.statistque1();
    }

    @GetMapping("/statage")
    public List<CountType> stats() {
        return userService.statistqueAge();
    }



    // forget password API Mail
    @GetMapping("/sendme/{emailUser}")
    public boolean forgotpass(@PathVariable("emailUser") String emailUser) {

         log.info(emailUser);
           return userService.sendResetPasswordCode(emailUser);

    }

    @GetMapping("/verify-resetpasscode/{email}")
    public User verifyUserPassResetCode(@PathVariable("email") String email) {
    return  userService.verifyUserPassResetCode(email);
    }



    // add Organisation to User

    @GetMapping("/findstream")
    public List<User> searchh(@RequestParam String nom) {
        return userService.searchh(nom);
    }


    // Scheduler
    @GetMapping("/scheduler")
    public List<User> getdisabledUsers() {
        return userService.getdisable();
    }

    @PostMapping("/participate/{fid}/{uid}")
    @ResponseBody
    public ResponseEntity<Formation> participateFormation(@PathVariable("fid") int fid, @PathVariable("uid") long uid) {
        if (this.getpartitipationperuser(uid)<3) {
            return ResponseEntity.ok(userService.participateFormation(fid, uid));
        }else {
            return null;
        }
    }


    @GetMapping("/formationbyuser/{iduser}")
    int getpartitipationperuser(@PathVariable("iduser") long userid){

        return userService.getpartitipationperuser(userid);
    }


    }