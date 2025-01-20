package co.aluracursos.literalura.jsonalias;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro(
        @JsonAlias("title") String titulo,
        @JsonAlias("authors") List<DatosAutor> autor,
        //Posible errores, debido a que el objeto json es una lista de lenguages
        @JsonAlias("languages") List<String> idioma,
        @JsonAlias("download_count") Float numeroDescargas
) {
}
