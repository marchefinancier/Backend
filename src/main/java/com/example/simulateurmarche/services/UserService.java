package com.example.simulateurmarche.services;


import com.example.simulateurmarche.DTO.CountType;
import com.example.simulateurmarche.Iservices.IUserService;
import com.example.simulateurmarche.entities.ERole;
import com.example.simulateurmarche.entities.Formation;
import com.example.simulateurmarche.entities.Role;
import com.example.simulateurmarche.entities.User;
import com.example.simulateurmarche.repositories.FormationRepository;
import com.example.simulateurmarche.repositories.RoleRepository;
import com.example.simulateurmarche.repositories.UserRepository;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Random;
import java.util.stream.Stream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


@Slf4j
@Service
public class UserService implements IUserService {
    @Value("${spring.mail.username}")
    private String sender;
    private final JavaMailSender javaMailSender;

    public UserService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    @Autowired
    UserRepository userRepo;
    @Autowired
    RoleRepository roleRepo;
    @Autowired
    FormationRepository formrep;
    @Override
    public List<User> getUsers() {

        return (List<User>) userRepo.findAll();
    }
    @Override
    public User saveUser(User user) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setStateuser(true);

        return userRepo.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public User findById(Long id) {
        return userRepo.findById(id).orElse(null);
    }




    @Override
    public User updateUser(User user) {
        User updateUser = userRepo.findById(user.getId()).get();
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
        return userRepo.save(updateUser);
    }
    //service avancés
    @Override
    public User addRoleToUser(String username, ERole roleName) {


        User user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleName).orElse(null);
        user.getRoles().add(role);
        return userRepo.save((user));

    }

    // recherche
    @Override
    public User getUser(String username) {

        return userRepo.findByUsername(username);
    }

    @Override
    public User getUserByMail(String mail) {
        return this.userRepo.findByEmail(mail);
    }

    @Override
    public List<User> retrieveUserByState(boolean stateUser) {
        return (List<User>) userRepo.findByStateuser(stateUser);
    }
    @Override
    public List<User> retrieveUserByAddress(String adressUser) {
        return userRepo.findByAddress(adressUser);
    }
    //activation user
    @Override
    public User activateUser(User user1)
    { User user=userRepo.findByUsername(user1.getUsername());
        if(user.isStateuser()==false)
        {
            user.setStateuser(true);
        }
        else if(user.isStateuser()==true)
        {
            user.setStateuser(false);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setEmail(user1.getEmail());
        user.setName(user1.getName());
        user.setPrenom(user1.getPrenom());
        user.setDatenaissance(user1.getDatenaissance());
        user.setTel(user1.getTel());
        user.setAddress(user1.getAddress());
        return userRepo.save(user);
    }
    @Override
    public User desactivateUser(User user1)
    { User user=userRepo.findByUsername(user1.getUsername());
        if(user.isStateuser()==true)
        {
            user.setStateuser(false);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setEmail(user1.getEmail());
        user.setName(user1.getName());
        user.setPrenom(user1.getPrenom());
        user.setDatenaissance(user1.getDatenaissance());
        user.setTel(user1.getTel());
        user.setAddress(user1.getAddress());
        return userRepo.save(user);
    }
    // update password
    public User updatePassword(String emailUser, String newPassword) {
        User u = userRepo.findByEmail(emailUser);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        u.setPassword(encodedPassword);
       return userRepo.save(u);
    }
    public List<User>  findAllByOrderBOrderByRolesDesc()
    {
        return userRepo.findAllByOrderByRolesDesc();
    }


    // stats nbre of users actives or desactive


    public List<CountType> statistque()
    {
        return userRepo.statistque();
    }

    public List<CountType> statistque1()
    {
        return userRepo.adresse();
    }


    @Override
    public boolean sendResetPasswordCode(String email) {
            User d = userRepo.findByEmail(email);
            log.info("hhhhhhhh"+d);
            if (d == null) {
                return false; // User not found with the provided email
            }else{

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom(sender);
                mailMessage.setTo(email);
                String code =this.CodeGen();
                mailMessage.setText("This is a non-reply message from dhia\n " + "Dear Client \n"
                        + "Please follow the following link to reset your password: \n" +code);
                mailMessage.setSubject("Password Reset");
                d.setCoderesetpassword(code);
                userRepo.save(d);
                javaMailSender.send(mailMessage);
                return true;


    }
    }

    @Override
    public User verifyUserPassResetCode(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User BlocUser(long id) {
         User u = userRepo.findById(id).get();
          u.setBlocked(true);
           return userRepo.save(u);

    }
    @Override
    public User unblockuser(long id) {
        User u = userRepo.findById(id).get();
        u.setBlocked(false);
        return userRepo.save(u);

    }

    @Override
    public Formation participateFormation(int fid, long uid) {
        User user = userRepo.findById(uid).orElse(null);
        Formation formation = formrep.findById(fid).orElse(null);

       formation.getUsers().add(user);
       user.getFormationss().add(formation);
       userRepo.save(user);
       return formrep.save(formation);


    }

    @Override
    public int getpartitipationperuser(long iduser) {

        User u =userRepo.findById(iduser).get();
        return u.getFormationss().size();
    }


    public  static String CodeGen(){
        String caracters="ABCDEFJHIJKLMNOPQRSTUVWXYZ0123456789";
        String randomcode="";
        int lenght=6;
        Random rand=new Random();
        char[] text = new char[lenght];
        for(int i=0;i<lenght;i++){
            text[i]=caracters.charAt(rand.nextInt(caracters.length()));
        }
        for(int i=0;i<lenght;i++){
            randomcode+=text[i];
        }
        return randomcode ;

    }

    // stats nbre of max age
    public  List<CountType> statistqueAge()
    {
        return userRepo.minmaxeage();
    }






// forget password mailing

    /*public void forgotpass(String emailuser) {

        User d = userRepo.findByEmail(emailuser);
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(emailuser);
            mailMessage.setText("This a non reply message from malak\n " + "Dear Client \n"
                    + "Please follow the following link to reset your password: \n" + "http://localhost:8090/user/updatepassword");
            mailMessage.setSubject("password reset");
            javaMailSender.send(mailMessage);
            //return "Mail Sent Successfully...";
        }*/
    public boolean forgotpass(String emailuser) {
        try {
            User d = userRepo.findByEmail(emailuser);
            log.info("hhhhhhhh"+d);
        if (d == null) {
                return false; // User not found with the provided email
            }else{

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(emailuser);
            mailMessage.setText("This is a non-reply message from dhia\n " + "Dear Client \n"
                    + "Please follow the following link to reset your password: \n" + "http://localhost:4200/update");
            mailMessage.setSubject("Password Reset");

            javaMailSender.send(mailMessage);
            return true;
             }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return false; // Failed to send mail
        }
    }




    public boolean ifEmailExist(String email) {
        return userRepo.existsByEmail(email);
    }
    /// recherche stream
    @Override
    public List<User> searchh(String s) {

        return userRepo.findAll().stream().filter(user -> user.getName()!=null )
                .filter(user -> user.getName().contains(s)  ).collect(Collectors.toList());


    }

// scheduler

    // @Scheduled(fixedRate = 5000)
    @Scheduled(cron = "0 0 8 * * MON")
    public List<User> getdisable()
    {

        return userRepo.getusersdisable();
    }

    // export pdf

    @Override
    public ByteArrayInputStream userExport(List<User> users)
    {
        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
            Paragraph para = new Paragraph("Liste des Utilisateurs", font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);
            PdfPTable table = new PdfPTable(5);


            Stream.of("nom", "adresse", "email", "tel", "ETAT").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                com.itextpdf.text.Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                header.setBackgroundColor(BaseColor.BLUE);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setBorderWidth(1);
                header.setPhrase(new Phrase(headerTitle, headFont));
                table.addCell(header);
            });
            for (User user : users) {
                PdfPCell NomCell = new PdfPCell(new Phrase(user.getName()));
                NomCell.setPaddingLeft(1);
                NomCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                NomCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(NomCell);


                PdfPCell adresseCell = new PdfPCell(new Phrase(user.getAddress()));
                adresseCell.setPaddingLeft(1);
                adresseCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                adresseCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(adresseCell);


                PdfPCell emailCell = new PdfPCell(new Phrase(user.getEmail()));
                emailCell.setPaddingLeft(1);
                emailCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                emailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(emailCell);
                PdfPCell telCell = new PdfPCell(new Phrase(user.getTel()));
                telCell.setPaddingLeft(1);
                telCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                telCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(telCell);

                PdfPCell siteCell = new PdfPCell(new Phrase(String.valueOf(user.isStateuser())));
                siteCell.setPaddingLeft(1);
                siteCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                siteCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(siteCell);
            }
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());

        // return null;
    }



}
