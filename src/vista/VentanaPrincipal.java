package vista;

import controlador.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Function;

public class VentanaPrincipal extends JFrame {
    private static final String[] LISTADOS = {"Alertas de una estación", "Regiones", "Comunas", "Estaciones de una comuna", "Sensores de una estación", "Mediciones de un sensor", "Resumen estadístico de un sensor"};



    private InstitutoMeteorologia instituto;
    private InterfazUsuario interfaz;
    private JLabel lblEstado;
    private JComboBox<String> cmbListados;

    public VentanaPrincipal(){
        super("Sistema de información Metereológica");
        instituto = InstitutoMeteorologia.getInstance();
        interfaz = InterfazUsuario.getInstance();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                salir();
            }
        });

        construirComponentes();
        actualizarEstado();
        pack();
        setLocationRelativeTo(null);
    }

    private void construirComponentes() {
        JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel titulo = new JLabel("SISTEMA DE INFORMACIÓN METEOROLÓGICA", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        contenido.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 10, 0));
        centro.add(crearPanelRegistro());
        centro.add(crearPanelDerecho());
        contenido.add(centro, BorderLayout.CENTER);

        contenido.add(crearPanelInferior(), BorderLayout.SOUTH);
        setContentPane(contenido);
    }

    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Registro"));
        panel.add(crearBoton("Crear región", e -> enConsola(interfaz::crearRegion)));
        panel.add(crearBoton("Crear comuna", e -> enConsola(interfaz::crearComuna)));
        panel.add(crearBoton("Crear estación meteorológica", e -> enConsola(interfaz::crearEstacion)));
        panel.add(crearBoton("Instalar sensor", e -> abrirDialogo(VentanaInstalarSensor::new)));
        panel.add(crearBoton("Registrar medición", e -> abrirDialogo(VentanaRegistrarMedicion::new)));
        panel.add(crearBoton("Cambiar estado de sensor", e -> abrirDialogo(VentanaCambiarEstadoSensor::new)));
        return panel;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.add(crearPanelDatos(), BorderLayout.NORTH);
        panel.add(crearPanelListados(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelDatos() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Datos"));
        panel.add(crearBoton("Cargar datos", e -> cargarDatos()));
        panel.add(crearBoton("Importar datos de texto", e -> importarDatosTexto()));
        panel.add(crearBoton("Guardar datos", e -> guardarDatos()));
        return panel;
    }

    private JPanel crearPanelListados() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Listados"));
        cmbListados = new JComboBox<>(LISTADOS);
        panel.add(new JLabel("Seleccione el listado:"));
        panel.add(cmbListados);
        panel.add(crearBoton("Generar listado", e -> generarListado()));
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        lblEstado = new JLabel();
        panel.add(lblEstado, BorderLayout.WEST);
        panel.add(crearBoton("Salir", e -> salir()), BorderLayout.EAST);
        return panel;
    }

    private JButton crearBoton(String texto, ActionListener accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(accion);
        return boton;
    }

    // ------------------------------------------------------------------ comportamiento

    private void actualizarEstado() {
        lblEstado.setText(instituto.existenDatos()
                ? "Estado: el sistema tiene datos registrados."
                : "Estado: el sistema no tiene datos registrados.");
    }

    /** Oculta la ventana mientras la operación ASCII usa la consola y la vuelve a mostrar al terminar. */
    private void enConsola(Runnable operacion) {
        setVisible(false);
        try {
            operacion.run();
        } finally {
            setVisible(true);
            actualizarEstado();
        }
    }

    /** Abre una ventana modal, siempre que exista al menos una estación registrada. */
    private void abrirDialogo(Function<JFrame, JDialog> ventana) {
        if (instituto.getCodigosEstaciones().length == 0) {
            JOptionPane.showMessageDialog(this, "No existen estaciones registradas.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ventana.apply(this).setVisible(true);
        actualizarEstado();
    }

    private void generarListado() {
        switch (cmbListados.getSelectedIndex()) {
            case 0 -> abrirDialogo(VentanaListadoAlertas::new);
            case 1 -> enConsola(interfaz::listarRegiones);
            case 2 -> enConsola(interfaz::listarComunas);
            case 3 -> enConsola(interfaz::listarEstaciones);
            case 4 -> enConsola(interfaz::listarSensores);
            case 5 -> enConsola(interfaz::listarMediciones);
            case 6 -> enConsola(interfaz::listarResumenMediciones);
            default -> { }
        }
    }

    private void cargarDatos() {
        try {
            instituto.cargaDatos();
            mostrarExito("Cargar datos", "Datos cargados correctamente desde el archivo datos/meteorologia.dat.");
        } catch (MeteorologiaException e) {
            mostrarError(e);
        }
        actualizarEstado();
    }

    private void importarDatosTexto() {
        try {
            instituto.importaDatosTexto();
            mostrarExito("Importar datos de texto", "Datos importados correctamente desde los archivos de texto de la carpeta datos/.");
        } catch (MeteorologiaException e) {
            mostrarError(e);
        }
        actualizarEstado();
    }

    private void guardarDatos() {
        try {
            instituto.guardaDatos();
            mostrarExito("Guardar datos", "Datos guardados correctamente en el archivo datos/meteorologia.dat.");
        } catch (MeteorologiaException e) {
            mostrarError(e);
        }
    }

    /** Sí guarda y finaliza (si falla, informa el error y no finaliza); No finaliza sin guardar; Cancelar vuelve. */
    private void salir() {
        if (instituto.existenDatos()) {
            int respuesta = JOptionPane.showConfirmDialog(this, "¿Desea guardar los datos antes de salir?",
                    "Salir", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (respuesta == JOptionPane.CANCEL_OPTION || respuesta == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    instituto.guardaDatos();
                } catch (MeteorologiaException e) {
                    mostrarError(e);
                    return;
                }
            }
        }
        dispose();
        System.exit(0);
    }

    private void mostrarExito(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(MeteorologiaException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}