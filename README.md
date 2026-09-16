# DOSW_Lab5_Aguirre_Gonzalez_Nieto-
# LABORATORIO - TDD, CUBRIMIENTO Y ANÁLISIS ESTÁTICO

**Escuela Colombiana de Ingeniería Julio Garavito**  
**Curso:** Desarrollo y Operaciones de Software - DOSW  
**Caso de estudio:** **SkyRescue - Coordinación de drones para emergencias**

---

## 1. Objetivo

Aplicar **Test-Driven Development (TDD)** como fundamento para estructurar técnicamente un proyecto de software, integrando además:

- desarrollo colaborativo con Git y Pull Requests;
- pruebas unitarias con JUnit 5;
- medición de cobertura con JaCoCo;
- análisis estático con SonarQube;
- documentación técnica y evidencias en el `README.md` del repositorio.

El laboratorio se desarrollará en los equipos correspondientes al **SQUAD** definido para el proyecto.

> **Regla principal:** en la sección TDD no se implementa primero la solución. Primero se escribe una prueba que falle (**RED**), después se implementa el código mínimo para hacerla pasar (**GREEN**) y finalmente se mejora el diseño sin romper las pruebas (**REFACTOR**).

---

# PARTE 1 - CONOCIENDO TDD CON SKYRESCUE

## 2. Contexto del reto

Una ciudad está implementando **SkyRescue**, una plataforma para coordinar drones que apoyan operaciones de emergencia.

Los drones pueden transportar pequeños kits médicos, cámaras térmicas o radios de comunicación hacia zonas de difícil acceso. El centro de operaciones debe registrar drones y operadores, asignar misiones y cerrar una misión cuando el dron regresa.

El sistema debe impedir situaciones como:

- asignar un dron que ya está atendiendo otra emergencia;
- enviar un dron a una distancia mayor que su autonomía;
- asignar una misión a un operador inexistente;
- permitir que un mismo operador controle dos misiones activas al mismo tiempo;
- cerrar dos veces la misma misión.

El objetivo no es construir una aplicación completa, sino desarrollar la **lógica de dominio** aplicando TDD.

---

## 3. Crear el proyecto Maven

Crear un proyecto Maven con los siguientes parámetros sugeridos:

```text
groupId:    edu.eci.dosw
artifactId: skyrescue-tdd
version:    1.0-SNAPSHOT
Java:       21
```

Verifique la versión instalada:

```bash
java -version
mvn -version
```

El proyecto debe compilar con:

```bash
mvn clean package
```

---

## 4. Agregar JUnit 5

### 4.1 Flujo Git

Crear la rama:

```bash
git checkout develop
git pull
git checkout -b feature/junit-dependency
```

Agregar JUnit 5 al `pom.xml`.

Para este laboratorio se utilizará **JUnit Jupiter 5.13.4**.

```xml
<properties>
    <maven.compiler.release>17</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <junit.version>5.13.4</junit.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Ejecute:

```bash
mvn test
```

### 4.2 Pull Request

Realice un PR desde:

```text
feature/junit-dependency -> develop
```

El PR debe ser revisado y aprobado por un integrante del equipo diferente de quien lo creó.

---

## 5. Estructura del proyecto

Crear la siguiente estructura dentro de `src/main/java`:

```text
edu.eci.dosw.tdd.skyrescue
├── center
│   └── RescueCenter.java
├── drone
│   └── Drone.java
├── mission
│   ├── Mission.java
│   └── MissionStatus.java
└── operator
    └── RescueOperator.java
```

Crear la misma estructura base dentro de `src/test/java` para las pruebas.

---

## 6. Crear las clases del dominio

Crear la rama:

```bash
git checkout -b feature/skyrescue-classes
```

### 6.1 Clase `Drone`

Archivo:

```text
src/main/java/edu/eci/dosw/tdd/skyrescue/drone/Drone.java
```

```java
package edu.eci.dosw.tdd.skyrescue.drone;

import java.util.Objects;

public class Drone {

    private final String id;
    private final String model;
    private final int maxRangeKm;
    private boolean available;

    public Drone(String id, String model, int maxRangeKm) {
        this.id = id;
        this.model = model;
        this.maxRangeKm = maxRangeKm;
        this.available = true;
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public int getMaxRangeKm() {
        return maxRangeKm;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Drone)) {
            return false;
        }
        Drone other = (Drone) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

---

### 6.2 Clase `RescueOperator`

Archivo:

```text
src/main/java/edu/eci/dosw/tdd/skyrescue/operator/RescueOperator.java
```

```java
package edu.eci.dosw.tdd.skyrescue.operator;

public class RescueOperator {

    private final String id;
    private final String name;

    public RescueOperator(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
```

---

### 6.3 Enum `MissionStatus`

Archivo:

```text
src/main/java/edu/eci/dosw/tdd/skyrescue/mission/MissionStatus.java
```

```java
package edu.eci.dosw.tdd.skyrescue.mission;

public enum MissionStatus {
    ACTIVE,
    COMPLETED
}
```

---

### 6.4 Clase `Mission`

Archivo:

```text
src/main/java/edu/eci/dosw/tdd/skyrescue/mission/Mission.java
```

```java
package edu.eci.dosw.tdd.skyrescue.mission;

import edu.eci.dosw.tdd.skyrescue.drone.Drone;
import edu.eci.dosw.tdd.skyrescue.operator.RescueOperator;

import java.time.LocalDateTime;

public class Mission {

    private final String id;
    private final String location;
    private final int distanceKm;
    private final Drone drone;
    private final RescueOperator operator;
    private final LocalDateTime startDate;

    private LocalDateTime endDate;
    private MissionStatus status;

    public Mission(
            String id,
            String location,
            int distanceKm,
            Drone drone,
            RescueOperator operator,
            LocalDateTime startDate,
            MissionStatus status) {
        this.id = id;
        this.location = location;
        this.distanceKm = distanceKm;
        this.drone = drone;
        this.operator = operator;
        this.startDate = startDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public int getDistanceKm() {
        return distanceKm;
    }

    public Drone getDrone() {
        return drone;
    }

    public RescueOperator getOperator() {
        return operator;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }
}
```

---

### 6.5 Clase `RescueCenter`

Archivo:

```text
src/main/java/edu/eci/dosw/tdd/skyrescue/center/RescueCenter.java
```

```java
package edu.eci.dosw.tdd.skyrescue.center;

import edu.eci.dosw.tdd.skyrescue.drone.Drone;
import edu.eci.dosw.tdd.skyrescue.mission.Mission;
import edu.eci.dosw.tdd.skyrescue.operator.RescueOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinates drones, operators and emergency missions.
 */
public class RescueCenter {

    private final List<RescueOperator> operators;
    private final Map<String, Drone> drones;
    private final List<Mission> missions;

    public RescueCenter() {
        this.operators = new ArrayList<>();
        this.drones = new HashMap<>();
        this.missions = new ArrayList<>();
    }

    /**
     * Registers a drone in the rescue center.
     *
     * Rules:
     * - The drone cannot be null.
     * - The drone id cannot be null or blank.
     * - Two drones cannot have the same id.
     * - A valid drone is stored as available.
     *
     * @param drone drone to register.
     * @return true if it was registered; false otherwise.
     */
    public boolean addDrone(Drone drone) {
        // TODO Implement using TDD.
        return false;
    }

    /**
     * Assigns an emergency mission to an operator and an available drone.
     *
     * Rules:
     * - operatorId, droneId and location must be valid.
     * - The operator must exist.
     * - The drone must exist and be available.
     * - distanceKm must be greater than zero.
     * - distanceKm cannot exceed the drone maxRangeKm.
     * - The same operator cannot have two ACTIVE missions.
     * - On success, create an ACTIVE mission with the current date.
     * - On success, the selected drone becomes unavailable.
     * - The created mission must be stored in the center.
     *
     * Suggested error policy:
     * - Invalid/nonexistent data -> IllegalArgumentException.
     * - Valid resource but invalid state -> IllegalStateException.
     *
     * @param operatorId operator identifier.
     * @param droneId drone identifier.
     * @param location emergency location description.
     * @param distanceKm mission distance in kilometers.
     * @return created mission.
     */
    public Mission assignMission(
            String operatorId,
            String droneId,
            String location,
            int distanceKm) {
        // TODO Implement using TDD.
        return null;
    }

    /**
     * Completes an active mission.
     *
     * Rules:
     * - missionId must be valid.
     * - The mission must exist.
     * - An already COMPLETED mission cannot be completed again.
     * - The mission status changes to COMPLETED.
     * - The end date is the current date/time.
     * - The drone assigned to the mission becomes available again.
     *
     * Suggested error policy:
     * - Invalid/nonexistent mission -> IllegalArgumentException.
     * - Mission already completed -> IllegalStateException.
     *
     * @param missionId mission identifier.
     * @return completed mission.
     */
    public Mission completeMission(String missionId) {
        // TODO Implement using TDD.
        return null;
    }

    public boolean addOperator(RescueOperator operator) {
        return operators.add(operator);
    }
}
```

### 6.6 Validar compilación

En este punto los métodos principales todavía tienen `TODO`, pero el proyecto debe compilar:

```bash
mvn clean package
```

Realice un PR:

```text
feature/skyrescue-classes -> develop
```

El PR debe ser revisado por otro integrante del SQUAD.

---

# 7. PRUEBAS UNITARIAS Y TDD

## 7.1 Crear la clase de pruebas

Crear una rama de trabajo para las pruebas. Ejemplo:

```bash
git checkout -b feature/skyrescue-tdd
```

Crear:

```text
src/test/java/edu/eci/dosw/tdd/skyrescue/center/RescueCenterTest.java
```

La clase y los métodos de prueba deben seguir convenciones de nombres claras.

Ejemplo de estilo:

```java
@Test
void shouldRegisterDroneWhenDataIsValid() {
    // Arrange
    // Act
    // Assert
}
```

> No implemente todavía la lógica de `addDrone`, `assignMission` o `completeMission` antes de tener pruebas que fallen.

---

## 7.2 Casos de prueba mínimos

Cada integrante del SQUAD debe aportar al menos **una prueba distinta por cada método**. El equipo debe alcanzar como mínimo **4 pruebas por método**.

### A. `addDrone`

Casos sugeridos:

| Caso | Resultado esperado |
|---|---|
| Registrar un dron válido | `true` |
| Registrar `null` | `false` |
| Registrar un dron con id vacío | `false` |
| Registrar dos drones con el mismo id | El segundo registro retorna `false` |

### B. `assignMission`

Casos sugeridos:

| Caso | Resultado esperado |
|---|---|
| Operador y dron válidos, distancia permitida | Se crea misión `ACTIVE` y el dron queda no disponible |
| Dron inexistente | `IllegalArgumentException` |
| Dron ya ocupado | `IllegalStateException` |
| Distancia superior a la autonomía | `IllegalArgumentException` |
| Operador inexistente | `IllegalArgumentException` |
| Operador con otra misión activa | `IllegalStateException` |

### C. `completeMission`

Casos sugeridos:

| Caso | Resultado esperado |
|---|---|
| Cerrar una misión activa | Estado `COMPLETED`, fecha de cierre no nula y dron disponible |
| Misión inexistente | `IllegalArgumentException` |
| Cerrar dos veces la misma misión | `IllegalStateException` |
| Cerrar una misión no debe modificar otra misión activa | La otra misión conserva su estado y su dron sigue ocupado |

El equipo puede proponer casos adicionales, especialmente casos frontera.

---

## 7.3 Ciclo TDD obligatorio

Para cada comportamiento:

### Paso 1 - RED

1. Escriba una prueba.
2. Ejecute:

```bash
mvn test
```

3. Compruebe que la prueba falla por la razón correcta.
4. Haga commit.

Formato sugerido:

```text
test: RED - no permite asignar un dron ocupado
```

### Paso 2 - GREEN

Implemente únicamente el código necesario para hacer pasar la prueba.

Ejecute nuevamente:

```bash
mvn test
```

Commit sugerido:

```text
feat: GREEN - valida disponibilidad del dron
```

### Paso 3 - REFACTOR

Mejore nombres, elimine duplicación o simplifique el diseño sin modificar el comportamiento.

Ejecute:

```bash
mvn test
```

Commit sugerido:

```text
refactor: simplifica validación de misiones activas
```

---

## 7.4 Trabajo colaborativo

Cada integrante debe trabajar en su propia rama.

Ejemplos:

```text
feature/tdd-add-drone-rg
feature/tdd-assign-mission-nt
feature/tdd-complete-mission-jp
```

Cada aporte debe entrar a `develop` mediante Pull Request.

Requisitos del PR:

- debe incluir pruebas;
- debe evidenciar al menos un ciclo RED -> GREEN;
- debe pasar `mvn test`;
- debe ser revisado por otra persona;
- no se permiten commits directos a `develop` para esta actividad.

---

# 8. COBERTURA CON JACOCO

Crear la rama:

```bash
git checkout -b feature/jacoco
```

Para este laboratorio se utilizará **JaCoCo 0.8.15**.

Agregar dentro de `<build><plugins>`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.15</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>

        <execution>
            <id>jacoco-report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>

        <execution>
            <id>jacoco-check</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.85</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Ejecute:

```bash
mvn clean verify
```

El reporte HTML debe quedar disponible en:

```text
target/site/jacoco/index.html
```

## Meta de cobertura

El proyecto debe alcanzar como mínimo:

```text
85% de cobertura de líneas
```

Si el proyecto no alcanza el porcentaje requerido, diseñe nuevas pruebas. **No se permite bajar el umbral para hacer pasar el build.**

### Evidencias en el README del repositorio

Guardar las capturas dentro del repositorio, por ejemplo:

```text
docs/evidence/coverage-first.png
docs/evidence/coverage-final.png
```

Y mostrarlas en el `README.md`:

```markdown
## Evidencia de cobertura

### Primera ejecución
![Cobertura inicial](docs/evidence/coverage-first.png)

### Cobertura final
![Cobertura final](docs/evidence/coverage-final.png)
```

Realice un PR:

```text
feature/jacoco -> develop
```

---

# 9. ANÁLISIS ESTÁTICO CON SONARQUBE

## 9.1 Ejecutar SonarQube con Docker

Para el laboratorio se puede usar la imagen Community oficial:

```bash
docker pull sonarqube:26.9.0.129388-community
```

Crear el contenedor:

```bash
docker run -d \
  --name sonarqube \
  -p 9000:9000 \
  sonarqube:26.9.0.129388-community
```

Validar:

```bash
docker ps
```

Abrir:

```text
http://localhost:9000
```

En una instalación nueva, SonarQube solicitará configurar las credenciales administrativas.

> **Importante:** no publique contraseñas ni tokens en el repositorio.

---

## 9.2 Crear el proyecto y generar token

En SonarQube:

1. Crear un proyecto local llamado `skyrescue-tdd`.
2. Generar un token para ejecutar el análisis.
3. Guardar el token temporalmente en una variable de entorno.

Linux/macOS:

```bash
export SONAR_TOKEN="TOKEN_GENERADO"
```

PowerShell:

```powershell
$env:SONAR_TOKEN="TOKEN_GENERADO"
```

El token **no debe** guardarse en Git.

---

## 9.3 Plugin de Sonar para Maven

Para este laboratorio se utilizará **SonarScanner for Maven 5.7.0.6970**.

Agregar dentro de `<build><plugins>`:

```xml
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>5.7.0.6970</version>
</plugin>
```

Agregar las propiedades:

```xml
<properties>
    <!-- otras propiedades del proyecto -->

    <sonar.projectKey>skyrescue-tdd</sonar.projectKey>
    <sonar.projectName>SkyRescue TDD Lab</sonar.projectName>
    <sonar.host.url>http://localhost:9000</sonar.host.url>
    <sonar.coverage.jacoco.xmlReportPaths>
        target/site/jacoco/jacoco.xml
    </sonar.coverage.jacoco.xmlReportPaths>
</properties>
```

---

## 9.4 Ejecutar el análisis

Primero valide todo el proyecto:

```bash
mvn clean verify
```

Después ejecute SonarQube.

Linux/macOS:

```bash
mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN
```

PowerShell:

```powershell
mvn sonar:sonar "-Dsonar.token=$env:SONAR_TOKEN"
```

También puede ejecutar ambos pasos de forma integrada:

```bash
mvn clean verify sonar:sonar -Dsonar.token=$SONAR_TOKEN
```

---

## 9.5 Requisitos del análisis

El equipo debe demostrar:

- `mvn clean verify` exitoso;
- cobertura mínima del 85%;
- análisis de SonarQube ejecutado correctamente;
- Quality Gate aprobado o, si alguna regla del entorno local impide aprobarlo, explicación documentada de la causa;
- corrección de los problemas relevantes encontrados en el código desarrollado por el equipo;
- ningún token o contraseña almacenado en el repositorio.

Si utilizan VS Code, IntelliJ IDEA o Eclipse, pueden instalar **SonarQube for IDE** para recibir retroalimentación durante el desarrollo.

---

# 10. DOCUMENTACIÓN OBLIGATORIA EN EL README DEL REPOSITORIO

El `README.md` del proyecto debe contener como mínimo:

## 10.1 Integrantes

```markdown
## Integrantes

- Nombre 1
- Nombre 2
- Nombre 3
- Nombre 4
```

## 10.2 Descripción de SkyRescue

Explique en máximo 10 líneas:

- qué problema resuelve;
- cuáles son las reglas principales del dominio;
- cuáles son las tres operaciones desarrolladas con TDD.

## 10.3 Evidencia TDD

Documente por lo menos un ciclo completo:

```markdown
### Ciclo TDD - asignación de misión

**RED:** prueba que demuestra que un dron ocupado no puede ser asignado.

![Prueba fallando](docs/evidence/tdd-red.png)

**GREEN:** implementación mínima que hace pasar la prueba.

![Prueba pasando](docs/evidence/tdd-green.png)

**REFACTOR:** breve explicación del cambio realizado.
```

## 10.4 Cobertura

Incluir captura inicial y captura final de JaCoCo.

## 10.5 SonarQube

Incluir captura del dashboard donde se observe:

- análisis terminado;
- cobertura;
- issues encontrados;
- estado del Quality Gate.

## 10.6 Flujo Git

Incluir enlaces a los Pull Requests más importantes.

Ejemplo:

```markdown
## Pull Requests

- PR JUnit: #1
- PR clases base: #2
- PR TDD addDrone: #3
- PR TDD assignMission: #4
- PR TDD completeMission: #5
- PR JaCoCo: #6
- PR SonarQube: #7
```

## 10.7 Reflexión técnica

Cada equipo debe responder brevemente:

1. ¿Qué error o comportamiento inesperado fue detectado primero gracias a una prueba?
2. ¿Qué parte del código cambió durante REFACTOR sin modificar el comportamiento?
3. ¿Qué casos adicionales aparecieron al revisar la cobertura?
4. ¿Qué hallazgo de SonarQube produjo un cambio real en el código?

---

# 11. RETO OPCIONAL - NIVEL AVANZADO

Si el equipo termina antes, implemente con TDD **una** de las siguientes reglas:

### Opción A - Misiones críticas

Agregar prioridad:

```text
LOW, MEDIUM, HIGH, CRITICAL
```

Una misión `CRITICAL` debe quedar claramente identificada en el dominio.

### Opción B - Nivel de batería

Agregar a `Drone` un porcentaje de batería. Una misión solo puede ser asignada si la batería cumple el mínimo definido por el equipo.

### Opción C - Dron en mantenimiento

Agregar el estado `MAINTENANCE`. Un dron en mantenimiento no puede recibir misiones aunque esté físicamente disponible.

La nueva regla debe incluir:

- pruebas RED;
- implementación GREEN;
- REFACTOR si aplica;
- nueva evidencia de cobertura.

---

# 12. ENTREGABLES

Entregar por Teams:

- URL del repositorio del laboratorio.
- Acceso al repositorio para la profesora.
- `README.md` completo con evidencias.
- Pull Requests comentados y revisados.
- Pruebas ejecutándose correctamente.
- Cobertura JaCoCo >= 85%.
- Análisis SonarQube.

---

# 13. CHECKLIST FINAL

Antes de entregar, valide:

- [ ] El proyecto usa Java 21.
- [ ] JUnit 5 está configurado.
- [ ] Existen pruebas para `addDrone`.
- [ ] Existen pruebas para `assignMission`.
- [ ] Existen pruebas para `completeMission`.
- [ ] Se evidencia al menos un ciclo RED -> GREEN -> REFACTOR.
- [ ] Todos los tests pasan.
- [ ] `mvn clean verify` termina correctamente.
- [ ] La cobertura de líneas es >= 85%.
- [ ] El reporte JaCoCo está documentado.
- [ ] SonarQube analiza el proyecto.
- [ ] Los cambios importantes llegaron a `develop` mediante PR.
- [ ] Los PR fueron revisados por otra persona.
- [ ] El `README.md` contiene evidencias y reflexión técnica.

---

## Comandos útiles

```bash
# Ejecutar pruebas
mvn test

# Compilar y validar cobertura
mvn clean verify

# Abrir reporte de cobertura
# target/site/jacoco/index.html

# Ver contenedores
docker ps

# Ver logs de SonarQube
docker logs -f sonarqube

# Ejecutar análisis SonarQube
mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN
```

---

**Fin del laboratorio.**
