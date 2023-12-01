package com.example.simulateurmarche.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;



import com.example.simulateurmarche.entities.ConfirmationToken;
import com.example.simulateurmarche.entities.ERole;
import com.example.simulateurmarche.entities.Role;
import com.example.simulateurmarche.entities.User;
import com.example.simulateurmarche.jwt.JwtUtils;
import com.example.simulateurmarche.payload.request.LoginRequest;
import com.example.simulateurmarche.payload.request.SignupRequest;
import com.example.simulateurmarche.payload.response.*;
import com.example.simulateurmarche.repositories.ConfirmationTokenRepository;
import com.example.simulateurmarche.repositories.RoleRepository;
import com.example.simulateurmarche.repositories.UserRepository;
import com.example.simulateurmarche.security.UserDetailsImpl;
import com.example.simulateurmarche.services.UserService;
import com.example.simulateurmarche.userFunction.AccountResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:4200") // Replace with your Angular app's origin
@RestController
@RequestMapping("/api-auth")
@Slf4j
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Value("${google.id}")
    private String idClient;

    @Value("${secretPsw}")
    String secretPsw;
    private final JavaMailSender javaMailSender;


    int n = 5;

    String email;

    public AuthController(JavaMailSender javaMailSender)
    {
        this.javaMailSender = javaMailSender;
    }



    // SIGN IN
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
          //login
        try{
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());
            if (userDetails.getUser().isBlocked()==true) {
                log.info(""+userDetails.getUser().isBlocked());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is Blocked");
            }
        if (userDetails.getUser().isStateuser()==true) {

            // User account is verified
            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUser().getId(), userDetails.getUsername(),
                    userDetails.getUser().getEmail(), roles, userDetails.getUser().isStateuser(),
                    userDetails.getUser().getName(), userDetails.getUser().getPrenom(),
                    userDetails.getUser().getTel()));

        }
       else {
            // User account is not verified
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not verified . Please verify your account.");
        }
    } catch (DisabledException e) {
            // Account is disabled or not verified
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not verified. Please verify your account.");
        } catch (AuthenticationException e) {
            // Incorrect username or password
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password.");
        }

 }

    private Role getRole(ERole roleName) {
        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        return roleOptional.orElseGet(() -> roleRepository.save(new Role(roleName)));
    }
    //SIGNUP

    @PostMapping("/signup")
    public AccountResponse registerUser( @RequestBody SignupRequest signUpRequest) {


        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()), signUpRequest.getAddress(), signUpRequest.getTel(),
                signUpRequest.getName(), signUpRequest.getPrenom(),signUpRequest.getBirth());

        // Affecter un role
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();



        strRoles.forEach(role -> {
                    switch (role) {
                        case "ADMIN":
                            roles.add(getRole(ERole.ADMIN));


                            break;
                        case "CONSULTANT":
                            roles.add(getRole(ERole.CONSULTANT));

                            break;

                        default:
                            roles.add(getRole(ERole.CLIENT));
                    }
                }
        );


        AccountResponse accountResponse = new AccountResponse();
        boolean result = userRepository.existsByEmail(signUpRequest.getEmail());
        if (result) {
            accountResponse.setResult(0);

        } else

        {

            user.setRoles(roles);
            userRepository.save(user);
            accountResponse.setResult(1);
            //user.setStateuser(false);
            ConfirmationToken confirmationToken = new ConfirmationToken(user);
            confirmationTokenRepository.save(confirmationToken);
            String appUrl="http://localhost:8089/api-auth/confirm-account?token=" ;

            String message = "<html><body><p>Bonjour " + signUpRequest.getUsername() + ",</p>" +
                    "<p>Votre inscription sur notre site a été effectuée avec succès.</p>" +
                    "<p>Veuillez cliquer sur le lien suivant pour vérifier votre compte :</p>" +
                    "<table border='1'><tr><td>URL</td><td>" + appUrl  + confirmationToken.getConfirmationToken() + "</td></tr>" +
                    "<tr><td>Code de vérification</td><td>" + confirmationToken.getConfirmationToken() + "</td></tr></table>" +
                    "<p>Merci de votre confiance.</p></body></html>";

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(signUpRequest.getEmail());
            mailMessage.setSubject("Inscription réussie");
            mailMessage.setText("Un email de confirmation vous a été envoyé à l'adresse " + signUpRequest.getEmail() + ". Veuillez suivre les instructions pour vérifier votre compte.");

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = null;
            try {
                helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setTo(mailMessage.getTo());
                helper.setSubject(mailMessage.getSubject());
                helper.setText(message, true);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }


            javaMailSender.send(mimeMessage);





        }
        log.info("" + ResponseEntity.ok(new MessageResponse("User registered successfully!")));
        return accountResponse;
    }






    static String getAlphaNumericString(int n) {

        String randomNumber = "0123456789";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int) (randomNumber.length() * Math.random());

            // add Character one by one in end of sb
            sb.append(randomNumber.charAt(index));
        }

        return sb.toString();
    }



    private LoginResponse login(String email, String username, String prenom ,String nom) {
        boolean result = userService.ifEmailExist(email); // t // f
        Set<Role> roles = new HashSet<>();
        roles.add(getRole(ERole.CLIENT));
        if (!result) {

            User user = new User();
            user.setEmail(email);
            user.setPassword(encoder.encode("root1234"));
            user.setUsername(username);
            user.setStateuser(true);
            user.setPrenom(prenom);
            user.setName(nom);
            user.setRoles(roles);
            log.info("test",user);
            userRepository.save(user);
        }
        JwtLogin jwtLogin = new JwtLogin();
        jwtLogin.setUsername(username);
        jwtLogin.setPassword("root1234");
        jwtLogin.setNom(nom);
        jwtLogin.setPrenom(prenom);

        log.info("jwt:" + jwtLogin.getUsername());
        log.info("jwt:" + jwtLogin.getPassword());

        return jwtUtils.login(jwtLogin);
    }

    // Social Login
    @PostMapping("/google")
    public LoginResponse loginWithGoogle(@RequestBody TokenDto tokenDto) throws IOException {

        NetHttpTransport transport = new NetHttpTransport();
        JacksonFactory factory = JacksonFactory.getDefaultInstance();
        GoogleIdTokenVerifier.Builder ver = new GoogleIdTokenVerifier.Builder(transport, factory)
                .setAudience(Collections.singleton(idClient));

        log.info("googleIdTokeen" + tokenDto.getValue());
        String tokenValue = tokenDto.getValue();
        if (tokenValue == null) {
            throw new IllegalArgumentException("Token value cannot be null");
        }
        //GoogleIdToken googleIdToken = GoogleIdToken.parse(ver.getJsonFactory(), tokenValue);
        GoogleIdToken googleIdToken = GoogleIdToken.parse(ver.getJsonFactory(), tokenDto.getValue());
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        //String username = payload.get("given_name").toString().concat("-").toString().concat(getAlphaNumericString(n));
        String username = payload.get("given_name").toString();
        String firstName = payload.get("given_name").toString();
        String lastName = payload.get("family_name").toString();
        // return new ResponseEntity<>(payload, HttpStatus.OK);


        return login(payload.getEmail(), username,firstName,lastName);

    }
    @PostMapping("/facebook")
    public LoginResponse loginWithFacebook(@RequestBody TokenDto tokenDto) {
        Facebook facebook = new FacebookTemplate(tokenDto.getValue());
        String[] data = { "email", "name" };
        User userFacebook = facebook.fetchObject("me", User.class, data);
        // String


        String username = userFacebook.getName().concat(getAlphaNumericString(n));

        return loginfb(userFacebook.getEmail(),username);
    }

    private LoginResponse loginfb(String email, String username) {
        boolean result = userService.ifEmailExist(email); // t // f
        Set<Role> roles = new HashSet<>();
        roles.add(getRole(ERole.CLIENT));
        if (!result) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(encoder.encode("root1234"));
            user.setUsername(username);
            user.setStateuser(true);
            user.setRoles(roles);
            userRepository.save(user);
        }
        JwtLogin jwtLogin = new JwtLogin();
        jwtLogin.setUsername(username);
        jwtLogin.setPassword("root1234");


        log.info("jwt:" + jwtLogin.getUsername());
        log.info("jwt:" + jwtLogin.getPassword());

        return jwtUtils.login(jwtLogin);
    }
    private User createUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName(("malak"));
        user.setPassword(encoder.encode("root123"));

        return userService.saveUser(user);
    }


    //CONFIRM ACCOUNT USER
    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token")String confirmationToken)
    {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if(token != null)
        {
            User user = userRepository.findByEmail(token.getUserEntity().getEmail());
            user.setStateuser(true);
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("Account verified! User registered successfully!"));

        }
        else
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("code de verification incorrect ");

        }
    }

    // Session
   @GetMapping("/user")
    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userOptional = userRepository.findByUsername(username);
        return new User(userOptional.getId());

    }

}
