package org.aguzman.springcloud.msvc.usuarios.models.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El campo nombre no puede ser vacio")
    private String nombre;

    @NotEmpty(message = "El campo email no puede ser vacio")
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank(message = "El campo password no puede ser vacio")
    private String password;

}
