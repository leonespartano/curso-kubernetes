package org.aguzman.springcloud.msvc.cursos.clients;

import org.aguzman.springcloud.msvc.cursos.models.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name="msvc-usuarios")
public interface UsuarioClientRest {

    @GetMapping("/{id}")
    Usuario detalle(@PathVariable("id") Long id, @RequestHeader(value = "Authorization", required = true) String token);

    @PostMapping("/")
    Usuario crear(@RequestBody Usuario usuario, @RequestHeader(value = "Authorization", required = true) String token);

    @GetMapping("/usuarios-por-curso")
    List<Usuario> obtenerAlumnosPorCurso(@RequestParam("ids") Iterable<Long> ids, @RequestHeader(value = "Authorization", required = true) String token);
}
