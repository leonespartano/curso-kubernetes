package org.aguzman.springcloud.msvc.usuarios.services;

import lombok.AllArgsConstructor;
import org.aguzman.springcloud.msvc.usuarios.clients.CursoClienteRest;
import org.aguzman.springcloud.msvc.usuarios.models.entity.Usuario;
import org.aguzman.springcloud.msvc.usuarios.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UsuarioServiceImpl {

    private final UsuarioRepository repository;
    private final CursoClienteRest cliente;

    @Transactional(readOnly = true)
    public List<Usuario> listar(){
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> porId(Long id){
        return repository.findById(id);
    }

    @Transactional
    public Usuario guardar(Usuario usuario){
        return repository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id){
        repository.deleteById(id);
        cliente.eliminarCursoUsuarioPorId(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> porEmail(String email){
        return repository.porEmail(email);
    }

    public boolean existePorEmail(String email){
        return repository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarPorIds(Iterable<Long> ids){
        return repository.findAllById(ids);
    }
}

