package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class VentanaListadoAlertas extends JDialog {

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);
    private static final String[] COLUMNAS = {"Fecha", "Hora", "Sensor", "Fenómeno", "Valor", "Nivel"};

    private final InstitutoMeteorologia instituto = InstitutoMeteorologia.getInstance();

    private final JComboBox<String> cmbEstaciones = new JComboBox<>(instituto.getCodigosEstaciones());
    private final JTextField txtInicio = new JTextField(11);
    private final JTextField txtFin = new JTextField(11);
    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JLabel lblResumen = new JLabel(" ");

    public VentanaListadoAlertas(JFrame owner) {
        super(owner, "Alertas de una estación", true);

        JButton btnListar = new JButton("Listar");
        JButton btnCerrar = new JButton("Cerrar");
        btnListar.addActionListener(e -> listar());
        btnCerrar.addActionListener(e -> dispose());

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.add(new JLabel("Estación:"));
        filtros.add(cmbEstaciones);
        filtros.add(new JLabel("Inicio:"));
        filtros.add(txtInicio);
        filtros.add(new JLabel("Fin:"));
        filtros.add(txtFin);
        filtros.add(btnListar);

        JLabel lblFormato = new JLabel("Fechas en formato dd/MM/yyyy HH:mm");
        lblFormato.setFont(lblFormato.getFont().deriveFont(Font.PLAIN));
        lblFormato.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        JPanel encabezado = new JPanel(new BorderLayout(0, 4));
        encabezado.add(filtros, BorderLayout.CENTER);
        encabezado.add(lblFormato, BorderLayout.SOUTH);

        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setFillsViewportHeight(true);
        tabla.setPreferredScrollableViewportSize(new Dimension(640, 220));
        tabla.getColumnModel().getColumn(3).setPreferredWidth(170);

        JPanel pie = new JPanel(new BorderLayout());
        pie.add(lblResumen, BorderLayout.CENTER);
        pie.add(btnCerrar, BorderLayout.EAST);

        JPanel contenido = new JPanel(new BorderLayout(0, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(new JScrollPane(tabla), BorderLayout.CENTER);
        contenido.add(pie, BorderLayout.SOUTH);
        setContentPane(contenido);

        getRootPane().setDefaultButton(btnListar);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(owner);
    }

    private void listar() {
        vaciarTabla();
        try {
            String codEstacion = (String) cmbEstaciones.getSelectedItem();
            if (codEstacion == null) {
                throw new IllegalArgumentException("Debe seleccionar una estación.");
            }
            LocalDateTime inicio = leerFechaHora(txtInicio, "inicio");
            LocalDateTime fin = leerFechaHora(txtFin, "término");


            String[][] alertas = instituto.listaAlertas(codEstacion, inicio, fin);

            if (alertas.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "La estación " + codEstacion + " no registra alertas en el período indicado.",
                        getTitle(), JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (String[] a : alertas) {
                modelo.addRow(new Object[]{a[0], a[1], a[2], a[3], a[4] + " " + a[5], a[6]});
            }
            lblResumen.setText("Período: " + inicio.format(FORMATO_FECHA_HORA)
                    + " a " + fin.format(FORMATO_FECHA_HORA)
                    + " — " + alertas.length + (alertas.length == 1 ? " alerta" : " alertas"));
        } catch (IllegalArgumentException | MeteorologiaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void vaciarTabla() {
        modelo.setRowCount(0);
        lblResumen.setText(" ");
    }

    private LocalDateTime leerFechaHora(JTextField campo, String nombre) {
        String texto = campo.getText().strip();
        if (texto.isEmpty()) {
            campo.requestFocusInWindow();
            throw new IllegalArgumentException("Debe ingresar la fecha y hora de " + nombre + ".");
        }
        try {
            return LocalDateTime.parse(texto, FORMATO_FECHA_HORA);
        } catch (DateTimeParseException e) {
            campo.requestFocusInWindow();
            throw new IllegalArgumentException("La fecha y hora de " + nombre + " " + texto
                    + " no es válida. Use el formato dd/MM/yyyy HH:mm.");
        }
    }
}
