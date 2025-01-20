package co.aluracursos.literalura.repository;

import co.aluracursos.literalura.model.Autor;
import co.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTituloAndAutor(String titulo, Autor autor);

    List<Libro> findByIdioma(String idioma);
}
