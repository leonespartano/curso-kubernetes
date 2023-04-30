package org.aguzman.springcloud.msvc.cursos.services;

import lombok.AllArgsConstructor;
import org.aguzman.springcloud.msvc.cursos.clients.UsuarioClientRest;
import org.aguzman.springcloud.msvc.cursos.models.Usuario;
import org.aguzman.springcloud.msvc.cursos.models.entity.Curso;
import org.aguzman.springcloud.msvc.cursos.models.entity.CursoUsuario;
import org.aguzman.springcloud.msvc.cursos.repositories.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CursoServiceImpl implements CursoService {

    private CursoRepository repository;
    private UsuarioClientRest client;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return repository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long id) {
        repository.eliminarCursoUarioPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdConUsuarios(Long id, String token) {
        return repository.findById(id)
                .map(curso -> {
                            if (!curso.getCursoUsuarios().isEmpty()) {
                                List<Long> ids = curso.getCursoUsuarios().stream()
                                        .map(CursoUsuario::getUsuarioId).toList();
                                List<Usuario> usuarios = client.obtenerAlumnosPorCurso(ids, token);
                                curso.setUsuarios(usuarios);
                            }
                            return curso;
                        }
                );
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId, String token) {
        return repository.findById(cursoId)
                .map(
                        curso -> {
                            Usuario usuarioMsvc = client.detalle(usuario.getId(), token);
                            CursoUsuario cursoUsuario = CursoUsuario.builder()
                                    .usuarioId(usuarioMsvc.getId())
                                    .build();
                            curso.addCursoUsuario(cursoUsuario);
                            repository.save(curso);
                            return usuarioMsvc;
                        }
                );
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId, String token) {
        return repository.findById(cursoId)
                .map(
                        curso -> {
                            Usuario usuarioNuevoMsvc = client.crear(usuario, token);
                            CursoUsuario cursoUsuario = CursoUsuario.builder()
                                    .usuarioId(usuarioNuevoMsvc.getId())
                                    .build();
                            curso.addCursoUsuario(cursoUsuario);
                            repository.save(curso);
                            return usuarioNuevoMsvc;
                        }
                );
    }


    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId, String token) {
        return repository.findById(cursoId)
                .map(
                        curso -> {
                            Usuario usuarioMsvc = client.detalle(usuario.getId(), token);
                            CursoUsuario cursoUsuario = CursoUsuario.builder()
                                    .usuarioId(usuarioMsvc.getId())
                                    .build();
                            curso.removeCursoUsuario(cursoUsuario);
                            repository.save(curso);
                            return usuarioMsvc;
                        }
                );
    }
}
