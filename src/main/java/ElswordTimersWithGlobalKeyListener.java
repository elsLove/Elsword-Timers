import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ElswordTimersWithGlobalKeyListener {

    private static class Timer {
        private String title;
        private int duration; // Duration in seconds
        private long startTime; // Start time in milliseconds
        private Point position;
        private boolean active;

        public Timer(String title, Point position) {
            this.title = title;
            this.duration = -1;
            this.startTime = -1;
            this.position = position;
            this.active = false;
        }

        public void start(int duration) {
            if (!active) {
                this.duration = duration;
                this.startTime = System.currentTimeMillis();
                this.active = true;
            }
        }

        public int getRemainingTime() {
            if (!active) {
                return 0;
            }
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            int remaining = duration - (int) elapsedTime;
            if (remaining <= 0) {
                active = false;
                return 0;
            }
            return remaining;
        }

        public void draw(Graphics g) {
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            g.setColor(Color.WHITE);
            String text;
            if (active) {
                text = title + ": " + getRemainingTime() + "s";
            } else {
                text = title + " is up!";
            }
            g.drawString(text, position.x, position.y);
        }

        public void reset() {
            this.active = false;
            this.startTime = -1;
        }
    }

    private static class TimerPanel extends JPanel {
        private final ArrayList<Timer> timers;
        private String titleState;
        private JButton resetButton;

        public TimerPanel() {
            timers = new ArrayList<>();
            timers.add(new Timer("FS", new Point(100, 70)));
            timers.add(new Timer("NP", new Point(100, 150)));
            timers.add(new Timer("TSS", new Point(100, 230)));
            titleState = "Neutral";

            // Auto-repaint loop
            javax.swing.Timer repaintTimer = new javax.swing.Timer(16, e -> repaint());
            repaintTimer.start();

            resetButton = new JButton("Hard Reset Both Timers");
            resetButton.addActionListener(e -> resetTimers());
            setLayout(new BorderLayout());
            add(resetButton, BorderLayout.SOUTH);

            setupKeyBindings();
        }

        private void setupKeyBindings() {
            InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap am = getActionMap();

            // H → switch state
            im.put(KeyStroke.getKeyStroke("H"), "switch");
            am.put("switch", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    titleState = "Changing";
                }
            });

            // I → Freed Shadow
            im.put(KeyStroke.getKeyStroke("I"), "up");
            am.put("up", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (titleState.equals("Changing")) titleState = "Freed Shadow";
                }
            });

            // K → Night Parade
            im.put(KeyStroke.getKeyStroke("K"), "down");
            am.put("down", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (titleState.equals("Changing")) titleState = "Night Parade";
                }
            });

            // J → (reserved for Left if needed later)
            im.put(KeyStroke.getKeyStroke("J"), "left");
            am.put("left", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // You can assign this state to something later if needed
                   if (titleState.equals("Changing")) titleState = "TSS";
                }
            });

            // L → (reserved for Right if needed later)
            im.put(KeyStroke.getKeyStroke("L"), "right");
            am.put("right", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // You can assign this state to something later if needed
                    System.out.println("Right (L) pressed — no state yet.");
                }
            });

            // Night Parade activation keys
            for (String key : new String[]{"F", "T", "R", "E", "Q", "A", "S", "D"}) {
                im.put(KeyStroke.getKeyStroke(key), "NP_" + key);
                am.put("NP_" + key, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (titleState.equals("Night Parade")) {
                            if (!timers.get(1).active) {
                                System.out.println("Night Parade Activated");
                                timers.get(1).start(30);
                            }
                            titleState = "Neutral";
                        }
                    }
                });
            }

            // Freed Shadow (Caps Lock)
            im.put(KeyStroke.getKeyStroke("control CONTROL"), "FS");
            am.put("FS", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (titleState.equals("Freed Shadow")) {
                        if (!timers.get(0).active) {
                            System.out.println("Freed Shadow Activated");
                            timers.get(0).start(60);
                        }
                        titleState = "Neutral";
                    }
                }
            });

            im.put(KeyStroke.getKeyStroke("5"), "TSS");
            am.put("TSS", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (titleState.equals("TSS")) {
                        if (!timers.get(2).active) {   // index 2 = TSS timer
                            System.out.println("TSS Activated");
                            timers.get(2).start(30);  // 30 seconds duration
                        }
                        titleState = "Neutral";
                    }
                }
            });
        }

        private void resetTimers() {
            timers.get(0).reset();
            timers.get(1).reset();
            timers.get(2).reset();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);
            for (Timer timer : timers) {
                timer.draw(g);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Elsword Timers");
        TimerPanel panel = new TimerPanel();

        frame.add(panel);
        frame.setSize(250, 320);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
}

