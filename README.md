# Sistema de Información Meteorológica - Avance 3

## Equipo

* **Nombre del equipo:** ParaDigma0-
* **Integrantes y responsabilidades:**

| Integrante | Responsabilidad |
|---|---|
| Esteban Pena | Capa controlador: `InstitutoMeteorologia`, persistencia de objetos y de texto, `Main` |
| Joab Vergara | Capa modelo (serialización y streams), `InterfazUsuario` y `VentanaPrincipal` |
| Nicolás Parra | Diálogos modales (`VentanaInstalarSensor`, `VentanaRegistrarMedicion`, `VentanaCambiarEstadoSensor`, `VentanaListadoAlertas`), README y coordinación de Git |

* **Repositorio:** https://github.com/nicooops7/paraDigma0-
* **Tablero Trello:** https://trello.com/invite/b/6ae342aede8c46f3660830bc/ATTI79646616403bca02222df6212b10cec75469EA7E/paradigma0-avance-3-proyecto-poo

## Requisitos

* JDK 25.
* No usa bibliotecas externas: la interfaz gráfica está hecha con Swing, que viene incluido en el JDK.

## Ejecución

### Desde IntelliJ IDEA (recomendado)

1. Clona el repositorio:
   ```
   git clone https://github.com/nicooops7/paraDigma0-.git
   ```
2. Abre la carpeta del proyecto en IntelliJ IDEA y configura el SDK del proyecto en JDK 25 (*File → Project Structure → Project*).
3. Ejecuta la clase `vista.Main`.

### Desde la terminal

Desde la raíz del proyecto:

```
javac -encoding UTF-8 -d out src/controlador/*.java src/modelo/*.java src/vista/*.java
java -cp out vista.Main
```

La aplicación debe ejecutarse desde la raíz del proyecto, porque los archivos de datos se leen desde la carpeta `datos/`.

## Estructura del proyecto

```
src/
├── controlador/   InstitutoMeteorologia (Singleton), MeteorologiaException, persistencia
├── modelo/        Region, Comuna, EstacionMeteorologica, Sensor y sus subclases,
│                  Medicion, AlertaMeteorologica, ResumenMediciones, enums
└── vista/         Main, VentanaPrincipal, diálogos modales e InterfazUsuario
datos/             Archivos de texto de importación y meteorologia.dat
```

## Archivos de datos

Todos los archivos están en la carpeta `datos/`.

### meteorologia.dat

Archivo binario con todos los datos del sistema guardados mediante serialización de objetos: la lista de regiones y, a través de ellas, sus comunas, estaciones, sensores, mediciones y alertas. Lo escribe **Guardar datos** y lo lee **Cargar datos**. No se edita a mano.

El archivo del repositorio se generó importando los archivos de texto de ejemplo y guardando después los datos.

### Archivos de texto de ejemplo

Los usa **Importar datos de texto**. Están codificados en UTF-8, tienen un registro por línea y separan los campos con `;`. Si una línea tiene un error, no se carga ningún dato y se informa el archivo y el número de línea.

| Archivo | Campos de cada línea | Registros |
|---|---|---|
| `regiones.txt` | código de región; nombre | 2 (Ñuble y Biobío) |
| `comunas.txt` | código de región; código de comuna; nombre | 4 |
| `estaciones.txt` | código; nombre; longitud; latitud; altitud; código de región; código de comuna | 4 |
| `sensores.txt` | código de estación; código de sensor; tipo (`TEMPERATURA`, `HUMEDAD`, `PRESION`, `VIENTO` o `PRECIPITACION`); marca; modelo; estado (`ACTIVO` o `INACTIVO`) | 10 |
| `mediciones.txt` | código de estación; código de sensor; fecha y hora (`dd/MM/yyyy HH:mm`); valor | 40 |

Ejemplo de una línea de `mediciones.txt`:

```
EST-NUBLE-01;ST-01;20/08/2026 08:00;-6.0
```

Al importar, las mediciones generan automáticamente las alertas que correspondan.

## Uso

Al iniciar se abre la ventana principal, que muestra en la parte inferior si el sistema tiene datos registrados. Tiene tres secciones:

### Registro

* **Crear región**, **Crear comuna** y **Crear estación meteorológica:** se ingresan los datos por consola.
* **Instalar sensor:** diálogo para elegir la estación, el tipo de sensor, la marca y el modelo.
* **Registrar medición:** diálogo para elegir la estación y el sensor (el combo de sensores depende de la estación elegida), la fecha y hora (`dd/MM/yyyy HH:mm`) y el valor.
* **Cambiar estado de sensor:** diálogo para elegir la estación, el sensor y el nuevo estado.

### Datos

* **Cargar datos:** carga el sistema desde `datos/meteorologia.dat` (solo si no hay datos registrados).
* **Importar datos de texto:** carga los archivos `regiones.txt`, `comunas.txt`, `estaciones.txt`, `sensores.txt` y `mediciones.txt` de la carpeta `datos/`.
* **Guardar datos:** guarda todo el sistema en `datos/meteorologia.dat`.

### Listados

Se elige el listado en el combo y se presiona **Generar listado**:

* **Alertas de una estación:** diálogo con una tabla de solo lectura con las alertas de la estación en un período.
* **Regiones**, **Comunas**, **Estaciones de una comuna**, **Sensores de una estación**, **Mediciones de un sensor** y **Resumen estadístico de un sensor:** se muestran por consola.

**Salir** pregunta si se desean guardar los datos antes de cerrar.

Los errores se informan en un diálogo con el mensaje correspondiente.
