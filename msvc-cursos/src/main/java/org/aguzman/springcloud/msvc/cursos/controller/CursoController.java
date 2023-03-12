package org.aguzman.springcloud.msvc.cursos.controller;

import feign.FeignException;
import lombok.AllArgsConstructor;
import org.aguzman.springcloud.msvc.cursos.models.Usuario;
import org.aguzman.springcloud.msvc.cursos.models.entity.Curso;
import org.aguzman.springcloud.msvc.cursos.services.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Controller
public class CursoController {

    private CursoService service;

    @GetMapping
    public ResponseEntity<List<Curso>> listar(){
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        return service.porIdConUsuarios(id)//service.porId(id)
                .map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult result){
        if(result.hasErrors()){
            return validar(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(curso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result, @PathVariable Long id){
        if(result.hasErrors()){
            return validar(result);
        }
        return service.porId(id)
                .map(
                        c -> {
                            c.setNombre(curso.getNombre());
                            return ResponseEntity.status(HttpStatus.CREATED)
                                    .body(service.guardar(c));
                        }
                )
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        return service.porId(id)
                .map(curso -> {
                    service.eliminar(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(()->ResponseEntity.notFound().build());
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        try{
            return service.asignarUsuario(usuario, cursoId)
                    .map(u -> ResponseEntity.status(HttpStatus.CREATED).body(u))
                    .orElseGet(()->ResponseEntity.notFound().build());
        }catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje", "No existe el usuario por el id o error en la comunicacion: " + e.getMessage()));
        }
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        try{
            return service.crearUsuario(usuario, cursoId)
                    .map(u -> ResponseEntity.status(HttpStatus.CREATED).body(u))
                    .orElseGet(()->ResponseEntity.notFound().build());
        }catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje", "No se puedo crear el usuario o error en la comunicacion: " + e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        try{
            return service.eliminarUsuario(usuario, cursoId)
                    .map(u -> ResponseEntity.status(HttpStatus.OK).body(u))
                    .orElseGet(()->ResponseEntity.notFound().build());
        }catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje", "No existe el usuario por el id o error en la comunicacion: " + e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id){
        service.eliminarCursoUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult result){
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(
                err -> {
                    errores.put(err.getField(), "El campo " + " " + err.getDefaultMessage());
                }
        );
        return ResponseEntity.badRequest().body(errores);
    }
}
