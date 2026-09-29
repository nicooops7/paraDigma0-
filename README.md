# Sistema de Información Meteorológica - Avance 1

## Equipo
* **Nombre del Equipo:** [ParaDigma0-]
* **Integrantes:**
  1. [Estaban Pena]
  2. [Joab Vergara]
  3. [Nicolás Parra]

## Instrucciones de Ejecución

El sistema es una aplicación de consola interactiva escrita en Java. No requiere dependencias externas ni bibliotecas adicionales.

### Opción 1: Ejecución desde IntelliJ IDEA (Recomendado)
1. Clona este repositorio desde GitHub en tu equipo local:
   https://github.com/nicooops7/paraDigma0-.git
   ## Descripción del Proyecto

El Sistema de Información Meteorológica es una aplicación de consola en Java desarrollada para una institución que requiere registrar observaciones climáticas y generar listados para diferentes localidades del país. Este primer avance se centra en la estructura territorial y el registro fundamental de los datos, gestionando la creación de regiones, comunas, estaciones meteorológicas, instalación de sensores especializados (temperatura, humedad, presión, viento y precipitación) y el registro inmutable de sus mediciones.

## Menú de Opciones

La aplicación interactúa con el usuario a través del siguiente menú principal:

1. **Crear región:** Registra una nueva región en el sistema mediante su código numérico y nombre.
2. **Crear comuna:** Asocia una nueva comuna a una región existente utilizando el código de la región padre.
3. **Crear estación meteorológica:** Crea una nueva estación activa en una comuna, definiendo su ubicación (latitud, longitud y altitud).
4. **Instalar sensor:** Añade un sensor especializado a una estación operativa, asignando su tipo, marca y modelo.
5. **Registrar medición:** Ingresa un valor climático validado para un sensor específico en una fecha y hora determinadas.
6. **Generar listados:** Despliega un submenú para visualizar tablas ordenadas de las regiones, comunas, estaciones, sensores y el historial de mediciones.
7. **Salir:** Cierra el sistema.
   
