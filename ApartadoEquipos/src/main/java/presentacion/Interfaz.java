/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presentacion;

import dto.AlumnoDTO;
import dto.ApartadoResultadoDTO;
import dto.ComputadoraDTO;
import negocio.*;
import persistencia.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Interfaz extends JFrame {

    // ── Colores del diseño ──
    static final Color AZUL        = new Color(14, 165, 233);
    static final Color AZUL_HOVER  = new Color(2, 132, 199);
    static final Color VERDE       = new Color(34, 197, 94);
    static final Color NARANJA     = new Color(249, 115, 22);
    static final Color AMARILLO    = new Color(245, 158, 11);
    static final Color FONDO       = new Color(240, 242, 245);
    static final Color BLANCO      = Color.WHITE;
    static final Color TEXTO_DARK  = new Color(30, 41, 59);
    static final Color TEXTO_MED   = new Color(100, 116, 139);
    static final Color TEXTO_LIGHT = new Color(148, 163, 184);
    static final Color BORDE       = new Color(226, 232, 240);
    static final Color AZUL_LIGHT  = new Color(240, 249, 255);
    static final Color VERDE_LIGHT = new Color(220, 252, 231);
    static final Color FONDO_SEC   = new Color(241, 245, 249);

    // ── Servicios del Sistema ──
    private final IAlumnoNegocio alumnoNegocio;
    private final IApartadoNegocio apartadoNegocio;

    // ── Estado Dinámico ──
    private int paso = 1;
    private AlumnoDTO alumnoDTO = null;
    private ComputadoraDTO pcSeleccionada = null;
    private Timer timer;
    private int countdown = 30;

    // ── Panel principal ──
    private final JPanel cardPanel;

    public Interfaz(IAlumnoNegocio alumnoNegocio, IApartadoNegocio apartadoNegocio) {
        this.alumnoNegocio = alumnoNegocio;
        this.apartadoNegocio = apartadoNegocio;

        setTitle("ITSON – Sistema de Apartado de Equipos");
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

    void mostrarPaso(int p) {
        this.paso = p;
        cardPanel.removeAll();

        JPanel inner = new JPanel(new BorderLayout(0, 0));
        inner.setBackground(BLANCO);
        inner.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel sysLabel = new JLabel("SISTEMA DE APARTADO");
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
            case 1:  content = buildPaso1(); break;
            case 2:  content = buildPaso2(); break;
            case 3:  content = buildPaso3(); break;
            default: content = buildPaso4(); break;
        }

        inner.add(content, BorderLayout.CENTER);
        cardPanel.add(inner, BorderLayout.CENTER);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // ══════════════════════════════════════════
    //  STEPPER DINÁMICO
    // ══════════════════════════════════════════
    JPanel buildStepper(int active) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BLANCO);
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0; g.anchor = GridBagConstraints.CENTER;

        String[] labels = {"Identificación", "Selección", "Confirmación"};
        for (int i = 1; i <= 3; i++) {
            boolean done = (active == 4 || i < active);
            boolean cur  = (active != 4 && i == active);

            JLabel circle = new JLabel(done ? "✓" : String.valueOf(i), SwingConstants.CENTER);
            circle.setPreferredSize(new Dimension(26, 26));
            circle.setFont(new Font("SansSerif", Font.BOLD, 11));
            if (done)     { circle.setBackground(VERDE); circle.setForeground(BLANCO); }
            else if (cur) { circle.setBackground(AZUL);  circle.setForeground(BLANCO); }
            else          { circle.setBackground(BORDE);  circle.setForeground(TEXTO_LIGHT); }
            circle.setOpaque(true);
            circle = makeRound(circle, 13);

            JLabel txt = new JLabel(labels[i - 1]);
            txt.setFont(new Font("SansSerif", cur ? Font.BOLD : Font.PLAIN, 12));
            txt.setForeground(done ? VERDE : cur ? AZUL : TEXTO_LIGHT);

            JPanel stepItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            stepItem.setBackground(BLANCO);
            stepItem.add(circle);
            stepItem.add(txt);

            g.gridx = (i - 1) * 2; g.weightx = 0;
            p.add(stepItem, g);

            if (i < 3) {
                JSeparator line = new JSeparator(JSeparator.HORIZONTAL);
                line.setPreferredSize(new Dimension(60, 2));
                line.setForeground((done) ? VERDE : BORDE);
                line.setBackground((done) ? VERDE : BORDE);
                g.gridx = (i - 1) * 2 + 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
                p.add(line, g);
                g.fill = GridBagConstraints.NONE; g.weightx = 0;
            }
        }
        return p;
    }

    JLabel makeRound(JLabel lbl, int radius) {
        Color bgColor = lbl.getBackground();
        Color fgColor = lbl.getForeground();
        Font  font    = lbl.getFont();
        String texto  = lbl.getText();

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
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        result.setPreferredSize(new Dimension(26, 26));
        result.setFont(font);
        result.setOpaque(false);
        return result;
    }

    // ══════════════════════════════════════════
    //  PASO 1 — IDENTIFICACIÓN (CONEXIÓN BD REAL)
    // ══════════════════════════════════════════
    JPanel buildPaso1() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BLANCO);

        JLabel titulo = new JLabel("Apartar equipo");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(TEXTO_DARK);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Ingresa tu ID de alumno para verificar inscripción, bloqueos y tiempo restante.");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(TEXTO_MED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fieldLabel = new JLabel("ID ALUMNO (NÚMERO DE CONTROL)");
        fieldLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        fieldLabel.setForeground(TEXTO_MED);
        fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField idField = new JTextField(alumnoDTO != null ? alumnoDTO.getNumeroControl() : "");
        idField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        idField.setForeground(TEXTO_DARK);
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        idField.setAlignmentX(Component.LEFT_ALIGNMENT);
        styleBorderField(idField, AZUL);

        // Placeholder lógico
        if (idField.getText().isEmpty()) {
            idField.setForeground(TEXTO_LIGHT);
            idField.setText("Ej. 1");
            idField.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (idField.getText().equals("Ej. 1")) {
                        idField.setText(""); idField.setForeground(TEXTO_DARK);
                    }
                }
                public void focusLost(FocusEvent e) {
                    if (idField.getText().isEmpty()) {
                        idField.setText("Ej. 1"); idField.setForeground(TEXTO_LIGHT);
                    }
                }
            });
        }

        JLabel errorLabel = new JLabel("");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(239, 68, 68));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setVisible(false);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BLANCO);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel hint = new JLabel("Paso 1 de 3");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hint.setForeground(TEXTO_LIGHT);

        JButton continuar = roundButton("Continuar →", AZUL, BLANCO);
        
        continuar.addActionListener(e -> {
            String val = idField.getText().trim();
            if (val.isEmpty() || val.equals("Ej. 1")) {
                styleBorderField(idField, new Color(239, 68, 68));
                errorLabel.setText("Ingresa un número de control válido.");
                errorLabel.setVisible(true);
                return;
            }

            continuar.setEnabled(false);
            continuar.setText("Verificando...");

            // Trabajador asíncrono para no congelar la pantalla al ir a MySQL
            SwingWorker<AlumnoDTO, Void> worker = new SwingWorker<>() {
                String errorMsg = null;

                @Override
                protected AlumnoDTO doInBackground() {
                    try {
                        return alumnoNegocio.identificarAlumno(val);
                    } catch (NegocioException ex) {
                        errorMsg = ex.getMessage();
                        return null;
                    }
                }

                @Override
                protected void done() {
                    continuar.setEnabled(true);
                    continuar.setText("Continuar →");
                    try {
                        AlumnoDTO result = get();
                        if (result == null) {
                            styleBorderField(idField, new Color(239, 68, 68));
                            errorLabel.setText("<html>" + errorMsg.replaceAll("\n", "<br>") + "</html>");
                            errorLabel.setVisible(true);
                        } else {
                            alumnoDTO = result;
                            mostrarPaso(2);
                        }
                    } catch (Exception ex) {
                        errorLabel.setText("Error crítico de conexión.");
                        errorLabel.setVisible(true);
                    }
                }
            };
            worker.execute();
        });

        footer.add(hint, BorderLayout.WEST);
        footer.add(continuar, BorderLayout.EAST);

        p.add(titulo); p.add(Box.createVerticalStrut(4));
        p.add(sub); p.add(Box.createVerticalStrut(18));
        p.add(fieldLabel); p.add(Box.createVerticalStrut(5));
        p.add(idField); p.add(Box.createVerticalStrut(4));
        p.add(errorLabel); p.add(Box.createVerticalGlue());
        p.add(footer);
        return p;
    }

    // ══════════════════════════════════════════
    //  PASO 2 — GRID DE COMPUTADORAS DESDE LA BD
    // ══════════════════════════════════════════
    JPanel buildPaso2() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BLANCO);

        JLabel titulo = new JLabel("Equipos disponibles");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(TEXTO_DARK);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Tiempo restante de " + alumnoDTO.getNombreCompleto() + ": " + alumnoDTO.getTiempoRestanteMinutos() + " min.");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(TEXTO_MED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel gridContainer = new JPanel(new GridLayout(0, 3, 10, 10));
        gridContainer.setBackground(BLANCO);
        gridContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel cargando = new JLabel("Consultando disponibilidad de red en tiempo real...", SwingConstants.CENTER);
        cargando.setAlignmentX(Component.CENTER_ALIGNMENT);
        gridContainer.add(cargando);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BLANCO);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton atras = roundButtonOutline("← Atrás");
        atras.addActionListener(e -> mostrarPaso(1));
        footer.add(atras, BorderLayout.WEST);

        p.add(titulo); p.add(Box.createVerticalStrut(3));
        p.add(sub); p.add(Box.createVerticalStrut(12));
        p.add(gridContainer); p.add(Box.createVerticalGlue());
        p.add(footer);

        // Hilo para descargar el estatus de las computadoras y sus IPs reales
        SwingWorker<List<ComputadoraDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ComputadoraDTO> doInBackground() throws Exception {
                return apartadoNegocio.obtenerComputadorasDelLaboratorio();
            }

            @Override
            protected void done() {
                gridContainer.removeAll();
                try {
                    List<ComputadoraDTO> pcs = get();
                    if (pcs.isEmpty()) {
                        gridContainer.add(new JLabel("No hay equipos vinculados a este laboratorio."));
                    } else {
                        for (ComputadoraDTO dto : pcs) {
                            gridContainer.add(buildPCCardDinamic(dto));
                        }
                    }
                } catch (Exception e) {
                    gridContainer.add(new JLabel("Error al leer la tabla Computadoras."));
                }
                gridContainer.revalidate();
                gridContainer.repaint();
            }
        };
        worker.execute();

        return p;
    }

    JPanel buildPCCardDinamic(ComputadoraDTO dto) {
        boolean disponible = dto.isLibre();
        boolean deshabilitada = "DESHABILITADA".equalsIgnoreCase(dto.getEstado());

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BLANCO);
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 6, 10, 6));

        Color iconColor = disponible ? AZUL : new Color(203, 213, 225);
        JLabel icon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconColor);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(2, 2, 26, 18, 3, 3);
                g2.drawLine(11, 20, 19, 20);
                g2.drawLine(15, 20, 15, 24);
                g2.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(30, 26));
        icon.setMaximumSize(new Dimension(30, 26));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nombre = new JLabel("Máquina " + dto.getNumeroMaquina(), SwingConstants.CENTER);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 12));
        nombre.setForeground(TEXTO_DARK);
        nombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Muestra el nombre del alumno que la tiene reservada o "Disponible" tal como exige el ITSON
        String displayAlumno = disponible ? "Disponible" : dto.getNombreAlumnoActual();
        JLabel alumnoLbl = new JLabel(displayAlumno, SwingConstants.CENTER);
        alumnoLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        alumnoLbl.setForeground(TEXTO_MED);
        alumnoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        String statusTxt = deshabilitada ? "Mantenimiento" : disponible ? "Libre" : "Ocupada";
        Color statusClr = deshabilitada ? NARANJA : disponible ? AZUL : AMARILLO;
        JLabel statusLbl = new JLabel(statusTxt, SwingConstants.CENTER);
        statusLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        statusLbl.setForeground(statusClr);
        statusLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(icon);
        card.add(Box.createVerticalStrut(4));
        card.add(nombre);
        card.add(Box.createVerticalStrut(2));
        card.add(alumnoLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(statusLbl);
        card.add(Box.createVerticalGlue());

        // Bloqueo de concurrencia: Solo permite hacer clic si la máquina está verdaderamente Libre
        if (disponible) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(AZUL_LIGHT);
                    card.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBackground(BLANCO);
                    card.repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    pcSeleccionada = dto;
                    mostrarPaso(3);
                }
            });
        }
        return card;
    }

    // ══════════════════════════════════════════
    //  PASO 3 — CONFIRMACIÓN Y RESUMEN REAL
    // ══════════════════════════════════════════
    JPanel buildPaso3() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BLANCO);

        JPanel pcHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pcHeader.setBackground(BLANCO);
        pcHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel numBox = new JLabel(pcSeleccionada.getNumeroMaquina(), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FONDO_SEC);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        numBox.setPreferredSize(new Dimension(48, 48));
        numBox.setFont(new Font("SansSerif", Font.BOLD, 18));
        numBox.setForeground(TEXTO_DARK);
        numBox.setOpaque(false);

        JPanel pcInfo = new JPanel();
        pcInfo.setLayout(new BoxLayout(pcInfo, BoxLayout.Y_AXIS));
        pcInfo.setBackground(BLANCO);
        JLabel pcTitle = new JLabel("Equipo #" + pcSeleccionada.getNumeroMaquina());
        pcTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        pcTitle.setForeground(TEXTO_DARK);
        JLabel pcLab = new JLabel("IP verificada: " + pcSeleccionada.getDireccionIp());
        pcLab.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pcLab.setForeground(TEXTO_MED);
        pcInfo.add(pcTitle); pcInfo.add(pcLab);

        pcHeader.add(numBox); pcHeader.add(pcInfo);

        JPanel cols = new JPanel(new GridLayout(1, 2, 12, 0));
        cols.setBackground(BLANCO);
        cols.setAlignmentX(Component.LEFT_ALIGNMENT);
        cols.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Carga de Software Dinámica de la BD
        JPanel swBox = infoBox("SOFTWARE INSTALADO");
        if (pcSeleccionada.getSoftwareInstalado() == null || pcSeleccionada.getSoftwareInstalado().isEmpty()) {
            swBox.add(new JLabel("No incluye software especializado."));
        } else {
            for (String s : pcSeleccionada.getSoftwareInstalado()) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
                row.setBackground(BLANCO);
                JLabel check = new JLabel("✓");
                check.setForeground(VERDE);
                check.setFont(new Font("SansSerif", Font.BOLD, 13));
                JLabel name = new JLabel(s);
                name.setFont(new Font("SansSerif", Font.PLAIN, 12));
                name.setForeground(TEXTO_DARK);
                row.add(check); row.add(name);
                swBox.add(row);
            }
        }

        JPanel resBox = infoBox("RESUMEN DE RESERVACIÓN");
        addSummaryRow(resBox, "Estudiante", alumnoDTO.getNombreCompleto());
        addSummaryRow(resBox, "Carrera", alumnoDTO.getCarrera());
        addSummaryRow(resBox, "Ubicación", "Laboratorio A");
        addSummaryRow(resBox, "Tiempo Máx", "2 horas");

        cols.add(swBox); cols.add(resBox);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BLANCO);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton confirmar = roundButton("Confirmar apartado", AZUL, BLANCO);
        confirmar.addActionListener(e -> {
            confirmar.setEnabled(false);
            confirmar.setText("Persistiendo...");

            SwingWorker<ApartadoResultadoDTO, Void> transaccion = new SwingWorker<>() {
                String errorMsg = null;

                @Override
                protected ApartadoResultadoDTO doInBackground() {
                    try {
                        return apartadoNegocio.realizarApartado(alumnoDTO.getNumeroControl(), pcSeleccionada.getId());
                    } catch (NegocioException ex) {
                        errorMsg = ex.getMessage();
                        return null;
                    }
                }

                @Override
                protected void done() {
                    try {
                        ApartadoResultadoDTO res = get();
                        if (res == null) {
                            confirmar.setEnabled(true);
                            confirmar.setText("Confirmar apartado");
                            JOptionPane.showMessageDialog(Interfaz.this, errorMsg, "Equipo Ocupado Concurrentemente", JOptionPane.ERROR_MESSAGE);
                            mostrarPaso(2); // Lo regresa para elegir otra máquina
                        } else {
                            mostrarPaso(4);
                        }
                    } catch (Exception ex) {
                        confirmar.setEnabled(true);
                        confirmar.setText("Confirmar apartado");
                    }
                }
            };
            transaccion.execute();
        });

        JButton btnAtras = roundButtonOutline("← Cambiar");
        btnAtras.addActionListener(e -> mostrarPaso(2));

        footer.add(btnAtras, BorderLayout.WEST);
        footer.add(confirmar, BorderLayout.EAST);

        p.add(pcHeader); p.add(Box.createVerticalStrut(14));
        p.add(cols); p.add(Box.createVerticalGlue());
        p.add(footer);
        return p;
    }

    // ══════════════════════════════════════════
    //  PASO 4 — ÉXITO (COUNTDOWN LOGÍSTICO)
    // ══════════════════════════════════════════
    JLabel timerLabel;

    JPanel buildPaso4() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BLANCO);

        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(VERDE_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(VERDE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth()/2, cy = getHeight()/2;
                int[] xs = {cx-10, cx-3, cx+10};
                int[] ys = {cy,    cy+7,  cy-8};
                g2.drawPolyline(xs, ys, 3);
                g2.dispose();
            }
        };
        iconCircle.setPreferredSize(new Dimension(64, 64));
        iconCircle.setMaximumSize(new Dimension(64, 64));
        iconCircle.setOpaque(false);
        iconCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("¡Apartado realizado con éxito!", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(TEXTO_DARK);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("La máquina " + pcSeleccionada.getNumeroMaquina() + " ha sido bloqueada en red para ti.", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(TEXTO_MED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel timerBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BLANCO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        timerBox.setLayout(new BoxLayout(timerBox, BoxLayout.Y_AXIS));
        timerBox.setBackground(BLANCO);
        timerBox.setOpaque(false);
        timerBox.setBorder(new EmptyBorder(12, 30, 12, 30));
        timerBox.setMaximumSize(new Dimension(180, 80));
        timerBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel timerLbl = new JLabel("Usa tu contraseña para entrar", SwingConstants.CENTER);
        timerLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        timerLbl.setForeground(TEXTO_MED);
        timerLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        countdown = 30;
        timerLabel = new JLabel("00:30", SwingConstants.CENTER);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        timerLabel.setForeground(AZUL);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timerBox.add(timerLbl);
        timerBox.add(timerLabel);

        JLabel timerHint = new JLabel("Tienes 30 segundos para llegar al equipo e iniciar sesión física.", SwingConstants.CENTER);
        timerHint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        timerHint.setForeground(TEXTO_MED);
        timerHint.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton otro = roundButtonOutline("Volver al Inicio");
        otro.setAlignmentX(Component.CENTER_ALIGNMENT);
        otro.addActionListener(e -> {
            if (timer != null) { timer.stop(); timer = null; }
            pcSeleccionada = null;
            alumnoDTO = null;
            countdown = 30;
            mostrarPaso(1);
        });

        p.add(Box.createVerticalGlue());
        p.add(iconCircle); p.add(Box.createVerticalStrut(10));
        p.add(titulo); p.add(Box.createVerticalStrut(4));
        p.add(sub); p.add(Box.createVerticalStrut(14));
        p.add(timerBox); p.add(Box.createVerticalStrut(6));
        p.add(timerHint); p.add(Box.createVerticalStrut(14));
        p.add(otro);
        p.add(Box.createVerticalGlue());

        if (timer != null) timer.stop();
        timer = new Timer(1000, e -> {
            countdown--;
            if (timerLabel != null)
                timerLabel.setText(String.format("00:%02d", Math.max(0, countdown)));
            if (countdown <= 0) timer.stop();
        });
        timer.start();

        return p;
    }

    // ── Utilidades Estéticas ──
    JPanel infoBox(String title) {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BLANCO); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDE); g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(BLANCO); box.setOpaque(false);
        box.setBorder(new EmptyBorder(10, 12, 10, 12));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(TEXTO_LIGHT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(lbl); box.add(Box.createVerticalStrut(8));
        return box;
    }

    void addSummaryRow(JPanel box, String key, String val) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(BLANCO);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        JLabel k = new JLabel(key);
        k.setFont(new Font("SansSerif", Font.PLAIN, 12));
        k.setForeground(TEXTO_MED);
        JLabel v = new JLabel(val);
        v.setFont(new Font("SansSerif", Font.BOLD, 12));
        v.setForeground(TEXTO_DARK);
        row.add(k, BorderLayout.WEST); row.add(v, BorderLayout.EAST);
        box.add(row); box.add(Box.createVerticalStrut(4));
    }

    JButton roundButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? AZUL_HOVER : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    JButton roundButtonOutline(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? FONDO_SEC : BLANCO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDE); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(TEXTO_DARK); g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 38));
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
        int radius; Color bg;
        RoundedPanel(int r, Color bg) { this.radius = r; this.bg = bg; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0,0,0,15));
            g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-2, radius*2, radius*2);
            g2.setColor(bg); g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius*2, radius*2);
            g2.dispose();
        }
    }

    static class RoundedBorder extends AbstractBorder {
        Color color; int radius, thickness;
        RoundedBorder(Color c, int r, int t) { color=c; radius=r; thickness=t; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w-1, h-1, radius*2, radius*2);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(thickness+2,thickness+2,thickness+2,thickness+2); }
    }
}