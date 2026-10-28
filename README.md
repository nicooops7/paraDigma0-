# ParaDigma0 - Avance 2 (sistema de información meteorológica)

Proyecto de POO, Universidad del Bío-Bío.

## Requisitos
Java 17 o superior. Ejecutar desde la carpeta raíz del proyecto para que el directorio `datos/` sea localizable.

## Compilación y ejecución desde terminal

```bash
javac -encoding UTF-8 -d out src/modelo/*.java src/controlador/*.java src/vista/*.java
java -cp out vista.Main
```

En IntelliJ: abrir la carpeta del proyecto y ejecutar `vista.Main`.

## Estructura
* `src/modelo`: entidades de dominio, sensores, resúmenes y alertas.
* `src/controlador`: coordinador del sistema, excepción y persistencia de texto.
* `src/vista`: menú y punto de entrada.
* `datos`: archivos de ejemplo `regiones.txt`, `comunas.txt`, `estaciones.txt`, `sensores.txt`, `mediciones.txt`, separados por punto y coma, UTF-8, sin cabecera.

Los archivos de ejemplo contienen 2 regiones, 4 comunas, 4 estaciones, 10 sensores y 40 mediciones. Para cargarlos, seleccione opción 8 al iniciar un proceso sin datos en memoria.

## Aviso
Esta carpeta es una copia de trabajo revisada. Integrar y probar con el repositorio real antes de hacer merge a `main`.