package com.mycompany.paneladministracion.gui;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.mycompany.paneladministracion.entidades.Alumno;
import com.mycompany.paneladministracion.entidades.Apartado;
import com.mycompany.paneladministracion.entidades.Bloqueo;
import com.mycompany.paneladministracion.entidades.Carrera;
import com.mycompany.paneladministracion.entidades.Computadora;
import com.mycompany.paneladministracion.entidades.Instituto;
import com.mycompany.paneladministracion.entidades.Laboratorio;
import com.mycompany.paneladministracion.entidades.Software;
import com.mycompany.paneladministracion.negocio.AdminNegocio;
import com.mycompany.paneladministracion.negocio.NegocioException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana principal del Panel de Administración. Presenta un sidebar de
 * navegación y pantallas tipo tarjeta para gestionar (CRUD) alumnos,
 * computadoras, laboratorios, carreras y software, además de bloqueos y
 * apartados.
 *
 * @author Cristian Devora
 */
public class VentanaPrincipal extends JFrame {

    private static final DateTimeFormatter F_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter F_FECHAHORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter F_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final AdminNegocio negocio;

    // Navegación
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final List<UI.NavButton> navButtons = new ArrayList<>();

    // Cachés por entidad
    private List<Alumno> alumnosCache = new ArrayList<>();
    private List<Computadora> computadorasCache = new ArrayList<>();
    private List<Bloqueo> bloqueosCache = new ArrayList<>();

    // Tablas restantes
    private DefaultTableModel modeloLaboratorios;
    private DefaultTableModel modeloCarreras;
    private DefaultTableModel modeloSoftware;
    private DefaultTableModel modeloBloqueos;

    // Componentes dinámicos
    private JPanel alumnosList;
    private UI.PlaceholderTextField alumnoSearch;

    private JPanel equiposList;
    private UI.RoundedPanel equiposDetail;
    private int equipoSeleccionadoId = -1;

    private JPanel apartadosList;
    private JPanel apartadosHeader;
    private boolean apartadosSoloActivos = true;
    private UI.PillButton btnActivos;
    private UI.PillButton btnTodos;

    public VentanaPrincipal(AdminNegocio negocio) {
        this.negocio = negocio;
        setTitle("ITSON · Panel de Administración de Laboratorios");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 700);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UI.WINDOW_BG);
        root.add(buildSidebar(), BorderLayout.WEST);

        content.setBackground(UI.CONTENT_BG);
        content.add(buildAlumnosPanel(), "ALUMNOS");
        content.add(buildApartadosPanel(), "APARTADOS");
        content.add(buildEquiposPanel(), "EQUIPOS");
        content.add(buildLaboratoriosPanel(), "LABORATORIOS");
        content.add(buildCarrerasPanel(), "CARRERAS");
        content.add(buildSoftwarePanel(), "SOFTWARE");
        content.add(buildBloqueosPanel(), "BLOQUEOS");
        root.add(content, BorderLayout.CENTER);

        setContentPane(root);

        cargarTodo();
        seleccionarNav("ALUMNOS");
    }

    private void cargarTodo() {
        cargarLaboratorios();
        cargarCarreras();
        cargarSoftware();
        cargarAlumnos();
        cargarComputadoras();
        cargarBloqueos();
        cargarApartados();
    }

    // ════════════════════════ SIDEBAR ════════════════════════
    private JComponent buildSidebar() {
        UI.GradientPanel side = new UI.GradientPanel(UI.SIDEBAR_TOP, UI.SIDEBAR_BOT);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(248, 0));
        side.setBorder(BorderFactory.createEmptyBorder(22, 0, 22, 0));

        // Encabezado de marca
        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        marca.setOpaque(false);
        marca.setBorder(BorderFactory.createEmptyBorder(0, 22, 0, 22));
        marca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);

        UI.RoundedPanel logo = new UI.RoundedPanel(12, UI.LOGO, null);
        logo.setLayout(new BorderLayout());
        logo.setPreferredSize(new Dimension(38, 38));
        JLabel logoTxt = UI.label("C", UI.FONT_LOGO, Color.WHITE);
        logoTxt.setHorizontalAlignment(SwingConstants.CENTER);
        logo.add(logoTxt, BorderLayout.CENTER);
        marca.add(logo);
        marca.add(UI.label("Administración", UI.FONT_BIG, Color.WHITE));

        side.add(marca);
        side.add(UI.vgap(26));

        side.add(navButton("alumnos", "Alumnos", "ALUMNOS"));
        side.add(UI.vgap(4));
        side.add(navButton("apartados", "Apartados", "APARTADOS"));
        side.add(UI.vgap(4));
        side.add(navButton("equipos", "Equipos", "EQUIPOS"));
        side.add(UI.vgap(4));
        side.add(navButton("laboratorios", "Laboratorios", "LABORATORIOS"));
        side.add(UI.vgap(4));
        side.add(navButton("carreras", "Carreras", "CARRERAS"));
        side.add(UI.vgap(4));
        side.add(navButton("software", "Software", "SOFTWARE"));
        side.add(UI.vgap(4));
        side.add(navButton("bloqueos", "Bloqueos", "BLOQUEOS"));

        side.add(Box.createVerticalGlue());
        return side;
    }

    private UI.NavButton navButton(String icon, String text, String card) {
        UI.NavButton b = new UI.NavButton(icon, text);
        b.addActionListener(e -> {
            cards.show(content, card);
            seleccionarNav(card);
            switch (card) {
                case "ALUMNOS" -> cargarAlumnos();
                case "APARTADOS" -> cargarApartados();
                case "EQUIPOS" -> cargarComputadoras();
                case "LABORATORIOS" -> cargarLaboratorios();
                case "CARRERAS" -> cargarCarreras();
                case "SOFTWARE" -> cargarSoftware();
                case "BLOQUEOS" -> cargarBloqueos();
                default -> { }
            }
        });
        b.putClientProperty("card", card);
        navButtons.add(b);
        return b;
    }

    private void seleccionarNav(String card) {
        for (UI.NavButton b : navButtons) {
            b.setSelected(card.equals(b.getClientProperty("card")));
        }
    }

    // ════════════════════════ utilidades de UI ════════════════════════
    private JPanel contentPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 18));
        p.setBackground(UI.CONTENT_BG);
        p.setBorder(BorderFactory.createEmptyBorder(28, 34, 28, 34));
        return p;
    }

    private JComponent tituloBloque(String titulo, String subtitulo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = UI.label(titulo, UI.FONT_TITLE, UI.TITLE);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel s = UI.label(subtitulo, UI.FONT_SUBTITLE, UI.MUTED);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(t);
        p.add(UI.vgap(4));
        p.add(s);
        return p;
    }

    private JPanel header(String titulo, String subtitulo, JComponent acciones) {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.add(tituloBloque(titulo, subtitulo), BorderLayout.WEST);
        if (acciones != null) {
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            right.setOpaque(false);
            right.add(acciones);
            h.add(right, BorderLayout.EAST);
        }
        return h;
    }

    private JPanel listaVertical() {
        JPanel l = new JPanel();
        l.setLayout(new BoxLayout(l, BoxLayout.Y_AXIS));
        l.setBackground(UI.CONTENT_BG);
        return l;
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Operación realizada", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean confirmar(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private static String iniciales(String nombre, String apellido) {
        StringBuilder sb = new StringBuilder();
        if (nombre != null && !nombre.isBlank()) sb.append(Character.toUpperCase(nombre.trim().charAt(0)));
        if (apellido != null && !apellido.isBlank()) sb.append(Character.toUpperCase(apellido.trim().charAt(0)));
        return sb.length() == 0 ? "?" : sb.toString();
    }

    // ════════════════════════ ALUMNOS ════════════════════════
    private JPanel buildAlumnosPanel() {
        JPanel root = contentPanel();

        UI.PillButton nuevo = UI.solid("+  Nuevo alumno", UI.BLUE, UI.CARD_SEL);
        nuevo.addActionListener(e -> dialogoAlumno(null));

        // Barra superior (título + búsqueda)
        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        JPanel head = header("Alumnos", "Bloquea o desbloquea el acceso de los alumnos al sistema.", nuevo);
        head.setAlignmentX(Component.LEFT_ALIGNMENT);
        norte.add(head);
        norte.add(UI.vgap(18));

        UI.RoundedPanel buscador = new UI.RoundedPanel(14, UI.SEARCH_BG, UI.CARD_BORDER);
        buscador.setLayout(new BorderLayout(8, 0));
        buscador.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        buscador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        buscador.setPreferredSize(new Dimension(10, 48));
        buscador.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComponent lupa = new JComponent() {
            @Override protected void paintComponent(java.awt.Graphics g) {
                UI.Icons.paint(UI.smooth(g), "search", 0, getHeight() / 2 - 9, 18, UI.MUTED);
            }
        };
        lupa.setPreferredSize(new Dimension(20, 20));
        buscador.add(lupa, BorderLayout.WEST);
        alumnoSearch = new UI.PlaceholderTextField("Buscar alumno...");
        alumnoSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { renderAlumnos(); }
            @Override public void removeUpdate(DocumentEvent e) { renderAlumnos(); }
            @Override public void changedUpdate(DocumentEvent e) { renderAlumnos(); }
        });
        buscador.add(alumnoSearch, BorderLayout.CENTER);
        norte.add(buscador);

        root.add(norte, BorderLayout.NORTH);

        alumnosList = listaVertical();
        root.add(UI.scroll(alumnosList), BorderLayout.CENTER);
        return root;
    }

    private void cargarAlumnos() {
        try {
            alumnosCache = negocio.listarAlumnos();
            renderAlumnos();
            cargarBloqueos();
        } catch (NegocioException ex) {
            error(ex.getMessage());
        }
    }

    private void renderAlumnos() {
        if (alumnosList == null) return;
        alumnosList.removeAll();
        String q = alumnoSearch == null ? "" : alumnoSearch.getText().trim().toLowerCase();
        boolean alguno = false;
        for (Alumno a : alumnosCache) {
            String idFmt = String.format("%011d", a.getId());
            if (!q.isEmpty()
                    && !a.getNombreCompleto().toLowerCase().contains(q)
                    && !idFmt.contains(q)
                    && !String.valueOf(a.getId()).contains(q)) {
                continue;
            }
            alumnosList.add(tarjetaAlumno(a, idFmt));
            alumnosList.add(UI.vgap(12));
            alguno = true;
        }
        if (!alguno) {
            JLabel vacio = UI.label("No hay alumnos que coincidan con la búsqueda.", UI.FONT_SUBTITLE, UI.MUTED);
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            vacio.setBorder(BorderFactory.createEmptyBorder(12, 4, 0, 0));
            alumnosList.add(vacio);
        }
        alumnosList.add(Box.createVerticalGlue());
        alumnosList.revalidate();
        alumnosList.repaint();
    }

    private JComponent tarjetaAlumno(Alumno a, String idFmt) {
        UI.RoundedPanel card = UI.card();
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Izquierda: avatar + datos
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        izq.setOpaque(false);
        izq.add(new UI.Avatar(iniciales(a.getNombre(), a.getApellido())));
        JPanel datos = new JPanel();
        datos.setOpaque(false);
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
        JLabel nom = UI.label(a.getNombreCompleto(), UI.FONT_CARD_TITLE, UI.TITLE);
        nom.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel id = UI.label("ID " + idFmt, UI.FONT_CARD_SUB, UI.MUTED);
        id.setAlignmentX(Component.LEFT_ALIGNMENT);
        datos.add(Box.createVerticalGlue());
        datos.add(nom);
        datos.add(UI.vgap(3));
        datos.add(id);
        datos.add(Box.createVerticalGlue());
        izq.add(datos);
        card.add(izq, BorderLayout.WEST);

        // Derecha: estado + acciones
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);
        if (a.isBloqueado()) {
            der.add(new UI.Badge("Bloqueado", UI.RED_BG, UI.RED, true));
            UI.PillButton desbloq = UI.solid("Desbloquear", UI.BTN_GREEN, UI.BTN_GREEN_HV);
            desbloq.addActionListener(e -> desbloquearAlumnoAccion(a));
            der.add(desbloq);
        } else {
            der.add(new UI.Badge("Activo", UI.GREEN_BG, UI.GREEN, true));
            UI.PillButton bloq = UI.outline("Bloquear", UI.RED, UI.RED_BG);
            bloq.addActionListener(e -> dialogoBloquearAlumno(a));
            der.add(bloq);
        }
        UI.PillButton editar = UI.outline("Editar", UI.GRAY_TEXT, UI.SEARCH_BG).small();
        editar.setForeground(UI.GRAY_TEXT);
        editar.addActionListener(e -> dialogoAlumno(a));
        UI.PillButton eliminar = UI.outline("Eliminar", UI.RED, UI.RED_BG).small();
        eliminar.addActionListener(e -> {
            if (confirmar("¿Eliminar al alumno \"" + a.getNombreCompleto() + "\"?")) {
                try {
                    negocio.eliminarAlumno(a.getId());
                    info("Alumno eliminado.");
                    cargarAlumnos();
                } catch (NegocioException ex) {
                    error(ex.getMessage());
                }
            }
        });
        der.add(editar);
        der.add(eliminar);
        card.add(der, BorderLayout.EAST);

        return card;
    }

    private void desbloquearAlumnoAccion(Alumno a) {
        if (!confirmar("¿Desbloquear a \"" + a.getNombreCompleto() + "\"?")) return;
        try {
            negocio.desbloquearAlumno(a.getId());
            info("Alumno desbloqueado.");
            cargarAlumnos();
        } catch (NegocioException ex) {
            error(ex.getMessage());
        }
    }

    private void dialogoAlumno(Alumno existente) {
        boolean esNuevo = existente == null;
        List<Carrera> carreras;
        try {
            carreras = negocio.listarCarreras();
        } catch (NegocioException ex) {
            error(ex.getMessage());
            return;
        }
        if (carreras.isEmpty()) {
            error("No hay carreras registradas. El catálogo se siembra automáticamente al iniciar.");
            return;
        }

        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JComboBox<String> cmbEstatus = new JComboBox<>(new String[]{"Inscrito", "No inscrito"});
        JComboBox<Carrera> cmbCarrera = new JComboBox<>(carreras.toArray(new Carrera[0]));
        JPasswordField txtContrasena = new JPasswordField();

        if (!esNuevo) {
            txtNombre.setText(existente.getNombre());
            txtApellido.setText(existente.getApellido());
            cmbEstatus.setSelectedItem("INSCRITO".equalsIgnoreCase(existente.getEstatusInscripcion()) ? "Inscrito" : "No inscrito");
            for (Carrera c : carreras) {
                if (c.getId() == existente.getIdCarrera()) {
                    cmbCarrera.setSelectedItem(c);
                    break;
                }
            }
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Apellido:")); form.add(txtApellido);
        form.add(new JLabel("Estatus:")); form.add(cmbEstatus);
        form.add(new JLabel("Carrera:")); form.add(cmbCarrera);
        form.add(new JLabel(esNuevo ? "Contraseña:" : "Contraseña (vacío = no cambiar):")); form.add(txtContrasena);
        form.setPreferredSize(new Dimension(420, 170));

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, form,
                    esNuevo ? "Nuevo alumno" : "Editar alumno", JOptionPane.OK_CANCEL_OPTION);
            if (op != JOptionPane.OK_OPTION) return;

            Alumno a = esNuevo ? new Alumno() : existente;
            a.setNombre(txtNombre.getText().trim());
            a.setApellido(txtApellido.getText().trim());
            a.setEstatusInscripcion((String) cmbEstatus.getSelectedItem());
            a.setIdCarrera(((Carrera) cmbCarrera.getSelectedItem()).getId());
            String pass = new String(txtContrasena.getPassword());

            try {
                negocio.guardarAlumno(a, pass, esNuevo);
                info(esNuevo ? "Alumno registrado." : "Alumno actualizado.");
                cargarAlumnos();
                return;
            } catch (NegocioException ex) {
                error(ex.getMessage());
            }
        }
    }

    /** Bloqueo directo de un alumno desde su tarjeta (pide fecha y motivo). */
    private void dialogoBloquearAlumno(Alumno a) {
        DatePicker dpFecha = new DatePicker();
        dpFecha.setDateToToday();
        JTextField txtMotivo = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Alumno:")); form.add(new JLabel(a.getNombreCompleto()));
        form.add(new JLabel("Fecha de bloqueo:")); form.add(dpFecha);
        form.add(new JLabel("Motivo:")); form.add(txtMotivo);
        form.setPreferredSize(new Dimension(440, 130));

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, form, "Bloquear alumno", JOptionPane.OK_CANCEL_OPTION);
            if (op != JOptionPane.OK_OPTION) return;
            try {
                String notificacion = negocio.bloquearAlumno(a, dpFecha.getDate(), txtMotivo.getText());
                JOptionPane.showMessageDialog(this, "Bloqueo registrado.\n\n" + notificacion,
                        "Bloqueo y notificación", JOptionPane.INFORMATION_MESSAGE);
                cargarAlumnos();
                return;
            } catch (NegocioException ex) {
                error(ex.getMessage());
            }
        }
    }

    // ════════════════════════ APARTADOS ════════════════════════
    private JPanel buildApartadosPanel() {
        JPanel root = contentPanel();

        btnActivos = UI.solid("Activos", UI.BLUE, UI.CARD_SEL);
        btnTodos = UI.outline("Historial", UI.GRAY_TEXT, UI.SEARCH_BG);
        btnTodos.setForeground(UI.GRAY_TEXT);
        btnActivos.addActionListener(e -> { apartadosSoloActivos = true; actualizarToggleApartados(); cargarApartados(); });
        btnTodos.addActionListener(e -> { apartadosSoloActivos = false; actualizarToggleApartados(); cargarApartados(); });
        JPanel toggle = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toggle.setOpaque(false);
        toggle.add(btnActivos);
        toggle.add(btnTodos);

        JPanel norte = new JPanel();
        norte.setOpaque(false);
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        JPanel head = header("Apartados activos", "Reservas de equipos en curso. Puedes cancelarlas.", toggle);
        head.setAlignmentX(Component.LEFT_ALIGNMENT);
        norte.add(head);
        norte.add(UI.vgap(16));

        apartadosHeader = new JPanel(new BorderLayout());
        apartadosHeader.setOpaque(false);
        apartadosHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UI.CARD_BORDER));
        apartadosHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        apartadosHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        norte.add(apartadosHeader);

        root.add(norte, BorderLayout.NORTH);

        apartadosList = listaVertical();
        root.add(UI.scroll(apartadosList), BorderLayout.CENTER);
        return root;
    }

    private void actualizarToggleApartados() {
        if (btnActivos == null) return;
        if (apartadosSoloActivos) {
            btnActivos.estilo(UI.BLUE, null, Color.WHITE);
            btnTodos.estilo(Color.WHITE, UI.GRAY_BORDER, UI.GRAY_TEXT);
        } else {
            btnActivos.estilo(Color.WHITE, UI.GRAY_BORDER, UI.GRAY_TEXT);
            btnTodos.estilo(UI.BLUE, null, Color.WHITE);
        }
    }

    /** Envuelve el contenido de una celda para que las columnas se alineen entre filas. */
    private JComponent celda(JComponent inner, boolean right) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMinimumSize(new Dimension(1, 1));
        p.setPreferredSize(new Dimension(1, inner.getPreferredSize().height));
        p.add(inner, right ? BorderLayout.EAST : BorderLayout.WEST);
        return p;
    }

    private JPanel filaApartado(JComponent c0, JComponent c1, JComponent c2, JComponent c3, boolean header) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, header ? 32 : 56));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (!header) {
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF0, 0xF2, 0xF6)));
        }
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.insets = new Insets(0, 2, 0, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.weightx = 0.40; row.add(celda(c0, false), gc);
        gc.gridx = 1; gc.weightx = 0.22; row.add(celda(c1, false), gc);
        gc.gridx = 2; gc.weightx = 0.20; row.add(celda(c2, false), gc);
        gc.gridx = 3; gc.weightx = 0.18; row.add(celda(c3, true), gc);
        return row;
    }

    private void cargarApartados() {
        if (apartadosList == null) return;
        try {
            List<Apartado> todos = negocio.listarApartados();

            apartadosHeader.removeAll();
            JPanel cols = filaApartado(
                    UI.label("ALUMNO", UI.FONT_COLHEAD, UI.MUTED),
                    UI.label("EQUIPO", UI.FONT_COLHEAD, UI.MUTED),
                    UI.label(apartadosSoloActivos ? "TIEMPO" : "INICIO", UI.FONT_COLHEAD, UI.MUTED),
                    UI.label(apartadosSoloActivos ? "" : "ESTADO", UI.FONT_COLHEAD, UI.MUTED),
                    true);
            apartadosHeader.add(cols, BorderLayout.CENTER);
            apartadosHeader.revalidate();
            apartadosHeader.repaint();

            apartadosList.removeAll();
            boolean alguno = false;
            for (Apartado ap : todos) {
                if (apartadosSoloActivos && !ap.isActivo()) continue;
                apartadosList.add(filaApartado(
                        UI.label(ap.getAlumnoNombre(), UI.FONT_CARD_TITLE, UI.TITLE),
                        new UI.Badge("PC " + String.format("%02d", ap.getNumeroMaquina()), UI.BLUE_BG, UI.BLUE, true),
                        UI.label(tercerCampoApartado(ap), UI.FONT_CARD_SUB, UI.GRAY_TEXT),
                        cuartoCampoApartado(ap), false));
                alguno = true;
            }
            if (!alguno) {
                JLabel vacio = UI.label(apartadosSoloActivos
                        ? "No hay apartados activos en este momento."
                        : "No hay apartados registrados.", UI.FONT_SUBTITLE, UI.MUTED);
                vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
                vacio.setBorder(BorderFactory.createEmptyBorder(14, 4, 0, 0));
                apartadosList.add(vacio);
            }
            apartadosList.add(Box.createVerticalGlue());
            apartadosList.revalidate();
            apartadosList.repaint();
        } catch (NegocioException ex) {
            error(ex.getMessage());
        }
    }

    private String tercerCampoApartado(Apartado ap) {
        if (apartadosSoloActivos) {
            if (ap.getInicioPrestamo() == null) return "—";
            Duration d = Duration.between(ap.getInicioPrestamo(), LocalDateTime.now());
            if (d.isNegative()) d = Duration.ZERO;
            long h = d.toHours();
            long m = d.toMinutes() % 60;
            return String.format("%02d:%02d", h, m);
        }
        return ap.getInicioPrestamo() != null ? ap.getInicioPrestamo().format(F_FECHAHORA) : "";
    }

    private JComponent cuartoCampoApartado(Apartado ap) {
        if (apartadosSoloActivos) {
            UI.PillButton cancelar = UI.outline("Cancelar", UI.RED, UI.RED_BG);
            cancelar.addActionListener(e -> {
                if (confirmar("¿Cancelar el apartado de \"" + ap.getAlumnoNombre()
                        + "\" en la PC " + String.format("%02d", ap.getNumeroMaquina()) + "?")) {
                    try {
                        negocio.cancelarApartado(ap.getId());
                        info("Apartado cancelado y equipo liberado.");
                        cargarApartados();
                        cargarComputadoras();
                    } catch (NegocioException ex) {
                        error(ex.getMessage());
                    }
                }
            });
            return cancelar;
        }
        if (ap.isActivo()) {
            return new UI.Badge("Activo", UI.GREEN_BG, UI.GREEN, true);
        }
        return UI.label(ap.getFinPrestamo() != null ? ap.getFinPrestamo().format(F_FECHAHORA) : "Finalizado",
                UI.FONT_CARD_SUB, UI.GRAY_TEXT);
    }

    // ════════════════════════ EQUIPOS ════════════════════════
    private JPanel buildEquiposPanel() {
        JPanel root = contentPanel();

        UI.PillButton nueva = UI.solid("+  Nueva", UI.BLUE, UI.CARD_SEL);
        nueva.addActionListener(e -> dialogoComputadora(null));
        UI.PillButton editar = UI.outline("Editar", UI.GRAY_TEXT, UI.SEARCH_BG);
        editar.setForeground(UI.GRAY_TEXT);
        editar.addActionListener(e -> {
            Computadora c = equipoSeleccionado();
            if (c != null) dialogoComputadora(c);
        });
        UI.PillButton software = UI.outline("Software…", UI.BLUE, UI.BLUE_BG);
        software.addActionListener(e -> {
            Computadora c = equipoSeleccionado();
            if (c != null) dialogoSoftwareDeComputadora(c);
        });
        UI.PillButton eliminar = UI.outline("Eliminar", UI.RED, UI.RED_BG);
        eliminar.addActionListener(e -> {
            Computadora c = equipoSeleccionado();
            if (c != null && confirmar("¿Eliminar la PC " + String.format("%02d", c.getNumeroMaquina()) + "?")) {
                try {
                    negocio.eliminarComputadora(c.getId());
                    info("Computadora eliminada.");
                    equipoSeleccionadoId = -1;
                    cargarComputadoras();
                } catch (NegocioException ex) {
                    error(ex.getMessage());
                }
            }
        });
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(nueva);
        acciones.add(editar);
        acciones.add(software);
        acciones.add(eliminar);

        JPanel head = header("Equipos",
                "Habilita o deshabilita equipos para el apartado. Selecciona uno para ver el detalle.", acciones);
        root.add(head, BorderLayout.NORTH);

        equiposList = listaVertical();
        JScrollPane scroll = UI.scroll(equiposList);

        equiposDetail = new UI.RoundedPanel(16, UI.DETAIL_BG, UI.CARD_BORDER);
        equiposDetail.setLayout(new BorderLayout());

        JPanel centro = new JPanel(new BorderLayout(20, 0));
        centro.setOpaque(false);
        centro.add(scroll, BorderLayout.CENTER);
        JPanel derWrap = new JPanel(new BorderLayout());
        derWrap.setOpaque(false);
        derWrap.setPreferredSize(new Dimension(300, 0));
        derWrap.add(equiposDetail, BorderLayout.NORTH);
        centro.add(derWrap, BorderLayout.EAST);

        root.add(centro, BorderLayout.CENTER);
        return root;
    }

    private Computadora equipoSeleccionado() {
        for (Computadora c : computadorasCache) {
            if (c.getId() == equipoSeleccionadoId) return c;
        }
        error("Selecciona un equipo de la lista primero.");
        return null;
    }

    private void cargarComputadoras() {
        try {
            computadorasCache = negocio.listarComputadoras();
            if (equiposList == null) return;
            boolean existe = false;
            for (Computadora c : computadorasCache) {
                if (c.getId() == equipoSeleccionadoId) { existe = true; break; }
            }
            if (!existe && !computadorasCache.isEmpty()) {
                equipoSeleccionadoId = computadorasCache.get(0).getId();
            } else if (computadorasCache.isEmpty()) {
                equipoSeleccionadoId = -1;
            }
            renderEquipos();
        } catch (NegocioException ex) {
            error(ex.getMessage());
        }
    }

    private void renderEquipos() {
        equiposList.removeAll();
        for (Computadora c : computadorasCache) {
            equiposList.add(tarjetaEquipo(c));
            equiposList.add(UI.vgap(10));
        }
        if (computadorasCache.isEmpty()) {
            JLabel vacio = UI.label("No hay equipos registrados. Usa “Nueva” para agregar uno.", UI.FONT_SUBTITLE, UI.MUTED);
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            equiposList.add(vacio);
        }
        equiposList.add(Box.createVerticalGlue());
        equiposList.revalidate();
        equiposList.repaint();
        renderDetalleEquipo();
    }

    private JComponent tarjetaEquipo(Computadora c) {
        boolean sel = c.getId() == equipoSeleccionadoId;
        UI.RoundedPanel card = new UI.RoundedPanel(14, UI.CARD_BG, sel ? UI.CARD_SEL : UI.CARD_BORDER);
        card.setBorderWidth(sel ? 2f : 1f);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izq.setOpaque(false);
        JComponent icono = new JComponent() {
            @Override protected void paintComponent(java.awt.Graphics g) {
                UI.Icons.paint(UI.smooth(g), "equipos", 0, getHeight() / 2 - 11, 22, UI.BLUE);
            }
        };
        icono.setPreferredSize(new Dimension(26, 26));
        izq.add(icono);
        izq.add(UI.label("PC " + String.format("%02d", c.getNumeroMaquina()), UI.FONT_CARD_TITLE, UI.TITLE));
        card.add(izq, BorderLayout.WEST);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        der.setOpaque(false);
        der.add(badgeEstatus(c.getEstatus()));
        card.add(der, BorderLayout.EAST);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                equipoSeleccionadoId = c.getId();
                renderEquipos();
            }
        });
        return card;
    }

    private UI.Badge badgeEstatus(String estatus) {
        if ("Deshabilitada".equalsIgnoreCase(estatus)) {
            return new UI.Badge("Deshabilitada", UI.RED_BG, UI.RED, true);
        }
        if ("Apartada".equalsIgnoreCase(estatus)) {
            return new UI.Badge("Ocupada", UI.ORANGE_BG, UI.ORANGE, true);
        }
        return new UI.Badge("Disponible", UI.GREEN_BG, UI.GREEN, true);
    }

    private void renderDetalleEquipo() {
        equiposDetail.removeAll();
        Computadora c = null;
        for (Computadora x : computadorasCache) {
            if (x.getId() == equipoSeleccionadoId) { c = x; break; }
        }

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        if (c == null) {
            JLabel ph = UI.label("Selecciona un equipo para ver el detalle.", UI.FONT_SUBTITLE, UI.MUTED);
            ph.setAlignmentX(Component.LEFT_ALIGNMENT);
            cuerpo.add(ph);
            equiposDetail.add(cuerpo, BorderLayout.NORTH);
            equiposDetail.revalidate();
            equiposDetail.repaint();
            return;
        }

        JLabel titulo = UI.label("Equipo #" + String.format("%02d", c.getNumeroMaquina()), UI.FONT_BIG, UI.TITLE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cuerpo.add(titulo);
        cuerpo.add(UI.vgap(18));

        JLabel lblCentro = UI.label("Centro", UI.FONT_CARD_SUB, UI.MUTED);
        lblCentro.setAlignmentX(Component.LEFT_ALIGNMENT);
        cuerpo.add(lblCentro);
        JLabel centro = UI.label(c.getLaboratorioNombre() != null ? c.getLaboratorioNombre() : "—", UI.FONT_CARD_TITLE, UI.TITLE);
        centro.setAlignmentX(Component.LEFT_ALIGNMENT);
        cuerpo.add(UI.vgap(2));
        cuerpo.add(centro);
        cuerpo.add(UI.vgap(16));

        JLabel lblEstado = UI.label("Estado", UI.FONT_CARD_SUB, UI.MUTED);
        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        cuerpo.add(lblEstado);
        cuerpo.add(UI.vgap(4));
        UI.Badge be = badgeEstatus(c.getEstatus());
        be.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeWrap.setOpaque(false);
        badgeWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        badgeWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        badgeWrap.add(be);
        cuerpo.add(badgeWrap);
        cuerpo.add(UI.vgap(20));

        JButton accion;
        if ("Apartada".equalsIgnoreCase(c.getEstatus())) {
            accion = UI.button("Ocupada", UI.SEARCH_BG, UI.GRAY_BORDER, UI.GRAY_TEXT, null);
            accion.setEnabled(false);
        } else if ("Deshabilitada".equalsIgnoreCase(c.getEstatus())) {
            UI.PillButton hab = UI.solid("Habilitar", UI.BTN_GREEN, UI.BTN_GREEN_HV);
            final Computadora cc = c;
            hab.addActionListener(e -> cambiarEstatusEquipo(cc, "Disponible"));
            accion = hab;
        } else {
            UI.PillButton des = UI.outline("Deshabilitar", UI.RED, UI.RED_BG);
            final Computadora cc = c;
            des.addActionListener(e -> cambiarEstatusEquipo(cc, "Deshabilitada"));
            accion = des;
        }
        accion.setAlignmentX(Component.LEFT_ALIGNMENT);
        accion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        accion.setPreferredSize(new Dimension(240, 44));
        cuerpo.add(accion);

        equiposDetail.add(cuerpo, BorderLayout.NORTH);
        equiposDetail.revalidate();
        equiposDetail.repaint();
    }

    private void cambiarEstatusEquipo(Computadora c, String nuevoEstatus) {
        try {
            negocio.cambiarEstatusComputadora(c, nuevoEstatus);
            cargarComputadoras();
        } catch (NegocioException ex) {
            error(ex.getMessage());
        }
    }

    private void dialogoComputadora(Computadora existente) {
        boolean esNuevo = existente == null;
        List<Laboratorio> labs;
        try {
            labs = negocio.listarLaboratorios();
        } catch (NegocioException ex) {
            error(ex.getMessage());
            return;
        }
        if (labs.isEmpty()) {
            error("Primero registra al menos un laboratorio.");
            return;
        }

        JTextField txtNumero = new JTextField();
        JTextField txtIp = new JTextField();
        JComboBox<String> cmbEstatus = new JComboBox<>(new String[]{"Disponible", "Apartada", "Deshabilitada"});
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Windows", "Mac", "Linux"});
        JComboBox<Laboratorio> cmbLab = new JComboBox<>(labs.toArray(new Laboratorio[0]));

        if (!esNuevo) {
            txtNumero.setText(String.valueOf(existente.getNumeroMaquina()));
            txtIp.setText(existente.getDireccionIp());
            cmbEstatus.setSelectedItem(existente.getEstatus());
            cmbTipo.setSelectedItem(existente.getTipoComputadora());
            for (Laboratorio l : labs) {
                if (l.getId() == existente.getIdLaboratorio()) {
                    cmbLab.setSelectedItem(l);
                    break;
                }
            }
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("N° de máquina:")); form.add(txtNumero);
        form.add(new JLabel("Dirección IP:")); form.add(txtIp);
        form.add(new JLabel("Estatus:")); form.add(cmbEstatus);
        form.add(new JLabel("Tipo:")); form.add(cmbTipo);
        form.add(new JLabel("Laboratorio:")); form.add(cmbLab);
        form.setPreferredSize(new Dimension(420, 170));

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, form,
                    esNuevo ? "Nueva computadora" : "Editar computadora", JOptionPane.OK_CANCEL_OPTION);
            if (op != JOptionPane.OK_OPTION) return;

            Computadora c = esNuevo ? new Computadora() : existente;
            try {
                c.setNumeroMaquina(Integer.parseInt(txtNumero.getText().trim()));
            } catch (NumberFormatException nfe) {
                error("El número de máquina debe ser un entero.");
                continue;
            }
            c.setDireccionIp(txtIp.getText().trim());
            c.setEstatus((String) cmbEstatus.getSelectedItem());
            c.setTipoComputadora((String) cmbTipo.getSelectedItem());
            c.setIdLaboratorio(((Laboratorio) cmbLab.getSelectedItem()).getId());

            try {
                negocio.guardarComputadora(c, esNuevo);
                info(esNuevo ? "Computadora registrada." : "Computadora actualizada.");
                if (esNuevo) equipoSeleccionadoId = c.getId();
                cargarComputadoras();
                return;
            } catch (NegocioException ex) {
                error(ex.getMessage());
            }
        }
    }

    private void dialogoSoftwareDeComputadora(Computadora pc) {
        try {
            List<Software> todos = negocio.listarSoftware();
            if (todos.isEmpty()) {
                error("No hay software en el catálogo. Regístralo en la pestaña Software.");
                return;
            }
            Set<Integer> asignados = negocio.idsSoftwareAsignado(pc.getId());

            JPanel lista = new JPanel();
            lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
            List<JCheckBox> checks = new ArrayList<>();
            for (Software s : todos) {
                JCheckBox chk = new JCheckBox(s.getNombre(), asignados.contains(s.getId()));
                chk.putClientProperty("idSoftware", s.getId());
                checks.add(chk);
                lista.add(chk);
            }
            JScrollPane scroll = new JScrollPane(lista);
            scroll.setPreferredSize(new Dimension(360, 260));

            int op = JOptionPane.showConfirmDialog(this, scroll,
                    "Software de " + pc, JOptionPane.OK_CANCEL_OPTION);
            if (op != JOptionPane.OK_OPTION) return;

            List<Integer> seleccion = new ArrayList<>();
            for (JCheckBox chk : checks) {
                if (chk.isSelected()) seleccion.add((Integer) chk.getClientProperty("idSoftware"));
            }
            negocio.asignarSoftware(pc.getId(), seleccion);
            info("Software actualizado para " + pc + ".");
        } catch (NegocioException ex) {
            error(ex.getMessage());
        }
    }

    // ════════════════════════ Paneles de tabla (Lab/Carrera/Software/Bloqueos) ════════════════════════
    private JTable tablaNoEditable(DefaultTableModel modelo) {
        JTable t = new JTable(modelo) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UI.styleTable(t);
        return t;
    }

    private JPanel panelTabla(String titulo, String subtitulo, JTable tabla, JComponent acciones) {
        JPanel root = contentPanel();
        root.add(header(titulo, subtitulo, acciones), BorderLayout.NORTH);
        UI.RoundedPanel cont = UI.card();
        cont.setLayout(new BorderLayout());
        cont.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(null);
        sp.getViewport().setBackground(Color.WHITE);
        cont.add(sp, BorderLayout.CENTER);
        root.add(cont, BorderLayout.CENTER);
        return root;
    }

    // ──────────── LABORATORIOS ────────────
    private JPanel buildLaboratoriosPanel() {
        modeloLaboratorios = new DefaultTableModel(new Object[]{"ID", "Nombre", "Horario", "Unidad"}, 0);
        JTable tabla = tablaNoEditable(modeloLaboratorios);

        UI.PillButton nuevo = UI.solid("+  Nuevo", UI.BLUE, UI.CARD_SEL);
        UI.PillButton editar = UI.outline("Editar", UI.GRAY_TEXT, UI.SEARCH_BG);
        editar.setForeground(UI.GRAY_TEXT);
        UI.PillButton eliminar = UI.outline("Eliminar", UI.RED, UI.RED_BG);

        nuevo.addActionListener(e -> dialogoLaboratorio(null));
        editar.addActionListener(e -> {
            int r = tabla.getSelectedRow();
            if (r < 0) { error("Selecciona un laboratorio primero."); return; }
            dialogoLaboratorio(laboratoriosCache.get(r));
        });
        eliminar.addActionListener(e -> {
            int r = tabla.getSelectedRow();
            if (r < 0) { error("Selecciona un laboratorio primero."); return; }
            if (confirmar("¿Eliminar el laboratorio seleccionado?")) {
                try {
                    negocio.eliminarLaboratorio(laboratoriosCache.get(r).getId());
                    info("Laboratorio eliminado.");
                    cargarLaboratorios();
                } catch (NegocioException ex) {
                    error(ex.getMessage());
                }
            }
        });

        JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acc.setOpaque(false);
        acc.add(nuevo); acc.add(editar); acc.add(eliminar);
        return panelTabla("Laboratorios", "Centros de cómputo y su horario de servicio.", tabla, acc);
    }

    private List<Laboratorio> laboratoriosCache = new ArrayList<>();

    private void cargarLaboratorios() {
        try {
            laboratoriosCache = negocio.listarLaboratorios();
            if (modeloLaboratorios == null) return;
            modeloLaboratorios.setRowCount(0);
            for (Laboratorio l : laboratoriosCache) {
                String horario = (l.getHoraInicio() != null ? l.getHoraInicio().format(F_HORA) : "?")
                        + " - " + (l.getHoraFin() != null ? l.getHoraFin().format(F_HORA) : "?");
                modeloLaboratorios.addRow(new Object[]{l.getId(), l.getNombre(), horario, l.getInstitutoNombre()});
            }
        } catch (NegocioException ex) {
            error(ex.getMessage());
        }
    }

    private TimePicker nuevoTimePicker() {
        TimePickerSettings settings = new TimePickerSettings();
        settings.use24HourClockFormat();
        settings.initialTime = LocalTime.of(7, 0);
        return new TimePicker(settings);
    }

    private void dialogoLaboratorio(Laboratorio existente) {
        boolean esNuevo = existente == null;
        List<Instituto> unidades;
        try {
            unidades = negocio.listarInstitutos();
        } catch (NegocioException ex) {
            error(ex.getMessage());
            return;
        }
        if (unidades.isEmpty()) {
            error("No hay unidades académicas. El catálogo se siembra automáticamente al iniciar.");
            return;
        }

        JTextField txtNombre = new JTextField();
        TimePicker tpInicio = nuevoTimePicker();
        TimePicker tpFin = nuevoTimePicker();
        JComboBox<Instituto> cmbUnidad = new JComboBox<>(unidades.toArray(new Instituto[0]));
        JPasswordField txtMaestra = new JPasswordField();

        tpInicio.setTime(LocalTime.of(7, 0));
        tpFin.setTime(LocalTime.of(21, 0));

        if (!esNuevo) {
            txtNombre.setText(existente.getNombre());
            tpInicio.setTime(existente.getHoraInicio());
            tpFin.setTime(existente.getHoraFin());
            for (Instituto u : unidades) {
                if (u.getId() == existente.getIdInstituto()) {
                    cmbUnidad.setSelectedItem(u);
                    break;
                }
            }
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Hora inicio:")); form.add(tpInicio);
        form.add(new JLabel("Hora fin:")); form.add(tpFin);
        form.add(new JLabel("Unidad:")); form.add(cmbUnidad);
        form.add(new JLabel(esNuevo ? "Contraseña maestra:" : "Contraseña maestra (vacío = no cambiar):")); form.add(txtMaestra);
        form.setPreferredSize(new Dimension(440, 180));

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, form,
                    esNuevo ? "Nuevo laboratorio" : "Editar laboratorio", JOptionPane.OK_CANCEL_OPTION);
            if (op != JOptionPane.OK_OPTION) return;

            Laboratorio l = esNuevo ? new Laboratorio() : existente;
            l.setNombre(txtNombre.getText().trim());
            l.setHoraInicio(tpInicio.getTime());
            l.setHoraFin(tpFin.getTime());
            l.setIdInstituto(((Instituto) cmbUnidad.getSelectedItem()).getId());
            String pass = new String(txtMaestra.getPassword());

            try {
                negocio.guardarLaboratorio(l, pass, esNuevo);
                info(esNuevo ? "Laboratorio registrado." : "Laboratorio actualizado.");
                cargarLaboratorios();
                return;
            } catch (NegocioException ex) {
                error(ex.getMessage());
            }
        }
    }

    // ──────────── CARRERAS ────────────
    private List<Carrera> carrerasCache = new ArrayList<>();

    private JPanel buildCarrerasPanel() {
        modeloCarreras = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Tiempo límite diario", "Teléfono academia"}, 0);
        JTable tabla = tablaNoEditable(modeloCarreras);

        UI.PillButton nuevo = UI.solid("+  Nueva", UI.BLUE, UI.CARD_SEL);
        UI.PillButton editar = UI.outline("Editar", UI.GRAY_TEXT, UI.SEARCH_BG);
        editar.setForeground(UI.GRAY_TEXT);
        UI.PillButton eliminar = UI.outline("Eliminar", UI.RED, UI.RED_BG);

        nuevo.addActionListener(e -> dialogoCarrera(null));
        editar.addActionListener(e -> {
            int r = tabla.getSelectedRow();
            if (r < 0) { error("Selecciona una carrera primero."); return; }
            dialogoCarrera(carrerasCache.get(r));
        });
        eliminar.addActionListener(e -> {
            int r = tabla.getSelectedRow();
            if (r < 0) { error("Selecciona una carrera primero."); return; }
            if (confirmar("¿Eliminar la carrera seleccionada?")) {
                try {
                    negocio.eliminarCarrera(carrerasCache.get(r).getId());
                    info("Carrera eliminada.");
                    cargarCarreras();
                } catch (NegocioException ex) {
                    error(ex.getMessage());
                }
            }
        });

        JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acc.setOpaque(false);
        acc.add(nuevo); acc.add(editar); acc.add(eliminar);
        return panelTabla("Carreras", "Programas educativos y su tiempo límite de uso diario.", tabla, acc);
    }

    private void cargarCarreras() {
        try {
            carrerasCache = negocio.listarCarreras();
            if (modeloCarreras == null) return;
            modeloCarreras.setRowCount(0);
            for (Carrera c : carrerasCache) {
                modeloCarreras.addRow(new Object[]{
                    c.getId(), c.getNombre(),
                    c.getTiempoLimiteDiario() != null ? c.getTiempoLimiteDiario().format(F_HORA) : "",
                    c.getTelefonoAcademia()});
            }
        } catch (NegocioException ex) {
            error(ex.getMessage());
        }
    }

    private void dialogoCarrera(Carrera existente) {
        boolean esNuevo = existente == null;
        JTextField txtNombre = new JTextField();
        TimePicker tpLimite = nuevoTimePicker();
        JTextField txtTelefono = new JTextField();
        tpLimite.setTime(LocalTime.of(2, 0));

        if (!esNuevo) {
            txtNombre.setText(existente.getNombre());
            tpLimite.setTime(existente.getTiempoLimiteDiario());
            txtTelefono.setText(existente.getTelefonoAcademia());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Tiempo límite diario:")); form.add(tpLimite);
        form.add(new JLabel("Teléfono academia:")); form.add(txtTelefono);
        form.setPreferredSize(new Dimension(420, 130));

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, form,
                    esNuevo ? "Nueva carrera" : "Editar carrera", JOptionPane.OK_CANCEL_OPTION);
            if (op != JOptionPane.OK_OPTION) return;

            Carrera c = esNuevo ? new Carrera() : existente;
            c.setNombre(txtNombre.getText().trim());
            c.setTiempoLimiteDiario(tpLimite.getTime());
            c.setTelefonoAcademia(txtTelefono.getText().trim());

            try {
                negocio.guardarCarrera(c, esNuevo);
                info(esNuevo ? "Carrera registrada." : "Carrera actualizada.");
                cargarCarreras();
                return;
            } catch (NegocioException ex) {
                error(ex.getMessage());
            }
        }
    }

    // ──────────── SOFTWARE ────────────
    private List<Software> softwareCache = new ArrayList<>();

    private JPanel buildSoftwarePanel() {
        modeloSoftware = new DefaultTableModel(new Object[]{"ID", "Nombre", "Descripción"}, 0);
        JTable tabla = tablaNoEditable(modeloSoftware);

        UI.PillButton nuevo = UI.solid("+  Nuevo", UI.BLUE, UI.CARD_SEL);
        UI.PillButton editar = UI.outline("Editar", UI.GRAY_TEXT, UI.SEARCH_BG);
        editar.setForeground(UI.GRAY_TEXT);
        UI.PillButton eliminar = UI.outline("Eliminar", UI.RED, UI.RED_BG);

        nuevo.addActionListener(e -> dialogoSoftware(null));
        editar.addActionListener(e -> {
            int r = tabla.getSelectedRow();
            if (r < 0) { error("Selecciona un software primero."); return; }
            dialogoSoftware(softwareCache.get(r));
        });
        eliminar.addActionListener(e -> {
            int r = tabla.getSelectedRow();
            if (r < 0) { error("Selecciona un software primero."); return; }
            if (confirmar("¿Eliminar el software seleccionado?")) {
                try {
                    negocio.eliminarSoftware(softwareCache.get(r).getId());
                    info("Software eliminado.");
                    cargarSoftware();
                } catch (NegocioException ex) {
                    error(ex.getMessage());
                }
            }
        });

        JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acc.setOpaque(false);
        acc.add(nuevo); acc.add(editar); acc.add(eliminar);
        return panelTabla("Software", "Catálogo de programas disponibles para asignar a los equipos.", tabla, acc);
    }

    private void cargarSoftware() {
        try {
            softwareCache = negocio.listarSoftware();
            if (modeloSoftware == null) return;
            modeloSoftware.setRowCount(0);
            for (Software s : softwareCache) {
                modeloSoftware.addRow(new Object[]{s.getId(), s.getNombre(), s.getDescripcion()});
            }
        } catch (NegocioException ex) {
            error(ex.getMessage());
        }
    }

    private void dialogoSoftware(Software existente) {
        boolean esNuevo = existente == null;
        JTextField txtNombre = new JTextField();
        JTextField txtDescripcion = new JTextField();

        if (!esNuevo) {
            txtNombre.setText(existente.getNombre());
            txtDescripcion.setText(existente.getDescripcion());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Nombre:")); form.add(txtNombre);
        form.add(new JLabel("Descripción:")); form.add(txtDescripcion);
        form.setPreferredSize(new Dimension(420, 90));

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, form,
                    esNuevo ? "Nuevo software" : "Editar software", JOptionPane.OK_CANCEL_OPTION);
            if (op != JOptionPane.OK_OPTION) return;

            Software s = esNuevo ? new Software() : existente;
            s.setNombre(txtNombre.getText().trim());
            s.setDescripcion(txtDescripcion.getText().trim());

            try {
                negocio.guardarSoftware(s, esNuevo);
                info(esNuevo ? "Software registrado." : "Software actualizado.");
                cargarSoftware();
                return;
            } catch (NegocioException ex) {
                error(ex.getMessage());
            }
        }
    }

    // ──────────── BLOQUEOS ────────────
    private JPanel buildBloqueosPanel() {
        modeloBloqueos = new DefaultTableModel(new Object[]{"ID", "Alumno", "Fecha bloqueo", "Motivo"}, 0);
        JTable tabla = tablaNoEditable(modeloBloqueos);

        UI.PillButton bloquear = UI.solid("+  Bloquear alumno", UI.BLUE, UI.CARD_SEL);
        UI.PillButton desbloquear = UI.solid("Desbloquear", UI.BTN_GREEN, UI.BTN_GREEN_HV);
        UI.PillButton refrescar = UI.outline("Refrescar", UI.GRAY_TEXT, UI.SEARCH_BG);
        refrescar.setForeground(UI.GRAY_TEXT);

        bloquear.addActionListener(e -> dialogoBloqueo());
        desbloquear.addActionListener(e -> {
            int r = tabla.getSelectedRow();
            if (r < 0) { error("Selecciona un bloqueo primero."); return; }
            if (confirmar("¿Desbloquear al alumno seleccionado?")) {
                try {
                    negocio.desbloquearAlumno(bloqueosCache.get(r).getIdAlumno());
                    info("Alumno desbloqueado.");
                    cargarBloqueos();
                    cargarAlumnos();
                } catch (NegocioException ex) {
                    error(ex.getMessage());
                }
            }
        });
        refrescar.addActionListener(e -> cargarBloqueos());

        JPanel acc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acc.setOpaque(false);
        acc.add(bloquear); acc.add(desbloquear); acc.add(refrescar);
        return panelTabla("Bloqueos", "Bloqueos activos de alumnos con su fecha y motivo.", tabla, acc);
    }

    private void cargarBloqueos() {
        try {
            bloqueosCache = negocio.listarBloqueosActivos();
            if (modeloBloqueos == null) return;
            modeloBloqueos.setRowCount(0);
            for (Bloqueo b : bloqueosCache) {
                modeloBloqueos.addRow(new Object[]{
                    b.getId(), b.getAlumnoNombre(),
                    b.getFechaBloqueo() != null ? b.getFechaBloqueo().format(F_FECHA) : "",
                    b.getMotivo()});
            }
        } catch (NegocioException ex) {
            error(ex.getMessage());
        }
    }

    private void dialogoBloqueo() {
        List<Alumno> alumnos;
        try {
            alumnos = negocio.listarAlumnos();
        } catch (NegocioException ex) {
            error(ex.getMessage());
            return;
        }
        List<Alumno> disponibles = new ArrayList<>();
        for (Alumno a : alumnos) {
            if (!a.isBloqueado()) disponibles.add(a);
        }
        if (disponibles.isEmpty()) {
            error("No hay alumnos disponibles para bloquear.");
            return;
        }

        JComboBox<Alumno> cmbAlumno = new JComboBox<>(disponibles.toArray(new Alumno[0]));
        DatePicker dpFecha = new DatePicker();
        dpFecha.setDateToToday();
        JTextField txtMotivo = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Alumno:")); form.add(cmbAlumno);
        form.add(new JLabel("Fecha de bloqueo:")); form.add(dpFecha);
        form.add(new JLabel("Motivo:")); form.add(txtMotivo);
        form.setPreferredSize(new Dimension(440, 130));

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, form, "Bloquear alumno", JOptionPane.OK_CANCEL_OPTION);
            if (op != JOptionPane.OK_OPTION) return;

            Alumno a = (Alumno) cmbAlumno.getSelectedItem();
            try {
                String notificacion = negocio.bloquearAlumno(a, dpFecha.getDate(), txtMotivo.getText());
                JOptionPane.showMessageDialog(this,
                        "Bloqueo registrado.\n\n" + notificacion,
                        "Bloqueo y notificación", JOptionPane.INFORMATION_MESSAGE);
                cargarBloqueos();
                cargarAlumnos();
                return;
            } catch (NegocioException ex) {
                error(ex.getMessage());
            }
        }
    }
}
