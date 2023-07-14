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
