import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class RaportApp extends JFrame {

    private JPanel mainPanel;
    private JTextArea reportArea;
    private JPanel bottomPanel;
    private JButton generateButton;
    private JProgressBar progressBar;

    public RaportApp() {
        super("INF.04 - Generator Raportów");
        setContentPane(mainPanel);
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        reportArea.setEditable(false);
        buildMenu();

        generateButton.addActionListener(e -> startGenerating());
    }

    private void buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuPlik = new JMenu("Plik");

        JMenuItem itemZapisz = new JMenuItem("Zapisz Raport");
        itemZapisz.setAccelerator(KeyStroke.getKeyStroke('S',
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        itemZapisz.addActionListener(e -> saveReport());

        JMenuItem itemWyjscie = new JMenuItem("Wyjscie");
        itemWyjscie.addActionListener(e -> System.exit(0));

        menuPlik.add(itemZapisz);
        menuPlik.addSeparator();
        menuPlik.add(itemWyjscie);
        menuBar.add(menuPlik);
        setJMenuBar(menuBar);
    }

    private void startGenerating() {
        generateButton.setEnabled(false);
        reportArea.setText("");
        progressBar.setValue(0);

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(300);
                    publish("Wiersz raportu nr " + i + " - dane wygenerowane\n");
                    setProgress(i * 10);
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String line : chunks) {
                    reportArea.append(line);
                }
            }

            @Override
            protected void done() {
                generateButton.setEnabled(true);
                progressBar.setValue(100);
                JOptionPane.showMessageDialog(RaportApp.this, "Raport wygenerowany");
            }
        };

        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer) evt.getNewValue());
            }
        });
        worker.execute();
    }

    private void saveReport() {
        if (reportArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Brak raportu do zapisania",
                    "Ostrzezenie", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz miejsce zapisu");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Pliki tekstowe (*.txt)", "txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            try {
                Files.writeString(file.toPath(), reportArea.getText(), StandardCharsets.UTF_8);
                JOptionPane.showMessageDialog(this, "Zapisano: " + file.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Blad zapisu " + ex.getMessage(),
                        "Blad", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RaportApp().setVisible(true));
    }
}
