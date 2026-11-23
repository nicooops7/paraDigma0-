package controlador;

import modelo.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class InstitutoMeteorologia {
    private static InstitutoMeteorologia instancia;
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private ArrayList<Region> regiones;
    private ArrayList<EstacionMeteorologica> estaciones;

    private InstitutoMeteorologia() {
        regiones = new ArrayList<>();
        estaciones = new ArrayList<>();
    }

    public static synchronized InstitutoMeteorologia getInstance() {
        if (instancia == null) {
            instancia = new InstitutoMeteorologia();
        }
        return instancia;
    }

    public void creaRegion(int codigo, String nombre) throws MeteorologiaException {
        if (regiones.stream().anyMatch(r -> r.getCodigo() == codigo)) {
            throw new MeteorologiaException("Ya existe una region con el codigo: " + codigo + ".");
        }
        if (regiones.stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(nombre))) {
            throw new MeteorologiaException("Ya existe una región con el nombre: " + nombre + ".");
        }
        regiones.add(new Region(codigo, nombre));
    }

    public void creaComuna(int codigo, String nombre, int codigoRegion) throws MeteorologiaException {
        Region region = findRegion(codigoRegion).orElseThrow(() ->
                new MeteorologiaException("No existe una región con el codigo " + codigoRegion + "."));
        region.addComuna(codigo, nombre);
    }

    public void creaEstacion(String cod, String nombre, float lon, float lat, float alt,
                             int codRegion, int codComuna) throws MeteorologiaException {
        if (findEstacion(cod).isPresent()) {
            throw new MeteorologiaException("Ya existe una estación con el código " + cod + ".");
        }
        Region region = findRegion(codRegion).orElseThrow(() ->
                new MeteorologiaException("No existe una región con el codigo " + codRegion + "."));
        Comuna comuna = region.findComunaById(codComuna).orElseThrow(() ->
                new MeteorologiaException("No existe comuna con el codigo " + codComuna
                        + " en la region " + region.getNombre() + "."));

        EstacionMeteorologica estacion = new EstacionMeteorologica(cod, nombre, lon, lat, alt, comuna);
        comuna.addEstacion(estacion);
        estaciones.add(estacion);
    }

    public void instalaSensor(String cod, String marca, String modelo, TipoSensor tipo,
                              String codigoEstacion) throws MeteorologiaException {
        obtenerEstacion(codigoEstacion).instalaSensor(cod, marca, modelo, tipo);
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion,
                                 String codSensor) throws MeteorologiaException {
        obtenerEstacion(codEstacion).registraMedicion(fechaHora, valor, codSensor);
    }

    public void cambiaEstadoSensor(String codigoEstacion, String codigoSensor, Estado estado)
            throws MeteorologiaException {
        obtenerEstacion(codigoEstacion).cambiaEstadoSensor(codigoSensor, estado);
    }

    public boolean existenDatos() {
        return !regiones.isEmpty();
    }

    public String[] getCodigosEstaciones() {
        return estaciones.stream()
                .map(EstacionMeteorologica::getCodigo)
                .sorted()
                .toArray(String[]::new);
    }

    public String[] getCodigosSensores(String codEstacion) throws MeteorologiaException {
        return Arrays.stream(obtenerEstacion(codEstacion).getSensores())
                .map(Sensor::getCodigo)
                .toArray(String[]::new);
    }

    public String[] getDatosSensor(String codEstacion, String codSensor) throws MeteorologiaException {
        Sensor sensor = obtenerEstacion(codEstacion).findSensorById(codSensor).orElseThrow(() ->
                new MeteorologiaException("No existe el sensor " + codSensor
                        + " en la estación " + codEstacion + "."));
        return new String[]{sensor.getTipo().name(), sensor.getUnidad(), sensor.getEstado().name()};
    }

    @SuppressWarnings("unchecked")
    public void cargaDatos() throws MeteorologiaException {
        if (existenDatos()) {
            throw new MeteorologiaException(
                    "La carga de datos solo puede realizarse cuando no existen datos registrados.");
        }
        Object leido = PersistenciaObjetos.leeObjeto("meteorologia.dat");
        if (!(leido instanceof List)) {
            throw new MeteorologiaException("El archivo meteorologia.dat no contiene datos válidos del sistema.");
        }
        try {
            ArrayList<Region> regionesLeidas = new ArrayList<>((List<Region>) leido);
            ArrayList<EstacionMeteorologica> estacionesLeidas = regionesLeidas.stream()
                    .flatMap(r -> Arrays.stream(r.getComunas()))
                    .flatMap(c -> Arrays.stream(c.getEstaciones()))
                    .collect(Collectors.toCollection(ArrayList::new));
            regiones = regionesLeidas;
            estaciones = estacionesLeidas;
        } catch (ClassCastException e) {
            throw new MeteorologiaException("El archivo meteorologia.dat no contiene datos válidos del sistema.");
        }
    }

    public void guardaDatos() throws MeteorologiaException {
        if (!existenDatos()) {
            throw new MeteorologiaException("No existen datos para guardar.");
        }
        PersistenciaObjetos.escribeObjeto("meteorologia.dat", regiones);
    }

    public void importaDatosTexto() throws MeteorologiaException {
        if (existenDatos() || !estaciones.isEmpty()) {
            throw new MeteorologiaException(
                    "La carga de datos solo puede realizarse cuando no existen datos registrados.");
        }

        PersistenciaTexto p = PersistenciaTexto.getInstance();
        try {
            String[][] lineasReg = p.leeRegistros("regiones.txt");
            String[][] lineasCom = p.leeRegistros("comunas.txt");
            String[][] lineasEst = p.leeRegistros("estaciones.txt");
            String[][] lineasSen = p.leeRegistros("sensores.txt");
            String[][] lineasMed = p.leeRegistros("mediciones.txt");

            for (int i = 0; i < lineasReg.length; i++) {
                String[] v = lineasReg[i];
                if (v.length != 2) throw new IllegalArgumentException(
                        "Línea " + (i + 1) + " del archivo regiones.txt: formato inválido.");
                try {
                    creaRegion(Integer.parseInt(v[0].trim()), v[1].trim());
                } catch (RuntimeException | MeteorologiaException e) {
                    throw new MeteorologiaException("Línea " + (i + 1)
                            + " del archivo regiones.txt: " + e.getMessage() + " No se cargó ningún dato.");
                }
            }
            for (int i = 0; i < lineasCom.length; i++) {
                String[] v = lineasCom[i];
                if (v.length != 3) throw new IllegalArgumentException(
                        "Línea " + (i + 1) + " del archivo comunas.txt: formato inválido.");
                try {
                    creaComuna(Integer.parseInt(v[1].trim()), v[2].trim(), Integer.parseInt(v[0].trim()));
                } catch (RuntimeException | MeteorologiaException e) {
                    throw new MeteorologiaException("Línea " + (i + 1)
                            + " del archivo comunas.txt: " + e.getMessage() + " No se cargó ningún dato.");
                }
            }
            for (int i = 0; i < lineasEst.length; i++) {
                String[] v = lineasEst[i];
                if (v.length != 7) throw new IllegalArgumentException(
                        "Línea " + (i + 1) + " del archivo estaciones.txt: formato inválido.");
                try {
                    creaEstacion(v[0].trim(), v[1].trim(), Float.parseFloat(v[2].trim()),
                            Float.parseFloat(v[3].trim()), Float.parseFloat(v[4].trim()),
                            Integer.parseInt(v[5].trim()), Integer.parseInt(v[6].trim()));
                } catch (RuntimeException | MeteorologiaException e) {
                    throw new MeteorologiaException("Línea " + (i + 1)
                            + " del archivo estaciones.txt: " + e.getMessage() + " No se cargó ningún dato.");
                }
            }

            for (int i = 0; i < lineasMed.length; i++) {
                if (lineasMed[i].length != 4) {
                    throw new MeteorologiaException("Línea " + (i + 1)
                            + " del archivo mediciones.txt: formato inválido. No se cargó ningún dato.");
                }
            }
            List<String[]> activarFinal = new ArrayList<>();
            for (int i = 0; i < lineasSen.length; i++) {
                String[] v = lineasSen[i];
                if (v.length != 6) throw new MeteorologiaException("Línea " + (i + 1)
                        + " del archivo sensores.txt: formato inválido. No se cargó ningún dato.");
                try {
                    String codEst = v[0].trim();
                    String codSen = v[1].trim();
                    TipoSensor tipo = TipoSensor.valueOf(v[2].trim());
                    Estado estado = Estado.valueOf(v[5].trim());
                    EstacionMeteorologica estacion = obtenerEstacion(codEst);
                    for (Sensor anterior : estacion.getSensores()) {
                        if (anterior.getTipo() == tipo && anterior.getEstado() == Estado.ACTIVO) {
                            cambiaEstadoSensor(codEst, anterior.getCodigo(), Estado.INACTIVO);
                        }
                    }
                    instalaSensor(codSen, v[3].trim(), v[4].trim(), tipo, codEst);

                    for (int j = 0; j < lineasMed.length; j++) {
                        String[] m = lineasMed[j];
                        if (m[0].trim().equals(codEst) && m[1].trim().equals(codSen)) {
                            try {
                                LocalDateTime fecha = LocalDateTime.parse(m[2].trim(), FORMATO_FECHA_HORA);
                                registraMedicion(fecha, Float.parseFloat(m[3].trim()), codEst, codSen);
                            } catch (RuntimeException | MeteorologiaException e) {
                                throw new MeteorologiaException("Línea " + (j + 1)
                                        + " del archivo mediciones.txt: " + e.getMessage()
                                        + " No se cargó ningún dato.");
                            }
                        }
                    }
                    cambiaEstadoSensor(codEst, codSen, Estado.INACTIVO);
                    if (estado == Estado.ACTIVO) {
                        activarFinal.add(new String[]{codEst, codSen, String.valueOf(i + 1)});
                    }
                } catch (RuntimeException | MeteorologiaException e) {
                    if (e instanceof MeteorologiaException
                            && e.getMessage().contains("del archivo mediciones.txt")) {
                        throw (MeteorologiaException) e;
                    }
                    throw new MeteorologiaException("Línea " + (i + 1)
                            + " del archivo sensores.txt: " + e.getMessage() + " No se cargó ningún dato.");
                }
            }
            for (int i = 0; i < lineasMed.length; i++) {
                String[] m = lineasMed[i];
                if (findSensor(m[0].trim(), m[1].trim()).isEmpty()) {
                    throw new MeteorologiaException("Línea " + (i + 1)
                            + " del archivo mediciones.txt: formato inválido o sensor inexistente. No se cargó ningún dato.");
                }
            }
            for (String[] s : activarFinal) {
                try {
                    cambiaEstadoSensor(s[0], s[1], Estado.ACTIVO);
                } catch (MeteorologiaException e) {
                    throw new MeteorologiaException("Línea " + s[2]
                            + " del archivo sensores.txt: " + e.getMessage() + " No se cargó ningún dato.");
                }
            }
        } catch (MeteorologiaException e) {
            regiones.clear();
            estaciones.clear();
            throw e;
        } catch (IllegalArgumentException e) {
            regiones.clear();
            estaciones.clear();
            throw new MeteorologiaException(e.getMessage() + " No se cargó ningún dato.");
        }
    }

    public String[][] listaRegiones() {
        return regiones.stream()
                .map(r -> new String[]{
                        String.valueOf(r.getCodigo()),
                        r.getNombre(),
                        String.valueOf(r.getComunas().length),
                        String.valueOf(r.getCantidadEstaciones())})
                .toArray(String[][]::new);
    }

    public String[][] listaComunas() {
        return regiones.stream()
                .flatMap(r -> Arrays.stream(r.getComunas())
                        .map(c -> new String[]{
                                String.valueOf(c.getCodigo()),
                                c.getNombre(),
                                r.getNombre(),
                                String.valueOf(c.getEstaciones().length),
                                String.valueOf(c.getCantidadEstacionesActivas())}))
                .toArray(String[][]::new);
    }

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) throws MeteorologiaException {
        Region region = findRegion(codigoRegion).orElseThrow(() ->
                new MeteorologiaException("No existe una región con el código " + codigoRegion + "."));
        Comuna comuna = region.findComunaById(codigoComuna).orElseThrow(() ->
                new MeteorologiaException("No existe una comuna con el código " + codigoComuna
                        + " en la región " + region.getNombre() + "."));
        return Arrays.stream(comuna.getEstaciones())
                .map(e -> new String[]{
                        e.getCodigo(),
                        e.getNombre(),
                        e.getPosicion(),
                        e.getEstado().name(),
                        String.valueOf(Arrays.stream(e.getSensores())
                                .filter(s -> s.getEstado() == Estado.ACTIVO)
                                .count())})
                .toArray(String[][]::new);
    }

    public String[][] listaSensores(String codigoEstacion) throws MeteorologiaException {
        return obtenerEstacion(codigoEstacion).getResumenSensores();
    }

    public String[][] listaMediciones(String codEstacion, String codSensor,
                                      LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        EstacionMeteorologica est = obtenerEstacion(codEstacion);
        if (est.findSensorById(codSensor).isEmpty()) {
            throw new MeteorologiaException("No existe el sensor " + codSensor
                    + " en la estación " + codEstacion + ".");
        }
        validarPeriodo(inicio, fin);
        return est.getMedicionesSensorBetween(codSensor, inicio, fin);
    }

    public String[] listaResumenMediciones(String codigoEstacion, String codigoSensor,
                                           LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        EstacionMeteorologica est = obtenerEstacion(codigoEstacion);
        Sensor sensor = est.findSensorById(codigoSensor).orElseThrow(() ->
                new MeteorologiaException("No existe el sensor " + codigoSensor
                        + " en la estación " + codigoEstacion + "."));
        validarPeriodo(inicio, fin);
        return sensor.getResumenBetween(inicio, fin)
                .map(r -> new String[]{
                        sensor.getTipo().name(),
                        sensor.getUnidad(),
                        String.valueOf(r.getCantidad()),
                        String.format(Locale.US, "%.1f", r.getMinimo().getValor()),
                        r.getMinimo().getFechaHora().format(FORMATO_FECHA_HORA),
                        String.format(Locale.US, "%.1f", r.getMaximo().getValor()),
                        r.getMaximo().getFechaHora().format(FORMATO_FECHA_HORA),
                        String.format(Locale.US, "%.1f", r.getPromedio())})
                .orElse(new String[0]);
    }

    public String[][] listaAlertas(String codigoEstacion, LocalDateTime inicio,
                                   LocalDateTime fin) throws MeteorologiaException {
        EstacionMeteorologica est = obtenerEstacion(codigoEstacion);
        validarPeriodo(inicio, fin);
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");
        return Arrays.stream(est.getAlertasBetween(inicio, fin))
                .map(a -> new String[]{
                        a.getFechaHora().format(fmtFecha),
                        a.getFechaHora().format(fmtHora),
                        a.getSensor().getCodigo(),
                        a.getFenomeno(),
                        String.format(Locale.US, "%.1f", a.getValor()),
                        a.getSensor().getUnidad(),
                        a.getNivel().name()})
                .toArray(String[][]::new);
    }

    private void validarPeriodo(LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        if (fin.isBefore(inicio)) {
            throw new MeteorologiaException("La fecha y hora de término (" + fin.format(FORMATO_FECHA_HORA)
                    + ") es anterior a la de inicio (" + inicio.format(FORMATO_FECHA_HORA) + ").");
        }
    }

    private EstacionMeteorologica obtenerEstacion(String codigo) throws MeteorologiaException {
        return findEstacion(codigo).orElseThrow(() ->
                new MeteorologiaException("No existe una estación con el código " + codigo + "."));
    }

    private Optional<Region> findRegion(int codigo) {
        return regiones.stream().filter(r -> r.getCodigo() == codigo).findFirst();
    }

    private Optional<EstacionMeteorologica> findEstacion(String codigo) {
        return estaciones.stream().filter(e -> e.getCodigo().equals(codigo)).findFirst();
    }

    private Optional<Comuna> findComuna(int codRegion, int codComuna) {
        return findRegion(codRegion).flatMap(r -> r.findComunaById(codComuna));
    }

    private Optional<Sensor> findSensor(String codigoEstacion, String codigoSensor) {
        return findEstacion(codigoEstacion).flatMap(e -> e.findSensorById(codigoSensor));
    }
}