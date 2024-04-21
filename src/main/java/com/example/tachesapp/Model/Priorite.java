package com.example.tachesapp.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Priorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idpriorite;
    private String nompriorite;
    private int rang;



}
