package co.aluracursos.literalura.repository;

import co.aluracursos.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE (a.fechaFallecimiento >= :anio OR a.fechaFallecimiento IS NULL) AND a.fechaNacimiento <= :anio")
    List<Autor> findAutoresVivosEnAnio(Integer anio);
}
