package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import modelo.Estado;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.IntStream;

public class VentanaCambiarEstadoSensor extends JDialog {
    private final InstitutoMeteorologia instituto = InstitutoMeteorologia.getInstance();

    private final JComboBox<String> cmbEstaciones = new JComboBox<>(instituto.getCodigosEstaciones());
    private final JComboBox<String> cmbSensores = new JComboBox<>();
    private final JLabel lblEstadoActual = new JLabel("-");


    private final JRadioButton[] radios = Arrays.stream(Estado.values())
            .map(estado -> new JRadioButton(estado.name()))
            .toArray(JRadioButton[]::new);
    private final ButtonGroup grupoEstados = new ButtonGroup();

    public VentanaCambiarEstadoSensor(JFrame owner) {
        super(owner, "Cambiar estado de sensor", true);

        JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 8));
        formulario.add(new JLabel("Estación:"));
        formulario.add(cmbEstaciones);
        formulario.add(new JLabel("Sensor:"));
        formulario.add(cmbSensores);
        formulario.add(new JLabel("Estado actual:"));
        formulario.add(lblEstadoActual);

        JPanel panelEstados = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelEstados.setBorder(BorderFactory.createTitledBorder("Nuevo estado"));
        for (JRadioButton radio : radios) {
            grupoEstados.add(radio);
            panelEstados.add(radio);
        }

        JButton btnCambiar = new JButton("Cambiar estado");
        JButton btnCancelar = new JButton("Cancelar");
        cmbEstaciones.addActionListener(e -> llenarSensores());
        cmbSensores.addActionListener(e -> mostrarEstadoActual());
        btnCambiar.addActionListener(e -> cambiarEstado());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.add(btnCambiar);
        botones.add(btnCancelar);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        contenido.add(formulario, BorderLayout.NORTH);
        contenido.add(panelEstados, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);

        llenarSensores();

        getRootPane().setDefaultButton(btnCambiar);
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
        mostrarEstadoActual();
    }

    private void mostrarEstadoActual() {
        lblEstadoActual.setText("-");
        String codEstacion = (String) cmbEstaciones.getSelectedItem();
        String codSensor = (String) cmbSensores.getSelectedItem();
        if (codEstacion == null || codSensor == null) {
            return;
        }
        try {
            lblEstadoActual.setText(instituto.getDatosSensor(codEstacion, codSensor)[2]); // {tipo, unidad, estado}
        } catch (MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }

    private void cambiarEstado() {
        try {
            String codEstacion = (String) cmbEstaciones.getSelectedItem();
            if (codEstacion == null) {
                throw new IllegalArgumentException("Debe seleccionar una estación.");
            }
            String codSensor = (String) cmbSensores.getSelectedItem();
            if (codSensor == null) {
                throw new IllegalArgumentException("La estación " + codEstacion + " no tiene sensores instalados.");
            }
            Estado estado = estadoSeleccionado();

            instituto.cambiaEstadoSensor(codEstacion, codSensor, estado);

            JOptionPane.showMessageDialog(this,
                    "Estado del sensor " + codSensor + " actualizado a " + estado.name() + ".",
                    getTitle(), JOptionPane.INFORMATION_MESSAGE);
            mostrarEstadoActual();
            grupoEstados.clearSelection();
        } catch (IllegalArgumentException | MeteorologiaException e) {
            mostrarError(e.getMessage());
        }
    }


    private Estado estadoSeleccionado() {
        return IntStream.range(0, radios.length)
                .filter(i -> radios[i].isSelected())
                .mapToObj(i -> Estado.values()[i])
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Debe seleccionar el nuevo estado."));
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
