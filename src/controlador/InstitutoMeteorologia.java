package controlador;

import modelo.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;



public class InstitutoMeteorologia {
    private static InstitutoMeteorologia instancia;
    private ArrayList<Region> regiones;
    private ArrayList<EstacionMeteorologica> estaciones;



    private InstitutoMeteorologia(){
        regiones = new ArrayList<>();
        estaciones = new ArrayList<>();
    }

    public static synchronized InstitutoMeteorologia getInstance(){
        if(instancia == null){
            instancia = new InstitutoMeteorologia();
        }
        return instancia;
    }

    public void creaRegion(int codigo , String nombre ) throws MeteorologiaException{
        for (Region region : regiones){
            if (region.getCodigo() == codigo){
                throw new MeteorologiaException("Ya existe una region con el codigo: "+codigo+".");
            }
            if(region.getNombre().equalsIgnoreCase(nombre)){
                throw new MeteorologiaException("Ya existe una región con el nombre: "+nombre+".");
            }
        }

        Region nueva = new Region(codigo, nombre);
        regiones.add(nueva);
    }


    public void creaComuna(int codigo, String nombre , int codigoRegion) throws MeteorologiaException{
        Optional<Region> regionOpt = findRegion(codigoRegion);
        if(!regionOpt.isPresent()){
            throw new MeteorologiaException("No existe una región con el codigo "+codigoRegion+".");
        }
        Region region =regionOpt.get();
        region.addComuna(codigo, nombre);
    }

    public void creaEstacion(String cod, String nombre, float lon, float lat, float alt, int codRegion, int codComuna) throws MeteorologiaException{
        if (findEstacion(cod).isPresent()) {
            throw new MeteorologiaException("Ya existe una estación con el código " + cod + ".");
        }
        Optional<Region> regionOpt = findRegion(codRegion);
        if (!regionOpt.isPresent()) {
            throw new MeteorologiaException("No existe una región con el codigo "+codRegion+".");
        }
        Region region = regionOpt.get();

        Optional<Comuna> comunaOpt = region.findComunaById(codComuna);
        if(!comunaOpt.isPresent()){
            throw new MeteorologiaException("No existe comuna con el codigo "+codComuna+ " en la region "+region.getNombre()+".");
        }
        Comuna comuna = comunaOpt.get();

        EstacionMeteorologica estacion = new EstacionMeteorologica(cod, nombre, lon, lat, alt, comuna);
        comuna.addEstacion(estacion);
        estaciones.add(estacion);
    }

    public void instalaSensor(String cod, String marca, String modelo, TipoSensor tipo, String codigoEstacion) throws MeteorologiaException{
        Optional<EstacionMeteorologica> estOpt = findEstacion(codigoEstacion);
        if(!estOpt.isPresent()){
            throw new MeteorologiaException("No existe una estación con el codigo "+codigoEstacion+".");
        }
        EstacionMeteorologica estacion = estOpt.get();
        estacion.instalaSensor(cod,marca,modelo,tipo);
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor) throws MeteorologiaException{
        Optional<EstacionMeteorologica> estOpt =findEstacion(codEstacion);
        if(!estOpt.isPresent()){
            throw new MeteorologiaException("No existe una estación con el código "+codEstacion+".");
        }
        EstacionMeteorologica estacion = estOpt.get();
        estacion.registraMedicion(fechaHora, valor, codSensor);
    }

    public void cambiaEstadoSensor(String codigoEstacion, String codigoSensor, Estado estado) throws MeteorologiaException {
        EstacionMeteorologica estacion = findEstacion(codigoEstacion).orElseThrow(() ->
                new MeteorologiaException("No existe una estación con el código " + codigoEstacion + "."));
        estacion.cambiaEstadoSensor(codigoSensor, estado);
    }

    public void cargaDatos() throws MeteorologiaException {
        if (!regiones.isEmpty() || !estaciones.isEmpty()) {
            throw new MeteorologiaException(
                    "La carga de datos solo puede realizarse cuando no existen datos registrados.");
        }

        PersistenciaTexto p = PersistenciaTexto.getInstance();
        try {
            // Leer los cinco archivos antes de modificar el sistema.
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

            // Comprobar el formato de mediciones antes de procesar sensores.
            for (int i = 0; i < lineasMed.length; i++) {
                if (lineasMed[i].length != 4) {
                    throw new MeteorologiaException("Línea " + (i + 1)
                            + " del archivo mediciones.txt: formato inválido. No se cargó ningún dato.");
                }
            }
            // Procesar los sensores por separado para evitar conflictos de tipo activo.
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
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
                    // Durante la lectura, desactivar un sensor anterior del mismo tipo.
                    // Al final solo se activan los sensores que indique el archivo.
                    EstacionMeteorologica estacion = findEstacion(codEst)
                            .orElseThrow(() -> new MeteorologiaException(
                                    "No existe una estación con el código " + codEst + "."));
                    for (Sensor anterior : estacion.getSensores()) {
                        if (anterior.getTipo() == tipo && anterior.getEstado() == Estado.ACTIVO) {
                            cambiaEstadoSensor(codEst, anterior.getCodigo(), Estado.INACTIVO);
                        }
                    }
                    instalaSensor(codSen, v[3].trim(), v[4].trim(), tipo, codEst);

                    // Solo registrar las mediciones de este sensor, conservando el número de línea real.
                    for (int j = 0; j < lineasMed.length; j++) {
                        String[] m = lineasMed[j];
                        if (m.length == 4 && m[0].trim().equals(codEst)
                                && m[1].trim().equals(codSen)) {
                            try {
                                LocalDateTime fecha = LocalDateTime.parse(m[2].trim(), fmt);
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
                            && e.getMessage().contains("del archivo mediciones.txt")) throw (MeteorologiaException) e;
                    throw new MeteorologiaException("Línea " + (i + 1)
                            + " del archivo sensores.txt: " + e.getMessage() + " No se cargó ningún dato.");
                }
            }
            // Validar las mediciones no asociadas a un sensor existente y las mal formadas.
            for (int i = 0; i < lineasMed.length; i++) {
                String[] m = lineasMed[i];
                if (m.length != 4 || findSensor(m[0].trim(), m[1].trim()).isEmpty()) {
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

    public void guardaDatos() throws MeteorologiaException {

        if (regiones.isEmpty() && estaciones.isEmpty()) {
            throw new MeteorologiaException(
                    "No existen datos para guardar."
            );
        }

        List<String> lineasRegiones = new ArrayList<>();
        List<String> lineasComunas = new ArrayList<>();
        List<String> lineasEstaciones = new ArrayList<>();
        List<String> lineasSensores = new ArrayList<>();
        List<String> lineasMediciones = new ArrayList<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Region r : regiones) {

            lineasRegiones.add(
                    r.getCodigo() + PersistenciaTexto.SEPARADOR +
                            r.getNombre()
            );

            for (Comuna c : r.getComunas()) {

                lineasComunas.add(
                        r.getCodigo() + PersistenciaTexto.SEPARADOR +
                                c.getCodigo() + PersistenciaTexto.SEPARADOR +
                                c.getNombre()
                );

                for (EstacionMeteorologica est : c.getEstaciones()) {

                    // Obtener coordenadas con getPosicion()
                    String posicion = est.getPosicion();

                    String[] coordenadas = posicion
                            .replace("(", "")
                            .replace(" m)", "")
                            .split(";");

                    String longitud = coordenadas[0].trim();
                    String latitud = coordenadas[1].trim();
                    String altitud = coordenadas[2].trim();

                    lineasEstaciones.add(
                            est.getCodigo() + PersistenciaTexto.SEPARADOR +
                                    est.getNombre() + PersistenciaTexto.SEPARADOR +
                                    longitud + PersistenciaTexto.SEPARADOR +
                                    latitud + PersistenciaTexto.SEPARADOR +
                                    altitud + PersistenciaTexto.SEPARADOR +
                                    r.getCodigo() + PersistenciaTexto.SEPARADOR +
                                    c.getCodigo()
                    );

                    for (Sensor s : est.getSensores()) {

                        lineasSensores.add(
                                est.getCodigo() + PersistenciaTexto.SEPARADOR +
                                        s.getCodigo() + PersistenciaTexto.SEPARADOR +
                                        s.getTipo() + PersistenciaTexto.SEPARADOR +
                                        s.getMarca() + PersistenciaTexto.SEPARADOR +
                                        s.getModelo() + PersistenciaTexto.SEPARADOR +
                                        s.getEstado()
                        );

                        for (Medicion m : s.getMediciones()) {

                            lineasMediciones.add(
                                    est.getCodigo() + PersistenciaTexto.SEPARADOR +
                                            s.getCodigo() + PersistenciaTexto.SEPARADOR +
                                            m.getFechaHora().format(formatter) +
                                            PersistenciaTexto.SEPARADOR +
                                            m.getValor()
                            );
                        }
                    }
                }
            }
        }

        PersistenciaTexto p = PersistenciaTexto.getInstance();

        p.escribeRegistros(
                "regiones.txt",
                lineasRegiones.toArray(new String[0])
        );

        p.escribeRegistros(
                "comunas.txt",
                lineasComunas.toArray(new String[0])
        );

        p.escribeRegistros(
                "estaciones.txt",
                lineasEstaciones.toArray(new String[0])
        );

        p.escribeRegistros(
                "sensores.txt",
                lineasSensores.toArray(new String[0])
        );

        p.escribeRegistros(
                "mediciones.txt",
                lineasMediciones.toArray(new String[0])
        );
    }

    public String[][] listaRegiones() {
        String[][] resultado = new String[regiones.size()][4];
        for (int i = 0; i < regiones.size(); i++) {
            Region r = regiones.get(i);
            resultado[i][0] = String.valueOf(r.getCodigo());
            resultado[i][1] = r.getNombre();
            resultado[i][2] = String.valueOf(r.getComunas().length);
            resultado[i][3] = String.valueOf(r.getCantidadEstaciones());
        }
        return resultado;
    }

    public String[][] listaComunas() {
        List<String[]> lista = new ArrayList<>();
        for (Region r : regiones) {
            for (Comuna c : r.getComunas()) {
                lista.add(new String[]{
                        String.valueOf(c.getCodigo()), c.getNombre(),
                        r.getNombre(), String.valueOf(c.getEstaciones().length),
                        String.valueOf(c.getCantidadEstacionesActivas())
                });
            }
        }
        return lista.toArray(new String[0][0]);
    }

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna)
            throws MeteorologiaException {
        Region region = findRegion(codigoRegion).orElseThrow(() ->
                new MeteorologiaException("No existe una región con el código " + codigoRegion + "."));
        Comuna comuna = region.findComunaById(codigoComuna).orElseThrow(() ->
                new MeteorologiaException("No existe una comuna con el código " + codigoComuna
                        + " en la región " + region.getNombre() + "."));
        EstacionMeteorologica[] estacionesComuna = comuna.getEstaciones();
        String[][] resultado = new String[estacionesComuna.length][5];
        for (int i = 0; i < estacionesComuna.length; i++) {
            EstacionMeteorologica e = estacionesComuna[i];
            resultado[i][0] = e.getCodigo();
            resultado[i][1] = e.getNombre();
            resultado[i][2] = e.getPosicion();
            resultado[i][3] = e.getEstado().name();
            int activos = 0;
            for (Sensor sensor : e.getSensores()) {
                if (sensor.getEstado() == Estado.ACTIVO) activos++;
            }
            resultado[i][4] = String.valueOf(activos);
        }
        return resultado;
    }

    public String[][] listaSensores(String codigoEstacion) throws MeteorologiaException {
        Optional<EstacionMeteorologica> estOpt = findEstacion(codigoEstacion);
        if (!estOpt.isPresent()) {
            throw new MeteorologiaException("No existe una estación con el código " + codigoEstacion + ".");
        }
        return estOpt.get().getResumenSensores();
    }

    public String[][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        Optional<EstacionMeteorologica> estOpt = findEstacion(codEstacion);
        if (!estOpt.isPresent()) {
            throw new MeteorologiaException("No existe una estación con el código " + codEstacion + ".");
        }
        EstacionMeteorologica est = estOpt.get();
        Optional<Sensor> sensorOpt = est.findSensorById(codSensor);
        if (!sensorOpt.isPresent()) {
            throw new MeteorologiaException("No existe el sensor " + codSensor + " en la estación " + codEstacion + ".");
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (fin.isBefore(inicio)) {
            throw new MeteorologiaException("La fecha y hora de término (" + fin.format(fmt) + ") es anterior a la de inicio (" + inicio.format(fmt) + ").");
        }
        return est.getMedicionesSensorBetween(codSensor, inicio, fin);
    }

    public String[] listaResumenMediciones(String codigoEstacion, String codigoSensor, LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        Optional<EstacionMeteorologica> estOpt = findEstacion(codigoEstacion);
        if (!estOpt.isPresent()) {
            throw new MeteorologiaException("No existe una estación con el código " + codigoEstacion + ".");
        }
        EstacionMeteorologica est = estOpt.get();
        Optional<Sensor> sensorOpt = est.findSensorById(codigoSensor);
        if (!sensorOpt.isPresent()) {
            throw new MeteorologiaException("No existe el sensor " + codigoSensor + " en la estación " + codigoEstacion + ".");
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (fin.isBefore(inicio)) {
            throw new MeteorologiaException("La fecha y hora de término (" + fin.format(fmt) + ") es anterior a la de inicio (" + inicio.format(fmt) + ").");
        }
        Sensor sensor = sensorOpt.get();
        Optional<ResumenMediciones> resumenOpt = sensor.getResumenBetween(inicio, fin);
        if (!resumenOpt.isPresent()) {
            return new String[0];
        }
        ResumenMediciones r = resumenOpt.get();
        DateTimeFormatter fmtMinMax = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return new String[]{
                sensor.getTipo().name(),
                sensor.getUnidad(),
                String.valueOf(r.getCantidad()),
                String.format(Locale.US, "%.1f", r.getMinimo().getValor()),
                r.getMinimo().getFechaHora().format(fmtMinMax),
                String.format(Locale.US, "%.1f", r.getMaximo().getValor()),
                r.getMaximo().getFechaHora().format(fmtMinMax),
                String.format(Locale.US, "%.1f", r.getPromedio())
        };
    }

    public String[][] listaAlertas(String codigoEstacion, LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        Optional<EstacionMeteorologica> estOpt = findEstacion(codigoEstacion);
        if (!estOpt.isPresent()) {
            throw new MeteorologiaException("No existe una estación con el código " + codigoEstacion + ".");
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (fin.isBefore(inicio)) {
            throw new MeteorologiaException("La fecha y hora de término (" + fin.format(fmt) + ") es anterior a la de inicio (" + inicio.format(fmt) + ").");
        }
        EstacionMeteorologica est = estOpt.get();
        AlertaMeteorologica[] alertas = est.getAlertasBetween(inicio, fin);
        String[][] matriz = new String[alertas.length][7];
        DateTimeFormatter fmtF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtH = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < alertas.length; i++) {
            AlertaMeteorologica a = alertas[i];
            matriz[i][0] = a.getFechaHora().format(fmtF);
            matriz[i][1] = a.getFechaHora().format(fmtH);
            matriz[i][2] = a.getSensor().getCodigo();
            matriz[i][3] = a.getFenomeno();
            matriz[i][4] = String.format(Locale.US, "%.1f", a.getValor());
            matriz[i][5] = a.getSensor().getUnidad();
            matriz[i][6] = a.getNivel().name();
        }
        return matriz;
    }

    private Optional<Region> findRegion(int codigo){
        for(Region r : regiones){
            if(r.getCodigo() == codigo) return Optional.of(r);
        }
        return Optional.empty();
    }

    private Optional<EstacionMeteorologica> findEstacion(String codigo){
        for(EstacionMeteorologica e: estaciones){
            if(e.getCodigo().equals(codigo)) return Optional.of(e);
        }
        return Optional.empty();
    }

    private Optional<Comuna> findComuna(int codRegion, int codComuna){
        Optional<Region> r = findRegion(codRegion);
        if(r.isPresent()){
            return r.get().findComunaById(codComuna);
        }
        return Optional.empty();
    }

    private Optional<Sensor> findSensor(String codigoEstacion, String codigoSensor){
        Optional<EstacionMeteorologica> est = findEstacion(codigoEstacion);
        if(est.isPresent()){
            return est.get().findSensorById(codigoSensor);
        }
        return Optional.empty();
    }
}
