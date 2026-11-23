package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class VentanaRegistrarMedicion extends JDialog {

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private final InstitutoMeteorologia instituto = InstitutoMeteorologia.getInstance();

    private final JComboBox<String> cmbEstaciones = new JComboBox<>(instituto.getCodigosEstaciones());
    private final JComboBox<String> cmbSensores = new JComboBox<>();
    private final JLabel lblTipo = new JLabel("-");
    private final JLabel lblUnidad = new JLabel("-");
    private final JTextField txtFechaHora = new JTextField(15);
    private final JTextField txtValor = new JTextField(15);

    public VentanaRegistrarMedicion(JFrame owner) {
        super(owner, "Registrar medición", true);

        JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 8));
        formulario.add(new JLabel("Estación:"));
        formulario.add(cmbEstaciones);
        formulario.add(new JLabel("Sensor:"));
        formulario.add(cmbSensores);
        formulario.add(new JLabel("Tipo:"));
        formulario.add(lblTipo);
        formulario.add(new JLabel("Unidad:"));
        formulario.add(lblUnidad);
        formulario.add(new JLabel("Fecha y hora (dd/MM/yyyy HH:mm):"));
        formulario.add(txtFechaHora);
        formulario.add(new JLabel("Valor:"));
        formulario.add(txtValor);

        JButton btnRegistrar = new JButton("Registrar");
        JButton btnCancelar = new JButton("Cancelar");
        cmbEstaciones.addActionListener(e -> llenarSensores());
        cmbSensores.addActionListener(e -> mostrarDatosSensor());
        btnRegistrar.addActionListener(e -> registrar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.add(btnRegistrar);
        botones.add(btnCancelar);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);

        llenarSensores();

        getRootPane().setDefaultButton(btnRegistrar);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }


    private void llenarSensores() {
        String codEstacion = (String) cmbEstaciones.getSelectedItem();
        String[] codigos = new String[0];
        if (codEstacion != null) {
            try {
                codigos = instituto.getCodigosSensores(codEstacion);
            } catch (MeteorologiaException e) {
                mostrarError(e.getMessage());
            }
        }
        cmbSensores.setModel(new DefaultComboBoxModel<>(codigos));
        mostrarDatosSensor();
    }

    private void mostrarDatosSensor() {
        lblTipo.setText("-");
        lblUnidad.setText("-");
        String codEstacion = (String) cmbEstaciones.getSelectedItem();
        String codSensor = (String) cmbSensores.getSelectedItem();
        if (codEstacion == null || codSensor == null) {
            return;
        }
        try {
            String[] datos = instituto.getDatosSensor(codEstacion, codSensor);
            lblTipo.setText(datos[0]);
            lblUnidad.setText(datos[1]);
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void registrar() {
        try {
            String codEstacion = (String) cmbEstaciones.getSelectedItem();
            if (codEstacion == null) {
                throw new IllegalArgumentException("Debe seleccionar una estación.");
            }
            String codSensor = (String) cmbSensores.getSelectedItem();
            if (codSensor == null) {
                throw new IllegalArgumentException("La estación " + codEstacion + " no tiene sensores instalados.");
            }
            LocalDateTime fechaHora = leerFechaHora();
            float valor = leerValor();

            instituto.registraMedicion(fechaHora, valor, codEstacion, codSensor);

            JOptionPane.showMessageDialog(this, "Medición registrada correctamente.",
                    getTitle(), JOptionPane.INFORMATION_MESSAGE);
            txtValor.setText("");
            txtValor.requestFocusInWindow();
        } catch (IllegalArgumentException | MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private LocalDateTime leerFechaHora() {
        String texto = txtFechaHora.getText().strip();
        if (texto.isEmpty()) {
            txtFechaHora.requestFocusInWindow();
            throw new IllegalArgumentException("Debe ingresar la fecha y hora de la medición.");
        }
        try {
            return LocalDateTime.parse(texto, FORMATO_FECHA_HORA);
        } catch (DateTimeParseException e) {
            txtFechaHora.requestFocusInWindow();
            throw new IllegalArgumentException(
                    "La fecha y hora \"" + texto + "\" no es válida. Use el formato dd/MM/yyyy HH:mm.");
        }
    }


    private float leerValor() {
        String texto = txtValor.getText().strip();
        if (texto.isEmpty()) {
            txtValor.requestFocusInWindow();
            throw new IllegalArgumentException("Debe ingresar el valor de la medición.");
        }
        try {
            float valor = Float.parseFloat(texto.replace(',', '.'));
            if (Float.isFinite(valor)) {
                return valor;
            }
        } catch (NumberFormatException e) {

        }
        txtValor.requestFocusInWindow();
        throw new IllegalArgumentException("El valor \"" + texto + "\" no es un número válido.");
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
