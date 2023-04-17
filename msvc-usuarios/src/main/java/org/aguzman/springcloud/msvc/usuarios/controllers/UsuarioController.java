package org.aguzman.springcloud.msvc.usuarios.controllers;

import lombok.AllArgsConstructor;
import org.aguzman.springcloud.msvc.usuarios.models.entity.Usuario;
import org.aguzman.springcloud.msvc.usuarios.services.UsuarioServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@AllArgsConstructor
@RestController
public class UsuarioController {

    private final UsuarioServiceImpl service;
    private ApplicationContext context;
    private Environment env;

    @GetMapping("/crash")
    public void crash(){
        ((ConfigurableApplicationContext)context).close();
    }


    @GetMapping("/")
    public ResponseEntity<?> listar(){
        Map<String, Object> body = new HashMap<>();
        body.put("users", service.listar());
        body.put("pod_info", env.getProperty("MY_POD_NAME") + ":" + env.getProperty("MY_POD_IP"));
        body.put("texto", env.getProperty("config.texto"));
//        return Collections.singletonMap("users", service.listar());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        Optional<Usuario> usuarioOptional = service.porId(id);
        return usuarioOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result){
        if(result.hasErrors()){
            return validar(result);
        }
        if(!usuario.getEmail().isEmpty() && service.existePorEmail(usuario.getEmail()))
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("mensaje", "Ya existe un usuario con ese correo electronico."));

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable Long id){
        if(result.hasErrors()){
            return validar(result);
        }
        Optional<Usuario> usuarioOptional = service.porId(id);
        return usuarioOptional.map(
                u -> {
                    if(u.getEmail().equalsIgnoreCase(usuario.getEmail()) && service.porEmail(usuario.getEmail()).isPresent())
                        return ResponseEntity.badRequest().body(
                            Collections.singletonMap("mensaje", "Ya existe un usuario con ese correo electronico."));
                    u.setNombre(usuario.getNombre());
                    u.setEmail(usuario.getEmail());
                    u.setPassword(usuario.getPassword());
                    return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(u));
                }
        ).orElseGet(()->ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        return service.porId(id)
                .map(u -> {
                    service.eliminar(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/usuarios-por-curso")
    public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids){
        return ResponseEntity.ok(service.listarPorIds(ids));
    }

    @GetMapping("/authorized")
    public Map<String, Object> authorized(@RequestParam(name = "code") String code){
        return Collections.singletonMap("code", code);
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
