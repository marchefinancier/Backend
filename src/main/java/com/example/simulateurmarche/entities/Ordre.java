package com.example.simulateurmarche.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Builder
public class Ordre implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_Ordre;
    private String type;
    private Integer qte;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Action action;
}
