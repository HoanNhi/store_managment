package adapter;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import structure.*;

import java.sql.SQLException;
import java.util.List;
import java.sql.Date;


public interface DataAdapterInterface {
    //Product related functions
    Product loadProduct(int id);
    List<Product> loadAllProducts();
    String[][] loadAllProductsData();
    boolean saveProduct(Product product);
    boolean deleteProduct(int productID);


    //Order related functions
    Order loadOrder(int id);
    List<Order> loadAllOrders();
    boolean saveOrder(Order order);
    boolean deleteOrder(int orderID);
    List<Order> loadOrdersByDateRange(Date startDate, Date endDate);
    List<Product> loadProductsByPriceRange(double minPrice, double maxPrice);
    List<Product> loadProductsByCustomQuery(String query);

    //User related functions
    User loadUser(String username, String password);
    User loadUser(int userID);
    List<User> loadAllUsers();
    String[][] loadAllUsersData() throws SQLException;
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int employeeID);

    //Supplier related functions
    Supplier loadSupplier(int supplierID);
    List<Supplier> loadAllSuppliers();
    boolean saveSupplier(Supplier supplier);
    boolean deleteSupplier(int supplierID);

    //Customer related functions
    Customer loadCustomer(int customerID);
    boolean createCustomer(Customer customer);
    boolean saveCustomer(Customer customer);
    boolean deleteCustomer(int customerID);
    Document loadCustomerDoc(int userID);
    MongoCollection<Document> getCustomerCollection();

    //Shipper related functions
    Document loadShipper(int shipperID);
    List<Shipper> loadAllShippers();
    boolean deleteShipper(int shipperID);
    MongoCollection<Document> loadAllShippersDoc();


    boolean updateCustomer(Customer updatedCustomer);

}