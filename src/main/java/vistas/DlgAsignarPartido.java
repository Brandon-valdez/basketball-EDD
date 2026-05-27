/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package vistas;

import java.awt.Frame;

/**
 *
 * @author kathy
 */
public class DlgAsignarPartido extends javax.swing.JDialog {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgAsignarPartido.class.getName());

    private final dao.PartidoDAO partidoDAO = new dao.PartidoDAO();
private final dao.TorneoDAO  torneoDAO  = new dao.TorneoDAO();
private final dao.ArbitroDAO arbitroDAO = new dao.ArbitroDAO();

private java.util.List<modelo.Partido> partidosPendientes;
private java.util.List<modelo.Arbitro> listaArbitros;

    /**
     * Creates new form DlgAsignarPartido
     */
public DlgAsignarPartido(Frame parent, boolean par) {
    super(parent, "Asignar Árbitro a Partido", true);
    initComponents();
    cargarCombos();
    cmbTorneo.addActionListener(e -> cargarPartidosPendientes());
    btnAsignar.addActionListener(e -> asignarArbitro());
}

    private void cargarCombos() {
    try {
        listaArbitros = arbitroDAO.listar();
        cmbArbitroAsig.removeAllItems();
        for (modelo.Arbitro a : listaArbitros) {
            cmbArbitroAsig.addItem(a.getNombreCompleto() + " [" + a.getLicencia() + "]");
        }

        cmbTorneo.removeAllItems();
        cmbTorneo.addItem("-- Seleccionar torneo --");
        for (modelo.Torneo t : torneoDAO.listar()) {
            cmbTorneo.addItem(t.getIdTorneo() + " | " + t.getNombre());
        }
    } catch (java.sql.SQLException e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
    }
}

private void cargarPartidosPendientes() {
    if (cmbTorneo.getSelectedIndex() <= 0) {
        cmbPartido.removeAllItems();
        return;
    }
    try {
        String item = (String) cmbTorneo.getSelectedItem();
        int idTorneo = Integer.parseInt(item.split("\\|")[0].trim());

        partidosPendientes = partidoDAO.listarPendientesPorTorneo(idTorneo);
        cmbPartido.removeAllItems();

        if (partidosPendientes.isEmpty()) {
            cmbPartido.addItem("-- Sin partidos pendientes --");
        } else {
            for (modelo.Partido p : partidosPendientes) {
                cmbPartido.addItem(
                    p.getNombreEquipoLocal() + " vs " + p.getNombreEquipoVisit() +
                    "  (" + p.getFecha().toLocalDate() + ")" +
                    "  — Árbitro actual: " + p.getNombreArbitro());
            }
        }
    } catch (java.sql.SQLException e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error al cargar partidos: " + e.getMessage());
    }
}

private void asignarArbitro() {
    int idxPartido = cmbPartido.getSelectedIndex();
    int idxArbitro = cmbArbitroAsig.getSelectedIndex();

    if (cmbTorneo.getSelectedIndex() <= 0) {
        javax.swing.JOptionPane.showMessageDialog(this, "Seleccioná un torneo primero.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }
    if (partidosPendientes == null || partidosPendientes.isEmpty() || idxPartido < 0) {
        javax.swing.JOptionPane.showMessageDialog(this, "No hay partido pendiente seleccionado.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }
    if (idxArbitro < 0) {
        javax.swing.JOptionPane.showMessageDialog(this, "Seleccioná un árbitro.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }

    modelo.Partido partido = partidosPendientes.get(idxPartido);
    modelo.Arbitro arbitro = listaArbitros.get(idxArbitro);

    if (partido.getIdArbitro() == arbitro.getIdArbitro()) {
        javax.swing.JOptionPane.showMessageDialog(this,
            arbitro.getNombreCompleto() + " ya está asignado a este partido.",
            "Sin cambios", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
        "¿Asignar a " + arbitro.getNombreCompleto() + " al partido:\n" +
        partido.getNombreEquipoLocal() + " vs " + partido.getNombreEquipoVisit() + "?",
        "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
    if (confirm != javax.swing.JOptionPane.YES_OPTION) return;

    try {
        boolean ok = partidoDAO.reasignarArbitro(partido.getIdPartido(), arbitro.getIdArbitro());
        if (ok) {
            javax.swing.JOptionPane.showMessageDialog(this, "Árbitro asignado correctamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            cargarPartidosPendientes(); // refrescar lista
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                "No se pudo asignar. El partido puede ya estar finalizado.",
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    } catch (java.sql.SQLException e) {
        javax.swing.JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
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

        jLabel1 = new javax.swing.JLabel();
        cmbTorneo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cmbPartido = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cmbArbitroAsig = new javax.swing.JComboBox<>();
        btnAsignar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Torneo: ");

        cmbTorneo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Partido:");

        cmbPartido.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Arbitro:");

        cmbArbitroAsig.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnAsignar.setText("Asignar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbTorneo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbPartido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbArbitroAsig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(btnAsignar)))
                .addContainerGap(336, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbTorneo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cmbPartido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cmbArbitroAsig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(btnAsignar)
                .addContainerGap(184, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DlgAsignarPartido dialog = new DlgAsignarPartido(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAsignar;
    private javax.swing.JComboBox<String> cmbArbitroAsig;
    private javax.swing.JComboBox<String> cmbPartido;
    private javax.swing.JComboBox<String> cmbTorneo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
