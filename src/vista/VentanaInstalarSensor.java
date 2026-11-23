package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import modelo.TipoSensor;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.IntStream;

public class VentanaInstalarSensor extends JDialog {
    private final InstitutoMeteorologia instituto = InstitutoMeteorologia.getInstance();

    private final JComboBox<String> cmbEstaciones = new JComboBox<>(instituto.getCodigosEstaciones());
    private final JTextField txtCodigo = new JTextField(15);
    private final JTextField txtMarca = new JTextField(15);
    private final JTextField txtModelo = new JTextField(15);


    private final JRadioButton[] radios = Arrays.stream(TipoSensor.values())
            .map(tipo -> new JRadioButton(tipo.name()))
            .toArray(JRadioButton[]::new);
    private final ButtonGroup grupoTipos = new ButtonGroup();

    public VentanaInstalarSensor(JFrame owner) {
        super(owner, "Instalar sensor", true);

        JPanel formulario = new JPanel(new GridLayout(0, 2, 10, 8));
        formulario.add(new JLabel("Estación:"));
        formulario.add(cmbEstaciones);
        formulario.add(new JLabel("Código del sensor:"));
        formulario.add(txtCodigo);
        formulario.add(new JLabel("Marca:"));
        formulario.add(txtMarca);
        formulario.add(new JLabel("Modelo:"));
        formulario.add(txtModelo);

        JPanel panelTipos = new JPanel(new GridLayout(0, 2));
        panelTipos.setBorder(BorderFactory.createTitledBorder("Tipo de sensor"));
        for (JRadioButton radio : radios) {
            grupoTipos.add(radio);
            panelTipos.add(radio);
        }

        JButton btnInstalar = new JButton("Instalar");
        JButton btnCancelar = new JButton("Cancelar");
        btnInstalar.addActionListener(e -> instalar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.add(btnInstalar);
        botones.add(btnCancelar);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        contenido.add(formulario, BorderLayout.NORTH);
        contenido.add(panelTipos, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);

        getRootPane().setDefaultButton(btnInstalar);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void instalar() {
        try {
            String codEstacion = estacionSeleccionada();
            String codigo = textoObligatorio(txtCodigo, "el código del sensor");
            String marca = textoObligatorio(txtMarca, "la marca");
            String modelo = textoObligatorio(txtModelo, "el modelo");
            TipoSensor tipo = tipoSeleccionado();

            instituto.instalaSensor(codigo, marca, modelo, tipo, codEstacion);

            JOptionPane.showMessageDialog(this,
                    "Sensor " + codigo + " instalado en la estación " + codEstacion + ".",
                    getTitle(), JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
        } catch (IllegalArgumentException | MeteorologiaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtCodigo.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        grupoTipos.clearSelection();
        txtCodigo.requestFocusInWindow();
    }

    private String estacionSeleccionada() {
        String codEstacion = (String) cmbEstaciones.getSelectedItem();
        if (codEstacion == null) {
            throw new IllegalArgumentException("Debe seleccionar una estación.");
        }
        return codEstacion;
    }


    private String textoObligatorio(JTextField campo, String descripcion) {
        String texto = campo.getText().strip();
        if (texto.isEmpty()) {
            campo.requestFocusInWindow();
            throw new IllegalArgumentException("Debe ingresar " + descripcion + ".");
        }
        return texto;
    }


    private TipoSensor tipoSeleccionado() {
        return IntStream.range(0, radios.length)
                .filter(i -> radios[i].isSelected())
                .mapToObj(i -> TipoSensor.values()[i])
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Debe seleccionar el tipo de sensor."));
    }
}
