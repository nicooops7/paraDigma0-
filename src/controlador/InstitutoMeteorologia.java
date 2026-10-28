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
        if(findEstacion(cod).isPresent()){
            throw new MeteorologiaException("Ya existe una estación con el codigo "+cod+".");
        }

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
            throw new MeteorologiaException("No existe una estacióncon el codigo "+codEstacion+".");
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
            throw new MeteorologiaException("La carga de datos solo puede realizarse cuando no existen datos registrados.");
        }

        PersistenciaTexto persistencia = PersistenciaTexto.getInstance();
        List<String[]> sensoresConEstado = new ArrayList<>();

        try {
            String[] lineasRegiones = persistencia.leeRegistros("regiones.txt");
            for (int i = 0; i < lineasRegiones.length; i++) {
                String[] partes = lineasRegiones[i].split(PersistenciaTexto.SEPARADOR);
                if (partes.length != 2) {
                    throw new MeteorologiaException("Línea " + (i + 1) + " del archivo regiones.txt: formato inválido. No se cargó ningún dato.");
                }
                try {
                    creaRegion(Integer.parseInt(partes[0].trim()), partes[1].trim());
                } catch (Exception e) {
                    throw new MeteorologiaException("Línea " + (i + 1) + " del archivo regiones.txt: " + (e.getMessage() != null ? e.getMessage() : "Error") + " No se cargó ningún dato.");
                }
            }

            String[] lineasComunas = persistencia.leeRegistros("comunas.txt");
            for (int i = 0; i < lineasComunas.length; i++) {
                String[] partes = lineasComunas[i].split(PersistenciaTexto.SEPARADOR);
                if (partes.length != 3) {
                    throw new MeteorologiaException("Línea " + (i + 1) + " del archivo comunas.txt: formato inválido. No se cargó ningún dato.");
                }
                try {
                    creaComuna(Integer.parseInt(partes[1].trim()), partes[2].trim(), Integer.parseInt(partes[0].trim()));
                } catch (Exception e) {
                    throw new MeteorologiaException("Línea " + (i + 1) + " del archivo comunas.txt: " + (e.getMessage() != null ? e.getMessage() : "Error") + " No se cargó ningún dato.");
                }
            }

            String[] lineasEstaciones = persistencia.leeRegistros("estaciones.txt");
            for (int i = 0; i < lineasEstaciones.length; i++) {
                String[] partes = lineasEstaciones[i].split(PersistenciaTexto.SEPARADOR);
                if (partes.length != 7) {
                    throw new MeteorologiaException("Línea " + (i + 1) + " del archivo estaciones.txt: formato inválido. No se cargó ningún dato.");
                }
                try {
                    creaEstacion(partes[0].trim(), partes[1].trim(), Float.parseFloat(partes[2].trim()),
                            Float.parseFloat(partes[3].trim()), Float.parseFloat(partes[4].trim()),
                            Integer.parseInt(partes[5].trim()), Integer.parseInt(partes[6].trim()));
                } catch (Exception e) {
                    throw new MeteorologiaException("Línea " + (i + 1) + " del archivo estaciones.txt: " + (e.getMessage() != null ? e.getMessage() : "Error") + " No se cargó ningún dato.");
                }
            }

            String[] lineasSensores = persistencia.leeRegistros("sensores.txt");
            for (int i = 0; i < lineasSensores.length; i++) {
                String[] partes = lineasSensores[i].split(PersistenciaTexto.SEPARADOR);
                if (partes.length != 6) {
                    throw new MeteorologiaException("Línea " + (i + 1) + " del archivo sensores.txt: formato inválido. No se cargó ningún dato.");
                }
                try {
                    String codEstacion = partes[0].trim();
                    String codSensor = partes[1].trim();
                    TipoSensor tipo = TipoSensor.valueOf(partes[2].trim());
                    String marca = partes[3].trim();
                    String modelo = partes[4].trim();
                    Estado estadoGuardado = Estado.valueOf(partes[5].trim());

                    instalaSensor(codSensor, marca, modelo, tipo, codEstacion);
                    sensoresConEstado.add(new String[]{codEstacion, codSensor, estadoGuardado.name()});
                } catch (Exception e) {
                    throw new MeteorologiaException("Línea " + (i + 1) + " del archivo sensores.txt: " + (e.getMessage() != null ? e.getMessage() : "Error") + " No se cargó ningún dato.");
                }
            }

            String[] lineasMediciones = persistencia.leeRegistros("mediciones.txt");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (int i = 0; i < lineasMediciones.length; i++) {
                String[] partes = lineasMediciones[i].split(PersistenciaTexto.SEPARADOR);
                if (partes.length != 4) {
                    throw new MeteorologiaException("Línea " + (i + 1) + " del archivo mediciones.txt: formato inválido. No se cargó ningún dato.");
                }
                try {
                    String codEstacion = partes[0].trim();
                    String codSensor = partes[1].trim();
                    LocalDateTime fechaHora = LocalDateTime.parse(partes[2].trim(), formatter);
                    float valor = Float.parseFloat(partes[3].trim());

                    registraMedicion(fechaHora, valor, codEstacion, codSensor);
                } catch (Exception e) {
                    throw new MeteorologiaException("Línea " + (i + 1) + " del archivo mediciones.txt: " + (e.getMessage() != null ? e.getMessage() : "Error") + " No se cargó ningún dato.");
                }
            }

            for (String[] sc : sensoresConEstado) {
                if (sc[2].equals("INACTIVO")) {
                    cambiaEstadoSensor(sc[0], sc[1], Estado.INACTIVO);
                }
            }

        } catch (MeteorologiaException e) {
            regiones.clear();
            estaciones.clear();
            throw e;
        }
    }


    public void guardaDatos() throws MeteorologiaException {
        if (regiones.isEmpty() && estaciones.isEmpty()) {
            throw new MeteorologiaException("No existen datos para guardar.");
        }

        List<String> lineasRegiones = new ArrayList<>();
        List<String> lineasComunas = new ArrayList<>();
        List<String> lineasEstaciones = new ArrayList<>();
        List<String> lineasSensores = new ArrayList<>();
        List<String> lineasMediciones = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Region r : regiones) {
            lineasRegiones.add(r.getCodigo() + PersistenciaTexto.SEPARADOR + r.getNombre());
            for (Comuna c : r.getComunas()) {
                lineasComunas.add(r.getCodigo() + PersistenciaTexto.SEPARADOR + c.getCodigo() + PersistenciaTexto.SEPARADOR + c.getNombre());
                for (EstacionMeteorologica est : c.getEstaciones()) {
                    lineasEstaciones.add(est.getCodigo() + PersistenciaTexto.SEPARADOR + est.getNombre() + PersistenciaTexto.SEPARADOR +
                            est.getLongitud() + PersistenciaTexto.SEPARADOR + est.getLatitud() + PersistenciaTexto.SEPARADOR +
                            est.getAltitud() + PersistenciaTexto.SEPARADOR + r.getCodigo() + PersistenciaTexto.SEPARADOR + c.getCodigo());

                    for (Sensor s : est.getSensores()) {
                        lineasSensores.add(est.getCodigo() + PersistenciaTexto.SEPARADOR + s.getCodigo() + PersistenciaTexto.SEPARADOR +
                                s.getTipo() + PersistenciaTexto.SEPARADOR + s.getMarca() + PersistenciaTexto.SEPARADOR +
                                s.getModelo() + PersistenciaTexto.SEPARADOR + s.getEstado());

                        for (Medicion m : s.getMediciones()) {
                            lineasMediciones.add(est.getCodigo() + PersistenciaTexto.SEPARADOR + s.getCodigo() + PersistenciaTexto.SEPARADOR +
                                    m.getFechaHora().format(formatter) + PersistenciaTexto.SEPARADOR + m.getValor());
                        }
                    }
                }
            }
        }

        PersistenciaTexto p = PersistenciaTexto.getInstance();
        p.escribeRegistros("regiones.txt", lineasRegiones.toArray(new String[0]));
        p.escribeRegistros("comunas.txt", lineasComunas.toArray(new String[0]));
        p.escribeRegistros("estaciones.txt", lineasEstaciones.toArray(new String[0]));
        p.escribeRegistros("sensores.txt", lineasSensores.toArray(new String[0]));
        p.escribeRegistros("mediciones.txt", lineasMediciones.toArray(new String[0]));
    }

    public String[][] listaRegiones() {
        List<String[]> lista = new ArrayList<>();
        for (Region r : regiones) {
            lista.add(new String[]{String.valueOf(r.getCodigo()), r.getNombre()});
        }
        return lista.toArray(new String[0][0]);
    }

    public String[][] listaComunas() {
        List<String[]> lista = new ArrayList<>();
        for (Region r : regiones) {
            for (Comuna c : r.getComunas()) {
                lista.add(new String[]{String.valueOf(r.getCodigo()), r.getNombre(), String.valueOf(c.getCodigo()), c.getNombre()});
            }
        }
        return lista.toArray(new String[0][0]);
    }

    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) throws MeteorologiaException {
        Optional<Region> regionOpt = findRegion(codigoRegion);
        if (!regionOpt.isPresent()) {
            throw new MeteorologiaException("No existe una región con el código " + codigoRegion + ".");
        }
        Region region = regionOpt.get();

        Optional<Comuna> comunaOpt = region.findComunaById(codigoComuna);
        if (!comunaOpt.isPresent()) {
            throw new MeteorologiaException("No existe una comuna con el código " + codigoComuna + " en la región " + region.getNombre() + ".");
        }
        Comuna comuna = comunaOpt.get();

        EstacionMeteorologica[] ests = comuna.getEstaciones();
        String[][] matriz = new String[ests.length][];
        for (int i = 0; i < ests.length; i++) {
            matriz[i] = new String[]{ests[i].getCodigo(), ests[i].getNombre(), ests[i].getEstado().name()};
        }
        return matriz;
    }

    public String[][] listaSensores(String codigoEstacion) throws MeteorologiaException {
        Optional<EstacionMeteorologica> estOpt = findEstacion(codigoEstacion);
        if (!estOpt.isPresent()) {
            throw new MeteorologiaException("No existe una estación con el código " + codigoEstacion + ".");
        }
        return estOpt.get().getResumenSensores();
    }

    public String[][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (fin.isBefore(inicio)) {
            throw new MeteorologiaException("La fecha y hora de término (" + fin.format(fmt) + ") es anterior a la de inicio (" + inicio.format(fmt) + ").");
        }
        Optional<EstacionMeteorologica> estOpt = findEstacion(codEstacion);
        if (!estOpt.isPresent()) {
            throw new MeteorologiaException("No existe una estación con el código " + codEstacion + ".");
        }
        EstacionMeteorologica est = estOpt.get();
        Optional<Sensor> sensorOpt = est.findSensorById(codSensor);
        if (!sensorOpt.isPresent()) {
            throw new MeteorologiaException("No existe el sensor " + codSensor + " en la estación " + codEstacion + ".");
        }
        return est.getMedicionesSensorBetween(codSensor, inicio, fin);
    }

    public String[] listaResumenMediciones(String codigoEstacion, String codigoSensor, LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (fin.isBefore(inicio)) {
            throw new MeteorologiaException("La fecha y hora de término (" + fin.format(fmt) + ") es anterior a la de inicio (" + inicio.format(fmt) + ").");
        }
        Optional<EstacionMeteorologica> estOpt = findEstacion(codigoEstacion);
        if (!estOpt.isPresent()) {
            throw new MeteorologiaException("No existe una estación con el código " + codigoEstacion + ".");
        }
        EstacionMeteorologica est = estOpt.get();
        Optional<Sensor> sensorOpt = est.findSensorById(codigoSensor);
        if (!sensorOpt.isPresent()) {
            throw new MeteorologiaException("No existe el sensor " + codigoSensor + " en la estación " + codigoEstacion + ".");
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
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (fin.isBefore(inicio)) {
            throw new MeteorologiaException("La fecha y hora de término (" + fin.format(fmt) + ") es anterior a la de inicio (" + inicio.format(fmt) + ").");
        }
        Optional<EstacionMeteorologica> estOpt = findEstacion(codigoEstacion);
        if (!estOpt.isPresent()) {
            throw new MeteorologiaException("No existe una estación con el código " + codigoEstacion + ".");
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
