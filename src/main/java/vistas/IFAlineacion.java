/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vistas;

import dao.AlineacionDAO;
import dao.EquipoDAO;
import dao.JugadorDAO;
import dao.PartidoDAO;
import dao.SuplenteDAO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import modelo.Alineacion;
import modelo.Equipo;
import modelo.Jugador;
import modelo.Partido;
import modelo.Suplente;
import util.Sesion;

/**
 *
 * @author 1
 */
public class IFAlineacion extends javax.swing.JInternalFrame {

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final PartidoDAO partidoDAO = new PartidoDAO();
    private final JugadorDAO jugadorDAO = new JugadorDAO();
    private final AlineacionDAO alineacionDAO = new AlineacionDAO();
    private final SuplenteDAO suplenteDAO = new SuplenteDAO();

    private final Map<Integer, Jugador> jugadoresPorId = new HashMap<>();

    private DefaultTableModel modeloDisponibles;
    private DefaultTableModel modeloSuplentes;

    private Equipo equipoActual;
    private Alineacion alineacionActual;

    private static final int DIAMETRO_TITULAR = 56;
    private final JLabel[] titularesCancha = new JLabel[5];

    /**
     * Creates new form IFAlineacion
     */
    public IFAlineacion() {
        try {
            initComponents();
            inicializarVista();
            conectarEventos();
            cargarDatosIniciales();
        } catch (Exception ex) {
            // Log error pero permite que el formulario se muestre
            System.err.println("Error inicializando IFAlineacion:");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error inicializando el formulario:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void inicializarVista() {
        jButton3.setText("Cargar");

        ((JComboBox) jComboBox7).setModel(new DefaultComboBoxModel<>(new String[]{"Ofensivo", "Defensivo"}));

        modeloDisponibles = new DefaultTableModel(new Object[]{"ID", "Jugador", "Posición", "Dorsal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(modeloDisponibles);

        modeloSuplentes = new DefaultTableModel(new Object[]{"ID", "Jugador", "Posición", "Dorsal", "Orden"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable2.setModel(modeloSuplentes);

        configurarHeaderTabla(jTable1);
        configurarHeaderTabla(jTable2);

        jTable1.setRowHeight(24);
        jTable2.setRowHeight(24);

        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);

        jTable2.getColumnModel().getColumn(0).setMinWidth(0);
        jTable2.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable2.getColumnModel().getColumn(0).setWidth(0);

        inicializarOverlayCancha();
        actualizarTitularesEnCancha();
    }

    private void inicializarOverlayCancha() {
        jLabel2.setLayout(null);
        jLabel2.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                actualizarPosicionesTitulares();
            }
        });

        for (int i = 0; i < titularesCancha.length; i++) {
            JLabel lbl = new JLabel();
            lbl.setOpaque(false);
            lbl.setSize(DIAMETRO_TITULAR, DIAMETRO_TITULAR);
            titularesCancha[i] = lbl;
            jLabel2.add(lbl);
        }
        actualizarPosicionesTitulares();
    }

    private void actualizarPosicionesTitulares() {
        int ancho = jLabel2.getWidth() > 0 ? jLabel2.getWidth() : jLabel2.getPreferredSize().width;
        int alto = jLabel2.getHeight() > 0 ? jLabel2.getHeight() : jLabel2.getPreferredSize().height;
        if (ancho <= 0) {
            ancho = 320;
        }
        if (alto <= 0) {
            alto = 211;
        }

        // Base, Escolta, Alero, Ala-Pivot, Pivot
        // Posicionamiento según cancha: canasta izquierda y derecha
        double[][] pos = new double[][]{
            {0.50, 0.90},  // Base - centro abajo (zona de ataque)
            {0.75, 0.75},  // Escolta - derecha abajo
            {0.25, 0.75},  // Alero - izquierda abajo
            {0.80, 0.35},  // Ala-Pivot - derecha arriba (zona de defensa)
            {0.20, 0.35}   // Pivot - izquierda arriba (zona de defensa)
        };

        for (int i = 0; i < titularesCancha.length; i++) {
            int x = (int) (ancho * pos[i][0]) - (DIAMETRO_TITULAR / 2);
            int y = (int) (alto * pos[i][1]) - (DIAMETRO_TITULAR / 2);
            titularesCancha[i].setBounds(x, y, DIAMETRO_TITULAR, DIAMETRO_TITULAR);
        }
        jLabel2.repaint();
    }

    private void actualizarTitularesEnCancha() {
        Jugador[] titulares = new Jugador[]{
            getJugadorSeleccionado(jComboBox2),
            getJugadorSeleccionado(jComboBox3),
            getJugadorSeleccionado(jComboBox4),
            getJugadorSeleccionado(jComboBox5),
            getJugadorSeleccionado(jComboBox6)
        };
        String[] fallback = new String[]{"B", "E", "A", "AP", "P"};

        for (int i = 0; i < titularesCancha.length; i++) {
            titularesCancha[i].setIcon(crearIconoCircular(titulares[i], fallback[i]));
        }
        jLabel2.repaint();
    }

    private ImageIcon crearIconoCircular(Jugador jugador, String fallback) {
        BufferedImage avatar = new BufferedImage(DIAMETRO_TITULAR, DIAMETRO_TITULAR, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = avatar.createGraphics();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Ellipse2D circulo = new Ellipse2D.Double(1, 1, DIAMETRO_TITULAR - 2, DIAMETRO_TITULAR - 2);

            Image imagenJugador = cargarImagenJugador(jugador);
            if (imagenJugador != null) {
                g2.setClip(circulo);
                int iw = imagenJugador.getWidth(null);
                int ih = imagenJugador.getHeight(null);
                if (iw > 0 && ih > 0) {
                    double escala = Math.max((double) DIAMETRO_TITULAR / iw, (double) DIAMETRO_TITULAR / ih);
                    int nw = (int) (iw * escala);
                    int nh = (int) (ih * escala);
                    int x = (DIAMETRO_TITULAR - nw) / 2;
                    int y = (DIAMETRO_TITULAR - nh) / 2;
                    g2.drawImage(imagenJugador, x, y, nw, nh, null);
                }
                g2.setClip(null);
            } else {
                g2.setColor(new Color(85, 85, 85));
                g2.fill(circulo);
                g2.setColor(Color.WHITE);
                String texto = jugador != null ? String.valueOf(jugador.getNumeroCamiseta()) : fallback;
                g2.setFont(g2.getFont().deriveFont(java.awt.Font.BOLD, 15f));
                int tw = g2.getFontMetrics().stringWidth(texto);
                int th = g2.getFontMetrics().getAscent();
                g2.drawString(texto, (DIAMETRO_TITULAR - tw) / 2, (DIAMETRO_TITULAR + th) / 2 - 3);
            }

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(circulo);
        } finally {
            g2.dispose();
        }
        return new ImageIcon(avatar);
    }

    private Image cargarImagenJugador(Jugador jugador) {
        if (jugador == null || jugador.getImagen() == null || jugador.getImagen().isBlank()) {
            return null;
        }
        File archivo = new File(jugador.getImagen());
        if (!archivo.exists()) {
            return null;
        }
        ImageIcon icon = new ImageIcon(jugador.getImagen());
        if (icon.getIconWidth() <= 0) {
            return null;
        }
        return icon.getImage();
    }

    private void configurarHeaderTabla(JTable tabla) {
        JTableHeader header = tabla.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setOpaque(true);
                setBackground(Color.BLACK);
                setForeground(Color.WHITE);
                setHorizontalAlignment(CENTER);
                return this;
            }
        });
    }

    private void conectarEventos() {
        jButton1.addActionListener(e -> guardarAlineacion());
        jButton2.addActionListener(e -> limpiarFormulario(false));
        jButton3.addActionListener(e -> cargarAlineacionDelPartidoSeleccionado());
        jButton4.addActionListener(e -> agregarSuplenteDesdeDisponibles());

        jComboBox2.addActionListener(e -> actualizarTitularesEnCancha());
        jComboBox3.addActionListener(e -> actualizarTitularesEnCancha());
        jComboBox4.addActionListener(e -> actualizarTitularesEnCancha());
        jComboBox5.addActionListener(e -> actualizarTitularesEnCancha());
        jComboBox6.addActionListener(e -> actualizarTitularesEnCancha());

        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    quitarSuplenteSeleccionado();
                }
            }
        });
    }

    private void cargarDatosIniciales() {
        try {
            if (!Sesion.getInstancia().estaLogueado() || Sesion.getInstancia().getUsuario() == null) {
                JOptionPane.showMessageDialog(this,
                        "Debes iniciar sesión para gestionar alineaciones.",
                        "Sesión requerida",
                        JOptionPane.WARNING_MESSAGE);
                deshabilitarFormulario();
                return;
            }

            try {
                equipoActual = equipoDAO.buscarPorUsuario(Sesion.getInstancia().getUsuario().getIdUsuario());
                if (equipoActual == null) {
                    JOptionPane.showMessageDialog(this,
                            "No tienes un equipo activo.",
                            "Equipo requerido",
                            JOptionPane.WARNING_MESSAGE);
                    deshabilitarFormulario();
                    return;
                }

                cargarPartidos();
                cargarJugadores();
                limpiarFormulario(true);
            } catch (SQLException ex) {
                System.err.println("Error SQL cargando datos iniciales: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error cargando datos iniciales:\n" + ex.getMessage(),
                        "Error de Base de Datos",
                        JOptionPane.ERROR_MESSAGE);
                deshabilitarFormulario();
            }
        } catch (Exception ex) {
            System.err.println("Error inesperado en cargarDatosIniciales:");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error inesperado:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deshabilitarFormulario() {
        jButton1.setEnabled(false);
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
        jButton4.setEnabled(false);
        jComboBox1.setEnabled(false);
        jComboBox2.setEnabled(false);
        jComboBox3.setEnabled(false);
        jComboBox4.setEnabled(false);
        jComboBox5.setEnabled(false);
        jComboBox6.setEnabled(false);
        jComboBox7.setEnabled(false);
    }

    private void cargarPartidos() {
        try {
            DefaultComboBoxModel<Object> modelo = new DefaultComboBoxModel<>();
            modelo.addElement("Selecciona un partido");

            try {
                List<Partido> partidos = partidoDAO.listarPorEquipo(equipoActual.getIdEquipo());
                for (Partido p : partidos) {
                    modelo.addElement(p);
                }
            } catch (SQLException ex) {
                System.err.println("Error cargando partidos: " + ex.getMessage());
                ex.printStackTrace();
            }

            ((JComboBox) jComboBox1).setModel(modelo);
            ((JComboBox) jComboBox1).setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setText(value == null ? "" : value.toString());
                    return this;
                }
            });
        } catch (Exception ex) {
            System.err.println("Error inesperado en cargarPartidos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarJugadores() {
        try {
            jugadoresPorId.clear();
            modeloDisponibles.setRowCount(0);

            try {
                List<Jugador> jugadores = jugadorDAO.listarPorEquipo(equipoActual.getIdEquipo());
                for (Jugador j : jugadores) {
                    jugadoresPorId.put(j.getIdJugador(), j);
                    modeloDisponibles.addRow(new Object[]{j.getIdJugador(), j.getNombreCompleto(), j.getPosicion(), j.getNumeroCamiseta()});
                }

                cargarJugadoresEnCombo(jComboBox2, jugadores);
                cargarJugadoresEnCombo(jComboBox3, jugadores);
                cargarJugadoresEnCombo(jComboBox4, jugadores);
                cargarJugadoresEnCombo(jComboBox5, jugadores);
                cargarJugadoresEnCombo(jComboBox6, jugadores);
                actualizarTitularesEnCancha();
            } catch (SQLException ex) {
                System.err.println("Error cargando jugadores: " + ex.getMessage());
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            System.err.println("Error inesperado en cargarJugadores: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cargarJugadoresEnCombo(JComboBox<String> combo, List<Jugador> jugadores) {
        DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
        model.addElement("Selecciona");
        for (Jugador j : jugadores) {
            model.addElement(j);
        }
        ((JComboBox) combo).setModel(model);
        ((JComboBox) combo).setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "" : value.toString());
                return this;
            }
        });
    }

    private void agregarSuplenteDesdeDisponibles() {
        int fila = jTable1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un jugador disponible.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idJugador = Integer.parseInt(modeloDisponibles.getValueAt(fila, 0).toString());
        if (esTitularSeleccionado(idJugador)) {
            JOptionPane.showMessageDialog(this,
                    "Ese jugador ya está seleccionado como titular.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (estaEnSuplentes(idJugador)) {
            JOptionPane.showMessageDialog(this,
                    "Ese jugador ya está en suplentes.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Jugador j = jugadoresPorId.get(idJugador);
        int orden = modeloSuplentes.getRowCount() + 1;
        modeloSuplentes.addRow(new Object[]{j.getIdJugador(), j.getNombreCompleto(), j.getPosicion(), j.getNumeroCamiseta(), orden});
    }

    private void quitarSuplenteSeleccionado() {
        int fila = jTable2.getSelectedRow();
        if (fila < 0) {
            return;
        }
        modeloSuplentes.removeRow(fila);
        for (int i = 0; i < modeloSuplentes.getRowCount(); i++) {
            modeloSuplentes.setValueAt(i + 1, i, 4);
        }
    }

    private void cargarAlineacionDelPartidoSeleccionado() {
        Partido partido = getPartidoSeleccionado();
        if (partido == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un partido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            limpiarFormulario(true);
            alineacionActual = alineacionDAO.buscarPorPartidoYEquipo(partido.getIdPartido(), equipoActual.getIdEquipo());
            
            if (alineacionActual == null) {
                JOptionPane.showMessageDialog(this, 
                        "No hay alineación guardada para este partido.\nPuedes crear una nueva.",
                        "Sin alineación",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Cargar los titulares
            seleccionarJugadorComboPorId(jComboBox2, alineacionActual.getBaseId());
            seleccionarJugadorComboPorId(jComboBox3, alineacionActual.getEscoltaId());
            seleccionarJugadorComboPorId(jComboBox4, alineacionActual.getAleroId());
            seleccionarJugadorComboPorId(jComboBox5, alineacionActual.getAlaPivotId());
            seleccionarJugadorComboPorId(jComboBox6, alineacionActual.getPivotId());

            // Cargar el tipo de alineación
            if (alineacionActual.getTipo() != null) {
                ((JComboBox) jComboBox7).setSelectedItem(alineacionActual.getTipo().getValorDb());
            }

            // Cargar los suplentes
            List<Suplente> suplentes = suplenteDAO.listarPorAlineacion(alineacionActual.getIdAlineacion());
            for (Suplente s : suplentes) {
                Jugador j = jugadoresPorId.get(s.getIdJugador());
                if (j != null) {
                    modeloSuplentes.addRow(new Object[]{j.getIdJugador(), j.getNombreCompleto(), j.getPosicion(), j.getNumeroCamiseta(), s.getOrdenIngreso()});
                }
            }
            
            actualizarTitularesEnCancha();
            JOptionPane.showMessageDialog(this, 
                    "Alineación cargada correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error cargando la alineación:\n" + ex.getMessage(),
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void guardarAlineacion() {
        Partido partido = getPartidoSeleccionado();
        if (partido == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un partido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Jugador base = getJugadorSeleccionado(jComboBox2);
        Jugador escolta = getJugadorSeleccionado(jComboBox3);
        Jugador alero = getJugadorSeleccionado(jComboBox4);
        Jugador alaPivot = getJugadorSeleccionado(jComboBox5);
        Jugador pivot = getJugadorSeleccionado(jComboBox6);

        if (base == null || escolta == null || alero == null || alaPivot == null || pivot == null) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar los 5 titulares.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<Integer> idsTitulares = new HashSet<>();
        idsTitulares.add(base.getIdJugador());
        idsTitulares.add(escolta.getIdJugador());
        idsTitulares.add(alero.getIdJugador());
        idsTitulares.add(alaPivot.getIdJugador());
        idsTitulares.add(pivot.getIdJugador());
        if (idsTitulares.size() < 5) {
            JOptionPane.showMessageDialog(this,
                    "No puedes repetir jugadores en titulares.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (haySuplenteRepetidoConTitular(idsTitulares)) {
            JOptionPane.showMessageDialog(this,
                    "Hay suplentes repetidos con titulares.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (alineacionActual == null) {
            alineacionActual = new Alineacion();
        }
        alineacionActual.setIdPartido(partido.getIdPartido());
        alineacionActual.setIdEquipo(equipoActual.getIdEquipo());
        alineacionActual.setBaseId(base.getIdJugador());
        alineacionActual.setEscoltaId(escolta.getIdJugador());
        alineacionActual.setAleroId(alero.getIdJugador());
        alineacionActual.setAlaPivotId(alaPivot.getIdJugador());
        alineacionActual.setPivotId(pivot.getIdJugador());
        String tipo = String.valueOf(((JComboBox) jComboBox7).getSelectedItem());
        alineacionActual.setTipo("Defensivo".equalsIgnoreCase(tipo)
                ? Alineacion.TipoAlineacion.DEFENSIVO
                : Alineacion.TipoAlineacion.OFENSIVO);

        try {
            if (!alineacionDAO.guardar(alineacionActual)) {
                JOptionPane.showMessageDialog(this, "No se pudo guardar la alineación.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            suplenteDAO.eliminarPorAlineacion(alineacionActual.getIdAlineacion());
            for (int i = 0; i < modeloSuplentes.getRowCount(); i++) {
                int idJugador = Integer.parseInt(modeloSuplentes.getValueAt(i, 0).toString());
                int orden = Integer.parseInt(modeloSuplentes.getValueAt(i, 4).toString());
                suplenteDAO.insertar(new Suplente(0, alineacionActual.getIdAlineacion(), idJugador, orden));
            }

            JOptionPane.showMessageDialog(this,
                    "Alineación guardada correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar la alineación:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean haySuplenteRepetidoConTitular(Set<Integer> idsTitulares) {
        for (int i = 0; i < modeloSuplentes.getRowCount(); i++) {
            int idJugador = Integer.parseInt(modeloSuplentes.getValueAt(i, 0).toString());
            if (idsTitulares.contains(idJugador)) {
                return true;
            }
        }
        return false;
    }

    private void limpiarFormulario(boolean conservarPartido) {
        alineacionActual = null;
        if (!conservarPartido) {
            ((JComboBox) jComboBox1).setSelectedIndex(0);
        }
        ((JComboBox) jComboBox2).setSelectedIndex(0);
        ((JComboBox) jComboBox3).setSelectedIndex(0);
        ((JComboBox) jComboBox4).setSelectedIndex(0);
        ((JComboBox) jComboBox5).setSelectedIndex(0);
        ((JComboBox) jComboBox6).setSelectedIndex(0);
        ((JComboBox) jComboBox7).setSelectedIndex(0);
        modeloSuplentes.setRowCount(0);
        actualizarTitularesEnCancha();
    }

    private Partido getPartidoSeleccionado() {
        Object item = ((JComboBox) jComboBox1).getSelectedItem();
        return item instanceof Partido ? (Partido) item : null;
    }

    private Jugador getJugadorSeleccionado(JComboBox<String> combo) {
        Object item = ((JComboBox) combo).getSelectedItem();
        return item instanceof Jugador ? (Jugador) item : null;
    }

    private boolean esTitularSeleccionado(int idJugador) {
        List<Jugador> titulares = new ArrayList<>();
        titulares.add(getJugadorSeleccionado(jComboBox2));
        titulares.add(getJugadorSeleccionado(jComboBox3));
        titulares.add(getJugadorSeleccionado(jComboBox4));
        titulares.add(getJugadorSeleccionado(jComboBox5));
        titulares.add(getJugadorSeleccionado(jComboBox6));
        for (Jugador titular : titulares) {
            if (titular != null && titular.getIdJugador() == idJugador) {
                return true;
            }
        }
        return false;
    }

    private boolean estaEnSuplentes(int idJugador) {
        for (int i = 0; i < modeloSuplentes.getRowCount(); i++) {
            if (Integer.parseInt(modeloSuplentes.getValueAt(i, 0).toString()) == idJugador) {
                return true;
            }
        }
        return false;
    }

    private void seleccionarJugadorComboPorId(JComboBox<String> combo, int idJugador) {
        JComboBox rawCombo = (JComboBox) combo;
        for (int i = 0; i < rawCombo.getItemCount(); i++) {
            Object item = rawCombo.getItemAt(i);
            if (item instanceof Jugador && ((Jugador) item).getIdJugador() == idJugador) {
                rawCombo.setSelectedIndex(i);
                return;
            }
        }
        rawCombo.setSelectedIndex(0);
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
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jComboBox6 = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox<>();

        setBorder(null);
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setAutoscrolls(true);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 51, 0));
        jLabel1.setText("Alineación");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Partido");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cancha.jpg"))); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setBackground(new java.awt.Color(255, 102, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Guardar");

        jButton2.setBackground(new java.awt.Color(255, 102, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Limpiar");

        jButton3.setBackground(new java.awt.Color(255, 102, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Editar");

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Base");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Escolta");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Alero");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Ala-Pivot");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Pivot");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Seleccione los Titulares");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox2, 0, 159, Short.MAX_VALUE)
                            .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox6, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(111, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42))
        );

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Sustituciones");

        jButton4.setBackground(new java.awt.Color(255, 102, 0));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Agregar");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 146, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(134, 134, 134))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jButton4)
                                .addGap(14, 14, 14))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(14, 14, 14))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Alineación");

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ofensiva", "Defensiva" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(64, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(47, 47, 47)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)))
                        .addGap(44, 44, 44))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(365, 365, 365))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(111, 111, 111)
                        .addComponent(jLabel2)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jLabel9)
                    .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        // Crear scroll pane para envolver jPanel1
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(jPanel1);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setSize(1200, 800);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
