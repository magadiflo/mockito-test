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

## Realizando primeras pruebas con Mockito

Como vamos a probar el método **findExamByName(String name)** de la clase **ExamenServiceImpl** necesitamos crear su
clase de test. Como ya sabemos, podemos hacerlo manualmente o mejor usando el IDE **IntelliJ IDEA**. Abrimos la clase
**ExamenServiceImpl** y nos posicionamos con el mouse en ella, presionamos ``Ctrl + Shift + T`` y seleccionamos
``Create New Test...``, seleccionamos el método a probar y damos ok. Finalmente, **tendremos creada nuestra clase test
en el directorio de /test:**

````java
package org.magadiflo.mockito.app.services.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExamenServiceImplTest {

    @Test
    void findExamByName() {
    }
}
````

Antes de comenzar a trabajar con **Mockito**, veamos un ejemplo rápido de **cómo trabajaríamos usando solo JUnit:**

````java
class ExamenServiceImplTest {

    @Test
    void findExamByName() {
        IExamRepository examRepository = new ExamRepositoryImpl();
        IExamService examService = new ExamenServiceImpl(examRepository);

        Exam exam = examService.findExamByName("Aritmética");

        assertNotNull(exam);
        assertEquals(1L, exam.getId());
        assertEquals("Aritmética", exam.getName());
    }
}
````

Ahora, qué pasaría si quisiéramos probar cuando el **examService.findExamByName("Aritmética")** internamente llame al
**examRepository.findAll()** y nos retorne una lista vacía, **¿qué comportamiento debería tener?**, bueno, tendríamos
que ir a modificar la implementación del repositorio para que nos retorne una lista vacía, y **¡eso no puede ser!**,
porque lo que nosotros estamos probando es el método que pertenece a la clase service. En pocas palabras, tenemos que
probar la clase en la que estamos, pero **¿si tenemos dependencias con otros objetos?**, entonces allí entra a relucir
**Mockito**. Lo que hará será **simular** las dependencias que nosotros necesitamos.

En la implementación del método **findAll()** que usamos del **ExamRepositoryImpl()** tenemos un conjunto de exámenes
que son retornados, qué pasaría si dicha implementación aún no estuviera realizada o no tenemos acceso a dicha
implementación, entonces esa es otra de las razones del por qué usar Mockito.

Según lo mencionado en el apartado anterior, usaremos **Mockito** para simular el comportamiento cuando se
llame al método **examRepository.findAll()**.

Por lo tanto, como usaremos **Mockito** para simular la implementación del **IExamRepository**, podemos **eliminar la
implementación ExamRepositoryImpl** que tenemos para ver que efectivamente trabajaremos con la simulación que nos hará
**Mockito** de su implementación, pero en nuestro caso solo modificaremos la implementación para que nos retorne una
lista vacía.

````java
public class ExamRepositoryImpl implements IExamRepository {
    @Override
    public List<Exam> findAll() {
        return List.of();
    }
}
````

Ahora sí, llegó el momento de crear nuestro **primer Test Unitario usando Mockito**:

````java
class ExamenServiceImplTest {

    @Test
    void findExamByName() {
        IExamRepository examRepository = mock(IExamRepository.class);       //(1)
        IExamService examService = new ExamenServiceImpl(examRepository);

        List<Exam> exams = List.of(
                new Exam(1L, "Aritmética"),
                new Exam(2L, "Geometría"),
                new Exam(3L, "Álgebra"),
                new Exam(4L, "Trigonometría"),
                new Exam(5L, "Programación"),
                new Exam(6L, "Bases de Datos"),
                new Exam(7L, "Estructura de datos"),
                new Exam(8L, "Java 17"));
        when(examRepository.findAll()).thenReturn(exams);                   //(2)

        Optional<Exam> optionalExam = examService.findExamByName("Aritmética");

        assertTrue(optionalExam.isPresent());
        assertEquals(1L, optionalExam.get().getId());
        assertEquals("Aritmética", optionalExam.get().getName());
    }
}
````

**DONDE**

- **(1)**, creará una implementación al vuelo de IExamenRepository pero simulada, con sus métodos.
- **(2)**, usamos Mockito y decimos cuando (when) llames al método **findAll()** del examRepository, entonces retornarás
  la lista de exams.

**NOTA**
> Recordar que tanto Mockito como el Assertions para JUnit los estamos importando de manera estática, por eso podemos
> usar directamente sus métodos: <br>
> ``import static org.junit.jupiter.api.Assertions.*;``<br>
> ``import static org.mockito.Mockito.*;``
>
> No se pueden hacer mocks de todos los métodos, solo de los públicos o defaults. No se pueden hacer de los privados,
> de los estáticos ni de los finales.

Incluso podemos usar la implementación real **ExamRepositoryImpl** para mocker el repository, no afectará en nada,
únicamente Mockito la usará para crear una simulación:

````java
class ExamenServiceImplTest {
    @Test
    void findExamByName() {
        /* omitted code */
        IExamRepository examRepository = mock(ExamRepositoryImpl.class);
        /* omitted code */
    }
}
````

Modificaremos nuestra interfaz **IExamService** para retornar un ``Optional<Exam>``:

````java
public interface IExamService {
    Optional<Exam> findExamByName(String name);
}
````

También tendríamos que modificar el **ExamServiceImpl** para que el método **findExamByName** retorne un optional:

````java
public class ExamenServiceImpl implements IExamService {
    private final IExamRepository examRepository;

    public ExamenServiceImpl(IExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Optional<Exam> findExamByName(String name) {
        return this.examRepository.findAll().stream()
                .filter(exam -> exam.getName().equals(name))
                .findFirst();
    }
}
````

Modificamos el test que ya teníamos para encontrar un examen por su nombre:

````java
class ExamenServiceImplTest {

    @Test
    void findExamByName() {
        IExamRepository examRepository = mock(IExamRepository.class);
        IExamService examService = new ExamenServiceImpl(examRepository);

        List<Exam> exams = List.of(
                new Exam(1L, "Aritmética"),
                new Exam(2L, "Geometría"),
                new Exam(3L, "Álgebra"),
                new Exam(4L, "Trigonometría"),
                new Exam(5L, "Programación"),
                new Exam(6L, "Bases de Datos"),
                new Exam(7L, "Estructura de datos"),
                new Exam(8L, "Java 17"));
        when(examRepository.findAll()).thenReturn(exams);

        Optional<Exam> optionalExam = examService.findExamByName("Aritmética");

        assertTrue(optionalExam.isPresent());
        assertEquals(1L, optionalExam.get().getId());
        assertEquals("Aritmética", optionalExam.get().getName());
    }
}
````

Creamos ahora un **segundo Test Unitario con Mockito**, donde tenemos que verificar qué pasaría si al llamar
al **examRepository.findAll()** nos retorna una lista vacía, debería sí o sí devolvernos un optional vacío:

````java
class ExamenServiceImplTest {
    @Test
    @DisplayName("Retorna un optional vacío ya que no existe ningún elemento en la lista")
    void findExamByNameReturnOptionalEmpty() {
        IExamRepository examRepository = mock(IExamRepository.class);
        IExamService examService = new ExamenServiceImpl(examRepository);

        List<Exam> exams = List.of();
        when(examRepository.findAll()).thenReturn(exams);

        Optional<Exam> optionalExam = examService.findExamByName("Aritmética");

        assertTrue(optionalExam.isEmpty());
    }
}
````

Como podemos observar en los dos test anteriores, no necesitamos ir a la implementación real y modificar las respuestas
según nuestras necesidades, tan solo usando **Mockito simularemos el comportamiento de los métodos cuyo objeto que la
contiene es una dependencia de la clase a probar, en nuestro caso el ExamServiceImpl (clase a ser probada), depende del
objeto IExamRepository y es este último quien tiene el método que simularemos con Mockito**

## Refactorizando nuestra clase ExamenServiceImplTest

Podemos refactorizar nuestra clase **ExamenServiceImplTest**, reutilizando parte del código que se repite constantemente
en los métodos test, por ejemplo el ``IExamRepository examRepository = mock(IExamRepository.class);`` y también
``IExamService examService = new ExamenServiceImpl(examRepository);`` se están repitiendo constantemente en los métodos
test. Podemos usar la anotación del ciclo de vida **@BeforeEach** que aprendimos en **JUnit 5**. Esta repetición no es
mera casualidad, recordemos que estamos probando la clase **ExamenServiceImpl** por lo tanto sí o sí vamos a requerir
simular sus dependencias en todos los métodos test.

````java
class ExamenServiceImplTest {
    private IExamRepository examRepository;
    private IExamService examService;

    @BeforeEach
    void setUp() {
        this.examRepository = mock(IExamRepository.class);
        this.examService = new ExamenServiceImpl(this.examRepository);
    }

    @Test
    void findExamByName() {
        List<Exam> exams = List.of(/* omitted elements */);
        when(this.examRepository.findAll()).thenReturn(exams);

        Optional<Exam> optionalExam = this.examService.findExamByName("Aritmética");

        /* omitted assertions */
    }

    @Test
    @DisplayName("Retorna un optional vacío ya que no existe ningún elemento en la lista")
    void findExamByNameReturnOptionalEmpty() {
        List<Exam> exams = List.of();
        when(this.examRepository.findAll()).thenReturn(exams);

        Optional<Exam> optionalExam = this.examService.findExamByName("Aritmética");

        assertTrue(optionalExam.isEmpty());
    }
}
````

Observamos en el código anterior que las líneas repetidas los colocamos dentro del método anotado con **@BeforeEach**,
y para que los objetos **examRepository** y **examService** sean reutilizados en los métodos test, los declaramos como
atributos privados de manera global, de esa manera se reutilizarán nuestras dependencias mockeadas.

## Agregando nuevas dependencias mock

Crearemos un nuevo repositorio para las preguntas **(Question)**, definimos un método que nos permitirá buscar la lista
de preguntas según el identificador del examen proporcionado:

````java
public interface IQuestionRepository {
    List<String> findQuestionsByExamId(Long id);
}
````

Creamos un nuevo método en la interfaz **IExamenService** llamado **findExamByNameWithQuestions()** cuya implementación
retornará un examen pero conteniendo el listado de sus preguntas:

````java
public interface IExamService {
    Optional<Exam> findExamByName(String name);

    Exam findExamByNameWithQuestions(String name);
}
````

Implementamos el nuevo método en la clase **ExamenServiceImpl**, para ello previamente habría que definir un atributo
de la interfaz **IQuestionRepository** cuya implementación será pasado por constructor, luego simplemente toca
implementar el método **findExamByNameWithQuestions(String name):**

````java
public class ExamenServiceImpl implements IExamService {
    private final IExamRepository examRepository;
    private final IQuestionRepository questionRepository;

    public ExamenServiceImpl(IExamRepository examRepository, IQuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Optional<Exam> findExamByName(String name) {
        return this.examRepository.findAll().stream()
                .filter(exam -> exam.getName().equals(name))
                .findFirst();
    }

    @Override
    public Exam findExamByNameWithQuestions(String name) {
        Optional<Exam> examOptional = this.findExamByName(name);
        if (examOptional.isEmpty()) {
            throw new NoSuchElementException(String.format("¡No existe el exam %s buscado!", name));
        }
        Exam exam = examOptional.get();
        List<String> questions = this.questionRepository.findQuestionsByExamId(exam.getId());
        exam.setQuestions(questions);
        return exam;
    }
}
````

En el código anterior mostramos toda la clase **ExamenServiceImpl** incluyendo la implementación del nuevo método
agregado **findExamByNameWithQuestions()**. Observar que dicha implementación retorna un examen conteniendo la lista
de preguntas asociadas a él, además si no existe el examen buscado, se lanzará una excepción.

Finalmente, como última modificación habría que agregar la nueva interfaz que creamos **IQuestionRepository** en nuestra
clase de prueba **ExamenServiceImplTest,** ya que ahora al definir el objeto del **ExamenServiceImpl()** está esperando
recibir por constructor no solo la implementación del IExamRepository, sino también una implementación del
**IQuestionRepository**. Dicha implementación también será simulada con **Mockito**.

````java
class ExamenServiceImplTest {
    private IExamRepository examRepository;
    private IQuestionRepository questionRepository;
    private IExamService examService;

    @BeforeEach
    void setUp() {
        this.examRepository = mock(IExamRepository.class);
        this.questionRepository = mock(IQuestionRepository.class);

        this.examService = new ExamenServiceImpl(this.examRepository, this.questionRepository);
    }

    /* omitted tests */
}
````

## Refactorizando clase de prueba ExamenServiceImplTest

Crearemos una clase que contenga la lista de exámenes para poder reutilizarlas en los distintos métodos test, para eso
creamos en:

````java
// test/java/org/magadiflo/mockito/app/source/

public class Data {
    public static final List<Exam> EXAMS = List.of(
            new Exam(1L, "Aritmética"),
            new Exam(2L, "Geometría"),
            new Exam(3L, "Álgebra"),
            new Exam(4L, "Trigonometría"),
            new Exam(5L, "Programación"),
            new Exam(6L, "Bases de Datos"),
            new Exam(7L, "Estructura de datos"),
            new Exam(8L, "Java 17"));
}
````

Ahora, solo reemplazamos en los test que usan la lista para que la usen de la **Data.EXAMS**:

````java
class ExamenServiceImplTest {
    /* omitted code */
    @Test
    void findExamByName() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);
        /* omitted code */
    }
    /* omitted code */
}
````

## Probando nuevas dependencias mock

Creamos previamente una lista de preguntas en la clase **Data**:

````java
public class Data {
    /* omitted code */
    public static final List<String> QUESTIONS = List.of("Pregunta 1", "Pregunta 2", "Pregunta 3",
            "Pregunta 4", "Pregunta 5", "Pregunta 6", "Pregunta 7", "Pregunta 8", "Pregunta 9",
            "Pregunta 10");
}
````

Nuestro nuevo método a testear quedaría de la siguiente forma:

````java
class ExamenServiceImplTest {
    /* omitted code */

    @Test
    void findExamByNameWithQuestions() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);                                 // (1)
        when(this.questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);  // (2)

        Exam exam = this.examService.findExamByNameWithQuestions("Geometría");                      // (3)

        assertEquals(10, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Pregunta 10"));
    }
}
````

**Donde**

- **(1)**, le decimos a mockito, que cuando se llame al **findAll()** del **examRepository** entonces que nos retorne la
  lista completa de los exámenes: Data.EXAMS.
- **(2)**, cuando se llame al método **findQuestionsByExamId()** del **questionRepository** y se le pase por argumento
  **cualquier Long (anyLong)** entonces que nos retorne la lista de preguntas: Data.QUESTIONS.
- **(3)**, es el método de la clase **ExamenServiceImpl** que estamos probando. Recordar que dicho método internamente
  hace uso de los repositorios: **examRepository y questionRepository**, por eso es la necesidad de Mocker los
  repositorios.

Creando un método test para probar el lanzamiento de la excepción cuando un examen no es encontrado:

````java
class ExamenServiceImplTest {
    /* omitted code */
    @Test
    void throwNoSuchElementExceptionIfNotExistsExam() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            this.examService.findExamByNameWithQuestions("Lenguaje");
        });

        assertEquals(NoSuchElementException.class, exception.getClass());
        assertEquals("¡No existe el exam Lenguaje buscado!", exception.getMessage());
    }
}
````

Observar que en el test anterior no necesitamos mockear el:

> when(this.questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

Porque lo que estamos probando es el lanzamiento de la excepción **NoSuchElementException** y este lanzamiento debe
ocurrir, según la implementación real, antes de llamar al **this.questionRepository.findQuestionsByExamId(...)**

## Probando con verify de Mockito

**Verify** nos permite "verificar" si nuestro método mockeado ha sido ejecutado el número de veces que le hayamos
definido.

````java
class ExamenServiceImplTest {
    @Test
    void findExamByNameWithQuestionsUsingVerify() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);
        when(this.questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        Exam exam = this.examService.findExamByNameWithQuestions("Geometría");

        assertEquals(10, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Pregunta 10"));

        verify(this.examRepository).findAll();                              // (1)
        verify(this.questionRepository).findQuestionsByExamId(anyLong());   // (2)
    }
}
````

**DONDE**

- **(1)**, mockito verifica que del **this.examRepository** se haya llamado al método **findAll()**.
- **(2)**, mockito verifica que del **this.questionRepository** se haya llamado al método
  **findQuestionsByExamId(anyLong())**.
- En ambos verify, **por defecto el número de veces que debe ser llamado cada método es 1 vez**, más adelante veremos
  cómo definirle el número de veces que debe ser llamada algún método usando el método estático de **mockito.times()**.

Creamos otro test donde busquemos un examen que no existe, debemos verificar que el **this.examRepository.findAll()**
sí sea llamado, mientras que el **this.questionRepository.findQuestionsByExamId(anyLong())** no debe ser llamado, ya que
como no existe el examen, se lanza la excepción, por lo tanto nunca llega al método **findQuestionsByExamId(anyLong())**

````java
class ExamenServiceImplTest {
    @Test
    void throwNoSuchElementExceptionIfNotExistsExamUsingVerify() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);
        when(this.questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            this.examService.findExamByNameWithQuestions("Lenguaje");
        });

        assertEquals(NoSuchElementException.class, exception.getClass());
        assertEquals("¡No existe el exam Lenguaje buscado!", exception.getMessage());

        verify(this.examRepository).findAll();                                      // (1)
        verify(this.questionRepository, never()).findQuestionsByExamId(anyLong());  // (2)
    }
}
````

**DONDE**

- **(1)**, mockito verifica que del **this.examRepository** se haya llamado al método **findAll()**.
- **(2)**, mockito verifica que del **this.questionRepository** el método **findQuestionsByExamId(anyLong())** no se
  haya llamado nunca: **never()**.

## Inyección de dependencia y anotaciones @Mock, @InjectMocks y @ExtendWith

Recordemos que nosotros **estamos creando manualmente las instancias mockeadas de los repositories** y luego
inyectándolos a la implementación concreta el ExamenServiceImpl dentro del **@BeforeEach:**

````java
class ExamenServiceImplTest {
    private IExamRepository examRepository;             // Interfaz
    private IQuestionRepository questionRepository;     // Interfaz
    private IExamService examService;                   // Interfaz


    @BeforeEach
    void setUp() {
        this.examRepository = mock(IExamRepository.class);
        this.questionRepository = mock(IQuestionRepository.class);

        this.examService = new ExamenServiceImpl(this.examRepository, this.questionRepository);
    }
}
````

Ahora, **usaremos las anotaciones proporcionadas por Mockito** para realizar inyección de dependencias de nuestros
repositorios mockeados y luego usarlos para inyectar la clase que vamos a testear:

````java
class ExamenServiceImplTest {
    @Mock
    private IExamRepository examRepository;             // Interfaz
    @Mock
    private IQuestionRepository questionRepository;     // Interfaz

    @InjectMocks
    private ExamenServiceImpl examService;              // (1) Implementación concreta

}
````

**DONDE**

- **(1)**, aquí debemos usar una implementación concreta para que pueda hacer la inyección de los repositorios mockeados
  con la anotación **@Mock**. Recordemos que la implementación concreta recibe por argumento del constructor los dos
  repositorios: ``new ExamenServiceImpl(examRepository, questionRepository)`` estos argumentos son precisamente los
  repositorios mockeados con **@Mock**. Si usamos una interfaz como el **IExamService** no dará un error. Por lo tanto,
  ``para usar el @InjectMocks sí o sí debe ser con una implementación concreta`` y en automático Mockito inyectará los
  dos repositorios anotados con @Mock.

Ahora, necesitamos habilitar el uso de las anotaciones **@Mock e @InjectMocks**, para eso existen dos formas:

### Forma 1. Habilitar anotaciones de Mockito para inyección de dependencia

Podemos usar el método anotado con @BeforeEach y agregar el **MockitoAnnotations.openMocks(this)**, es decir le pasamos
la instancia de esta clase test: ExamenServiceImplTest:

````java
class ExamenServiceImplTest {
    @Mock
    private IExamRepository examRepository;
    @Mock
    private IQuestionRepository questionRepository;

    @InjectMocks
    private ExamenServiceImpl examService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); //<-- habilita las anotaciones de mockito: @Mock, @InjectMocks
    }
    /* omitted tests */
}
````

### Forma 2. Habilitar anotaciones de Mockito para inyección de dependencia

La segunda forma es anotando la clase test con **@ExtendWith(MockitoExtension.class)**, para eso es importante como lo
hicimos al principio del curso agregar la dependencia:

````xml

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>
````

Entonces, nuestra clase de test quedaría de la siguiente manera:

````java

@ExtendWith(MockitoExtension.class) //<-- habilita las anotaciones de mockito: @Mock, @InjectMocks
class ExamenServiceImplTest {
    @Mock
    private IExamRepository examRepository;
    @Mock
    private IQuestionRepository questionRepository;

    @InjectMocks
    private ExamenServiceImpl examService;

    /* omitted tests */
}
````

Ejecutamos las pruebas **con cualquiera de las dos formas** y veremos que todo está funcionando correctamente. En mi
caso, optaré por quedarme con la segunda forma.

## Realizando más pruebas del repositorio con el método guardar

Para eso debemos definir los métodos en las interfaces:

````java
public interface IExamRepository {
    /* omitted method */
    Exam saveExam(Exam exam);
}
````

````java
public interface IQuestionRepository {
    /* omitted method */
    void saveQuestions(List<String> questions);
}
````

````java
public interface IExamService {
    /* omitted methods*/
    Exam saveExam(Exam exam);
}

````

Ahora toca implementar el método **saveExam()** en la clase concreta **ExamenServiceImpl**:

````java
public class ExamenServiceImpl implements IExamService {
    @Override
    public Exam saveExam(Exam exam) {
        List<String> questions = exam.getQuestions();
        if (!questions.isEmpty()) {
            this.questionRepository.saveQuestions(questions);
        }
        return this.examRepository.saveExam(exam);
    }
}
````

Listo, hasta este momento ya tenemos implementado el método **saveExam()** de la clase concreta **ExamenServiceImpl**,
llega el momento de crearle un test para probar dicho método:

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */
    /* other tests */
    @Test
    void saveExamWithoutQuestions() {
        when(this.examRepository.saveExam(any(Exam.class))).thenReturn(Data.EXAM);  // (1)
        Exam examDB = this.examService.saveExam(Data.EXAM);                         // (2)

        assertNotNull(examDB);
        assertEquals(9L, examDB.getId());
        assertEquals("Docker", examDB.getName());

        verify(this.examRepository).saveExam(any(Exam.class));                      // (3)
        verify(this.questionRepository, never()).saveQuestions(anyList());          // (4)
    }
}
````

**DONDE**

- **(1)** le decimos a mockito que cuando el **this.examRepository** haga un **saveExam(...)** y se le pase por
  parámetro cualquier examen o sea un **any(Exam.class)**, entonces que nos retorne el **Data.EXAM**.
- **(2)** es el método que vamos a probar de la clase de servicio **ExamenServiceImpl**.
- **(3)** le decimos a mockito que verifique que del **this.examRepository** su método **saveExam()** con un parámetro *
  *any(Exam.class)** haya sido llamado una vez (por defecto).
- **(4)** le decimos a mockito que verifique que del **this.questionRepository** su método **saveQuestions()** que
  recibe un parámetro de una lista de objetos, no interesa cuál, solo que recibe una lista **anyList()** nunca se haya
  llamado, es decir **never()**. Esto debe ser cierto, ya que el examen que guardamos no tiene una lista de preguntas.

Ahora crearemos un método test que guarde un examen que tiene preguntas:

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */
    /* other tests */
    @Test
    void saveExamWithQuestions() {
        Exam exam = Data.EXAM;
        exam.setQuestions(Data.QUESTIONS);

        when(this.examRepository.saveExam(any(Exam.class))).thenReturn(exam);
        doNothing().when(this.questionRepository).saveQuestions(anyList());     // (1)

        Exam examDB = this.examService.saveExam(exam);

        assertNotNull(examDB);
        assertEquals(9L, examDB.getId());
        assertEquals("Docker", examDB.getName());

        verify(this.examRepository).saveExam(any(Exam.class));      // (2)
        verify(this.questionRepository).saveQuestions(anyList());   // (3)
    }
}
````

**DONDE**

- **(1)**, como el método saveQuestions(...) retorna un void, entonces usando el **doNothing()** le decimos a mockito
  que no haga nada cuando del **this.questionRepository** se llame a su método **saveQuestions(anyList())**.
- **(2) y (3)**, le decimos a mockito que verifique que los métodos de dichos repositorios se hayan llamado.

## Test del id incremental en el método guardar usando Invocation Argument

Cuando guardamos un examen, este previamente no tiene un identificador, luego de que se haya guardado en la base de
datos, lo que se devuelve es un examen con el id poblado. Entonces, podemos hacer un poco más real la prueba unitaria,
enviando a guardar un examen sin id pero al momento de recuperarla debemos ver que el examen guardado ya viene con id.

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */
    /* other tests */
    @Test
    void saveExamWithQuestionsReturnExamWithId() {
        // given
        Exam exam = Data.EXAM_WHITOUT_ID;
        exam.setQuestions(Data.QUESTIONS);

        when(this.examRepository.saveExam(any(Exam.class))).then(new Answer<Exam>() {
            Long sequence = 8L;

            @Override
            public Exam answer(InvocationOnMock invocation) throws Throwable {
                Exam examToSave = invocation.getArgument(0);
                examToSave.setId(sequence++);
                return examToSave;
            }
        });
        doNothing().when(this.questionRepository).saveQuestions(anyList());

        // when
        Exam examDB = this.examService.saveExam(exam);

        // then
        assertNotNull(examDB);
        assertEquals(8L, examDB.getId());
        assertEquals("Kubernetes", examDB.getName());

        verify(this.examRepository).saveExam(any(Exam.class));
        verify(this.questionRepository).saveQuestions(anyList());
    }
}
````

Del test anterior, el código que nos interesa es el siguiente:

````
when(this.examRepository.saveExam(any(Exam.class))).then(new Answer<Exam>() {
    Long sequence = 8L;

    @Override
    public Exam answer(InvocationOnMock invocation) throws Throwable {
        Exam examToSave = invocation.getArgument(0);
        examToSave.setId(sequence++);
        return examToSave;
    }
});
````

Lo que hace el código anterior es decirle a Mockito, cuando se llame al **this.examRepository** su método
**saveExam(any(Exam.class))** enviándole por argumento un examen a guardar, entonces él dará una nueva respuesta
``then(new Answer<Exam>() {...}``. El parámetro **invocation** contiene en el argument con índice 0 (cero) el examen que
se le pasa en el método real: ``this.examRepository.saveExam(exam)``. Obtenemos el examen pasado y le asignamos un
identificador, podría ser cualquiera, pero en este caso le decimos ``sequence++`` por si queremos ejecutar varias veces
el método save. Finalmente, se retornará el examen con un identificador ya establecido y es el que recibiremos en la
variable **examenDB**.

## Comprobaciones de excepciones usando when y thenThrow

Necesitamos crear una lista de exámenes que tengan como id = null:

````java
public class Data {
    public static final List<Exam> EXAMS_ID_NULL = List.of(
            new Exam(null, "Aritmética"),
            new Exam(null, "Geometría"),
            new Exam(null, "Álgebra"));
}
````

Creamos nuestro test para ver cómo mockito trabaja con las excepciones:

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */
    /* other tests */
    @Test
    void workingWithExceptions() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS_ID_NULL);
        when(this.questionRepository.findQuestionsByExamId(isNull())).thenThrow(IllegalArgumentException.class);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.examService.findExamByNameWithQuestions("Aritmética");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(this.examRepository).findAll();
        verify(this.questionRepository).findQuestionsByExamId(isNull());
    }
}
````

Del test anterior, estamos mockeando cuando se llame al **this.examRepository.findAll()** nos retorne una lista de
exámenes con id = null, también le decimos a mockito que si se llama al
**this.questionRepository.findQuestionsByExamId(isNull())** cuyo argumento pasado sea null, entonces que nos retorne
una excepción del tipo **IllegalArgumentException**. Finalmente usando el **assertThrows** de JUnit capturamos la
excepción que nos debería lanzar, porque cuando busca el examen **Aritmética** sí lo va a encontrar, pero tendrá su
identificador igual a null, por lo tanto cuando se busque usando el método **findQuestionsByExamId()** el identificador
pasado será null, es allí donde entra el segundo método mockeado lanzándonos el **IllegalArgumentException**.

## Argument matchers - uso del argThat() y eq()

El **Argument matchers** es una característica de mockito que permite saber si coincide el valor real que se pasa por
argumento en el método que se está probando y lo comparamos con los definidos en el mock (when o verify), es decir,
son utilidades que se utilizan para verificar los argumentos pasados a los métodos simulados durante las pruebas
unitarias.

Hay varios tipos de Argument Matchers disponibles en Mockito, como **any(), eq(), isNull(), isNotNull(), anyLong(),
etc.**

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */
    /* other tests */
    @Test
    void argumentMatchersTest() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);
        when(this.questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        this.examService.findExamByNameWithQuestions("Aritmética");

        verify(this.examRepository).findAll();
        verify(this.questionRepository).findQuestionsByExamId(argThat(arg -> arg != null && arg.equals(1L)));   // (1)
        verify(this.questionRepository).findQuestionsByExamId(eq(1L));                                          // (2)
    }
}
````

**DONDE**

- **(1)** le decimos a mockito que verifique que del **this.questionRepository** se haya llamado al
  **findQuestionsByExamId()** y que el argumento con el que se le ha llamado sea 1L. Podemos usar el **Argument
  Matchers** llamado **argThat(...)** y usar una expresión lambda para agregar cierta lógica. Podríamos también haber
  pasado directamente el argumento así: **findQuestionsByExamId(1L)**, pero no tendríamos la ventaja de poder hacer
  cierta lógica, digamos que usando expresiones lamba hace más potente el desarrollo.
- **(2)** le decimos a mockito que verifique que del **this.questionRepository** se haya llamado a su
  **findQuestionsByExamId()** y que el argumento que se le haya pasado sea igual a 1L, pero eso lo expresamos con el
  argument matchers **eq(1L)**.
- **(1) y (2)**, con el verify, no solamente nos aseguramos que se invocó un método mock, sino también esa invocación se
  produjo con un valor específico.

## Argument Matchers - Creación de clase personalizada

Ahora veremos cómo implementar un **Argument Matchers personalizado** pero con una clase. Puede ser una clase
implementada al vuelo o una clase separada o incluso una inner class. En mi caso crearé una clase separada:

````java
public class MiArgsMatchers implements ArgumentMatcher<Long> { // (1)
    private Long argument;

    @Override
    public boolean matches(Long argument) { // (2)
        this.argument = argument;
        return this.argument != null && this.argument > 0;
    }

    @Override
    public String toString() {
        return String.format("El arg enviado fue %d, se esperaba que fuera un entero positivo", this.argument);
    }
}
````

**DONDE**

- **(1) y (2)** el tipo Long corresponde al tipo de dato del argumento que es pasado como un argumentMatchers. En
  nuestro ejemplo, el método donde aplicaremos este argumentMatchers será el **findQuestionsByExamId()**, quien
  precisamente espera recibir un tipo de dato Long.

Una vez tengamos la clase la podemos usar en nuestro método test. Crearemos uno para hacer pruebas cuando se llame al
**finAll()** lista de los exámenes, pero retornará **exámenes con id negativo**, solo para probar nuestra clase
**MiArgsMatchers**.

````java
public class Data {
    public static final List<Exam> EXAMS_NEGATIVES = List.of(
            new Exam(-1L, "Aritmética"),
            new Exam(-2L, "Geometría"),
            new Exam(-3L, "Álgebra"));
}
````

Usamos nuestra clase personalizada **MiArgsMatchers** en conjunto con la verificación de Mockito:

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */
    /* other tests */
    @Test
    void argumentMatchersTest2() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS_NEGATIVES);
        when(this.questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        this.examService.findExamByNameWithQuestions("Aritmética");

        verify(this.examRepository).findAll();
        verify(this.questionRepository).findQuestionsByExamId(argThat(new MiArgsMatchers())); // (1)
    }
}
````

**DONDE**

- **(1)** usamos el **MiArgsMatchers** el cual hará las validaciones definidas en la clase.

## Capturando argumentos de métodos con Argument capture

Úselo para capturar valores de argumento **para más assertions**. Mockito verifica los valores de los argumentos en
estilo java natural: usando un método equals(). Esta es también la forma recomendada de hacer coincidir los argumentos
porque hace que las pruebas sean limpias y simples. Sin embargo, en algunas situaciones, es útil afirmar ciertos
argumentos después de la verificación real. Por ejemplo:

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */
    /* other tests */
    @Test
    void testArgumentCaptor() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        this.examService.findExamByNameWithQuestions("Aritmética");

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);          // (1)
        verify(this.questionRepository).findQuestionsByExamId(captor.capture());    // (2)

        assertEquals(1L, captor.getValue());                                        // (3)
    }
}
````

**DONDE**

- **(1)** definimos un **ArgumentCaptor** del tipo **Long**.
- **(2)** usamos la verificación de Mockito para capturar el valor pasado por argumento al método
  **findQuestionsByExamId(..)**.
- **(3)** usamos el argumento capturado para hacer un assertion (afirmación). En nuestro ejemplo, el argumento capturado
  debe ser igual a 1L, ya que se está buscando **Aritmética** quien tiene un id = 1L.

## Argument Capture con anotación @Capture

En el test anterior hicimos de manera explícita y manual el uso de ArgumentCaptor, pero podríamos haber usado
anotaciones, tal como lo veremos en este ejemplo:

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */

    @Captor
    ArgumentCaptor<Long> captor;

    /* other tests */
    @Test
    void testArgumentCaptorWithAnnotations() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        this.examService.findExamByNameWithQuestions("Aritmética");

        verify(this.questionRepository).findQuestionsByExamId(captor.capture());

        assertEquals(1L, captor.getValue());
    }
}
````

## doThrow, usándolo para comprobar excepciones en métodos void

Cuando estamos mockeando un método, generalmente cuando dicho método nos retorna algo usamos el ``when().thenReturn()``,
por ejemplo: ``when(this.examRepository.findAll()).thenReturn(Data.EXAMS)``. Ahora, qué pasa si queremos mockear un
método que no nos retorna nada, es decir es un **void**, allí utilizaríamos una familia de métodos que inician con
``do``, por ejemplo:

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */
    /* other tests */

    @Test
    void testDoThrow() {
        Exam exam = Data.EXAM_WHITOUT_ID;
        exam.setQuestions(Data.QUESTIONS);

        doThrow(IllegalArgumentException.class).when(this.questionRepository).saveQuestions(anyList()); // (1)

        assertThrows(IllegalArgumentException.class, () -> {    // (2)
            this.examService.saveExam(exam);
        });
    }
}
````

**DONDE**

- **(1)**, hacer que se lance la excepción **IllegalArgumentException** cuando del **this.questionRepository** se
  llame a su método **saveQuestion()**.
- **(2)**, afirmamos que se haya lanzado la excepción **IllegalArgumentException**.

Como se observa, es un ejemplo donde nosotros queremos **lanzar una excepción** cuando se llame al **saveQuestions()**,
entonces una forma de mockear ese comportamiento es usando el **doThrow()**.

## doAnswer

Anteriormente, ya habíamos usado el **Answer** en el método **saveExamWithQuestionsReturnExamWithId()** pero de una
manera más programática, lo usamos para poder generar un id cuando se guarde un examen. En esta oportunidad usaremos el
**doAnswer(...)**, que tiene casi el mismo comportamiento, veamos el mismo ejemplo pero con el **doAnswer()**:

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */
    /* other tests */

    @Test
    void doAnswerSaveExamWithQuestionsReturnExamWithId() {
        // given
        Exam exam = Data.EXAM_WHITOUT_ID;
        exam.setQuestions(Data.QUESTIONS);

        doAnswer(invocation -> {
            Exam examDB = invocation.getArgument(0);
            examDB.setId(10L);
            return examDB;
        }).when(this.examRepository).saveExam(any(Exam.class));

        doNothing().when(this.questionRepository).saveQuestions(anyList());

        // when
        Exam examDB = this.examService.saveExam(exam);

        // then
        assertNotNull(examDB);
        assertEquals(10L, examDB.getId());
        assertEquals("Kubernetes", examDB.getName());

        verify(this.examRepository).saveExam(any(Exam.class));
        verify(this.questionRepository).saveQuestions(anyList());
    }
}
````

En el ejemplo anterior usamos el método de mockito **doAnswer()** para obtener a través del **invocation** el examen
pasado por el método **saveQuestions(...)**, luego proceder a agregarle un identificador simulando que se ha registrado
en la base de datos.

A continuación se muestra otro ejemplo, pero en este caso, dependiendo del id obtenido retornar una lista de preguntas o
un listado vacío.

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */
    /* other tests */

    @Test
    void testDoAnswer() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        doAnswer(invocation -> {                                                // (1)
            Long id = invocation.getArgument(0);                                // (2)
            return id == 5L ? Data.QUESTIONS : List.of();
        }).when(this.questionRepository).findQuestionsByExamId(anyLong());      // (3)

        Exam exam = this.examService.findExamByNameWithQuestions("Programación");

        assertEquals(5L, exam.getId());
        assertFalse(exam.getQuestions().isEmpty());
    }
}
````

Lo que estamos haciendo en el test anterior es mockear el método **findQuestionsByExamId()** del repositorio
**this.questionRepository**, donde:

- **(1)** el doAnswer nos permitirá definir un **Answer** definiendo una expresión lambda.
- **(2)** con el **invocation** que es del tipo **InvocationOnMock** obtenemos el argumento en el índice 0. Es índice 0
  porque es un solo argumento el que se le pasa: **findQuestionsByExamId(anyLong())**
- **(3)** es el método mockeado, es decir, cuando se llame a este método real se aplicará el **doAnswer()**.

## doCallRealMethod, usándolo para la llamada real a un método mock

**doCallRealMethod** nos va a permitir invocar el método real del mock (no el simulado), pero para eso debemos tener una
implementación real y no una interfaz o clase abstracta de los objetos simulados.

En primer lugar, crearemos una implementación concreta de la interfaz **IQuestionRepository** implementando solo el
método **findQuestionsByExamId()** ya que será el que usemos para verificar que se va a llamar a este método real.

````java
public class QuestionRepositoryImpl implements IQuestionRepository {
    @Override
    public List<String> findQuestionsByExamId(Long id) {
        return List.of("Pregunta 1 (real)", "Pregunta 2 (real)", "Pregunta 3 (real)",
                "Pregunta 4 (real)", "Pregunta 5 (real)");
    }

    @Override
    public void saveQuestions(List<String> questions) {

    }
}
````

Luego, como usaremos el **doCallRealMethod**, forzosamente necesitamos que los mocks sean del tipo concreto, **¡Ojo!
solo de los mocks que queremos obtener sus valores reales**, por eso cambiamos solo el **IQuestionRepository** por
el **QuestionRepositoryImpl**:

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    @Mock
    private IExamRepository examRepository;             // (1)

    @Mock
    private QuestionRepositoryImpl questionRepository;  // (2)

    @InjectMocks
    private ExamenServiceImpl examService;
    /* omitted code */
}
````

**DONDE**

- **(1)** este objeto sí lo simularemos por eso lo dejamos con su forma más abstracta que es la interfaz.
- **(2)** observamos que ahora estamos usando la implementación concreta, pues si vamos a usar el **doCallRealMethod**,
  necesariamente tiene ser implementación concreta.

Ahora toca implementar el test para usar el método real **findQuestionsByExamId()**:

````java

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    /* @Mock e @InjectMocks */
    /* other tests */

    @Test
    void testToCallRealMethod() {
        when(this.examRepository.findAll()).thenReturn(Data.EXAMS);

        doCallRealMethod().when(this.questionRepository).findQuestionsByExamId(anyLong()); // (1)

        Exam exam = this.examService.findExamByNameWithQuestions("Aritmética");

        assertEquals(1L, exam.getId());
        assertEquals("Aritmética", exam.getName());
        assertFalse(exam.getQuestions().isEmpty());
        assertEquals("Pregunta 1 (real)", exam.getQuestions().get(0));                     // (2)
    }
}
````

**DONDE**

- **(1)** estamos usando el **doCallRealMethod()** para llamar al método real **findQuestionsByExamId()** y según
  nuestra implementación concreta dicho método real nos retorna una lista de 5 preguntas.
- **(2)** comprobamos que de las preguntas reales retornadas nos retorne ese.

**NOTA**
> El método test **testToCallRealMethod()** lo dejaré deshabilitado, ya que anotar con @Mock la implementación
> concreta solo fue para trabajar dicho método. Ahora el @Mock está sobre la interfaz IQuestionRepository, tal como lo
> hemos venido trabajando hasta ahora.