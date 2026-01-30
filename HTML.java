package execution;

import IDE.mainWindow;
import utils.handleFiles;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class HTML {

    private final mainWindow parentWindow;
    private final JTabbedPane editorPane;
    private handleFiles fh;

    public HTML(mainWindow parentWindow, JTabbedPane editorPane) {
        this.parentWindow = parentWindow;
        this.editorPane = editorPane;
        this.fh = new handleFiles();
    }

    void handleHtmlFile(File file) throws IOException {
        String[] options = {"Preview HTML", "Open in Browser", "Cancel"};
        int choice = JOptionPane.showOptionDialog(parentWindow,
                "How would you like to run this HTML file?",
                "Run HTML File",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0: // Preview HTML
                this.showHtmlPreview(file);
                break;
            case 1: // Open in Browser
                this.openFileInDefaultBrowser(file);
                break;
            default:
                break;
        }
    }
    /**
     * Opens an HTML file in a custom preview window.
     * we should think about doing this in FX
     */
    public void showHtmlPreview(File file) {
        try {
            String content = fh.readFileContent(file);

            JFrame htmlFrame = new JFrame("HTML Preview - " + file.getName());
            htmlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            htmlFrame.setSize(900, 700);

            JTabbedPane tabbedPane = new JTabbedPane();


            JEditorPane htmlPane = new JEditorPane();
            htmlPane.setContentType("text/html");
            htmlPane.setEditable(false);

            try {
                htmlPane.setPage(file.toURI().toURL());
            } catch (IOException e) {
                htmlPane.setText(content);
            }


            htmlPane.addHyperlinkListener(e -> {
                if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        }
                    } catch (Exception ex) {
                    }
                }
            });

            JScrollPane htmlScrollPane = new JScrollPane(htmlPane);
            tabbedPane.addTab("Preview", htmlScrollPane);

            JTextArea sourceArea = new JTextArea(content);
            sourceArea.setEditable(false);
            sourceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            sourceArea.setTabSize(2);
            sourceArea.setBackground(new Color(40, 44, 52));
            sourceArea.setForeground(new Color(171, 178, 191));
            sourceArea.setCaretColor(Color.WHITE);

            JScrollPane sourceScrollPane = new JScrollPane(sourceArea);
            tabbedPane.addTab("Source", sourceScrollPane);

            //  buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(e -> {
                try {
                    String newContent = fh.readFileContent(file);
                    htmlPane.setText(newContent);
                    sourceArea.setText(newContent);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(htmlFrame,
                            "Error refreshing file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton openInBrowserButton = new JButton("Open in Browser");
            openInBrowserButton.addActionListener(e -> {
                openFileInDefaultBrowser(file);
            });

            buttonPanel.add(refreshButton);
            buttonPanel.add(openInBrowserButton);

            htmlFrame.setLayout(new BorderLayout());
            htmlFrame.add(tabbedPane, BorderLayout.CENTER);
            htmlFrame.add(buttonPanel, BorderLayout.SOUTH);

            htmlFrame.setLocationRelativeTo(null);
            htmlFrame.setVisible(true);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentWindow,
                    "Could not read HTML file for preview: " + file.getName() + "\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void openFileInDefaultBrowser(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(file);
                } else {
                }
            } else {
            }
        } catch (IOException e) {
        }
    }

    public void runHtmlInBrowser(File file) {
        try {
            String fileName = file.getName().toLowerCase();
            if (!fileName.endsWith(".html") && !fileName.endsWith(".htm")) {
                return;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(file.toURI());
                } else {
                }
            } else {
            }
        } catch (IOException e) {
        }
    }


}
