import java.util.List;

public interface ShoppingManager {
    void addNewProduct();

    void deleteProduct();

    void printProductList();

    void saveProductsToFile();

    void loadProductsFromTextFile();

    List<Product> loadProductsFromFile();
}
