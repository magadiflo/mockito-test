# Mockito

---

## Configurando el proyecto con JUnit 5 y Mockito

Primero debemos agregar las dependencias de **JUnit 5 y Mockito** en nuestro archivo **pom.xml**:

````xml

<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.9.3</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.4.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.4.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
````

**DONDE**

- **junit-jupiter**, dependencia de JUnit 5 pero con la versión 5.9.3.
- **mockito-core**, dependencia del core de Mockito.
- **mockito-junit-jupiter**, contiene la extensión de Mockito en JUnit 5, es decir poder ejecutar nuestras pruebas
  unitarias con integración con Mockito, para todo lo que es Inyección de Dependencia.

## Creando directorios y archivos para las pruebas

Crearemos inicialmente una estructura básica donde tendremos los paquetes: **models, repositories y services** y dentro
de ellos sus respectivas clases/interfaces:

````java
//models
public class Exam {
    private Long id;
    private String name;
    private List<String> questions = new ArrayList<>();

    public Exam(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /* settes and getters */
}
````

````java
//repositories
public interface IExamRepository {
    List<Exam> findAll();
}
````

````java
//services
public interface IExamService {
    Exam findExamByName(String name);
}
````

## Implementando la clase Service

Crearemos una implementación de la interfaz service y del repository, para empezar a realizar las pruebas. Entonces,
comenzamos implementando una clase concreta de la interfaz **IExamRepository**:

````java
public class ExamRepositoryImpl implements IExamRepository {
    @Override
    public List<Exam> findAll() {
        return List.of(
                new Exam(1L, "Aritmética"),
                new Exam(2L, "Geometría"),
                new Exam(3L, "Álgebra"),
                new Exam(4L, "Trigonometría"),
                new Exam(5L, "Programación"),
                new Exam(6L, "Bases de Datos"),
                new Exam(7L, "Estructura de datos"),
                new Exam(8L, "Java 17"));
    }
}
````

La implementación anterior, solo será como nuestra **fuente de datos**, como si el **findAll()** fuese a consultar datos
de una base de datos, pero nosotros lo tendremos **hard codeado** en la clase.

Ahora, toca crear una implementación de la interfaz **IExamService** e implementar el método **findExamByName()**:

````java
public class ExamenServiceImpl implements IExamService {
    private final IExamRepository examRepository;

    public ExamenServiceImpl(IExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Exam findExamByName(String name) {
        return this.examRepository.findAll().stream()
                .filter(exam -> exam.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No existe el examen: " + name));
    }
}
````

Listo, ya tenemos implementado el método **findExamByName(String name)** de nuestra clase **ExamenServiceImpl**, ahora
el siguiente paso es **realizar la prueba unitaria del método implementado**.

