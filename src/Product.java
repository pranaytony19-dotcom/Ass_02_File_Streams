import java.io.IOException;
import java.io.RandomAccessFile;

public class Product {
    public static final int NAME_LENGTH = 35;
    public static final int DESCRIPTION_LENGTH = 75;
    public static final int ID_LENGTH = 6;

    public static final int RECORD_SIZE =
            (NAME_LENGTH + DESCRIPTION_LENGTH + ID_LENGTH) * 2 + 8;

    private String name;
    private String description;
    private final String ID;
    private double cost;

    public Product(String name, String description, String ID, double cost) {
        this.name = name.trim();
        this.description = description.trim();
        this.ID = ID.trim();
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getID() {
        return ID;
    }

    public double getCost() {
        return cost;
    }

    public static String padString(String value, int length) {
        if (value.length() > length) {
            throw new IllegalArgumentException("Field is too long.");
        }

        StringBuilder result = new StringBuilder(value);

        while (result.length() < length) {
            result.append(" ");
        }

        return result.toString();
    }

    public void writeToFile(RandomAccessFile file) throws IOException {
        file.writeChars(padString(name, NAME_LENGTH));
        file.writeChars(padString(description, DESCRIPTION_LENGTH));
        file.writeChars(padString(ID, ID_LENGTH));
        file.writeDouble(cost);
    }

    public static Product readFromFile(RandomAccessFile file)
            throws IOException {

        String name = readFixedString(file, NAME_LENGTH);
        String description = readFixedString(file, DESCRIPTION_LENGTH);
        String ID = readFixedString(file, ID_LENGTH);
        double cost = file.readDouble();

        return new Product(name, description, ID, cost);
    }

    private static String readFixedString(RandomAccessFile file, int length)
            throws IOException {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(file.readChar());
        }

        return result.toString().trim();
    }

    @Override
    public String toString() {
        return "ID: " + ID +
                "\nName: " + name +
                "\nDescription: " + description +
                "\nCost: $" + String.format("%.2f", cost);
    }
}
