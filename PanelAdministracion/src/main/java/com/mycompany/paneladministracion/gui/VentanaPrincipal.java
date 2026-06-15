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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Ventana principal del Panel de Administración. Reúne en pestañas el CRUD de
 * alumnos, computadoras, laboratorios, carreras y software, además de la
 * gestión de bloqueos y la consulta de apartados.
 */
public class VentanaPrincipal extends JFrame {

    private static final DateTimeFormatter F_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter F_FECHAHORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter F_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final AdminNegocio negocio;

    // Tablas y cachés por entidad
    private DefaultTableModel modeloAlumnos;
    private List<Alumno> alumnosCache = new ArrayList<>();
    private DefaultTableModel modeloComputadoras;
    private List<Computadora> computadorasCache = new ArrayList<>();
    private DefaultTableModel modeloLaboratorios;
    private List<Laboratorio> laboratoriosCache = new ArrayList<>();
    private DefaultTableModel modeloCarreras;
    private List<Carrera> carrerasCache = new ArrayList<>();
    private DefaultTableModel modeloSoftware;
    private List<Software> softwareCache = new ArrayList<>();
    private DefaultTableModel modeloBloqueos;
    private List<Bloqueo> bloqueosCache = new ArrayList<>();
    private DefaultTableModel modeloApartados;

    public VentanaPrincipal(AdminNegocio negocio) {
        this.negocio = negocio;
        setTitle("ITSON · Panel de Administración de Laboratorios");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Alumnos", buildAlumnosTab());
        tabs.addTab("Computadoras", buildComputadorasTab());
        tabs.addTab("Laboratorios", buildLaboratoriosTab());
        tabs.addTab("Carreras", buildCarrerasTab());
        tabs.addTab("Software", buildSoftwareTab());
        tabs.addTab("Bloqueos", buildBloqueosTab());
        tabs.addTab("Apartados", buildApartadosTab());
        add(tabs);

        cargarTodo();
    }

    private void cargarTodo() {
        cargarCarreras();
        cargarSoftware();
        cargarLaboratorios();
        cargarAlumnos();
        cargarComputadoras();
        cargarBloqueos();
        cargarApartados();
    }

    // ════════════════ utilidades de UI ════════════════
    private JTable tablaNoEditable(DefaultTableModel modelo) {
        JTable t = new JTable(modelo) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setRowHeight(24);
        return t;
    }

    private JPanel barraBotones(JButton... botones) {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton b : botones) {
            barra.add(b);
        }
        return barra;
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Operación realizada", JOptionPane.INFORMATION_MESSAGE);
    }

    private int filaSeleccionada(JTable tabla) {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            error("Selecciona un registro de la tabla primero.");
        }
        return row;
    }

    private boolean confirmar(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ════════════════════════ ALUMNOS ════════════════════════
    private JPanel buildAlumnosTab() {
        modeloAlumnos = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Apellido", "Estatus", "Carrera", "Bloqueado"}, 0);
        JTable tabla = tablaNoEditable(modeloAlumnos);

        JButton nuevo = new JButton("Nuevo");
        JButton editar = new JButton("Editar");
        JButton eliminar = new JButton("Eliminar");
        JButton refrescar = new JButton("Refrescar");

        nuevo.addActionListener(e -> dialogoAlumno(null));
        editar.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0) dialogoAlumno(alumnosCache.get(r));
        });
        eliminar.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0 && confirmar("¿Eliminar al alumno seleccionado?")) {
                try {
                    negocio.eliminarAlumno(alumnosCache.get(r).getId());
                    info("Alumno eliminado.");
                    cargarAlumnos();
                } catch (NegocioException ex) {
                    error(ex.getMessage());
                }
            }
        });
        refrescar.addActionListener(e -> cargarAlumnos());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(barraBotones(nuevo, editar, eliminar, refrescar), BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void cargarAlumnos() {
        try {
            alumnosCache = negocio.listarAlumnos();
            modeloAlumnos.setRowCount(0);
            for (Alumno a : alumnosCache) {
                modeloAlumnos.addRow(new Object[]{
                    a.getId(), a.getNombre(), a.getApellido(), a.getEstatusInscripcion(),
                    a.getCarreraNombre(), a.isBloqueado() ? "Sí" : "No"});
            }
            cargarBloqueos();
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

    // ════════════════════════ COMPUTADORAS ════════════════════════
    private JPanel buildComputadorasTab() {
        modeloComputadoras = new DefaultTableModel(
                new Object[]{"ID", "N° Máquina", "IP", "Estatus", "Tipo", "Laboratorio"}, 0);
        JTable tabla = tablaNoEditable(modeloComputadoras);

        JButton nuevo = new JButton("Nueva");
        JButton editar = new JButton("Editar");
        JButton eliminar = new JButton("Eliminar");
        JButton software = new JButton("Software…");
        JButton refrescar = new JButton("Refrescar");

        nuevo.addActionListener(e -> dialogoComputadora(null));
        editar.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0) dialogoComputadora(computadorasCache.get(r));
        });
        eliminar.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0 && confirmar("¿Eliminar la computadora seleccionada?")) {
                try {
                    negocio.eliminarComputadora(computadorasCache.get(r).getId());
                    info("Computadora eliminada.");
                    cargarComputadoras();
                } catch (NegocioException ex) {
                    error(ex.getMessage());
                }
            }
        });
        software.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0) dialogoSoftwareDeComputadora(computadorasCache.get(r));
        });
        refrescar.addActionListener(e -> cargarComputadoras());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(barraBotones(nuevo, editar, eliminar, software, refrescar), BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void cargarComputadoras() {
        try {
            computadorasCache = negocio.listarComputadoras();
            modeloComputadoras.setRowCount(0);
            for (Computadora c : computadorasCache) {
                modeloComputadoras.addRow(new Object[]{
                    c.getId(), c.getNumeroMaquina(), c.getDireccionIp(), c.getEstatus(),
                    c.getTipoComputadora(), c.getLaboratorioNombre()});
            }
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

    // ════════════════════════ LABORATORIOS ════════════════════════
    private JPanel buildLaboratoriosTab() {
        modeloLaboratorios = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Horario", "Unidad"}, 0);
        JTable tabla = tablaNoEditable(modeloLaboratorios);

        JButton nuevo = new JButton("Nuevo");
        JButton editar = new JButton("Editar");
        JButton eliminar = new JButton("Eliminar");
        JButton refrescar = new JButton("Refrescar");

        nuevo.addActionListener(e -> dialogoLaboratorio(null));
        editar.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0) dialogoLaboratorio(laboratoriosCache.get(r));
        });
        eliminar.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0 && confirmar("¿Eliminar el laboratorio seleccionado?")) {
                try {
                    negocio.eliminarLaboratorio(laboratoriosCache.get(r).getId());
                    info("Laboratorio eliminado.");
                    cargarLaboratorios();
                } catch (NegocioException ex) {
                    error(ex.getMessage());
                }
            }
        });
        refrescar.addActionListener(e -> cargarLaboratorios());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(barraBotones(nuevo, editar, eliminar, refrescar), BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void cargarLaboratorios() {
        try {
            laboratoriosCache = negocio.listarLaboratorios();
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

    // ════════════════════════ CARRERAS ════════════════════════
    private JPanel buildCarrerasTab() {
        modeloCarreras = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Tiempo límite diario", "Teléfono academia"}, 0);
        JTable tabla = tablaNoEditable(modeloCarreras);

        JButton nuevo = new JButton("Nueva");
        JButton editar = new JButton("Editar");
        JButton eliminar = new JButton("Eliminar");
        JButton refrescar = new JButton("Refrescar");

        nuevo.addActionListener(e -> dialogoCarrera(null));
        editar.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0) dialogoCarrera(carrerasCache.get(r));
        });
        eliminar.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0 && confirmar("¿Eliminar la carrera seleccionada?")) {
                try {
                    negocio.eliminarCarrera(carrerasCache.get(r).getId());
                    info("Carrera eliminada.");
                    cargarCarreras();
                } catch (NegocioException ex) {
                    error(ex.getMessage());
                }
            }
        });
        refrescar.addActionListener(e -> cargarCarreras());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(barraBotones(nuevo, editar, eliminar, refrescar), BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void cargarCarreras() {
        try {
            carrerasCache = negocio.listarCarreras();
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

    // ════════════════════════ SOFTWARE ════════════════════════
    private JPanel buildSoftwareTab() {
        modeloSoftware = new DefaultTableModel(new Object[]{"ID", "Nombre", "Descripción"}, 0);
        JTable tabla = tablaNoEditable(modeloSoftware);

        JButton nuevo = new JButton("Nuevo");
        JButton editar = new JButton("Editar");
        JButton eliminar = new JButton("Eliminar");
        JButton refrescar = new JButton("Refrescar");

        nuevo.addActionListener(e -> dialogoSoftware(null));
        editar.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0) dialogoSoftware(softwareCache.get(r));
        });
        eliminar.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0 && confirmar("¿Eliminar el software seleccionado?")) {
                try {
                    negocio.eliminarSoftware(softwareCache.get(r).getId());
                    info("Software eliminado.");
                    cargarSoftware();
                } catch (NegocioException ex) {
                    error(ex.getMessage());
                }
            }
        });
        refrescar.addActionListener(e -> cargarSoftware());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(barraBotones(nuevo, editar, eliminar, refrescar), BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void cargarSoftware() {
        try {
            softwareCache = negocio.listarSoftware();
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

    // ════════════════════════ BLOQUEOS ════════════════════════
    private JPanel buildBloqueosTab() {
        modeloBloqueos = new DefaultTableModel(
                new Object[]{"ID", "Alumno", "Fecha bloqueo", "Motivo"}, 0);
        JTable tabla = tablaNoEditable(modeloBloqueos);

        JButton bloquear = new JButton("Bloquear alumno…");
        JButton desbloquear = new JButton("Desbloquear seleccionado");
        JButton refrescar = new JButton("Refrescar");

        bloquear.addActionListener(e -> dialogoBloqueo());
        desbloquear.addActionListener(e -> {
            int r = filaSeleccionada(tabla);
            if (r >= 0 && confirmar("¿Desbloquear al alumno seleccionado?")) {
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

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(barraBotones(bloquear, desbloquear, refrescar), BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
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

    // ════════════════════════ APARTADOS (consulta) ════════════════════════
    private JPanel buildApartadosTab() {
        modeloApartados = new DefaultTableModel(
                new Object[]{"ID", "Alumno", "N° Máquina", "Laboratorio", "Inicio", "Fin / Estado"}, 0);
        JTable tabla = tablaNoEditable(modeloApartados);

        JButton refrescar = new JButton("Refrescar");
        refrescar.addActionListener(e -> cargarApartados());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(barraBotones(refrescar), BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void cargarApartados() {
        try {
            modeloApartados.setRowCount(0);
            for (Apartado ap : negocio.listarApartados()) {
                String fin = ap.isActivo() ? "ACTIVO"
                        : (ap.getFinPrestamo() != null ? ap.getFinPrestamo().format(F_FECHAHORA) : "");
                modeloApartados.addRow(new Object[]{
                    ap.getId(), ap.getAlumnoNombre(), ap.getNumeroMaquina(), ap.getLaboratorioNombre(),
                    ap.getInicioPrestamo() != null ? ap.getInicioPrestamo().format(F_FECHAHORA) : "",
                    fin});
            }
        } catch (NegocioException ex) {
            error(ex.getMessage());
        }
    }
}
