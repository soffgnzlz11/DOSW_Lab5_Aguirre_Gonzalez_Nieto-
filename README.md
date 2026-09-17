# DOSW_Lab5_Aguirre_Gonzalez_Nieto-

### Ciclo TDD - Finalización Misión (`CompleteMission`)

**RED:** Se agregaron 2 nuevas pruebas unitarias (shouldCompleteActiveMissionSuccessfully y shouldThrowIllegalArgumentExceptionWhenMissionDoesNotExist). Al ejecutar mvn test, la suite reporta Tests run: 9, Failures: 2, confirmando que la lógica para completar una misión aún no estaba implementada en el centro de rescate y no se validaba la existencia de la misión.

[Prueba fallando]
<img width="1490" height="638" alt="image" src="https://github.com/user-attachments/assets/00082083-bb87-41b0-b2dd-42e3c13aea0b" />

**GREEN:** Se implementó el método completeMission en RescueCenter.java utilizando Streams para validar la existencia de la misión con IllegalArgumentException, actualizar el estado a COMPLETED, asignar la fecha de cierre (LocalDateTime.now()) y liberar el dron correspondiente marcándolo como disponible. Al reejecutar mvn test, la suite pasa a Tests run: 9, Failures: 0 (BUILD SUCCESS), confirmando el cumplimiento de todas las pruebas.

[Prueba pasando]
<img width="1380" height="590" alt="image" src="https://github.com/user-attachments/assets/4fceffb1-fea5-4bd1-9ecb-9a5d92d6a2df" />


**REFACTOR:** Se optimizó la búsqueda y actualización de la misión en RescueCenter.java mediante encadenamiento de métodos sobre Optional (orElseThrow), garantizando un código expresivo, libre de verificaciones nulas manuales y desacoplado de la gestión directa del estado del dron.


### Cobertura con JaCoCo

## Evidencia de cobertura

### Primera ejecución

![mvn clean verify inicial](skyrescue-tdd/docs/evidence/mvn%20verify%20inicial.png)

![Cobertura inicial](skyrescue-tdd/docs/evidence/coverage-first.png)

### Cobertura final

![mvn clean verify final](skyrescue-tdd/docs/evidence/mvn%20verify%20final.png)

![Cobertura final](skyrescue-tdd/docs/evidence/coverage-final.png)

