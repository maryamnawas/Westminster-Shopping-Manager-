import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ShoppingGUI class represents the main graphical user interface for the Westminster Shopping Centre application.
 * It allows users to browse and interact with a list of products, view details, and add products to a shopping cart.
 */
public class ShoppingGUI extends JFrame {
    private final WestminsterShoppingManager shoppingManager;
    private final DefaultTableModel tableModel;
    private final JTextArea selectedProductDetailsTextArea;
    private ShoppingCartGUI shoppingCartGUI; // Declare shoppingCartGUI as an instance variable

    /**
     * Constructor for the ShoppingGUI class.
     * Initializes the main frame, sets up components, and displays the GUI.
     *
     * @param shoppingManager The WestminsterShoppingManager instance managing the product data.
     */
    public ShoppingGUI(WestminsterShoppingManager shoppingManager) {
        this.shoppingManager = shoppingManager;

        setTitle("Westminster Shopping Centre");
        setLayout(new BorderLayout());

        // Create an EmptyBorder for the whole frame
        EmptyBorder frameEmptyBorder = new EmptyBorder(10, 10, 10, 10);

        // Apply the EmptyBorder to the content pane
        Container contentPane = getContentPane();
        ((JComponent) contentPane).setBorder(frameEmptyBorder);

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());

        // Center the categoryComboBox
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(new JLabel("Select Product Category"));
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"All", "Electronics", "Clothing"});
        centerPanel.add(categoryComboBox);

        topPanel.add(centerPanel, BorderLayout.CENTER);

        // Shopping Cart button in the top-right corner
        JButton shoppingCartButton = new JButton("Shopping Cart");
        topPanel.add(shoppingCartButton, BorderLayout.EAST);

        // Add ActionListener to the Shopping Cart button
        shoppingCartButton.addActionListener(e -> {
            // Check if the shopping cart GUI is not null and if there are items in the cart
            if (shoppingCartGUI != null && shoppingCartGUI.getCartTableModel().getRowCount() > 0) {
                // Show the shopping cart GUI
                shoppingCartGUI.setVisible(true);
            } else {
                // Display a message that there are no products in the cart
                JOptionPane.showMessageDialog(this, "No products in the cart.", "Empty Cart", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanelWithDetails = new JPanel(new BorderLayout());

        // Table
        String[] columnNames = {"Product ID", "Name", "Category", "Price(£)", "Info"};
        tableModel = new DefaultTableModel(null, columnNames);
        JTable table = new JTable(tableModel);

        // Set row height to increase the size of the table cells
        table.setRowHeight(40);

        // Set custom cell renderer to center the text vertically
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setVerticalAlignment(SwingConstants.CENTER);

        // Set custom cell renderer to color cells based on availability
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                Object productId = table.getValueAt(row, 0); // Assuming the Product ID is in the first column

                if (productId != null) {
                    for (Product p : shoppingManager.loadProductsFromFile()) {
                        if (Objects.equals(productId, p.getProductId())) {
                            int availableItems = p.getAvaliableItems();
                            if (availableItems < 3) {
                                cellComponent.setBackground(Color.RED); // Set the background color to red for cells with less than 3 items available
                                cellComponent.setForeground(Color.WHITE); // Set text color to white for better visibility
                            } else {
                                cellComponent.setBackground(table.getBackground()); // Set default background color
                                cellComponent.setForeground(table.getForeground()); // Set default text color
                            }
                        }
                    }
                }

                return cellComponent;
            }
        });

        // Add spacing around the table
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        centerPanelWithDetails.add(tableScrollPane, BorderLayout.CENTER);

        // Selected Product Details
        selectedProductDetailsTextArea = new JTextArea("\nSelected Product - Details");
        selectedProductDetailsTextArea.setEditable(false);

        // Add padding to the printed text
        selectedProductDetailsTextArea.setMargin(new Insets(15, 15, 15, 15));

        centerPanelWithDetails.add(selectedProductDetailsTextArea, BorderLayout.SOUTH);

        add(centerPanelWithDetails, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        JButton addToCartButton = new JButton("Add to Shopping Cart");
        bottomPanel.add(addToCartButton, BorderLayout.SOUTH);

        // Add ActionListener to the "Add to Shopping Cart" button
        addToCartButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                addToShoppingCart(selectedRow);
            }
        });

        add(bottomPanel, BorderLayout.SOUTH);

        // ActionListener for categoryComboBox
        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            refreshTable(selectedCategory);
        });

        // ListSelectionListener for table
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    displaySelectedProductDetails(selectedRow);
                }
            }
        });

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Refreshes the table based on the selected product category.
     *
     * @param selectedCategory The selected product category.
     */
    public void refreshTable(String selectedCategory) {
        List<Product> products;

        if ("All".equalsIgnoreCase(selectedCategory)) {
            products = shoppingManager.loadProductsFromFile();
        } else {
            products = shoppingManager.loadProductsFromFile();
            products = filterProductsByCategory(products, selectedCategory);
        }

        // Sort products alphabetically by ID
        Collections.sort(products, (p1, p2) -> p1.getProductId().compareTo(p2.getProductId()));

        // Remove all rows from the table
        tableModel.setRowCount(0);

        for (Product product : products) {
            // Add product details to the table
            Object[] rowData = new Object[5];
            rowData[0] = product.getProductId();
            rowData[1] = product.getProductName();
            rowData[2] = product instanceof Clothing ? "Clothing" : "Electronics";
            rowData[3] = product.getPrice();

            if (product instanceof Electronics electronics) {
                rowData[4] = electronics.getBrand() + ", " + electronics.getWarrantyPeriod() + " years warranty";
            } else if (product instanceof Clothing clothing) {
                rowData[4] = clothing.getSize() + ", " + clothing.getColour();
            }
            tableModel.addRow(rowData);
        }

        // Refresh the entire frame
        revalidate();
        repaint();
    }
    /**
     * Filters products based on the selected category.
     *
     * @param products         The list of products to filter.
     * @param selectedCategory The selected category.
     * @return A list of products belonging to the selected category.
     */
    private List<Product> filterProductsByCategory(List<Product> products, String selectedCategory) {
        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : products) {
            String productCategory = (product instanceof Clothing) ? "Clothing" : "Electronics";
            if (selectedCategory.equalsIgnoreCase(productCategory)) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    /**
     * Displays the details of the selected product in the text area.
     *
     * @param selectedRow The selected row in the table.
     */
    private void displaySelectedProductDetails(int selectedRow) {
        selectedProductDetailsTextArea.setText("\nSelected Product - Details\n");

        String productId = (String) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);
        String category = (String) tableModel.getValueAt(selectedRow, 2);

        selectedProductDetailsTextArea.append("\nProduct ID: " + productId);
        selectedProductDetailsTextArea.append("\nCategory: " + category);
        selectedProductDetailsTextArea.append("\nName: " + productName);

        if ("Electronics".equalsIgnoreCase(category)) {
            String brandAndWarranty = (String) tableModel.getValueAt(selectedRow, 4);
            String[] brandAndWarrantyArray = brandAndWarranty.split(", ");
            String brand = brandAndWarrantyArray[0];
            String warranty = brandAndWarrantyArray[1];

            selectedProductDetailsTextArea.append("\nBrand: " + brand);
            selectedProductDetailsTextArea.append("\nWarranty: " + warranty);
        } else if ("Clothing".equalsIgnoreCase(category)) {
            String sizeAndColor = (String) tableModel.getValueAt(selectedRow, 4);
            String[] sizeAndColorArray = sizeAndColor.split(", ");
            String size = sizeAndColorArray[0];
            String color = sizeAndColorArray[1];

            selectedProductDetailsTextArea.append("\nSize: " + size);
            selectedProductDetailsTextArea.append("\nColor: " + color);
        }

        // Retrieve the available items from the file
        String availableItems = getAvailableItems(productId);
        selectedProductDetailsTextArea.append("\nAvailable Items: " + availableItems);
    }

    /**
     * Retrieves the available items for a specific product ID.
     *
     * @param productId The ID of the product.
     * @return The number of available items or "N/A" if the product is not found.
     */
    private String getAvailableItems(String productId) {
        // Load products from the file
        List<Product> products = shoppingManager.loadProductsFromFile();

        // Search for the product with the specified ID
        for (Product product : products) {
            if (productId.equals(product.getProductId())) {
                // Return the available items for the found product
                return String.valueOf(product.getAvaliableItems());
            }
        }

        // If the product is not found, return a default value
        return "N/A";
    }

    /**
     * Handles the addition of a selected product to the shopping cart.
     * Retrieves product details, deducts the available quantity, and adds the product to the cart.
     * Displays appropriate messages for success and out-of-stock situations.
     *
     * @param selectedRow The selected row from the product table.
     */
    private void addToShoppingCart(int selectedRow) {
        String productId = (String) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);
        String category = (String) tableModel.getValueAt(selectedRow, 2);

        // Retrieve the product from the productList based on the productId
        Product selectedProduct = getProductById(productId);

        if (selectedProduct != null && selectedProduct.getAvaliableItems() > 0) {
            // Reduce the available items by 1
            selectedProduct.setAvaliableItems(selectedProduct.getAvaliableItems() - 1);

            String productInfo;
            if ("Electronics".equalsIgnoreCase(category)) {
                String brandAndWarranty = (String) tableModel.getValueAt(selectedRow, 4);
                String[] brandAndWarrantyArray = brandAndWarranty.split(", ");
                String brand = brandAndWarrantyArray[0];
                String warranty = brandAndWarrantyArray[1];
                productInfo = "Brand: " + brand + ", Warranty: " + warranty;
            } else if ("Clothing".equalsIgnoreCase(category)) {
                String sizeAndColor = (String) tableModel.getValueAt(selectedRow, 4);
                String[] sizeAndColorArray = sizeAndColor.split(", ");
                String size = sizeAndColorArray[0];
                String color = sizeAndColorArray[1];
                productInfo = "Size: " + size + ", Color: " + color;
            } else {
                // Default case if the category is not recognized
                productInfo = "Info not available";
            }

            // Get quantity and calculate total price (you can modify this logic based on your requirement)
            int quantity = 1; // You may adjust the quantity as needed
            double totalPrice = Double.parseDouble(tableModel.getValueAt(selectedRow, 3).toString()) * quantity;

            // Format the product details for the shopping cart
            String formattedProduct = productId + ", " + productName + ", " + category + ", " + productInfo;

            // Check if shoppingCartGUI is null and create a new instance
            if (shoppingCartGUI == null) {
                shoppingCartGUI = new ShoppingCartGUI();
            }

            // Add the details to the shopping cart
            shoppingCartGUI.addToCartTable(formattedProduct, quantity, totalPrice);

            // Display a success message
            JOptionPane.showMessageDialog(this, "Product added to cart successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Refresh the table after adding to the cart
            refreshTable("All");

        } else {
            JOptionPane.showMessageDialog(this, "Product is out of stock!", "Out of Stock", JOptionPane.WARNING_MESSAGE);
        }
    }


    /**
     * Helper method to get a product by its ID from the productList.
     *
     * @param productId The ID of the product to retrieve.
     * @return The Product object corresponding to the given ID, or null if not found.
     */
    private Product getProductById(String productId) {
        for (Product product : shoppingManager.loadProductsFromFile()) {
            if (productId.equals(product.getProductId())) {
                return product;
            }
        }
        return null;
    }


    /**
    * Main method to launch the shopping application using SwingUtilities.invokeLater.
    * It initializes a WestminsterShoppingManager and creates an instance of the ShoppingGUI.
    */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a new WestminsterShoppingManager instance
            WestminsterShoppingManager shoppingManager = new WestminsterShoppingManager();

            // Launch the ShoppingGUI with the initialized shopping manager
            new ShoppingGUI(shoppingManager);
        });
    }
}
