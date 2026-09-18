# DOSW_Lab5_Aguirre_Gonzalez_Nieto-
# LABORATORIO - TDD, CUBRIMIENTO Y ANÁLISIS ESTÁTICO

*Escuela Colombiana de Ingeniería Julio Garavito*  
*Curso:* Desarrollo y Operaciones de Software - DOSW  
*Caso de estudio:* *SkyRescue - Coordinación de drones para emergencias*

---

## 1. Objetivo

Aplicar *Test-Driven Development (TDD)* como fundamento para estructurar técnicamente un proyecto de software, integrando además:

- desarrollo colaborativo con Git y Pull Requests;
- pruebas unitarias con JUnit 5;
- medición de cobertura con JaCoCo;
- análisis estático con SonarQube;
- documentación técnica y evidencias en el README.md del repositorio.

El laboratorio se desarrollará en los equipos correspondientes al *SQUAD* definido para el proyecto.

> *Regla principal:* en la sección TDD no se implementa primero la solución. Primero se escribe una prueba que falle (*RED), después se implementa el código mínimo para hacerla pasar (GREEN) y finalmente se mejora el diseño sin romper las pruebas (REFACTOR*).

---
### 10. DOCUMENTACIÓN OBLIGATORIA EN EL README DEL REPOSITORIO

## 10.1 Integrantes

- Camilo Aguirre
- Juan Nieto
- Sara Sofía González

## 10.2 Descripción de SkyRescue

SkyRescue coordina drones de emergencia que llevan kits médicos, cámaras
térmicas o radios hacia zonas de difícil acceso en una ciudad. El centro
de operaciones registra drones y operadores, asigna misiones y las cierra
cuando el dron regresa.

Las reglas principales del dominio son: un dron no puede atender dos
emergencias al mismo tiempo ni ser enviado más allá de su autonomía; un
operador debe existir y no puede tener dos misiones activas a la vez; y
una misión no puede cerrarse más de una vez.

Las tres operaciones desarrolladas con TDD son addDrone (registra un
dron validando duplicados y datos inválidos), assignMission (asigna una
misión verificando operador, disponibilidad y autonomía del dron) y
completeMission (cierra una misión activa liberando el dron).

## 10.3 Evidencia TDD

### Ciclo TDD - Finalización de misión (completeMission)

*RED:* Se agregaron 2 nuevas pruebas unitarias (shouldCompleteActiveMissionSuccessfully
y shouldThrowIllegalArgumentExceptionWhenMissionDoesNotExist). Al ejecutar
mvn test, la suite reportó Tests run: 9, Failures: 2, confirmando que
la lógica para completar una misión aún no estaba implementada en el
centro de rescate y no se validaba la existencia de la misión.

![Prueba fallando](https://github.com/user-attachments/assets/00082083-bb87-41b0-b2dd-42e3c13aea0b)

*GREEN:* Se implementó el método completeMission en RescueCenter.java
utilizando Streams para validar la existencia de la misión con
IllegalArgumentException, actualizar el estado a COMPLETED, asignar
la fecha de cierre (LocalDateTime.now()) y liberar el dron
correspondiente marcándolo como disponible. Al reejecutar mvn test, la
suite pasó a Tests run: 9, Failures: 0 (BUILD SUCCESS).

![Prueba pasando](https://github.com/user-attachments/assets/4fceffb1-fea5-4bd1-9ecb-9a5d92d6a2df)

*REFACTOR:* Se optimizó la búsqueda y actualización de la misión en
RescueCenter.java mediante encadenamiento de métodos sobre Optional
(orElseThrow), garantizando un código expresivo, libre de
verificaciones nulas manuales y desacoplado de la gestión directa del
estado del dron.

## 10.4 Cobertura

### Primera ejecución

![mvn clean verify inicial](docs/evidence/mvn%20verify%20inicial.png)
![Cobertura inicial](docs/evidence/coverage-first.png)

### Cobertura final

![mvn clean verify final](docs/evidence/mvn%20verify%20final.png)
![Cobertura final](docs/evidence/coverage-final.png)

La suite final alcanzó *11 pruebas ejecutadas, 0 fallos*, cumpliendo el
mínimo del 85% de cobertura de líneas exigido por el laboratorio.

## 10.5 SonarQube

![Dashboard SonarQube](docs/evidence/sonarqube-dashboard.png)

El análisis se ejecutó con mvn clean verify sonar:sonar sobre el
proyecto skyrescue-tdd. El **Quality Gate quedó
en estado Passed**. Los resultados por categoría fueron: Security con 0
issues abiertos, Reliability con 2 issues abiertos y Maintainability con
3 issues abiertos, todos calificados en nivel A. La cobertura reportada
fue de *87.5%* sobre 88 líneas a cubrir, y las duplicaciones de código
fueron del 0.0%, superando el mínimo del 85% requerido por el
laboratorio.

## 10.6 Flujo Git

- PR #1: fix and update pom with Junit
- PR #2: add structure of package and classes
- PR #3: Feature/junit dependency
- PR #4: Feature/tdd add drone ca
- PR #5: Feature/tdd assign mission ca
- PR #6: Feature/tdd assign mission jn
- PR #7: feature/tdd-assign-mission-sg
- PR #8: feat: GREEN - implementa completeMission y validación de existencia
- PR #9: Feature/tdd complete mission jn
- PR #10: Feature/tdd complete mission sg
- PR #11: Feature/jacoco
- PR #12: Feature/sonarqube
- PR #13: Feature/documentacion -> develop

## 10.7 Reflexión técnica

**¿Qué error o comportamiento inesperado fue detectado primero gracias a
una prueba?**
Al escribir las pruebas RED para completeMission, se detectó que el
método no validaba la existencia de la misión ni lanzaba la excepción
esperada, ya que la lógica todavía no estaba implementada (`Tests run: 9,
Failures: 2`).

**¿Qué parte del código cambió durante REFACTOR sin modificar el
comportamiento?**
La búsqueda y actualización de la misión en RescueCenter.java se
reescribió usando Optional y orElseThrow en vez de validaciones
manuales con condicionales, manteniendo el mismo comportamiento externo
pero con un código más expresivo y desacoplado.

*¿Qué casos adicionales aparecieron al revisar la cobertura?*
Se agregaron casos límite como el envío de un missionId nulo o en
blanco a completeMission, y la verificación de que cerrar una misión no
afecte el estado de otras misiones activas.

*¿Qué hallazgo de SonarQube produjo un cambio real en el código?*
El análisis identificó 3 issues en RescueCenter.java y
RescueCenterTest.java: dos relacionados con el uso de `LocalDateTime.now()`sin especificar zona horaria, y uno sobre refactorizar una expresión lambda en las pruebas para evitar múltiples invocaciones que podrían lanzar una excepción en tiempo de ejecución,nosotros evaluamos  los tres hallazgos y decidimos aceptarlos, no afectaban el comportamiento del dominio dentro del alcance del laboratorio y con esto pudimos mantener el Quality Gate en estado Passed