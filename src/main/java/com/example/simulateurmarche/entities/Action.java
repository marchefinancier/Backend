package com.example.simulateurmarche.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Builder
public class Action implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_Action;
    private String nom;
    private String marcher;
    private float prix;
    private String variation;




    @OneToMany(mappedBy ="action" )
    @JsonIgnore
    private List<Ordre> List ;


}

