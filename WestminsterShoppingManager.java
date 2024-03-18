import java.io.*;
import java.util.*;
import java.util.Collections;
public class WestminsterShoppingManager{
    Scanner scanner = new Scanner(System.in);
    private final List<Product> productList = new ArrayList<>();
    private static final String FILE_PATH = "productList.txt";
    private boolean productsLoadedFromFile = false; // Initialized to false
    public void manageProducts() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Westminster Shopping Manager Menu ===");
            System.out.println("1. Add a new product");
            System.out.println("2. Delete a product");
            System.out.println("3. Print the list of products");
            System.out.println("4. Save products in a file");
            System.out.println("5. Exit");

            try {
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // newline character

                switch (choice) {
                    case 1:
                        addNewProduct();
                        break;
                    case 2:
                        deleteProduct();
                        break;
                    case 3:
                        printProductList();
                        break;
                    case 4:
                        saveProductsToFile();
                        break;
                    case 5:
                        System.out.println("Exiting Westminster Shopping Manager. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                scanner.nextLine(); // clear the invalid input from the scanner
                choice = 0; // Reset choice to force re-entry in the loop
            }
        } while (choice != 5);
    }

    public void addNewProduct() {
        if (productList.size() >= 50) {
            System.out.println("Maximum limit of 50 products reached. Cannot add more products.");
            return;
        }

        int productTypeChoice = 0;

        while (true) {
            try {
                System.out.println("\nChoose product type:");
                System.out.println("1. Electronics");
                System.out.println("2. Clothing");

                System.out.print("Enter your choice: ");
                productTypeChoice = scanner.nextInt();
                scanner.nextLine(); // consume newline character

                if (productTypeChoice == 1 || productTypeChoice == 2) {
                    break; // exit the loop if input is 1 or 2
                } else {
                    System.out.println("Invalid choice. Please enter 1 for Electronics or 2 for Clothing.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear the invalid input from the scanner
            }
        }

        System.out.print("Enter product ID: ");
        String productId = scanner.nextLine();

        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();

        int availableItems = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.print("Enter number of available items: ");
                int input = scanner.nextInt();

                if (input > 0) {
                    availableItems = input;
                    validInput = true;
                } else {
                    System.out.println("Invalid input. Please enter a non-negative integer.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine(); // Consume the invalid input
            }
        }

        double price = 0.0;
        validInput = false;
        while (!validInput) {
            try {
                System.out.print("Enter price: Rs.");
                price = scanner.nextDouble();
                scanner.nextLine(); // Consume the newline character after reading a double
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Consume the invalid input
            }
        }

        switch (productTypeChoice) {
            case 1:
                System.out.print("Enter brand: ");
                String brand = scanner.nextLine();

                int warrantyPeriod = 0;
                validInput = false;
                while (!validInput) {
                    try {
                        System.out.print("Enter warranty period in year: ");
                        warrantyPeriod = scanner.nextInt();
                        if (warrantyPeriod < 0) {
                            throw new InputMismatchException(); // If negative, treat as invalid input
                        }
                        scanner.nextLine();
                        validInput = true;
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a valid non-negative integer for warranty period.");
                        scanner.nextLine(); // Consume the invalid input
                    }
                }
                Electronics electronics = new Electronics(productId, productName, availableItems, price, brand, warrantyPeriod);
                productList.add(electronics);
                System.out.println("Electronics product added.");
                break;

            case 2:
                // List of clothing sizes
                List<String> sizeOptions = Arrays.asList("S", "M", "L", "XL", "XXL");

                // Display available size options
                System.out.println("Choose clothing size:");
                for (int i = 0; i < sizeOptions.size(); i++) {
                    System.out.println((i + 1) + ". " + sizeOptions.get(i));
                }

                // Get user's choice for size
                int sizeChoice = -1; // Initialize with an invalid value
                while (sizeChoice < 0 || sizeChoice > sizeOptions.size()) {
                    System.out.print("Enter your choice: ");
                    try {
                        sizeChoice = Integer.parseInt(scanner.nextLine());
                        if (sizeChoice < 1 || sizeChoice > sizeOptions.size()) {
                            System.out.println("Invalid size choice. Please enter a valid number.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                }

                String size = sizeOptions.get(sizeChoice - 1);

                System.out.print("Enter color: ");
                String color = scanner.nextLine();

                Clothing clothing = new Clothing(productId, productName, availableItems, price, size, color);
                productList.add(clothing);
                System.out.println("Clothing product added.");
                break;
            default:
                System.out.println("Invalid product type choice.");
        }
    }
    private void deleteProduct() {
        // Load products from file
        List<Product> loadedProducts = loadProductsFromFile();

        // Combine loaded products with existing products
        List<Product> combinedProducts = new ArrayList<>(productList);
        combinedProducts.addAll(loadedProducts);

        System.out.print("Enter product ID to delete: ");
        String productId = scanner.nextLine();

        // Variable to keep track of the total number of products left
        int totalProductsLeft = combinedProducts.size();

        // Variable to store information about the deleted product
        String deletedProductInfo = "";

        // Iterate over a copy of the list to avoid concurrent modification
        for (Product product : new ArrayList<>(combinedProducts)) {
            if (product.getProductId().equals(productId)) {
                deletedProductInfo = product.getType() + " - " + product.getProductId() + " " + product.getProductName();

                // Remove the product from the list
                combinedProducts.remove(product);

                // Update the total number of products left
                totalProductsLeft = combinedProducts.size();
                break;
            }
        }

        if (!deletedProductInfo.isEmpty()) {
            // Display information about the deleted product
            System.out.println(deletedProductInfo + " product removed successfully");
        } else {
            // Product with the specified ID not found
            System.out.println("Product with ID " + productId + " not found.");
        }

        // Display the total number of products left in the system
        System.out.println("Total number of products left in the system: " + totalProductsLeft);
    }
    public void printProductList() {
        // Load products from file
        List<Product> loadedProducts = loadProductsFromFile();

        if (!loadedProducts.isEmpty()) {
            // Combine loaded products with existing products
            List<Product> combinedProducts = new ArrayList<>(productList);
            combinedProducts.addAll(loadedProducts);

            // Sort the combinedProducts alphabetically by product ID
            Collections.sort(combinedProducts, (p1, p2) -> p1.getProductId().compareTo(p2.getProductId()));

            System.out.println("\n=== Product List ===");
            for (Product product : combinedProducts) {
                System.out.println(product);
            }
        } else {
            System.out.println("Loaded product list is empty.");
        }
    }
    public void saveProductsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            for (Product product : productList) {
                if (product instanceof Electronics) {
                    writer.write("Electronics:");
                } else if (product instanceof Clothing) {
                    writer.write("Clothing:");
                }
                writer.write("\nProduct ID: " + product.getProductId());
                writer.write("\nProduct Name: " + product.getProductName());
                writer.write("\nAvailable Items: " + product.getAvailableItems());
                writer.write("\nPrice: " + product.getPrice());
                if (product instanceof Electronics electronics) {
                    writer.write("\nBrand: " + electronics.getBrand());
                    writer.write("\nWarranty Period: " + electronics.getWarrantyPeriod() + "\n");
                } else if (product instanceof Clothing clothing) {
                    writer.write("\nSize: " + clothing.getSize());
                    writer.write("\nColor: " + clothing.getColor() + "\n");
                }
            }
            System.out.println("Products saved to file.");
        } catch (IOException e) {
            System.err.println("Error saving product list to file: " + e.getMessage());
        }
    }
    public List<Product> loadProductsFromFile() {
        List<Product> loadedProducts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.equals("Electronics:")) {
                    loadedProducts.add(readElectronics(reader));
                } else if (line.equals("Clothing:")) {
                    loadedProducts.add(readClothing(reader));
                }
            }
            System.out.println("Products loaded from file.");
        } catch (IOException e) {
            System.err.println("An error occurred trying to load from file: " + e.getMessage());
        }
        return loadedProducts;
    }
    private Electronics readElectronics(BufferedReader reader) throws IOException {
        // Read values for Electronics attributes
        String productId = readValue(reader, "Product ID:");
        String productName = readValue(reader, "Product Name:");
        int availableItems = Integer.parseInt(readValue(reader, "Available Items:"));
        double price = Double.parseDouble(readValue(reader, "Price:"));
        String brand = readValue(reader, "Brand:");
        int warrantyPeriod = Integer.parseInt(readValue(reader, "Warranty Period:"));

        // Create and return an Electronics object
        return new Electronics(productId, productName, availableItems, price, brand, warrantyPeriod);
    }
    private Clothing readClothing(BufferedReader reader) throws IOException {
        // Read values for Clothing attributes
        String productId = readValue(reader, "Product ID:");
        String productName = readValue(reader, "Product Name:");
        int availableItems = Integer.parseInt(readValue(reader, "Available Items:"));
        double price = Double.parseDouble(readValue(reader, "Price:"));
        String size = readValue(reader, "Size:");
        String color = readValue(reader, "Color:");

        // Create and return a Clothing object
        return new Clothing(productId, productName, availableItems, price, size, color);
    }
    private String readValue(BufferedReader reader, String attribute) throws IOException {
        // Read a line from the file
        String line = reader.readLine();

        // Check if the line starts with the expected attribute
        if (line != null && line.startsWith(attribute)) {
            // Remove the attribute and trim the value
            return line.substring(attribute.length()).trim();
        } else {
            throw new IOException("Invalid file format. Expected attribute: " + attribute);
        }
    }
}
