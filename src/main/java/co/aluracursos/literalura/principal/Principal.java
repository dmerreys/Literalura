package co.aluracursos.literalura.principal;

import co.aluracursos.literalura.jsonalias.DatosLibro;
import co.aluracursos.literalura.jsonalias.DatosResultado;
import co.aluracursos.literalura.model.Autor;
import co.aluracursos.literalura.model.Libro;
import co.aluracursos.literalura.repository.AutorRepository;
import co.aluracursos.literalura.repository.LibroRepository;
import co.aluracursos.literalura.service.ConsumoAPI;
import co.aluracursos.literalura.service.ConvierteDatos;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {

    private Scanner teclado = new Scanner(System.in);

    private ConvierteDatos conversor = new ConvierteDatos();

    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL = "https://gutendex.com/books/?search=";

    private AutorRepository autorRepository;

    private LibroRepository libroRepository;

    public Principal(AutorRepository autorRepository, LibroRepository libroRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }

    public void mostrarMenu() {
        var opcion = -1;
        var menu = """
                    Elija la opción a través del número:
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivo en un determinado año
                    5 - Listrar libros por idioma
                    
                    0 - Salir
                    """;



        while (opcion != 0) {
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    crearLibroConAutor();
                    break;
                case 2:
                    listarLibros();
                    break;
                case 3:
                    listarAutores();
                    break;
                case 4:
                    listarAutoresVivosEnAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private DatosLibro getDatosLibro() {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL + nombreLibro.replace(" ", "%20"));
        //System.out.println("Consulta de la api: " + json);
        DatosResultado datos = conversor.obtenerDatos(json, DatosResultado.class);
        System.out.println(datos.libros().get(0));
        return (datos.libros().get(0));
    }

    private void crearLibroConAutor(){
        DatosLibro datosLibro = getDatosLibro();



        Autor autor = autorRepository.findByNombre(datosLibro.autor().get(0).nombre())
                .orElseGet(() -> {
                    Autor nuevoAutor = new Autor();
                    nuevoAutor.setNombre(datosLibro.autor().get(0).nombre());
                    nuevoAutor.setFechaNacimiento(datosLibro.autor().get(0).fechaNacimiento());
                    nuevoAutor.setFechaFallecimiento(datosLibro.autor().get(0).fechaFallimiento());
                    return autorRepository.save(nuevoAutor);
                });

        // Verifica si el libro ya existe para este autor
        Optional<Libro> libroExistente = libroRepository.findByTituloAndAutor(datosLibro.titulo(), autor);

        if(libroExistente.isPresent()){
            System.out.println("El libro ya existe para este autor");
            return;
        }else{
            Libro libro = new Libro();

            libro.setTitulo(datosLibro.titulo());
            libro.setIdioma(datosLibro.idioma().get(0));
            libro.setNumeroDescargas(datosLibro.numeroDescargas());
            libro.setAutor(autor);

            // Guarda el nuevo libro
            Libro libroGuardado = libroRepository.save(libro);
            System.out.println("Nuevo libro creado: \n" + libroGuardado);
        }

    }

    public void listarLibros(){
        libroRepository.findAll().forEach(System.out::println);
    }

    public void listarAutores(){
        List<Autor> autores = autorRepository.findAll();
        autores.forEach(System.out::println);
    }

    public void listarAutoresVivosEnAnio(){
        System.out.println("Escribe el año para buscar autores vivos");
        var anio = teclado.nextInt();
        autorRepository.findAutoresVivosEnAnio(anio).forEach(System.out::println);
    }

    public void listarLibrosPorIdioma(){
        System.out.println("Escribe el idioma para buscar libros:\n" +
                "es -> Español\n" +
                "en -> Inglés\n" +
                "fr -> Francés\n" +
                "pt -> Portugués\n");
        var idioma = teclado.nextLine();
        libroRepository.findByIdioma(idioma).forEach(System.out::println);
    }

}
