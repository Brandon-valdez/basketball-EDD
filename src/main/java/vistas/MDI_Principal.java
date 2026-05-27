package vistas;
import util.Sesion;

public class MDI_Principal extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MDI_Principal.class.getName());

    /**
     * Creates new form mdi
     */
    public MDI_Principal() {
        initComponents();
    setLocationRelativeTo(null);
    setExtendedState(MAXIMIZED_BOTH);

    // Sacar jPanelMenu del desktopPane
    desktopPane.remove(jPanelMenu);

    // Reconstruir el panel menú con orden correcto
    jPanelMenu.removeAll();
    jPanelMenu.setLayout(new java.awt.GridBagLayout());
    jPanelMenu.setBackground(new java.awt.Color(0, 0, 0));
    jPanelMenu.setPreferredSize(new java.awt.Dimension(140, 0));

    java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
    gbc.gridx = 0;
    gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gbc.insets = new java.awt.Insets(3, 8, 3, 8);
    gbc.weightx = 1.0;

    // Spacer arriba para empujar al centro
    java.awt.GridBagConstraints spacer = new java.awt.GridBagConstraints();
    spacer.gridx = 0; spacer.gridy = 0;
    spacer.weighty = 1.0;
    spacer.fill = java.awt.GridBagConstraints.VERTICAL;
    jPanelMenu.add(new javax.swing.JLabel(), spacer);

    // Logo
    int row = 1;
    gbc.gridy = row++; gbc.insets = new java.awt.Insets(2, 8, 0, 8);
    jPanelMenu.add(jLabel1, gbc); // emoji balón

    gbc.gridy = row++; gbc.insets = new java.awt.Insets(0, 8, 0, 8);
    jPanelMenu.add(jLabel2, gbc); // BKB

    gbc.gridy = row++; gbc.insets = new java.awt.Insets(0, 8, 0, 8);
    jPanelMenu.add(jLabel3, gbc); // MANAGER

    gbc.gridy = row++; gbc.insets = new java.awt.Insets(8, 8, 2, 8);
    jPanelMenu.add(lblUsuario, gbc);

    gbc.gridy = row++; gbc.insets = new java.awt.Insets(2, 8, 10, 8);
    jPanelMenu.add(lblRol, gbc);

    // Botones
    gbc.insets = new java.awt.Insets(3, 8, 3, 8);
    javax.swing.JButton[] botones = {
        btnDashboard, btnUsuarios, btnEquipos, btnJugadores,
        btnTorneos, btnPartidos, btnArbitros, btnEstadisticas,
        btnMiEquipo, btnAlineacion, btnResultados
    };
    for (javax.swing.JButton btn : botones) {
        gbc.gridy = row++;
        jPanelMenu.add(btn, gbc);
    }

    // Cerrar sesión
    gbc.gridy = row++; gbc.insets = new java.awt.Insets(10, 8, 8, 8);
    jPanelMenu.add(btnCerrarSesion, gbc);

    // Spacer abajo
    java.awt.GridBagConstraints spacer2 = new java.awt.GridBagConstraints();
    spacer2.gridx = 0; spacer2.gridy = row;
    spacer2.weighty = 1.0;
    spacer2.fill = java.awt.GridBagConstraints.VERTICAL;
    jPanelMenu.add(new javax.swing.JLabel(), spacer2);

    // Layout principal
    getContentPane().setLayout(new java.awt.BorderLayout());
    getContentPane().add(jPanelMenu, java.awt.BorderLayout.WEST);
    getContentPane().add(desktopPane, java.awt.BorderLayout.CENTER);

    configurarRol();
    //labels
    lblUsuario.setForeground(java.awt.Color.WHITE);
lblUsuario.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
lblUsuario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

lblRol.setForeground(java.awt.Color.WHITE);
lblRol.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 11));
lblRol.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
}

private void configurarRol() {

    modelo.Usuario usuarioActual = util.Sesion.getInstancia().getUsuario();
     // DEBUG
    System.out.println("Usuario: " + (usuarioActual != null ? usuarioActual.getNombre() : "NULL"));
    System.out.println("Rol: " + (usuarioActual != null ? usuarioActual.getNombreRol() : "NULL"));
    if (usuarioActual == null) return;

    String rol = usuarioActual.getNombreRol().trim().toUpperCase();
    lblRol.setText(usuarioActual.getNombreRol());
    lblUsuario.setText(usuarioActual.getNombre());

    // Ocultar todos primero
    btnDashboard.setVisible(false);
    btnUsuarios.setVisible(false);
    btnTorneos.setVisible(false);
    btnEquipos.setVisible(false);
    btnJugadores.setVisible(false);
    btnPartidos.setVisible(false);
    btnArbitros.setVisible(false);
    btnEstadisticas.setVisible(false);
    btnMiEquipo.setVisible(false);
    btnAlineacion.setVisible(false);
    btnResultados.setVisible(false);

    switch (rol) {
        case "ADMIN":
            btnUsuarios.setVisible(true);
            btnEquipos.setVisible(true);
            btnTorneos.setVisible(true);
            btnArbitros.setVisible(true);
            btnEstadisticas.setVisible(true);
            break;

        case "COACH":
            btnMiEquipo.setVisible(true);
            btnJugadores.setVisible(true);
            btnPartidos.setVisible(true);
            btnAlineacion.setVisible(true);
            break;

        case "ARBITRO":
            btnPartidos.setVisible(true);  // "Mis Partidos"
            btnResultados.setVisible(true);
            btnEstadisticas.setVisible(true);
            break;
    }
}

private void abrirVentana(javax.swing.JInternalFrame ventana) {
    // Verificar si ya está abierto
    for (javax.swing.JInternalFrame frame : desktopPane.getAllFrames()) {
        if (frame.getClass() == ventana.getClass()) {
            try { frame.setSelected(true); } catch (Exception e) {}
            return;
        }
    }
    // Tamaño cómodo, no maximizado
    ventana.setSize(800, 500);
    // Centrar dentro del desktopPane
    int x = (desktopPane.getWidth() - 800) / 2;
    int y = (desktopPane.getHeight() - 500) / 2;
    ventana.setLocation(Math.max(0, x), Math.max(0, y));
    ventana.setVisible(true);
    desktopPane.add(ventana);
    try { ventana.setSelected(true); } catch (Exception e) {}
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktopPane = new javax.swing.JDesktopPane();
        jPanelMenu = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblRol = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        btnDashboard = new javax.swing.JButton();
        btnUsuarios = new javax.swing.JButton();
        btnEquipos = new javax.swing.JButton();
        btnJugadores = new javax.swing.JButton();
        btnTorneos = new javax.swing.JButton();
        btnPartidos = new javax.swing.JButton();
        btnArbitros = new javax.swing.JButton();
        btnEstadisticas = new javax.swing.JButton();
        btnMiEquipo = new javax.swing.JButton();
        btnAlineacion = new javax.swing.JButton();
        btnResultados = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        desktopPane.setBackground(new java.awt.Color(30, 30, 30));
        desktopPane.setAlignmentX(220.0F);
        desktopPane.setAlignmentY(0.0F);

        jPanelMenu.setBackground(new java.awt.Color(51, 51, 51));

        jLabel1.setForeground(new java.awt.Color(255, 51, 0));
        jLabel1.setText("🏐");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 102, 0));
        jLabel2.setText("BKB");

        lblRol.setBackground(new java.awt.Color(0, 0, 0));
        lblRol.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblRol.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 51, 0), new java.awt.Color(255, 51, 0)));

        lblUsuario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblUsuario.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(255, 51, 0), new java.awt.Color(255, 51, 0)));

        btnDashboard.setBackground(new java.awt.Color(255, 102, 0));
        btnDashboard.setForeground(new java.awt.Color(255, 255, 255));
        btnDashboard.setText("🏠 Dashboard");
        btnDashboard.addActionListener(this::btnDashboardActionPerformed);

        btnUsuarios.setBackground(new java.awt.Color(255, 102, 0));
        btnUsuarios.setForeground(new java.awt.Color(255, 255, 255));
        btnUsuarios.setText("👤 Usuarios");
        btnUsuarios.addActionListener(this::btnUsuariosActionPerformed);

        btnEquipos.setBackground(new java.awt.Color(255, 102, 0));
        btnEquipos.setForeground(new java.awt.Color(255, 255, 255));
        btnEquipos.setText("👥 Equipos");
        btnEquipos.addActionListener(this::btnEquiposActionPerformed);

        btnJugadores.setBackground(new java.awt.Color(255, 102, 0));
        btnJugadores.setForeground(new java.awt.Color(255, 255, 255));
        btnJugadores.setText("⛹ Jugadores");
        btnJugadores.addActionListener(this::btnJugadoresActionPerformed);

        btnTorneos.setBackground(new java.awt.Color(255, 102, 0));
        btnTorneos.setForeground(new java.awt.Color(255, 255, 255));
        btnTorneos.setText("🏆 Torneos");
        btnTorneos.addActionListener(this::btnTorneosActionPerformed);

        btnPartidos.setBackground(new java.awt.Color(255, 102, 0));
        btnPartidos.setForeground(new java.awt.Color(255, 255, 255));
        btnPartidos.setText("📅 Partidos");
        btnPartidos.addActionListener(this::btnPartidosActionPerformed);

        btnArbitros.setBackground(new java.awt.Color(255, 102, 0));
        btnArbitros.setForeground(new java.awt.Color(255, 255, 255));
        btnArbitros.setText("⚠️ Árbitros");
        btnArbitros.addActionListener(this::btnArbitrosActionPerformed);

        btnEstadisticas.setBackground(new java.awt.Color(255, 102, 0));
        btnEstadisticas.setForeground(new java.awt.Color(255, 255, 255));
        btnEstadisticas.setText("📊 Estadísticas");
        btnEstadisticas.addActionListener(this::btnEstadisticasActionPerformed);

        btnMiEquipo.setBackground(new java.awt.Color(255, 102, 0));
        btnMiEquipo.setForeground(new java.awt.Color(255, 255, 255));
        btnMiEquipo.setText("⛹️‍ Mi Equipo");
        btnMiEquipo.addActionListener(this::btnMiEquipoActionPerformed);

        btnAlineacion.setBackground(new java.awt.Color(255, 102, 0));
        btnAlineacion.setForeground(new java.awt.Color(255, 255, 255));
        btnAlineacion.setText("📋 Alineación");
        btnAlineacion.addActionListener(this::btnAlineacionActionPerformed);

        btnResultados.setBackground(new java.awt.Color(255, 102, 0));
        btnResultados.setForeground(new java.awt.Color(255, 255, 255));
        btnResultados.setText("✅ Resultados");
        btnResultados.addActionListener(this::btnResultadosActionPerformed);

        btnCerrarSesion.setBackground(new java.awt.Color(255, 102, 0));
        btnCerrarSesion.setForeground(new java.awt.Color(255, 255, 255));
        btnCerrarSesion.setText("🚪 Cerrar Sesión");
        btnCerrarSesion.addActionListener(this::btnCerrarSesionActionPerformed);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 102, 0));
        jLabel3.setText("MANAGER");

        javax.swing.GroupLayout jPanelMenuLayout = new javax.swing.GroupLayout(jPanelMenu);
        jPanelMenu.setLayout(jPanelMenuLayout);
        jPanelMenuLayout.setHorizontalGroup(
            jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMenuLayout.createSequentialGroup()
                .addGroup(jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEquipos, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnJugadores, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTorneos, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPartidos, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnArbitros, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMiEquipo, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlineacion, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelMenuLayout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addGroup(jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanelMenuLayout.createSequentialGroup()
                                    .addGap(16, 16, 16)
                                    .addGroup(jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMenuLayout.createSequentialGroup()
                                                .addComponent(lblRol, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(24, 24, 24))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMenuLayout.createSequentialGroup()
                                                .addGroup(jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(54, 54, 54)))))))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMenuLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(btnEstadisticas, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnCerrarSesion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnResultados, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanelMenuLayout.setVerticalGroup(
            jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMenuLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(lblRol, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUsuarios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEquipos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnJugadores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTorneos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPartidos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnArbitros)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEstadisticas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMiEquipo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAlineacion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnResultados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        desktopPane.add(jPanelMenu);
        jPanelMenu.setBounds(0, 0, 138, 590);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPane, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        // Solo cerrar todos los InternalFrames abiertos
    for (javax.swing.JInternalFrame frame : desktopPane.getAllFrames()) {
        frame.dispose();
    }
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuariosActionPerformed
        IFUsuarios usuarios = new IFUsuarios();

    abrirVentana(usuarios);
    }//GEN-LAST:event_btnUsuariosActionPerformed

    private void btnEquiposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEquiposActionPerformed
        IFEquipos equipos = new IFEquipos();

    abrirVentana(equipos);
    }//GEN-LAST:event_btnEquiposActionPerformed

    private void btnJugadoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJugadoresActionPerformed
        IFJugadores jugadores = new IFJugadores();

    abrirVentana(jugadores);
    }//GEN-LAST:event_btnJugadoresActionPerformed

    private void btnTorneosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTorneosActionPerformed
        IFTorneos torneos = new IFTorneos();

    abrirVentana(torneos);
    }//GEN-LAST:event_btnTorneosActionPerformed

    private void btnPartidosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPartidosActionPerformed
        modelo.Usuario usuarioActual = Sesion.getInstancia().getUsuario();
        boolean esCoach = usuarioActual != null
                && usuarioActual.getNombreRol() != null
                && "COACH".equalsIgnoreCase(usuarioActual.getNombreRol().trim());

        javax.swing.JInternalFrame partidos = esCoach ? new IFPartidosCoach() : new IFPartidos();
        abrirVentana(partidos);

    }//GEN-LAST:event_btnPartidosActionPerformed

    private void btnArbitrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArbitrosActionPerformed
        IFArbitros arbitros = new IFArbitros();

    abrirVentana(arbitros);
    }//GEN-LAST:event_btnArbitrosActionPerformed

    private void btnEstadisticasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstadisticasActionPerformed
        IFINFORMES estadisticas = new IFINFORMES();

    abrirVentana(estadisticas);
    }//GEN-LAST:event_btnEstadisticasActionPerformed

    private void btnMiEquipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMiEquipoActionPerformed
        IFMiEquipo equipo = new IFMiEquipo();

    abrirVentana(equipo);
    }//GEN-LAST:event_btnMiEquipoActionPerformed

    private void btnAlineacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlineacionActionPerformed
        IFAlineacion alineacion = new IFAlineacion();

    abrirVentana(alineacion);
    }//GEN-LAST:event_btnAlineacionActionPerformed

    private void btnResultadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResultadosActionPerformed
        IFResultados resultados = new IFResultados();

    abrirVentana(resultados);
    }//GEN-LAST:event_btnResultadosActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
         dispose();

    Login_BKB login = new Login_BKB();

    login.setVisible(true);
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MDI_Principal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlineacion;
    private javax.swing.JButton btnArbitros;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnEquipos;
    private javax.swing.JButton btnEstadisticas;
    private javax.swing.JButton btnJugadores;
    private javax.swing.JButton btnMiEquipo;
    private javax.swing.JButton btnPartidos;
    private javax.swing.JButton btnResultados;
    private javax.swing.JButton btnTorneos;
    private javax.swing.JButton btnUsuarios;
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanelMenu;
    private javax.swing.JLabel lblRol;
    private javax.swing.JLabel lblUsuario;
    // End of variables declaration//GEN-END:variables
}
