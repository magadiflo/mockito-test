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
