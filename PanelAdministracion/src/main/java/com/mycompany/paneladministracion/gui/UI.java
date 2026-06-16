package com.mycompany.paneladministracion.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;

/**
 * Tema visual y componentes reutilizables del panel (look moderno, pintado a
 * mano sobre Swing para no depender de librerías externas).
 *
 * @author Cristian Devora
 */
public final class UI {

    private UI() {
    }

    // ─────────────── Paleta ───────────────
    public static final Color WINDOW_BG   = new Color(0xEE, 0xF1, 0xF5);
    public static final Color SIDEBAR_TOP  = new Color(0x0E, 0x22, 0x33);
    public static final Color SIDEBAR_BOT  = new Color(0x12, 0x30, 0x49);
    public static final Color NAV_TEXT     = new Color(0xB6, 0xC4, 0xD2);
    public static final Color NAV_SEL_A    = new Color(0x1B, 0xA7, 0xE8);
    public static final Color NAV_SEL_B    = new Color(0x14, 0x77, 0xC7);
    public static final Color LOGO         = new Color(0x17, 0xA6, 0xE6);

    public static final Color CONTENT_BG   = Color.WHITE;
    public static final Color TITLE        = new Color(0x16, 0x27, 0x3B);
    public static final Color MUTED        = new Color(0x7C, 0x87, 0x94);
    public static final Color CARD_BG      = Color.WHITE;
    public static final Color CARD_BORDER  = new Color(0xE4, 0xE8, 0xEF);
    public static final Color CARD_SEL     = new Color(0x1F, 0x86, 0xC9);
    public static final Color SEARCH_BG    = new Color(0xF2, 0xF5, 0xF9);
    public static final Color DETAIL_BG    = new Color(0xF7, 0xF9, 0xFC);

    public static final Color AVATAR_BG    = new Color(0xDD, 0xEB, 0xF6);
    public static final Color AVATAR_FG    = new Color(0x1E, 0x5A, 0x82);

    public static final Color GREEN        = new Color(0x18, 0xA9, 0x57);
    public static final Color GREEN_BG     = new Color(0xE5, 0xF7, 0xEC);
    public static final Color RED          = new Color(0xD8, 0x45, 0x5A);
    public static final Color RED_BG       = new Color(0xFB, 0xE3, 0xE7);
    public static final Color ORANGE       = new Color(0xE0, 0x92, 0x2A);
    public static final Color ORANGE_BG    = new Color(0xFB, 0xEF, 0xD9);
    public static final Color BLUE         = new Color(0x1F, 0x86, 0xC9);
    public static final Color BLUE_BG      = new Color(0xE3, 0xF1, 0xFB);

    public static final Color BTN_GREEN    = new Color(0x21, 0xB2, 0x4C);
    public static final Color BTN_GREEN_HV = new Color(0x1C, 0x9D, 0x43);
    public static final Color GRAY_BORDER  = new Color(0xCE, 0xD6, 0xE0);
    public static final Color GRAY_TEXT    = new Color(0x60, 0x6B, 0x79);

    // ─────────────── Tipografías ───────────────
    private static final String FAMILY = "Segoe UI";
    public static final Font FONT_TITLE      = new Font(FAMILY, Font.BOLD, 23);
    public static final Font FONT_SUBTITLE   = new Font(FAMILY, Font.PLAIN, 13);
    public static final Font FONT_CARD_TITLE = new Font(FAMILY, Font.BOLD, 15);
    public static final Font FONT_CARD_SUB   = new Font(FAMILY, Font.PLAIN, 12);
    public static final Font FONT_BTN        = new Font(FAMILY, Font.BOLD, 13);
    public static final Font FONT_BTN_SM     = new Font(FAMILY, Font.PLAIN, 12);
    public static final Font FONT_NAV        = new Font(FAMILY, Font.BOLD, 14);
    public static final Font FONT_BADGE      = new Font(FAMILY, Font.BOLD, 12);
    public static final Font FONT_COLHEAD    = new Font(FAMILY, Font.BOLD, 11);
    public static final Font FONT_LOGO       = new Font(FAMILY, Font.BOLD, 17);
    public static final Font FONT_BIG        = new Font(FAMILY, Font.BOLD, 18);

    static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    // ─────────────── Fábricas ───────────────
    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static RoundedPanel card() {
        RoundedPanel p = new RoundedPanel(16, CARD_BG, CARD_BORDER);
        return p;
    }

    public static PillButton button(String text, Color fill, Color border, Color fg, Color hover) {
        return new PillButton(text, fill, border, fg, hover);
    }

    /** Botón sólido (relleno de color, texto blanco). */
    public static PillButton solid(String text, Color fill, Color hover) {
        return new PillButton(text, fill, null, Color.WHITE, hover);
    }

    /** Botón de contorno (fondo blanco, borde y texto del color dado). */
    public static PillButton outline(String text, Color color, Color hoverBg) {
        return new PillButton(text, Color.WHITE, color, color, hoverBg);
    }

    public static JScrollPane scroll(JComponent view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(true);
        sp.getViewport().setBackground(CONTENT_BG);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        return sp;
    }

    public static void styleTable(JTable t) {
        t.setRowHeight(32);
        t.setFont(new Font(FAMILY, Font.PLAIN, 13));
        t.setGridColor(new Color(0xEC, 0xEF, 0xF4));
        t.setShowVerticalLines(false);
        t.setSelectionBackground(BLUE_BG);
        t.setSelectionForeground(TITLE);
        t.setForeground(new Color(0x2C, 0x39, 0x47));
        JTableHeader h = t.getTableHeader();
        h.setFont(FONT_COLHEAD);
        h.setForeground(MUTED);
        h.setBackground(Color.WHITE);
        h.setPreferredSize(new Dimension(h.getPreferredSize().width, 30));
    }

    public static Component vgap(int px) {
        return javax.swing.Box.createVerticalStrut(px);
    }

    public static Component hgap(int px) {
        return javax.swing.Box.createHorizontalStrut(px);
    }

    // ════════════════ Componentes ════════════════

    /** Panel con esquinas redondeadas, fondo y borde opcionales. */
    public static class RoundedPanel extends JPanel {
        private final int arc;
        private Color bg;
        private Color border;
        private float borderWidth = 1f;

        public RoundedPanel(int arc, Color bg, Color border) {
            this.arc = arc;
            this.bg = bg;
            this.border = border;
            setOpaque(false);
        }

        public void setBg(Color c) { this.bg = c; repaint(); }
        public void setBorderColor(Color c) { this.border = c; repaint(); }
        public void setBorderWidth(float w) { this.borderWidth = w; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth(), h = getHeight();
            float bw = borderWidth;
            if (bg != null) {
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc));
            }
            if (border != null) {
                g2.setColor(border);
                g2.setStroke(new BasicStroke(bw));
                g2.draw(new RoundRectangle2D.Float(bw / 2, bw / 2, w - 1 - bw, h - 1 - bw, arc, arc));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Panel del sidebar con degradado vertical. */
    public static class GradientPanel extends JPanel {
        private final Color top, bottom;

        public GradientPanel(Color top, Color bottom) {
            this.top = top;
            this.bottom = bottom;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    /** Botón "píldora" pintado a mano (relleno y/o contorno). */
    public static class PillButton extends JButton {
        private Color fill, border, fg;
        private final Color hover;
        private int arc = 12;

        public PillButton(String text, Color fill, Color border, Color fg, Color hover) {
            super(text);
            this.fill = fill;
            this.border = border;
            this.fg = fg;
            this.hover = hover;
            setForeground(fg);
            setFont(FONT_BTN);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(new EmptyBorder(8, 16, 8, 16));
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }

        public PillButton arc(int a) { this.arc = a; return this; }
        public PillButton small() { setFont(FONT_BTN_SM); setBorder(new EmptyBorder(5, 11, 5, 11)); return this; }

        /** Reconfigura el aspecto (usado para botones tipo "toggle"). */
        public void estilo(Color fill, Color border, Color fg) {
            this.fill = fill; this.border = border; this.fg = fg;
            setForeground(fg); repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth(), h = getHeight();
            Color base = fill;
            if (getModel().isRollover() && hover != null) {
                base = hover;
            }
            if (base != null) {
                g2.setColor(base);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc));
            }
            if (border != null) {
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1.4f));
                g2.draw(new RoundRectangle2D.Float(0.7f, 0.7f, w - 2.4f, h - 2.4f, arc, arc));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Etiqueta de estado redondeada con punto opcional. */
    public static class Badge extends JComponent {
        private String text;
        private Color bg, fg;
        private final boolean dot;

        public Badge(String text, Color bg, Color fg, boolean dot) {
            this.text = text;
            this.bg = bg;
            this.fg = fg;
            this.dot = dot;
            setFont(FONT_BADGE);
        }

        public void set(String text, Color bg, Color fg) {
            this.text = text; this.bg = bg; this.fg = fg;
            revalidate(); repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            int tw = getFontMetrics(getFont()).stringWidth(text);
            int w = tw + 24 + (dot ? 14 : 0);
            return new Dimension(w, 26);
        }

        @Override
        public Dimension getMaximumSize() { return getPreferredSize(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth(), h = getHeight();
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, h, h));
            int x = 12;
            if (dot) {
                g2.setColor(fg);
                g2.fillOval(x, h / 2 - 3, 6, 6);
                x += 12;
            }
            g2.setColor(fg);
            g2.setFont(getFont());
            int ty = (h - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(text, x, ty);
            g2.dispose();
        }
    }

    /** Avatar circular con iniciales. */
    public static class Avatar extends JComponent {
        private final String initials;

        public Avatar(String initials) {
            this.initials = initials == null ? "?" : initials;
            setPreferredSize(new Dimension(46, 46));
            setMinimumSize(new Dimension(46, 46));
            setMaximumSize(new Dimension(46, 46));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int d = Math.min(getWidth(), getHeight());
            g2.setColor(AVATAR_BG);
            g2.fillRoundRect(0, 0, d, d, 14, 14);
            g2.setColor(AVATAR_FG);
            g2.setFont(new Font(FAMILY, Font.BOLD, 15));
            int tw = g2.getFontMetrics().stringWidth(initials);
            int ty = (d - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
            g2.drawString(initials, (d - tw) / 2, ty);
            g2.dispose();
        }
    }

    /** Campo de texto con placeholder y fondo transparente. */
    public static class PlaceholderTextField extends JTextField {
        private final String placeholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 0, 0));
            setFont(new Font(FAMILY, Font.PLAIN, 14));
            setForeground(TITLE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = smooth(g);
                g2.setColor(MUTED);
                g2.setFont(getFont());
                int ty = (getHeight() - g2.getFontMetrics().getHeight()) / 2 + g2.getFontMetrics().getAscent();
                g2.drawString(placeholder, 2, ty);
                g2.dispose();
            }
        }
    }

    /** Botón de navegación del sidebar. */
    public static class NavButton extends JButton {
        private final String iconName;
        private boolean selected;

        public NavButton(String iconName, String text) {
            super(text);
            this.iconName = iconName;
            setHorizontalAlignment(LEFT);
            setForeground(NAV_TEXT);
            setFont(FONT_NAV);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(new EmptyBorder(0, 52, 0, 12));
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setPreferredSize(new Dimension(220, 46));
            setAlignmentX(LEFT_ALIGNMENT);
        }

        public void setSelected(boolean sel) {
            this.selected = sel;
            setForeground(sel ? Color.WHITE : NAV_TEXT);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth(), h = getHeight();
            if (selected) {
                g2.setPaint(new GradientPaint(0, 0, NAV_SEL_A, w, 0, NAV_SEL_B));
                g2.fill(new RoundRectangle2D.Float(12, 3, w - 24, h - 6, 12, 12));
            }
            Icons.paint(g2, iconName, 22, h / 2 - 9, 18, selected ? Color.WHITE : NAV_TEXT);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ════════════════ Iconos vectoriales ════════════════
    public static final class Icons {
        private Icons() {
        }

        public static void paint(Graphics2D g0, String name, int x, int y, int size, Color color) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(x, y);
            double s = size / 24.0;
            g.scale(s, s);
            g.setColor(color);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (name) {
                case "alumnos" -> {
                    g.drawOval(8, 3, 8, 8);
                    g.drawArc(4, 14, 16, 16, 0, 180);
                }
                case "apartados" -> {
                    g.drawRoundRect(3, 5, 18, 16, 4, 4);
                    g.drawLine(8, 3, 8, 7);
                    g.drawLine(16, 3, 16, 7);
                    g.drawLine(3, 10, 21, 10);
                }
                case "equipos" -> {
                    g.drawRoundRect(3, 4, 18, 12, 3, 3);
                    g.drawLine(12, 16, 12, 19);
                    g.drawLine(8, 20, 16, 20);
                }
                case "laboratorios" -> {
                    g.drawLine(9, 3, 9, 10);
                    g.drawLine(15, 3, 15, 10);
                    g.drawLine(8, 3, 16, 3);
                    GeneralPath p = new GeneralPath();
                    p.moveTo(9, 10);
                    p.lineTo(4, 20);
                    p.lineTo(20, 20);
                    p.lineTo(15, 10);
                    g.draw(p);
                }
                case "carreras" -> {
                    GeneralPath d = new GeneralPath();
                    d.moveTo(12, 4); d.lineTo(22, 9); d.lineTo(12, 14); d.lineTo(2, 9); d.closePath();
                    g.draw(d);
                    g.drawLine(19, 10, 19, 16);
                    g.drawArc(7, 11, 10, 8, 180, 180);
                }
                case "software" -> {
                    g.drawRoundRect(4, 4, 7, 7, 2, 2);
                    g.drawRoundRect(13, 4, 7, 7, 2, 2);
                    g.drawRoundRect(4, 13, 7, 7, 2, 2);
                    g.drawRoundRect(13, 13, 7, 7, 2, 2);
                }
                case "bloqueos" -> {
                    g.drawArc(7, 3, 10, 12, 0, 180);
                    g.drawRoundRect(5, 11, 14, 10, 3, 3);
                    g.fillOval(11, 14, 2, 2);
                    g.drawLine(12, 16, 12, 18);
                }
                case "search" -> {
                    g.drawOval(3, 3, 12, 12);
                    g.drawLine(15, 15, 21, 21);
                }
                default -> g.drawOval(6, 6, 12, 12);
            }
            g.dispose();
        }
    }
}
