import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the GUI for the shopping cart, allowing users to view and manage items in their cart.
 */
public class ShoppingCartGUI extends JFrame {
    // DefaultTableModel for the shopping cart table
    private final DefaultTableModel cartTableModel;

    // Lists to keep track of items in the shopping cart
    private final List<String> productIds;
    private final List<Integer> quantities;
    private final List<Double> totalPrices;

    // Map to store the count of items in each category for discount calculation
    private final Map<String, Integer> categoryCounts;

    /**
     * Constructor for ShoppingCartGUI.
     * Sets up the frame layout, initializes the shopping cart table, and sets up the bottom panel.
     */
    public ShoppingCartGUI() {
        setTitle("Shopping Cart");
        setLayout(new BorderLayout());

        // Create an EmptyBorder for the whole frame
        EmptyBorder frameEmptyBorder = new EmptyBorder(10, 10,  10, 10);

        // Apply the EmptyBorder to the content pane
        Container contentPane = getContentPane();
        ((JComponent) contentPane).setBorder(frameEmptyBorder);

        // Table for the shopping cart
        String[] cartColumnNames = {"Product", "Quantity", "Price"};
        cartTableModel = new DefaultTableModel(null, cartColumnNames);
        JTable cartTable = new JTable(cartTableModel);

        // Add spacing around the table
        JScrollPane cartTableScrollPane = new JScrollPane(cartTable);
        cartTableScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(cartTableScrollPane, BorderLayout.CENTER);

        // Initialize additional fields
        productIds = new ArrayList<>();
        quantities = new ArrayList<>();
        totalPrices = new ArrayList<>();
        categoryCounts = new HashMap<>();

        // Bottom panel for displaying total and discounts
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));

        // Create an EmptyBorder for the bottom panel
        EmptyBorder bottomPanelEmptyBorder = new EmptyBorder(0, 0, 10, 10);

        // Add the bottom panel to the frame with the specified EmptyBorder
        bottomPanel.setBorder(bottomPanelEmptyBorder);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(500, 400);
        setLocationRelativeTo(null);
    }

    /**
     * Getter method for cartTableModel.
     *
     * @return The DefaultTableModel for the shopping cart table.
     */
    public DefaultTableModel getCartTableModel() {
        return cartTableModel;
    }

    /**
     * Adds or updates an item in the shopping cart.
     *
     * @param product    The product details.
     * @param quantity   The quantity of the product.
     * @param totalPrice The total price of the product.
     */
    public void addToCartTable(String product, int quantity, double totalPrice) {
        // Check if the product is already in the cart
        int index = findProductIndex(getProductIdFromProductString(product));
        if (index != -1) {
            // Product already exists, update the quantity and total price
            int newQuantity = quantities.get(index) + quantity;
            double newTotalPrice = totalPrices.get(index) + totalPrice;

            // Update the item in the cart
            updateCartItem(index, product, newQuantity, newTotalPrice);
        } else {
            // Product doesn't exist, add it to the cart
            cartTableModel.addRow(new Object[]{product, quantity, totalPrice});

            // Keep track of the item details
            productIds.add(getProductIdFromProductString(product));
            quantities.add(quantity);
            totalPrices.add(totalPrice);

            // Update category counts for discount calculation
            updateCategoryCounts(product);

            // Update the bottom panel
            updateBottomPanel();
        }
    }

    /**
     * Updates category counts for discount calculation.
     *
     * @param product The product details.
     */
    private void updateCategoryCounts(String product) {
        String[] parts = product.split(", ");
        String category = parts[2];
        categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);

        // Print statement for verification
        System.out.println("Category counts updated: " + categoryCounts);
    }

    /**
     * Updates the bottom panel with total and discounts.
     */
    private void updateBottomPanel() {
        // Calculate total
        double total = totalPrices.stream().mapToDouble(Double::doubleValue).sum();

        // Calculate category discount (20% for at least three items in the same category)
        double categoryDiscount = categoryCounts.values().stream().filter(count -> count >= 3)
                .mapToDouble(count -> totalPrices.stream().mapToDouble(Double::doubleValue).sum() * 0.20).sum();

        // Calculate final total
        double finalTotal = total - categoryDiscount;

        // Update the bottom panel labels
        updateBottomPanelLabels(total, categoryDiscount, finalTotal);
    }

    /**
     * Updates bottom panel labels with total and discounts.
     *
     * @param total           The total price of items in the cart.
     * @param categoryDiscount The discount applied based on category counts.
     * @param finalTotal       The final total after applying discounts.
     */
    private void updateBottomPanelLabels(double total, double categoryDiscount, double finalTotal) {
        // Get the bottom panel
        JPanel bottomPanel = (JPanel) getContentPane().getComponent(1);

        // Remove existing components
        bottomPanel.removeAll();

        // Create an EmptyBorder for the labels with right alignment and increased gap
        EmptyBorder labelEmptyBorder = new EmptyBorder(0, 0, 10, 0);

        // Add labels to the bottom panel with the specified EmptyBorder
        bottomPanel.add(createRightAlignedLabel("Total: " + total, labelEmptyBorder));
        bottomPanel.add(createRightAlignedLabel("Category Discount: " + categoryDiscount, labelEmptyBorder));
        bottomPanel.add(createRightAlignedLabel("Final Total: " + finalTotal, labelEmptyBorder));

        // Repaint the frame to reflect changes
        revalidate();
        repaint();
    }

    /**
     * Creates a right-aligned JLabel with a specified EmptyBorder.
     *
     * @param text   The text content of the label.
     * @param border The EmptyBorder for the label.
     * @return The created JLabel.
     */
    private JLabel createRightAlignedLabel(String text, EmptyBorder border) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setBorder(border);
        return label;
    }

    /**
     * Updates an item in the shopping cart.
     *
     * @param index      The index of the item in the shopping cart.
     * @param product    The product details.
     * @param quantity   The updated quantity of the product.
     * @param totalPrice The updated total price of the product.
     */
    private void updateCartItem(int index, String product, int quantity, double totalPrice) {
        // Update the item in the shopping cart
        cartTableModel.setValueAt(product, index, 0);
        cartTableModel.setValueAt(quantity, index, 1);
        cartTableModel.setValueAt(totalPrice, index, 2);

        // Update the item details
        productIds.set(index, getProductIdFromProductString(product));
        quantities.set(index, quantity);
        totalPrices.set(index, totalPrice);

        // Update category counts for discount calculation
        updateCategoryCounts(product);

        // Update the bottom panel
        updateBottomPanel();
    }

    /**
     * Finds the index of a product in the shopping cart.
     *
     * @param productId The ID of the product to find.
     * @return The index of the product in the shopping cart, or -1 if not found.
     */
    private int findProductIndex(String productId) {
        return productIds.indexOf(productId);
    }

    /**
     * Extracts the product ID from the formatted product string.
     *
     * @param product The formatted product string.
     * @return The extracted product ID.
     */
    private String getProductIdFromProductString(String product) {
        // Extract the product ID from the formatted product string
        String[] parts = product.split(", ");
        return parts[0];
    }

    /**
     * Main method to launch the ShoppingCartGUI.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShoppingCartGUI());
    }
}
