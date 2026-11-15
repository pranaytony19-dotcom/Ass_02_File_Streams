import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductMaker extends JFrame {

    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField idField;
    private JTextField costField;
    private JTextField countField;

    private int recordCount = 0;
    private final File dataFile = new File("products.dat");

    public RandProductMaker() {
        setTitle("Random Product Maker");
        setSize(550, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nameField = new JTextField();
        descriptionField = new JTextField();
        idField = new JTextField();
        costField = new JTextField();
        countField = new JTextField("0");
        countField.setEditable(false);

        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        panel.add(new JLabel("Product ID:"));
        panel.add(idField);

        panel.add(new JLabel("Cost:"));
        panel.add(costField);

        panel.add(new JLabel("Record Count:"));
        panel.add(countField);

        JButton addButton = new JButton("Add Product");
        JButton quitButton = new JButton("Quit");

        panel.add(addButton);
        panel.add(quitButton);

        add(panel);

        addButton.addActionListener(e -> addProduct());
        quitButton.addActionListener(e -> System.exit(0));

        if (dataFile.exists()) {
            recordCount = (int) (dataFile.length() / Product.RECORD_SIZE);
            countField.setText(String.valueOf(recordCount));
        }
    }

    private void addProduct() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String id = idField.getText().trim();
        String costText = costField.getText().trim();

        if (name.isEmpty() || description.isEmpty()
                || id.isEmpty() || costText.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please complete all fields.");
            return;
        }

        if (name.length() > Product.NAME_LENGTH
                || description.length() > Product.DESCRIPTION_LENGTH
                || id.length() > Product.ID_LENGTH) {

            JOptionPane.showMessageDialog(this,
                    "Name max: 35 characters\n" +
                            "Description max: 75 characters\n" +
                            "ID max: 6 characters");
            return;
        }

        double cost;

        try {
            cost = Double.parseDouble(costText);

            if (!Double.isFinite(cost) || cost < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid nonnegative cost.");
            return;
        }

        Product product = new Product(name, description, id, cost);

        try (RandomAccessFile file =
                     new RandomAccessFile(dataFile, "rw")) {

            // Prevent duplicate product IDs.
            long totalRecords = file.length() / Product.RECORD_SIZE;

            for (long i = 0; i < totalRecords; i++) {
                file.seek(i * Product.RECORD_SIZE);
                Product existing = Product.readFromFile(file);

                if (existing.getID().equalsIgnoreCase(id)) {
                    JOptionPane.showMessageDialog(this,
                            "This Product ID already exists.");
                    return;
                }
            }

            // Move to the end of the file.
            file.seek(file.length());
            product.writeToFile(file);

            recordCount++;
            countField.setText(String.valueOf(recordCount));

            JOptionPane.showMessageDialog(this,
                    "Product saved successfully!");

            nameField.setText("");
            descriptionField.setText("");
            idField.setText("");
            costField.setText("");

            nameField.requestFocusInWindow();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving product: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RandProductMaker frame = new RandProductMaker();
            frame.setVisible(true);
        });
    }
}