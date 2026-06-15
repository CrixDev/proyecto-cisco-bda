/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presentacion;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

import dto.SesionDTO;
import dto.UbicacionDTO;
import negocio.IBloqueoNegocio;
import negocio.NegocioException;

/**
 * Interfaz del Programa 1 - Bloqueador de PC (uso del alumno).
 *
 * Flujo (4 pasos, según el diagrama del proyecto):
 *   1. Identificar ubicación (N° de equipo y nombre del centro).
 *   2. Mostrar el nombre del alumno con apartado activo (30 seg) - Verificar Apartado.
 *   3. Desbloquear con la contraseña del alumno.
 *   4. Liberar apartado (regresa al paso 1 - pantalla bloqueada).
 * 
 * @author Dylan
 */

public class Interfaz extends JFrame {

    // ── Colores del diseño (mismos del Programa 2 para mantener identidad visual) ──
    static final Color AZUL = new Color(14, 165, 233);
    static final Color AZUL_HOVER = new Color(2, 132, 199);
    static final Color VERDE = new Color(34, 197, 94);
    static final Color NARANJA = new Color(249, 115, 22);
    static final Color ROJO = new Color(239, 68, 68);
    static final Color ROJO_HOVER = new Color(220, 38, 38);
    static final Color FONDO = new Color(240, 242, 245);
    static final Color BLANCO = Color.WHITE;
    static final Color TEXTO_DARK = new Color(30, 41, 59);
    static final Color TEXTO_MED = new Color(100, 116, 139);
    static final Color TEXTO_LIGHT = new Color(148, 163, 184);
    static final Color BORDE = new Color(226, 232, 240);
    static final Color ROJO_LIGHT = new Color(254, 226, 226);
    static final Color VERDE_LIGHT = new Color(220, 252, 231);
    static final Color FONDO_SEC = new Color(241, 245, 249);

    // ── Servicio de negocio ──
    private final IBloqueoNegocio bloqueoNegocio;

    // ── Estado dinámico ──
    private int paso = 1;
    private UbicacionDTO ubicacionDTO = null;
    private SesionDTO sesionDTO = null;
    private boolean cargandoUbicacion = false;
    private boolean verificandoApartado = false;
    private boolean apartadoNoEncontrado = false;
    private javax.swing.Timer countdownTimer;
    private int countdown = 30;

    // ── Panel principal ──
    private final JPanel cardPanel;

    public Interfaz(IBloqueoNegocio bloqueoNegocio) {
        this.bloqueoNegocio = bloqueoNegocio;

        setTitle("Bloqueador de PC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 500);
        setMinimumSize(new Dimension(600, 440));
        setLocationRelativeTo(null);
        getContentPane().setBackground(FONDO);
        setLayout(new GridBagLayout());

        cardPanel = new RoundedPanel(16, BLANCO);
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(580, 420));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        add(cardPanel, gbc);

        mostrarPaso(1);
        setVisible(true);
    }

    // ══════════════════════════════════════════
    //  RENDER PRINCIPAL
    // ══════════════════════════════════════════
    void mostrarPaso(int p) {
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }

        this.paso = p;
        cardPanel.removeAll();

        JPanel inner = new JPanel(new BorderLayout(0, 0));
        inner.setBackground(BLANCO);
        inner.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel sysLabel = new JLabel("BLOQUEADOR DE PC");
        sysLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        sysLabel.setForeground(TEXTO_LIGHT);
        sysLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel stepperPanel = buildStepper(p);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BLANCO);
        topPanel.add(sysLabel);
        topPanel.add(stepperPanel);
        topPanel.add(Box.createVerticalStrut(8));

        inner.add(topPanel, BorderLayout.NORTH);

        JPanel content;
        switch (p) {
            case 1:
                content = buildPaso1();
                break;
            case 2:
                content = buildPaso2();
                break;
            case 3:
                content = buildPaso3();
                break;
            default:
                content = buildPaso4();
                break;
        }

        inner.add(content, BorderLayout.CENTER);
        cardPanel.add(inner, BorderLayout.CENTER);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // ══════════════════════════════════════════
    //  STEPPER DINÁMICO (4 PASOS)
    // ══════════════════════════════════════════
    JPanel buildStepper(int active) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BLANCO);
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.anchor = GridBagConstraints.CENTER;

        String[] labels = {"Ubicación", "Apartado", "Desbloqueo", "En uso"};
        for (int i = 1; i <= 4; i++) {
            boolean done = (i < active);
            boolean cur = (i == active);

            JLabel circle = new JLabel(done ? "✓" : String.valueOf(i), SwingConstants.CENTER);
            circle.setPreferredSize(new Dimension(24, 24));
            circle.setFont(new Font("SansSerif", Font.BOLD, 11));
            if (done) {
                circle.setBackground(VERDE);
                circle.setForeground(BLANCO);
            } else if (cur) {
                circle.setBackground(AZUL);
                circle.setForeground(BLANCO);
            } else {
                circle.setBackground(BORDE);
                circle.setForeground(TEXTO_LIGHT);
            }
            circle.setOpaque(true);
            circle = makeRound(circle, 12);

            JLabel txt = new JLabel(labels[i - 1]);
            txt.setFont(new Font("SansSerif", cur ? Font.BOLD : Font.PLAIN, 11));
            txt.setForeground(done ? VERDE : cur ? AZUL : TEXTO_LIGHT);

            JPanel stepItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            stepItem.setBackground(BLANCO);
            stepItem.add(circle);
            stepItem.add(txt);

            g.gridx = (i - 1) * 2;
            g.weightx = 0;
            p.add(stepItem, g);

            if (i < 4) {
                JSeparator line = new JSeparator(JSeparator.HORIZONTAL);
                line.setPreferredSize(new Dimension(30, 2));
                line.setForeground(done ? VERDE : BORDE);
                line.setBackground(done ? VERDE : BORDE);
                g.gridx = (i - 1) * 2 + 1;
                g.weightx = 1;
                g.fill = GridBagConstraints.HORIZONTAL;
                p.add(line, g);
                g.fill = GridBagConstraints.NONE;
                g.weightx = 0;
            }
        }
        return p;
    }

    JLabel makeRound(JLabel lbl, int radius) {
        Color bgColor = lbl.getBackground();
        Color fgColor = lbl.getForeground();
        Font font = lbl.getFont();
        String texto = lbl.getText();

        JLabel result = new JLabel(texto, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
                g2.setColor(fgColor);
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        result.setPreferredSize(new Dimension(24, 24));
        result.setFont(font);
        result.setOpaque(false);
        return result;
    }

    // ══════════════════════════════════════════
    //  PASO 1 — IDENTIFICAR UBICACIÓN
    // ══════════════════════════════════════════
    JPanel buildPaso1() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BLANCO);
        p.add(Box.createVerticalGlue());

        if (ubicacionDTO == null) {
            // Cargando...
            JLabel icono = bigIcon("📍", ROJO);
            JLabel titulo = centerLabel("Identificando ubicación...", 18, Font.BOLD, TEXTO_DARK);
            JLabel sub = centerLabel("Consultando el número de equipo y el centro asignado.", 13, Font.PLAIN, TEXTO_MED);

            p.add(icono);
            p.add(Box.createVerticalStrut(12));
            p.add(titulo);
            p.add(Box.createVerticalStrut(4));
            p.add(sub);

            if (!cargandoUbicacion) {
                cargandoUbicacion = true;
                SwingWorker<UbicacionDTO, Void> worker = new SwingWorker<>() {
                    String errorMsg = null;

                    @Override
                    protected UbicacionDTO doInBackground() {
                        try {
                            return bloqueoNegocio.identificarUbicacion();
                        } catch (NegocioException ex) {
                            errorMsg = ex.getMessage();
                            return null;
                        }
                    }

                    @Override
                    protected void done() {
                        cargandoUbicacion = false;
                        try {
                            UbicacionDTO resultado = get();
                            if (resultado != null) {
                                ubicacionDTO = resultado;
                                mostrarPaso(1);
                            } else {
                                JOptionPane.showMessageDialog(Interfaz.this,
                                        errorMsg != null ? errorMsg : "Error desconocido al identificar el equipo.",
                                        "Error de conexión", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(Interfaz.this,
                                    "Error de conexión con la base de datos.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
            }
        } else {
            // Ubicación identificada
            JLabel icono = bigIcon("🔒", ROJO);
            JLabel titulo = centerLabel("PC Bloqueada", 22, Font.BOLD, TEXTO_DARK);

            JPanel resumen = new JPanel();
            resumen.setLayout(new BoxLayout(resumen, BoxLayout.Y_AXIS));
            resumen.setBackground(FONDO_SEC);
            resumen.setBorder(new EmptyBorder(14, 18, 14, 18));
            resumen.setMaximumSize(new Dimension(320, 90));
            resumen.setAlignmentX(Component.CENTER_ALIGNMENT);
            resumen = roundedWrap(resumen, FONDO_SEC, 12);

            addSummaryRow(resumen, "N° de equipo:", "Equipo " + ubicacionDTO.getNumeroMaquina());
            addSummaryRow(resumen, "Centro:", ubicacionDTO.getNombreCentro());
            addSummaryRow(resumen, "Laboratorio:", ubicacionDTO.getNombreLaboratorio());

            JLabel sub = centerLabel("Verificando si este equipo tiene un apartado activo...", 12, Font.PLAIN, TEXTO_MED);

            p.add(icono);
            p.add(Box.createVerticalStrut(10));
            p.add(titulo);
            p.add(Box.createVerticalStrut(14));
            p.add(resumen);
            p.add(Box.createVerticalStrut(14));
            p.add(sub);

            // Avanza automáticamente al paso 2 a los 1.8s
            javax.swing.Timer avance = new javax.swing.Timer(1800, e -> mostrarPaso(2));
            avance.setRepeats(false);
            avance.start();
        }

        p.add(Box.createVerticalGlue());
        return p;
    }

    // ══════════════════════════════════════════
    //  PASO 2 — VERIFICAR APARTADO / MOSTRAR ALUMNO (30s)
    // ══════════════════════════════════════════
    JPanel buildPaso2() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BLANCO);
        p.add(Box.createVerticalGlue());

        if (sesionDTO == null && !verificandoApartado && !apartadoNoEncontrado) {
            verificandoApartado = true;
            SwingWorker<SesionDTO, Void> worker = new SwingWorker<>() {
                volatile String errorMsg = null;

                @Override
                protected SesionDTO doInBackground() {
                    try {
                        return bloqueoNegocio.verificarApartado(ubicacionDTO.getIdComputadora());
                    } catch (NegocioException ex) {
                        errorMsg = ex.getMessage();
                        return null;
                    }
                }

                @Override
                protected void done() {
                    verificandoApartado = false;
                    try {
                        sesionDTO = get();
                        apartadoNoEncontrado = (sesionDTO == null);
                        if (sesionDTO == null && errorMsg != null) {
                            JOptionPane.showMessageDialog(Interfaz.this, errorMsg,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (java.util.concurrent.ExecutionException ex) {
                        sesionDTO = null;
                        apartadoNoEncontrado = true;
                        Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                        JOptionPane.showMessageDialog(Interfaz.this,
                                "Error inesperado al verificar el apartado:\n" + causa.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        sesionDTO = null;
                        apartadoNoEncontrado = true;
                    }
                    mostrarPaso(2);
                }
            };
            worker.execute();
        }

        if (verificandoApartado || sesionDTO == null) {
            JLabel icono = bigIcon("🔎", AZUL);
            p.add(icono);
            p.add(Box.createVerticalStrut(12));

            if (verificandoApartado) {
                p.add(centerLabel("Verificando apartado...", 18, Font.BOLD, TEXTO_DARK));
                p.add(Box.createVerticalStrut(4));
                p.add(centerLabel("Equipo " + (ubicacionDTO != null ? ubicacionDTO.getNumeroMaquina() : "--"), 13, Font.PLAIN, TEXTO_MED));
            } else {
                p.add(centerLabel("Sin apartado activo", 18, Font.BOLD, TEXTO_DARK));
                p.add(Box.createVerticalStrut(4));
                p.add(centerLabel("Este equipo no tiene un apartado registrado.", 13, Font.PLAIN, TEXTO_MED));
                p.add(centerLabel("Realiza tu apartado en el Programa 2 y vuelve a intentarlo.", 13, Font.PLAIN, TEXTO_MED));
                p.add(Box.createVerticalStrut(18));

                JButton reintentar = roundButtonOutline("Reintentar");
                reintentar.setAlignmentX(Component.CENTER_ALIGNMENT);
                reintentar.addActionListener(e -> {
                    apartadoNoEncontrado = false;
                    sesionDTO = null;
                    mostrarPaso(2);
                });
                p.add(reintentar);
            }
        } else {
            // Apartado encontrado: mostrar nombre del alumno con cuenta atrás
            countdown = 30;

            JLabel icono = bigIcon("👤", AZUL);
            JLabel titulo = centerLabel("Equipo reservado para:", 14, Font.PLAIN, TEXTO_MED);
            JLabel nombre = centerLabel(sesionDTO.getNombreCompleto(), 24, Font.BOLD, TEXTO_DARK);

            JLabel cuenta = centerLabel("Accede en " + countdown + " s o presiona Continuar", 13, Font.PLAIN, TEXTO_LIGHT);

            JButton continuar = roundButton("Continuar →", AZUL, BLANCO);
            continuar.setAlignmentX(Component.CENTER_ALIGNMENT);
            continuar.addActionListener(e -> {
                if (countdownTimer != null) {
                    countdownTimer.stop();
                    countdownTimer = null;
                }
                mostrarPaso(3);
            });

            p.add(icono);
            p.add(Box.createVerticalStrut(8));
            p.add(titulo);
            p.add(Box.createVerticalStrut(4));
            p.add(nombre);
            p.add(Box.createVerticalStrut(10));
            p.add(cuenta);
            p.add(Box.createVerticalStrut(16));
            p.add(continuar);

            countdownTimer = new javax.swing.Timer(1000, e -> {
                countdown--;
                if (countdown <= 0) {
                    countdownTimer.stop();
                    mostrarPaso(3);
                } else {
                    cuenta.setText("Accede en " + countdown + " s o presiona Continuar");
                }
            });
            countdownTimer.start();
        }

        p.add(Box.createVerticalGlue());
        return p;
    }

    // ══════════════════════════════════════════
    //  PASO 3 — DESBLOQUEAR CON CONTRASEÑA DEL ALUMNO
    // ══════════════════════════════════════════
    JPanel buildPaso3() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BLANCO);

        JLabel icono = bigIcon("🔒", ROJO);
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Desbloquear equipo");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXTO_DARK);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("<html>Hola, " + (sesionDTO != null ? sesionDTO.getNombreCompleto() : "alumno")
                + ". Ingresa tu contraseña para desbloquear este equipo.<br>"
                + "Si eres personal del laboratorio, usa la <b>contraseña maestra</b>.</html>");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(TEXTO_MED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fieldLabel = new JLabel("CONTRASEÑA");
        fieldLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        fieldLabel.setForeground(TEXTO_MED);
        fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passField.setForeground(TEXTO_DARK);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passField.setAlignmentX(Component.LEFT_ALIGNMENT);
        styleBorderField(passField, AZUL);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(ROJO);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BLANCO);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JButton maestra = roundButton("Soy personal 🔑", NARANJA, BLANCO);
        JButton desbloquear = roundButton("Desbloquear 🔓", VERDE, BLANCO);

        // Desbloqueo del alumno (Enter en el campo y botón verde)
        ActionListener accionAlumno = e ->
                procesarDesbloqueo(false, new String(passField.getPassword()).trim(),
                        desbloquear, maestra, passField, errorLabel);
        // Desbloqueo del personal con contraseña maestra
        ActionListener accionMaestra = e ->
                procesarDesbloqueo(true, new String(passField.getPassword()).trim(),
                        desbloquear, maestra, passField, errorLabel);

        desbloquear.addActionListener(accionAlumno);
        passField.addActionListener(accionAlumno);
        maestra.addActionListener(accionMaestra);

        footer.add(maestra, BorderLayout.WEST);
        footer.add(desbloquear, BorderLayout.EAST);

        p.add(icono);
        p.add(Box.createVerticalStrut(10));
        p.add(titulo);
        p.add(Box.createVerticalStrut(6));
        p.add(sub);
        p.add(Box.createVerticalStrut(20));
        p.add(fieldLabel);
        p.add(Box.createVerticalStrut(6));
        p.add(passField);
        p.add(Box.createVerticalStrut(6));
        p.add(errorLabel);
        p.add(Box.createVerticalGlue());
        p.add(footer);

        return p;
    }

    /**
     * Valida la contraseña (de alumno o maestra) y, si es correcta, pasa al Paso 4.
     */
    private void procesarDesbloqueo(boolean maestra, String pass, JButton btnAlumno, JButton btnMaestra,
            JPasswordField passField, JLabel errorLabel) {
        if (pass.isEmpty()) {
            styleBorderField(passField, ROJO);
            errorLabel.setText(maestra ? "Ingresa la contraseña maestra." : "Ingresa tu contraseña.");
            return;
        }

        btnAlumno.setEnabled(false);
        btnMaestra.setEnabled(false);
        final JButton activo = maestra ? btnMaestra : btnAlumno;
        final String textoOriginal = activo.getText();
        activo.setText("Verificando...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String errorMsg = null;

            @Override
            protected Boolean doInBackground() {
                try {
                    if (maestra) {
                        return bloqueoNegocio.validarContrasenaMaestra(ubicacionDTO.getIdLaboratorio(), pass);
                    }
                    return bloqueoNegocio.validarContrasena(sesionDTO.getIdAlumno(), pass);
                } catch (NegocioException ex) {
                    errorMsg = ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                boolean correcto;
                try {
                    correcto = get();
                } catch (Exception ex) {
                    correcto = false;
                }

                if (correcto) {
                    mostrarPaso(4);
                } else {
                    btnAlumno.setEnabled(true);
                    btnMaestra.setEnabled(true);
                    activo.setText(textoOriginal);
                    styleBorderField(passField, ROJO);
                    errorLabel.setText(errorMsg != null ? errorMsg
                            : (maestra ? "Contraseña maestra incorrecta." : "Contraseña incorrecta. Intenta de nuevo."));
                    passField.setText("");
                }
            }
        };
        worker.execute();
    }

    // ══════════════════════════════════════════
    //  PASO 4 — LIBERAR APARTADO
    // ══════════════════════════════════════════
    JPanel buildPaso4() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BLANCO);
        p.add(Box.createVerticalGlue());

        JLabel icono = bigIcon("✅", VERDE);
        JLabel titulo = centerLabel("Equipo desbloqueado", 22, Font.BOLD, TEXTO_DARK);
        JLabel nombre = centerLabel("Sesión activa: " + (sesionDTO != null ? sesionDTO.getNombreCompleto() : "—"), 14, Font.BOLD, AZUL);

        JLabel info = centerLabel("Cuando termines de usar el equipo, presiona", 13, Font.PLAIN, TEXTO_MED);
        JLabel info2 = centerLabel("\"Liberar Apartado\" para bloquear la PC y dejarla disponible.", 13, Font.PLAIN, TEXTO_MED);

        JButton liberar = roundButton("Liberar Apartado", ROJO, BLANCO);
        liberar.setAlignmentX(Component.CENTER_ALIGNMENT);
        liberar.setPreferredSize(new Dimension(200, 42));

        liberar.addActionListener(e -> {
            liberar.setEnabled(false);
            liberar.setText("Liberando...");

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                String errorMsg = null;

                @Override
                protected Void doInBackground() {
                    try {
                        bloqueoNegocio.liberarApartado(ubicacionDTO.getIdComputadora());
                    } catch (NegocioException ex) {
                        errorMsg = ex.getMessage();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    if (errorMsg != null) {
                        liberar.setEnabled(true);
                        liberar.setText("Liberar Apartado");
                        JOptionPane.showMessageDialog(Interfaz.this, errorMsg,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Reinicia el ciclo: la PC vuelve a quedar bloqueada (Paso 1)
                        sesionDTO = null;
                        apartadoNoEncontrado = false;
                        mostrarPaso(1);
                    }
                }
            };
            worker.execute();
        });

        p.add(icono);
        p.add(Box.createVerticalStrut(8));
        p.add(titulo);
        p.add(Box.createVerticalStrut(4));
        p.add(nombre);
        p.add(Box.createVerticalStrut(14));
        p.add(info);
        p.add(info2);
        p.add(Box.createVerticalStrut(22));
        p.add(liberar);

        p.add(Box.createVerticalGlue());
        return p;
    }

    // ══════════════════════════════════════════
    //  HELPERS DE UI
    // ══════════════════════════════════════════
    JLabel bigIcon(String emoji, Color color) {
        JLabel lbl = new JLabel(emoji, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 48));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setForeground(color);
        return lbl;
    }

    JLabel centerLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", style, size));
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    void addSummaryRow(JPanel box, String key, String val) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(FONDO_SEC);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel k = new JLabel(key);
        k.setFont(new Font("SansSerif", Font.PLAIN, 12));
        k.setForeground(TEXTO_MED);
        JLabel v = new JLabel(val);
        v.setFont(new Font("SansSerif", Font.BOLD, 12));
        v.setForeground(TEXTO_DARK);
        row.add(k, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        box.add(row);
        box.add(Box.createVerticalStrut(4));
    }

    JPanel roundedWrap(JPanel content, Color bg, int radius) {
        RoundedPanel wrap = new RoundedPanel(radius, bg) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
                g2.dispose();
            }
        };
        wrap.setLayout(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(content, BorderLayout.CENTER);
        wrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        return wrap;
    }

    JButton roundButton(String text, Color bg, Color fg) {
        Color hover = bg.equals(ROJO) ? ROJO_HOVER : bg.equals(AZUL) ? AZUL_HOVER : bg.darker();
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    JButton roundButtonOutline(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? FONDO_SEC : BLANCO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(TEXTO_DARK);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    void styleBorderField(JTextField f, Color c) {
        f.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(c, 10, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    static class RoundedPanel extends JPanel {

        int radius;
        Color bg;

        RoundedPanel(int r, Color bg) {
            this.radius = r;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 2, radius * 2, radius * 2);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
            g2.dispose();
        }
    }

    static class RoundedBorder extends AbstractBorder {

        Color color;
        int radius, thickness;

        RoundedBorder(Color c, int r, int t) {
            color = c;
            radius = r;
            thickness = t;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius * 2, radius * 2);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
    }
}
