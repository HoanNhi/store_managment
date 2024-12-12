package adapter;

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



    //User related functions
    User loadUser(String username, String password);
    String[][] loadAllUsersData() throws SQLException;
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int employeeID);

    //Supplier related functions
    Supplier loadSupplier(int supplierID);
    boolean saveSupplier(Supplier supplier);
    boolean deleteSupplier(int supplierID);

    //Customer related functions
    Customer loadCustomer(int customerID);
    boolean createCustomer(Customer customer);
    boolean saveCustomer(Customer customer);
    boolean deleteCustomer(int customerID);

    //Shipper related functions
    Shipper loadShipper(int shipperID);
    List<Shipper> loadAllShippers();
    boolean deleteShipper(int shipperID);


    boolean updateCustomer(Customer updatedCustomer);
}