/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vistas;

import dao.EquipoDAO;
import dao.JugadorDAO;
import modelo.Equipo;
import modelo.Jugador;
import util.Sesion;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 *
 * @author 1
 */
public class IFJugadores extends javax.swing.JInternalFrame {

    private String rutaImagenSeleccionada = "";
    private Jugador jugadorSeleccionado = null;

    /**
     * Creates new form IFJugadores
     */
    public IFJugadores() {
        initComponents();
        JTableHeader header = tablaJugadores.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setOpaque(true);
                setBackground(Color.BLACK);
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setHorizontalAlignment(CENTER);
                return this;
            }
        });

        lblImagen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblImagen.setToolTipText("Haz clic para cargar una imagen");
        lblImagen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarImagen();
            }
        });

        jButton1.addActionListener(this::jButton1ActionPerformed);
        jButton2.addActionListener(this::jButton2ActionPerformed);
        jButton3.addActionListener(this::jButton3ActionPerformed);
        tablaJugadores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });
        cargarJugadores();
    }

    private void cargarSeleccionEnFormulario() {
        int fila = tablaJugadores.getSelectedRow();
        if (fila < 0) {
            jugadorSeleccionado = null;
            return;
        }

        txtNombre.setText(valorTexto(fila, 0));
        txtApellido.setText(valorTexto(fila, 1));
        dcFecha.setDate(valorFecha(fila, 2));
        cbPosicion.setSelectedItem(valorTexto(fila, 3));

        Object dorsal = tablaJugadores.getValueAt(fila, 4);
        if (dorsal instanceof Number) {
            spDorsal.setValue(((Number) dorsal).intValue());
        }

        rutaImagenSeleccionada = "";
        lblImagen.setIcon(null);
        lblImagen.setText("Sin imagen");

        try {
            Equipo equipo = new EquipoDAO().buscarPorUsuario(Sesion.getInstancia().getUsuario().getIdUsuario());
            if (equipo != null) {
                for (Jugador j : new JugadorDAO().listarPorEquipo(equipo.getIdEquipo())) {
                    if (j.getNombre().equals(valorTexto(fila, 0)) && 
                        j.getApellido().equals(valorTexto(fila, 1)) &&
                        j.getNumeroCamiseta() == ((Number) dorsal).intValue()) {
                        jugadorSeleccionado = j;
                        if (j.getImagen() != null && !j.getImagen().isEmpty()) {
                            rutaImagenSeleccionada = j.getImagen();
                            mostrarVistaPrevia(j.getImagen());
                        }
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            // Sin imagen disponible
        }
    }

    private String valorTexto(int fila, int columna) {
        Object valor = tablaJugadores.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private Date valorFecha(int fila, int columna) {
        Object valor = tablaJugadores.getValueAt(fila, columna);
        if (valor instanceof LocalDate) {
            return Date.from(((LocalDate) valor).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

    private void seleccionarImagen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar imagen del jugador");
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "png", "jpg", "jpeg", "gif", "bmp"));

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            mostrarVistaPrevia(archivo.getAbsolutePath());
            rutaImagenSeleccionada = archivo.getAbsolutePath();
        }
    }

    private void mostrarVistaPrevia(String ruta) {
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            lblImagen.setIcon(null);
            lblImagen.setText("Sin imagen");
            return;
        }

        ImageIcon icono = new ImageIcon(ruta);
        if (icono.getIconWidth() <= 0) {
            lblImagen.setIcon(null);
            lblImagen.setText("Sin imagen");
            return;
        }

        int ancho = lblImagen.getWidth() > 0 ? lblImagen.getWidth() : 132;
        int alto = lblImagen.getHeight() > 0 ? lblImagen.getHeight() : 126;
        
        // Calcular proporciones para ajustar la imagen sin distorsión
        double ratioImagen = (double) icono.getIconWidth() / icono.getIconHeight();
        double ratioLabel = (double) ancho / alto;
        
        int nuevoAncho, nuevoAlto;
        if (ratioImagen > ratioLabel) {
            // La imagen es más ancha
            nuevoAncho = ancho;
            nuevoAlto = (int) (ancho / ratioImagen);
        } else {
            // La imagen es más alta
            nuevoAlto = alto;
            nuevoAncho = (int) (alto * ratioImagen);
        }
        
        Image imagen = icono.getImage().getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
        lblImagen.setText("");
        lblImagen.setIcon(new ImageIcon(imagen));
        lblImagen.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        lblImagen.setVerticalAlignment(javax.swing.JLabel.CENTER);
    }

    private void cargarJugadores() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"Nombre", "Apellido", "Fecha nac.", "Posición", "Dorsal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            if (!Sesion.getInstancia().estaLogueado() || Sesion.getInstancia().getUsuario() == null) {
                tablaJugadores.setModel(modelo);
                return;
            }

            Equipo equipo = new EquipoDAO().buscarPorUsuario(Sesion.getInstancia().getUsuario().getIdUsuario());
            if (equipo == null) {
                tablaJugadores.setModel(modelo);
                return;
            }

            for (Jugador j : new JugadorDAO().listarPorEquipo(equipo.getIdEquipo())) {
                modelo.addRow(new Object[]{
                    j.getNombre(),
                    j.getApellido(),
                    j.getFechaNac(),
                    j.getPosicion(),
                    j.getNumeroCamiseta()
                });
            }
            tablaJugadores.setModel(modelo);
            tablaJugadores.clearSelection();
            limpiarFormulario();
        } catch (SQLException ex) {
            tablaJugadores.setModel(modelo);
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los jugadores:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellido.setText("");
        spDorsal.setValue(0);
        dcFecha.setDate(null);
        cbPosicion.setSelectedIndex(0);
        rutaImagenSeleccionada = "";
        jugadorSeleccionado = null;
        lblImagen.setIcon(null);
        lblImagen.setText("Sin imagen");
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        if (jugadorSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un jugador para eliminar.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Deseas eliminar a " + jugadorSeleccionado.getNombreCompleto() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            JugadorDAO dao = new JugadorDAO();
            if (dao.eliminar(jugadorSeleccionado.getIdJugador())) {
                JOptionPane.showMessageDialog(this,
                        "Jugador eliminado.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarJugadores();
                tablaJugadores.clearSelection();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el jugador.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar el jugador:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        Date fecha = dcFecha.getDate();
        String posicion = (String) cbPosicion.getSelectedItem();
        int dorsal = (Integer) spDorsal.getValue();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debes completar nombre y apellido.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (fecha == null) {
            JOptionPane.showMessageDialog(this,
                    "Debes seleccionar la fecha de nacimiento.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (posicion == null || posicion.isBlank() || " ".equals(posicion)) {
            JOptionPane.showMessageDialog(this,
                    "Debes seleccionar una posición.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!Sesion.getInstancia().estaLogueado() || Sesion.getInstancia().getUsuario() == null) {
            JOptionPane.showMessageDialog(this,
                    "Debes iniciar sesión para agregar jugadores.",
                    "Sesión requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Equipo equipo = new EquipoDAO().buscarPorUsuario(Sesion.getInstancia().getUsuario().getIdUsuario());
            if (equipo == null) {
                JOptionPane.showMessageDialog(this,
                        "Debes tener un equipo activo para agregar jugadores.",
                        "Equipo requerido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JugadorDAO dao = new JugadorDAO();
            if (dao.existeNumeroCamiseta(dorsal, equipo.getIdEquipo(), 0)) {
                JOptionPane.showMessageDialog(this,
                        "Ese dorsal ya está en uso en tu equipo.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate fechaNac = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Jugador jugador = new Jugador(0, nombre, apellido, fechaNac, posicion, dorsal, 
                    equipo.getIdEquipo(), rutaImagenSeleccionada.isEmpty() ? null : rutaImagenSeleccionada, 1);

            if (dao.insertar(jugador)) {
                JOptionPane.showMessageDialog(this,
                        "Jugador agregado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                rutaImagenSeleccionada = "";
                cargarJugadores();
                tablaJugadores.clearSelection();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo agregar el jugador.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el jugador:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        if (jugadorSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un jugador de la tabla para editar.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        Date fecha = dcFecha.getDate();
        String posicion = (String) cbPosicion.getSelectedItem();
        int dorsal = (Integer) spDorsal.getValue();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debes completar nombre y apellido.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (fecha == null) {
            JOptionPane.showMessageDialog(this,
                    "Debes seleccionar la fecha de nacimiento.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (posicion == null || posicion.isBlank() || " ".equals(posicion)) {
            JOptionPane.showMessageDialog(this,
                    "Debes seleccionar una posición.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            JugadorDAO dao = new JugadorDAO();
            if (dao.existeNumeroCamiseta(dorsal, jugadorSeleccionado.getIdEquipo(), jugadorSeleccionado.getIdJugador())) {
                JOptionPane.showMessageDialog(this,
                        "Ese dorsal ya está en uso en tu equipo.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            jugadorSeleccionado.setNombre(nombre);
            jugadorSeleccionado.setApellido(apellido);
            jugadorSeleccionado.setFechaNac(fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            jugadorSeleccionado.setPosicion(posicion);
            jugadorSeleccionado.setNumeroCamiseta(dorsal);
            jugadorSeleccionado.setImagen(rutaImagenSeleccionada == null || rutaImagenSeleccionada.isBlank()
                    ? jugadorSeleccionado.getImagen()
                    : rutaImagenSeleccionada);
            jugadorSeleccionado.setEstado(1);

            if (dao.actualizar(jugadorSeleccionado)) {
                JOptionPane.showMessageDialog(this,
                        "Jugador actualizado correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarJugadores();
                tablaJugadores.clearSelection();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo actualizar el jugador.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar el jugador:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaJugadores = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblImagen = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        spDorsal = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        dcFecha = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        cbPosicion = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();

        setBorder(null);
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setAutoscrolls(true);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jLabel5.setBackground(new java.awt.Color(0, 0, 0));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 51, 0));
        jLabel5.setText("Jugadores");

        tablaJugadores.setBackground(new java.awt.Color(0, 0, 0));
        tablaJugadores.setForeground(new java.awt.Color(255, 255, 255));
        tablaJugadores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaJugadores);

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));

        lblImagen.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), null));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nombre");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Apellido");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Dorsal");

        spDorsal.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Fecha de nacimiento");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Posición");

        cbPosicion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Base", "Ala-Pivot", "Escolta", "Alero", "Pivot", " " }));

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));

        jButton1.setBackground(new java.awt.Color(255, 102, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Agregar");

        jButton2.setBackground(new java.awt.Color(255, 102, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Editar");

        jButton3.setBackground(new java.awt.Color(255, 102, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Eliminar");

        jButton4.setBackground(new java.awt.Color(255, 102, 0));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Limpiar");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Imagen");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(spDorsal, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(txtNombre)
                    .addComponent(txtApellido)
                    .addComponent(dcFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                    .addComponent(cbPosicion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spDorsal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbPosicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(288, 288, 288))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(74, 74, 74))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 594, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(380, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbPosicion;
    private com.toedter.calendar.JDateChooser dcFecha;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JSpinner spDorsal;
    private javax.swing.JTable tablaJugadores;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
