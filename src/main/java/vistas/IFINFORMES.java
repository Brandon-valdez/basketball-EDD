/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vistas;
import dao.EstadisticaDAO;
import dao.PartidoDAO;
import modelo.Estadistica;
import modelo.Partido;
import util.Sesion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;
/**
 *
 * @author Usuario
 */
public class IFINFORMES extends javax.swing.JInternalFrame {
private final PartidoDAO partidoDAO = new PartidoDAO();
private final EstadisticaDAO estadisticaDAO = new EstadisticaDAO();
private List<Partido> partidos;
private DefaultTableModel modeloTabla;
    /**
     * Creates new form IFINFORMES
     */
    public IFINFORMES() {
        initComponents();
        configurarTabla();
    cargarPartidos();
    }
private void configurarTabla() {
    modeloTabla = new DefaultTableModel(
        new String[]{"idJugador", "Jugador", "Equipo", "Posición", "Puntos", "Rebotes", "Asistencias", "Faltas", "Minutos"},
        0
    ) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return col >= 4; // solo editable desde Puntos en adelante
        }
    };
    tblEstadisticas.setModel(modeloTabla);
    
    // Configurar renderer personalizado para las celdas
    javax.swing.table.DefaultTableCellRenderer renderer = new javax.swing.table.DefaultTableCellRenderer();
    renderer.setBackground(new java.awt.Color(51, 51, 51));
    renderer.setForeground(new java.awt.Color(0, 0, 0));
    
    for (int i = 0; i < tblEstadisticas.getColumnCount(); i++) {
        tblEstadisticas.getColumnModel().getColumn(i).setCellRenderer(renderer);
    }
    
    tblEstadisticas.setBackground(new java.awt.Color(51, 51, 51));
    tblEstadisticas.setForeground(new java.awt.Color(0, 0, 0));
    tblEstadisticas.setGridColor(new java.awt.Color(80, 80, 80));
    tblEstadisticas.setSelectionBackground(new java.awt.Color(80, 80, 80));
    tblEstadisticas.setSelectionForeground(new java.awt.Color(0, 0, 0));
    tblEstadisticas.getTableHeader().setBackground(new java.awt.Color(51, 51, 51));
    tblEstadisticas.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));
    // ocultar columna idJugador
    tblEstadisticas.getColumnModel().getColumn(0).setMinWidth(0);
    tblEstadisticas.getColumnModel().getColumn(0).setMaxWidth(0);
    tblEstadisticas.getTableHeader().setReorderingAllowed(false);
}

private void cargarPartidos() {
    try {
        int idArbitro = Sesion.getInstancia().getUsuario().getIdUsuario();
        partidos = partidoDAO.listarPorArbitro(idArbitro);
        cmbPartidos.removeAllItems();
        for (Partido p : partidos) {
            cmbPartidos.addItem(p.getNombreEquipoLocal() + " vs " + p.getNombreEquipoVisit()
                    + " — " + p.getFecha().toLocalDate());
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Error al cargar partidos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void cargarJugadores() {
    modeloTabla.setRowCount(0);
    int idx = cmbPartidos.getSelectedIndex();
    if (idx < 0) return;
    try {
        Partido p = partidos.get(idx);
        List<Estadistica> lista = estadisticaDAO.listarPorPartido(p.getIdPartido());
        for (Estadistica e : lista) {
            modeloTabla.addRow(new Object[]{
                e.getIdJugador(),
                e.getNombreJugador(),
                e.getNombreEquipo(),
                e.getPosicion(),
                e.getPuntos(),
                e.getRebotes(),
                e.getAsistencias(),
                e.getFaltas(),
                e.getMinutosJugados()
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Error al cargar jugadores: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void guardarEstadisticas() {
    int idx = cmbPartidos.getSelectedIndex();
    if (idx < 0) {
        JOptionPane.showMessageDialog(this, "Selecciona un partido.", "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }
    try {
        Partido p = partidos.get(idx);
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Estadistica e = new Estadistica();
            e.setIdJugador((int) modeloTabla.getValueAt(i, 0));
            e.setIdPartido(p.getIdPartido());
            e.setPuntos(Integer.parseInt(modeloTabla.getValueAt(i, 4).toString()));
            e.setRebotes(Integer.parseInt(modeloTabla.getValueAt(i, 5).toString()));
            e.setAsistencias(Integer.parseInt(modeloTabla.getValueAt(i, 6).toString()));
            e.setFaltas(Integer.parseInt(modeloTabla.getValueAt(i, 7).toString()));
            e.setMinutosJugados(Integer.parseInt(modeloTabla.getValueAt(i, 8).toString()));
            estadisticaDAO.guardar(e);
        }
        JOptionPane.showMessageDialog(this, "Estadísticas guardadas.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
                "Error al guardar: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
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
        jLabel1 = new javax.swing.JLabel();
        cmbPartidos = new javax.swing.JComboBox<>();
        btnCargar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblEstadisticas = new javax.swing.JTable();
        btnGuardar = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 102, 0));
        jLabel1.setText("Partido:");
        jLabel1.setOpaque(true);

        cmbPartidos.setBackground(new java.awt.Color(255, 102, 0));
        cmbPartidos.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cmbPartidos.setForeground(new java.awt.Color(255, 255, 255));
        cmbPartidos.addActionListener(this::cmbPartidosActionPerformed);

        btnCargar.setBackground(new java.awt.Color(255, 102, 0));
        btnCargar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCargar.setForeground(new java.awt.Color(255, 255, 255));
        btnCargar.setText("Cargar jugadores");
        btnCargar.addActionListener(this::btnCargarActionPerformed);

        tblEstadisticas.setBackground(new java.awt.Color(0, 0, 0));
        tblEstadisticas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tblEstadisticas.setForeground(new java.awt.Color(255, 255, 255));
        tblEstadisticas.setModel(new javax.swing.table.DefaultTableModel(
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
        tblEstadisticas.setGridColor(new java.awt.Color(255, 102, 0));
        jScrollPane2.setViewportView(tblEstadisticas);

        jScrollPane1.setViewportView(jScrollPane2);

        btnGuardar.setBackground(new java.awt.Color(255, 102, 0));
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Guardar Estadisticas");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(cmbPartidos, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(btnCargar, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 610, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGuardar)))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbPartidos, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCargar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 237, Short.MAX_VALUE)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(114, 114, 114))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbPartidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPartidosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbPartidosActionPerformed

    private void btnCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarActionPerformed
        // TODO add your handling code here:
        cargarJugadores();
    }//GEN-LAST:event_btnCargarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // TODO add your handling code here:
        guardarEstadisticas();
    }//GEN-LAST:event_btnGuardarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCargar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cmbPartidos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblEstadisticas;
    // End of variables declaration//GEN-END:variables
}
