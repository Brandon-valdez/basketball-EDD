/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vistas;

import dao.EstadisticaEquipoDAO;
import dao.PartidoDAO;
import modelo.EstadisticaEquipo;
import modelo.Partido;
import util.Sesion;
import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

/**
 * IFINFORMES - Vista para que el árbitro registre estadísticas por equipo
 * Permite editar 5 campos por equipo (sin Puntos): Faltas, Triples, Tiros Libres, Rebotes, Asistencias
 * @author Usuario
 */
public class IFINFORMES extends javax.swing.JInternalFrame {
    
    private final PartidoDAO partidoDAO = new PartidoDAO();
    private final EstadisticaEquipoDAO estadisticaEquipoDAO = new EstadisticaEquipoDAO();
    private List<Partido> partidos;
    private Partido partidoActual;

    // Componentes UI
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox<String> cmbPartidos;
    private javax.swing.JButton btnCargar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JPanel panelLocal;
    private javax.swing.JPanel panelVisitante;
    private javax.swing.JLabel lblEquipoLocal;
    private javax.swing.JLabel lblEquipoVisitante;

    // TextFields para equipo local
    private javax.swing.JTextField txtFaltasLocal;
    private javax.swing.JTextField txtTriplesLocal;
    private javax.swing.JTextField txtTirosLibresLocal;
    private javax.swing.JTextField txtRebotesLocal;
    private javax.swing.JTextField txtAsistenciasLocal;

    // TextFields para equipo visitante
    private javax.swing.JTextField txtFaltasVisitante;
    private javax.swing.JTextField txtTriplesVisitante;
    private javax.swing.JTextField txtTirosLibresVisitante;
    private javax.swing.JTextField txtRebotesVisitante;
    private javax.swing.JTextField txtAsistenciasVisitante;

    /**
     * Crea nueva instancia de IFINFORMES
     */
    public IFINFORMES() {
        initComponents();
        cargarPartidos();
    }

    /**
     * Carga los partidos asignados al árbitro logueado
     */
    private void cargarPartidos() {
        try {
            int idArbitro = Sesion.getInstancia().getUsuario().getIdArbitro();
            partidos = partidoDAO.listarPorArbitro(idArbitro);
            cmbPartidos.removeAllItems();
            
            if (partidos != null && !partidos.isEmpty()) {
                for (Partido p : partidos) {
                    String item = p.getNombreEquipoLocal() + " vs " + p.getNombreEquipoVisit()
                            + " — " + p.getFecha().toLocalDate();
                    cmbPartidos.addItem(item);
                }
            } else {
                cmbPartidos.addItem("No hay partidos asignados");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar partidos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga las estadísticas del partido seleccionado para ambos equipos
     */
    private void cargarEstadisticas() {
        int idx = cmbPartidos.getSelectedIndex();
        if (idx < 0 || partidos == null || partidos.isEmpty()) {
            limpiarFormulario();
            return;
        }

        try {
            partidoActual = partidos.get(idx);
            lblEquipoLocal.setText(partidoActual.getNombreEquipoLocal());
            lblEquipoVisitante.setText(partidoActual.getNombreEquipoVisit());

            // Cargar estadísticas del equipo local
            EstadisticaEquipo estadisticaLocal = estadisticaEquipoDAO.buscar(
                    partidoActual.getIdPartido(),
                    partidoActual.getIdEquipoLocal()
            );
            cargarDatosEquipo(estadisticaLocal, true);

            // Cargar estadísticas del equipo visitante
            EstadisticaEquipo estadisticaVisitante = estadisticaEquipoDAO.buscar(
                    partidoActual.getIdPartido(),
                    partidoActual.getIdEquipoVisit()
            );
            cargarDatosEquipo(estadisticaVisitante, false);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar estadísticas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga los datos de un equipo en sus respectivos campos de texto
     */
    private void cargarDatosEquipo(EstadisticaEquipo estadistica, boolean esLocal) {
        if (estadistica == null) {
            // Si no existen datos, mostrar ceros
            if (esLocal) {
                txtFaltasLocal.setText("0");
                txtTriplesLocal.setText("0");
                txtTirosLibresLocal.setText("0");
                txtRebotesLocal.setText("0");
                txtAsistenciasLocal.setText("0");
            } else {
                txtFaltasVisitante.setText("0");
                txtTriplesVisitante.setText("0");
                txtTirosLibresVisitante.setText("0");
                txtRebotesVisitante.setText("0");
                txtAsistenciasVisitante.setText("0");
            }
        } else {
            // Cargar los datos existentes
            if (esLocal) {
                txtFaltasLocal.setText(String.valueOf(estadistica.getFaltas()));
                txtTriplesLocal.setText(String.valueOf(estadistica.getTriplesAnotados()));
                txtTirosLibresLocal.setText(String.valueOf(estadistica.getTirosLibresAnotados()));
                txtRebotesLocal.setText(String.valueOf(estadistica.getRebotes()));
                txtAsistenciasLocal.setText(String.valueOf(estadistica.getAsistencias()));
            } else {
                txtFaltasVisitante.setText(String.valueOf(estadistica.getFaltas()));
                txtTriplesVisitante.setText(String.valueOf(estadistica.getTriplesAnotados()));
                txtTirosLibresVisitante.setText(String.valueOf(estadistica.getTirosLibresAnotados()));
                txtRebotesVisitante.setText(String.valueOf(estadistica.getRebotes()));
                txtAsistenciasVisitante.setText(String.valueOf(estadistica.getAsistencias()));
            }
        }
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarFormulario() {
        lblEquipoLocal.setText("—");
        lblEquipoVisitante.setText("—");
        txtFaltasLocal.setText("0");
        txtTriplesLocal.setText("0");
        txtTirosLibresLocal.setText("0");
        txtRebotesLocal.setText("0");
        txtAsistenciasLocal.setText("0");
        txtFaltasVisitante.setText("0");
        txtTriplesVisitante.setText("0");
        txtTirosLibresVisitante.setText("0");
        txtRebotesVisitante.setText("0");
        txtAsistenciasVisitante.setText("0");
    }

    /**
     * Guarda las estadísticas de ambos equipos en la base de datos
     */
    private void guardarEstadisticas() {
        if (partidoActual == null) {
            JOptionPane.showMessageDialog(this, 
                    "Selecciona un partido primero.", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Guardar estadísticas del equipo local
            EstadisticaEquipo estadisticaLocal = crearEstadisticaDesdeFormulario(
                    partidoActual.getIdPartido(),
                    partidoActual.getIdEquipoLocal(),
                    true
            );
            estadisticaEquipoDAO.guardar(estadisticaLocal);

            // Guardar estadísticas del equipo visitante
            EstadisticaEquipo estadisticaVisitante = crearEstadisticaDesdeFormulario(
                    partidoActual.getIdPartido(),
                    partidoActual.getIdEquipoVisit(),
                    false
            );
            estadisticaEquipoDAO.guardar(estadisticaVisitante);

            JOptionPane.showMessageDialog(this, 
                    "Estadísticas guardadas exitosamente.", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar: " + e.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Crea un objeto EstadisticaEquipo con los valores del formulario
     */
    private EstadisticaEquipo crearEstadisticaDesdeFormulario(int idPartido, int idEquipo, boolean esLocal) {
        EstadisticaEquipo e = new EstadisticaEquipo();
        e.setIdPartido(idPartido);
        e.setIdEquipo(idEquipo);
        e.setPuntos(0); // No se usa puntos en esta vista

        try {
            if (esLocal) {
                e.setFaltas(parseInt(txtFaltasLocal));
                e.setTriplesAnotados(parseInt(txtTriplesLocal));
                e.setTirosLibresAnotados(parseInt(txtTirosLibresLocal));
                e.setRebotes(parseInt(txtRebotesLocal));
                e.setAsistencias(parseInt(txtAsistenciasLocal));
            } else {
                e.setFaltas(parseInt(txtFaltasVisitante));
                e.setTriplesAnotados(parseInt(txtTriplesVisitante));
                e.setTirosLibresAnotados(parseInt(txtTirosLibresVisitante));
                e.setRebotes(parseInt(txtRebotesVisitante));
                e.setAsistencias(parseInt(txtAsistenciasVisitante));
            }
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Por favor ingresa solo números.", ex);
        }
        return e;
    }

    /**
     * Convierte el valor de un JTextField a entero, retorna 0 si está vacío
     */
    private int parseInt(javax.swing.JTextField txt) {
        String text = txt.getText().trim();
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    /**
     * Inicializa los componentes de la GUI
     */
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbPartidos = new javax.swing.JComboBox<>();
        btnCargar = new javax.swing.JButton();
        panelLocal = new javax.swing.JPanel();
        lblEquipoLocal = new javax.swing.JLabel();
        panelVisitante = new javax.swing.JPanel();
        lblEquipoVisitante = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();

        // Panel principal
        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        // Título
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36));
        jLabel1.setForeground(new java.awt.Color(255, 102, 0));
        jLabel1.setText("Informe de Estadísticas por Equipo");

        // ComboBox de partidos
        cmbPartidos.setBackground(new java.awt.Color(255, 102, 0));
        cmbPartidos.setFont(new java.awt.Font("Segoe UI", 1, 14));
        cmbPartidos.setForeground(new java.awt.Color(255, 255, 255));
        cmbPartidos.addActionListener(evt -> cargarEstadisticas());

        // Botón cargar
        btnCargar.setBackground(new java.awt.Color(255, 102, 0));
        btnCargar.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnCargar.setForeground(new java.awt.Color(255, 255, 255));
        btnCargar.setText("Cargar");
        btnCargar.addActionListener(evt -> cargarEstadisticas());

        // Panel para equipo local
        panelLocal.setBackground(new java.awt.Color(0, 0, 0));
        panelLocal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 102, 0), 2));
        lblEquipoLocal.setFont(new java.awt.Font("Segoe UI", 1, 24));
        lblEquipoLocal.setForeground(new java.awt.Color(255, 102, 0));
        lblEquipoLocal.setText("—");

        txtFaltasLocal = new javax.swing.JTextField();
        txtTriplesLocal = new javax.swing.JTextField();
        txtTirosLibresLocal = new javax.swing.JTextField();
        txtRebotesLocal = new javax.swing.JTextField();
        txtAsistenciasLocal = new javax.swing.JTextField();
        configurePanel(panelLocal, lblEquipoLocal, txtFaltasLocal, txtTriplesLocal, 
                       txtTirosLibresLocal, txtRebotesLocal, txtAsistenciasLocal);

        // Panel para equipo visitante
        panelVisitante.setBackground(new java.awt.Color(0, 0, 0));
        panelVisitante.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 102, 0), 2));
        lblEquipoVisitante.setFont(new java.awt.Font("Segoe UI", 1, 24));
        lblEquipoVisitante.setForeground(new java.awt.Color(255, 102, 0));
        lblEquipoVisitante.setText("—");

        txtFaltasVisitante = new javax.swing.JTextField();
        txtTriplesVisitante = new javax.swing.JTextField();
        txtTirosLibresVisitante = new javax.swing.JTextField();
        txtRebotesVisitante = new javax.swing.JTextField();
        txtAsistenciasVisitante = new javax.swing.JTextField();
        configurePanel(panelVisitante, lblEquipoVisitante, txtFaltasVisitante, txtTriplesVisitante,
                       txtTirosLibresVisitante, txtRebotesVisitante, txtAsistenciasVisitante);

        // Botón guardar
        btnGuardar.setBackground(new java.awt.Color(255, 102, 0));
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 16));
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(evt -> guardarEstadisticas());

        // Layout del panel principal
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmbPartidos, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCargar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(panelVisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbPartidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCargar))
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelVisitante, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        // Layout principal de la ventana
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setClosable(true);
        setResizable(true);
        pack();
    }

    /**
     * Configura el layout de un panel de equipo con sus 5 campos de estadísticas
     */
    private void configurePanel(javax.swing.JPanel panel, javax.swing.JLabel lblEquipo,
            javax.swing.JTextField txtFaltas, javax.swing.JTextField txtTriples,
            javax.swing.JTextField txtTirosLibres, javax.swing.JTextField txtRebotes,
            javax.swing.JTextField txtAsistencias) {

        // Crear etiquetas
        javax.swing.JLabel lblFaltas = new javax.swing.JLabel("Faltas:");
        lblFaltas.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblFaltas.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.JLabel lblTriples_lbl = new javax.swing.JLabel("Triples:");
        lblTriples_lbl.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblTriples_lbl.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.JLabel lblTirosLibres = new javax.swing.JLabel("Tiros Libres:");
        lblTirosLibres.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblTirosLibres.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.JLabel lblRebotes = new javax.swing.JLabel("Rebotes:");
        lblRebotes.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblRebotes.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.JLabel lblAsistencias = new javax.swing.JLabel("Asistencias:");
        lblAsistencias.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblAsistencias.setForeground(new java.awt.Color(255, 255, 255));

        // Configurar los textfields
        configureTextField(txtFaltas);
        configureTextField(txtTriples);
        configureTextField(txtTirosLibres);
        configureTextField(txtRebotes);
        configureTextField(txtAsistencias);

        // Layout del panel
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
        panel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEquipo)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFaltas)
                            .addComponent(lblTriples_lbl)
                            .addComponent(lblTirosLibres))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtFaltas, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                            .addComponent(txtTriples, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                            .addComponent(txtTirosLibres, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblRebotes)
                            .addComponent(lblAsistencias))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtRebotes, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                            .addComponent(txtAsistencias, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEquipo)
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFaltas)
                    .addComponent(txtFaltas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRebotes)
                    .addComponent(txtRebotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTriples_lbl)
                    .addComponent(txtTriples, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAsistencias)
                    .addComponent(txtAsistencias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTirosLibres)
                    .addComponent(txtTirosLibres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    /**
     * Configura el estilo de un JTextField (color de fondo, texto, fuente)
     */
    private void configureTextField(javax.swing.JTextField txt) {
        txt.setBackground(new java.awt.Color(51, 51, 51));
        txt.setForeground(new java.awt.Color(255, 255, 255));
        txt.setFont(new java.awt.Font("Segoe UI", 0, 12));
    }
}

