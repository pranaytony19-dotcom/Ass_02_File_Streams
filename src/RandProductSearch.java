import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductSearch extends JFrame {

    private JTextField searchField;
    private JTextArea resultsArea;

    private final File dataFile = new File("products.dat");

    public RandProductSearch() {
        setTitle("Random Product Search");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        topPanel.add(new JLabel("Enter Partial Product Name:"),
                BorderLayout.WEST);

        searchField = new JTextField();
        topPanel.add(searchField, BorderLayout.CENTER);

        JButton searchButton = new JButton("Search");
        topPanel.add(searchButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();

        JButton clearButton = new JButton("Clear");
        JButton quitButton = new JButton("Quit");

        bottomPanel.add(clearButton);
        bottomPanel.add(quitButton);

        add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchProducts());

        searchField.addActionListener(e -> searchProducts());

        clearButton.addActionListener(e -> {
            searchField.setText("");
            resultsArea.setText("");
        });

        quitButton.addActionListener(e -> System.exit(0));
    }

    private void searchProducts() {
        String searchText = searchField.getText().trim().toLowerCase();

        resultsArea.setText("");

        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a product name to search.");
            return;
        }

        if (!dataFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "Product data file does not exist.");
            return;
        }

        int matches = 0;

        try (RandomAccessFile file =
                     new RandomAccessFile(dataFile, "r")) {

            long totalRecords = file.length() / Product.RECORD_SIZE;

            for (long i = 0; i < totalRecords; i++) {
                file.seek(i * Product.RECORD_SIZE);

                Product product = Product.readFromFile(file);

                if (product.getName().toLowerCase().contains(searchText)) {
                    resultsArea.append(product.toString());
                    resultsArea.append("\n-----------------------------\n");
                    matches++;
                }
            }

            if (matches == 0) {
                resultsArea.setText("No matching products found.");
            } else {
                resultsArea.append("\nTotal Matches: " + matches);
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error reading file: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RandProductSearch frame = new RandProductSearch();
            frame.setVisible(true);
        });
    }
}