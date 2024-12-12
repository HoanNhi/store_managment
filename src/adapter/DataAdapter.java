package adapter;

import structure.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter implements DataAdapterInterface {
    private Connection connection;

    public DataAdapter(Connection connection) {
        this.connection = connection;
    }

    public Product loadProduct(int id) {
        try {
            String query = "SELECT * FROM Product WHERE ProductID = ?"; // Use parameterized query

            PreparedStatement statement = connection.prepareStatement(query); // Use PreparedStatement
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Product product = new Product();
                product.setProductID(resultSet.getInt("ProductID")); // Use column names
                product.setName(resultSet.getString("Name"));
                product.setPrice(resultSet.getDouble("Unit_price")); // Unit_price
                product.setCategory(resultSet.getString("Category")); // Add CategoryID
                product.setSupplierID(resultSet.getInt("SupplierID")); // Add SupplierID
                product.setDescription(resultSet.getString("Description")); // Add Description
                product.setQuantity(resultSet.getInt("Quantity")); //Add quantity
                // No quantity in structure.Product table according to schema
                resultSet.close();
                statement.close();
                return product;
            }

        } catch (SQLException e) {
            System.err.println("Database access error in loadProduct!"); // Use System.err for errors
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> loadAllProducts() {
        try {
            List<Product> products = new ArrayList<>();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Product"); // Select all orders

            while (resultSet.next()) {
                Product product = new Product();
                product.setProductID(resultSet.getInt("ProductID"));
                product.setName(resultSet.getString("Name"));
                product.setCategory(resultSet.getString("Category"));
                product.setSupplierID(resultSet.getInt("SupplierID"));
                product.setPrice(resultSet.getDouble("Unit_price"));
                product.setDescription(resultSet.getString("Description"));
                product.setQuantity(resultSet.getInt("Quantity"));
                products.add(product); // Add each loaded order to the List
            }

            resultSet.close();
            statement.close();
            return products; // Return the list of orders

        } catch (SQLException e) {
            System.err.println("Database access error in loadAllOrders!");
            e.printStackTrace();
            return null; //Return null in case of error
        }
    }

    public String[][] loadAllProductsData() { // Renamed to loadAllProductsData
        try {
            List<String[]> rows = new ArrayList<>();

            String query = "SELECT ProductID, Name, Category, SupplierID, Unit_price, Description, Quantity FROM Product";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String[] row = new String[7]; // Array for each row (7 columns)
                row[0] = String.valueOf(resultSet.getInt("ProductID"));
                row[1] = resultSet.getString("Name");
                row[2] = resultSet.getString("Category");
                row[3] = String.valueOf(resultSet.getInt("SupplierID")); // Convert SupplierID to String
                row[4] = String.valueOf(resultSet.getInt("Unit_price"));// Convert Unit_price to String
                row[5] = resultSet.getString("Description");
                row[6] = String.valueOf(resultSet.getInt("Quantity"));  // Convert Quantity to String
                rows.add(row);
            }

            resultSet.close();
            statement.close();

            return rows.toArray(new String[0][]);

        } catch (SQLException e) {
            System.err.println("Database access error in loadAllProductsData: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Product> findProductWithHTTP(Integer productID, String name, Double priceLessThan, Double priceGreaterThan) {
        List<Product> products = new ArrayList<>();

        try {
            StringBuilder sql = new StringBuilder("SELECT * FROM Product WHERE 1=1"); // Start with a always-true condition
            List<Object> params = new ArrayList<>(); // List to hold parameters


            if (productID != null) {
                sql.append(" AND ProductID = ?");
                params.add(productID);
            }

            if (name != null) {
                sql.append(" AND Name LIKE ?"); // Use LIKE for partial string matching
                params.add("%" + name + "%");  // Add wildcards
            }

            if (priceLessThan != null) {
                sql.append(" AND Unit_Price < ?");
                params.add(priceLessThan);
            }

            if (priceGreaterThan != null) {
                sql.append(" AND Unit_Price > ?");
                params.add(priceGreaterThan);
            }


            PreparedStatement statement = connection.prepareStatement(sql.toString());


            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    statement.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    statement.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    statement.setDouble(i + 1, (Double) param);
                }
                // ... add handling for other data types if needed ...


            }


            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductID(rs.getInt("ProductID"));
                product.setName(rs.getString("Name"));
                product.setPrice(rs.getDouble("Unit_price"));
                product.setCategory(rs.getString("Category"));
                product.setSupplierID(rs.getInt("SupplierID"));
                product.setDescription(rs.getString("Description"));
                product.setQuantity(rs.getInt("Quantity"));
                products.add(product);
            }


            rs.close();
            statement.close();

        } catch (SQLException e) {
            System.err.println("Error searching for products: " + e.getMessage());
            e.printStackTrace();
            // Handle the error appropriately (e.g., return null or an empty list)
        }


        return products;
    }

    public List<Product> loadProductsByPriceRange(double minPrice, double maxPrice) {
        List<Product> products = new ArrayList<>();

        String query = "SELECT * FROM Product WHERE Unit_price BETWEEN ? AND ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, minPrice);
            statement.setDouble(2, maxPrice);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = new Product();
                    product.setProductID(resultSet.getInt("productID"));
                    product.setName(resultSet.getString("name"));
                    product.setCategory(resultSet.getString("category"));
                    product.setPrice(resultSet.getDouble("Unit_price"));
                    product.setQuantity(resultSet.getInt("quantity"));
                    product.setDescription(resultSet.getString("description"));
                    products.add(product);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging the exception in a production system
        }

        return products;
    }

    public boolean saveProduct(Product product) {
        try {
            String query;
            PreparedStatement statement;
            if (loadSupplier(product.getSupplierID()) == null) {
                System.out.println("Supplier does not exists! Return");
                return false;
            }
            if (loadProduct(product.getProductID()) != null) {
                query = "UPDATE Product SET Name = ?, Unit_price = ?, Category = ?, SupplierID = ?, Description = ?, Quantity = ? WHERE ProductID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, product.getName());
                statement.setDouble(2, product.getPrice());
                statement.setString(3, product.getCategory());
                statement.setInt(4, product.getSupplierID());
                statement.setString(5, product.getDescription());
                statement.setInt(6, product.getQuantity());
                statement.setInt(7, product.getProductID());

            } else {
                query = "INSERT INTO Product (ProductID, Name, Category, SupplierID, Unit_Price, Description, Quantity) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                statement = connection.prepareStatement(query);
                statement.setInt(1, product.getProductID());
                statement.setString(2, product.getName());
                statement.setString(3, product.getCategory());
                statement.setInt(4, product.getSupplierID());
                statement.setDouble(5, product.getPrice());
                statement.setString(6, product.getDescription());
                statement.setInt(7, product.getQuantity());
            }

            int rowsAffected = statement.executeUpdate(); // Use executeUpdate for INSERT/UPDATE
            statement.close();
            return rowsAffected > 0; // Check if any rows were affected

        } catch (SQLException e) {
            System.err.println("Database access error in saveProduct!");
            e.printStackTrace();
            return false;
        }
    }


    public Order loadOrder(int id) {
        return null;
    }

    public List<Order> loadAllOrders() {
        return null;
    }


    public boolean saveOrder(Order order) {
        try {
            // Insert into OrderTable
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO OrderTable (CustomerID, OrderDate, EmployeeID, TotalPrice) VALUES (?, ?, ?, ?)");
            statement.setInt(1, order.getCustomerID());
            statement.setTimestamp(2, order.getDate());
//            statement.setInt(3, order.getEmployeeID());
            statement.setDouble(4, order.getTotalCost());

            statement.executeUpdate();

            // Get the generated OrderID using last_insert_rowid()
            statement = connection.prepareStatement("SELECT last_insert_rowid()");
            ResultSet rs = statement.executeQuery();
            int generatedOrderID = -1;
            if (rs.next()) {
                generatedOrderID = rs.getInt(1);
            }


            rs.close();
            statement.close();


            // Insert into structure.OrderItem
            if (generatedOrderID != -1) { // Only proceed if OrderID was successfully retrieved
                order.setOrderID(generatedOrderID); // Now the Order object has the correct OrderID

                statement = connection.prepareStatement("INSERT INTO OrderItem (OrderID, ProductID, Quantity, Total_cost) VALUES (?, ?, ?, ?)");
                for (OrderItem line : order.getLines()) {
                    line.setOrderID(generatedOrderID); // Set the OrderID for each OrderItem
                    statement.setInt(1, line.getOrderID());
                    statement.setInt(2, line.getProductID());
                    statement.setInt(3, line.getQuantity());
                    statement.setDouble(4, line.getCost());
                    statement.executeUpdate();
                }
                statement.close(); //Close statement after order items are added
            }


            return generatedOrderID > -1; // Return the generated ID (or -1 on error)
        } catch (SQLException e) {
            System.err.println("Database access error in saveOrder!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean addUser(User user) {
        try {
            // Check if username already exists (optional, but recommended)
            User existingUser = loadUser(user.getUsername(), null);
            if (existingUser != null) {
                System.err.println("Username already exists. Cannot add user.");
                return false; // Or throw an exception
            }


            String query = "INSERT INTO Employee (\"EmployeeID\", \"First Name\", \"Last Name\", Phone, Email, Username, Password, Address, Position) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, user.getUserID()); // Set EmployeeID first
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getLastName());
            statement.setString(4, user.getPhone());
            statement.setString(5, user.getEmail());
            statement.setString(6, user.getUsername());
            statement.setString(7, user.getPassword());
            statement.setString(8, user.getAddress());
            statement.setString(9, user.getRole().name());


            int rowsAffected = statement.executeUpdate();
            statement.close();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database access error in addUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public User loadUser(String username, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Employee WHERE Username = ? AND Password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setUserID(resultSet.getInt("EmployeeID"));
                user.setUsername(resultSet.getString("Username"));
                user.setPassword(resultSet.getString("Password"));

                String firstName = resultSet.getString("First Name");
                String lastName = resultSet.getString("Last Name");

                user.setFirstName(firstName != null ? firstName : "");
                user.setLastName(lastName != null ? lastName : "");
                user.setEmail(resultSet.getString("Email"));
                user.setPhone(resultSet.getString("Phone"));
                user.setAddress(resultSet.getString("Address"));
                String roleString = resultSet.getString("Position"); // Retrieve the role string
                User.UserRole userRole = User.UserRole.valueOf(roleString); // Convert to enum
                user.setRole(userRole);

                resultSet.close();
                statement.close();
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Database access error in loadUser!");
            e.printStackTrace();

        }
        return null;
    }

    public User loadUser(int userID) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Employee WHERE EmployeeID = ?");
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setUserID(resultSet.getInt("EmployeeID"));
                user.setUsername(resultSet.getString("Username"));
                user.setPassword(resultSet.getString("Password"));

                String firstName = resultSet.getString("First Name");
                String lastName = resultSet.getString("Last Name");

                user.setFirstName(firstName != null ? firstName : "");
                user.setLastName(lastName != null ? lastName : "");
                user.setEmail(resultSet.getString("Email"));
                user.setPhone(resultSet.getString("Phone"));
                user.setAddress(resultSet.getString("Address"));
                String roleString = resultSet.getString("Position"); // Retrieve the role string
                User.UserRole userRole = User.UserRole.valueOf(roleString); // Convert to enum
                user.setRole(userRole);

                resultSet.close();
                statement.close();
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Database access error in loadUser!");
            e.printStackTrace();

        }
        return null;
    }

    @Override
    public List<User> loadAllUsers() {
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Employee");
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new ArrayList<>();

            while (resultSet.next()) {
                User user = new User();
                user.setUserID(resultSet.getInt("EmployeeID"));
                user.setUsername(resultSet.getString("Username"));
                user.setPassword(resultSet.getString("Password"));

                String firstName = resultSet.getString("First Name");
                String lastName = resultSet.getString("Last Name");

                user.setFirstName(firstName != null ? firstName : "");
                user.setLastName(lastName != null ? lastName : "");
                user.setEmail(resultSet.getString("Email"));
                user.setPhone(resultSet.getString("Phone"));
                user.setAddress(resultSet.getString("Address"));
                String roleString = resultSet.getString("Position"); // Retrieve the role string
                User.UserRole userRole = User.UserRole.valueOf(roleString); // Convert to enum
                user.setRole(userRole);

                users.add(user);
            }

            resultSet.close();
            statement.close();
            return users;
        } catch (SQLException e) {
            System.err.println("Database access error in loadUser!");
            e.printStackTrace();

        }
        return null;
    }

    public String[][] loadAllUsersData() {
        try {
            List<String[]> rows = new ArrayList<>(); // Use a List to store rows dynamically

            String query = "SELECT EmployeeID, Username, Password, \"First Name\", \"Last Name\", Email, Phone, Address, Position FROM Employee"; // Select all columns
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String[] row = new String[9]; // Array for each row
                row[0] = String.valueOf(resultSet.getInt("EmployeeID")); // or "" + resultSet.getInt(...)
                row[1] = resultSet.getString("Username");
                row[2] = resultSet.getString("Password");
                row[3] = resultSet.getString("First Name");
                row[4] = resultSet.getString("Last Name");
                row[5] = resultSet.getString("Email");
                row[6] = resultSet.getString("Phone");
                row[7] = resultSet.getString("Address");
                row[8] = resultSet.getString("Position");
                rows.add(row);
            }

            resultSet.close();
            statement.close();

            // Convert List<String[]> to String[][]
            return rows.toArray(new String[0][]); // Efficient conversion

        } catch (SQLException e) {
            System.err.println("Database access error in loadAllUsersData: " + e.getMessage());
            e.printStackTrace();
            return null; // Or handle the error differently
        }
    }

    public boolean updateUser(User user) {
        try {
            String query = "UPDATE Employee SET " +
                    "\"First Name\" = ?, " +       // Update First Name
                    "\"Last Name\" = ?, " +        // Update Last Name
                    "Phone = ?, " +
                    "Email = ?, " +
                    "Username = ?, " +
                    "Password = ?, " +
                    "Address = ?, " +
                    "Position = ? " +       // Assuming Position represents access level
                    "WHERE EmployeeID = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getUsername());
            statement.setString(6, user.getPassword());
            statement.setString(7, user.getAddress());
            statement.setString(8, user.getRole().name()); // Set the role string
            statement.setInt(9, user.getUserID());

            int rowsAffected = statement.executeUpdate();
            statement.close();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database access error in updateUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Add similar methods for other tables (Employee, Category, Inventory, etc.)
    public boolean deleteUser(int employeeID) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Employee WHERE EmployeeID = ?");
            statement.setInt(1, employeeID);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error in deleteEmployee: " + e.getMessage());

            return false;
        }
    }

    public Supplier loadSupplier(int supplierID) {
        try {
            Supplier supplier = null;
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Supplier WHERE SupplierID = ?");
            statement.setInt(1, supplierID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                supplier = new Supplier();
                supplier.setSupplierID(resultSet.getInt("SupplierID"));
                supplier.setName(resultSet.getString("Name"));
                supplier.setContactPerson(resultSet.getString("Contact_person"));
                supplier.setPhone(resultSet.getString("Phone"));
            }

            resultSet.close();
            statement.close();
            return supplier;
        } catch (SQLException e) {
            System.err.println("Database access error in loadSupplier!");
            e.printStackTrace();
            return null;
        }
    }

    public List<Supplier> loadAllSuppliers(){
        try {
            List<Supplier> suppliers = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Supplier");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Supplier supplier = new Supplier();
                supplier = new Supplier();
                supplier.setSupplierID(resultSet.getInt("SupplierID"));
                supplier.setName(resultSet.getString("Name"));
                supplier.setContactPerson(resultSet.getString("Contact_person"));
                supplier.setPhone(resultSet.getString("Phone"));

                suppliers.add(supplier);
            }

            resultSet.close();
            statement.close();
            return suppliers;
        } catch (SQLException e) {
            System.err.println("Database access error in loadSupplier!");
            e.printStackTrace();
            return null;
        }
    }


    public boolean saveSupplier(Supplier supplier) {
        try {
            PreparedStatement statement;
            String query;

            if (loadSupplier(supplier.getSupplierID()) != null) { // Check if supplier exists
                // Update existing supplier
                query = "UPDATE Supplier SET Name = ?, Contact_person = ?, Phone = ? WHERE SupplierID = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, supplier.getName());
                statement.setString(2, supplier.getContactPerson());
                statement.setString(3, supplier.getPhone());
                statement.setInt(4, supplier.getSupplierID());
            } else {
                // Insert new supplier
                query = "INSERT INTO Supplier (SupplierID, Name, Contact_person, Phone) VALUES (?, ?, ?, ?)";
                statement = connection.prepareStatement(query);
                statement.setInt(1, supplier.getSupplierID());
                statement.setString(2, supplier.getName());
                statement.setString(3, supplier.getContactPerson());
                statement.setString(4, supplier.getPhone());
            }

            int rowsAffected = statement.executeUpdate();
            statement.close();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database access error in saveSupplier!");
            e.printStackTrace();
            return false;
        }
    }

    public Customer loadCustomer(int customerID) {
        try {
            Customer customer = null;

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Customer WHERE CustomerID = ?");
            statement.setInt(1, customerID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                customer = new Customer();
                customer.setCustomerID(resultSet.getInt("CustomerID"));
                customer.setFirstName(resultSet.getString("First Name"));
                customer.setLastName(resultSet.getString("Last Name"));
                customer.setEmail(resultSet.getString("Email"));
                customer.setPhone(resultSet.getString("Phone"));
                customer.setAddress(resultSet.getString("Address"));
            }

            resultSet.close();
            statement.close();

            return customer;

        } catch (SQLException e) {
            System.err.println("Database access error in loadCustomer!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean createCustomer(Customer customer) {
        return false;
    }

    public boolean saveCustomer(Customer customer) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO Customer (CustomerID, \"First Name\", \"Last Name\", Email, Phone, Address) " +
                            "VALUES (?, ?, ?, ?, ?, ?)"
            ); // Insert new customer
            statement.setInt(1, customer.getCustomerID());
            statement.setString(2, customer.getFirstName());
            statement.setString(3, customer.getLastName());
            statement.setString(4, customer.getEmail());
            statement.setString(5, customer.getPhone());
            statement.setString(6, customer.getAddress());




            int rowsAffected = statement.executeUpdate();
            statement.close();

            return rowsAffected > 0;

        }
        catch (SQLException e) {
            System.err.println("Database access error in saveCustomer!");
            e.printStackTrace();
            return false;
        }

    }

    public boolean deleteCustomer(int customerID) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Customer WHERE CustomerID = ?");
            statement.setInt(1, customerID);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Database error in deleteCustomer: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(int productID) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Product WHERE ProductID = ?");
            statement.setInt(1, productID);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // Return true if deletion successful

        } catch (SQLException e) {
            System.err.println("Database error in deleteProduct: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSupplier(int supplierID) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Product WHERE SupplierID = ?");
            statement.setInt(1, supplierID);
            int rowsAffected = statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement("DELETE FROM Supplier WHERE SupplierID = ?");
            statement.setInt(1, supplierID);
            rowsAffected += statement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error in deleteSupplier: " + e.getMessage());
            return false;

        }
    }

    public boolean deleteOrder(int orderID) {
        try {
            // Delete related OrderItems first due to foreign key constraint
            PreparedStatement itemStatement = connection.prepareStatement("DELETE FROM OrderItem WHERE OrderID = ?");
            itemStatement.setInt(1, orderID);
            itemStatement.executeUpdate();
            itemStatement.close();

            // Now delete the Order
            PreparedStatement orderStatement = connection.prepareStatement("DELETE FROM OrderTable WHERE OrderID = ?");
            orderStatement.setInt(1, orderID);

            int rowsAffected = orderStatement.executeUpdate();
            orderStatement.close();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error in deleteOrder: " + e.getMessage());

            return false;
        }
    }

    @Override
    public List<Order> loadOrdersByDateRange(Date startDate, Date endDate) {
        return null;
    }

    @Override
    public Shipper loadShipper(int shipperID) {
        return null;
    }

    @Override
    public List<Shipper> loadAllShippers() {
        return null;
    }

    @Override
    public boolean deleteShipper(int shipperID) {
        return false;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        return false;
    }

//    public boolean deleteProductsBySupplier(int supplierID) {
//        try {
//            PreparedStatement statement = connection.prepareStatement("DELETE FROM Product WHERE SupplierID = ?");
//            statement.setInt(1, supplierID);
//
//            int rowsAffected = statement.executeUpdate(); //returns num of rows affected
//            return rowsAffected > -1;
//
//
//        } catch (SQLException e) {
//            System.err.println("Database error in deleteProductsBySupplier: " + e.getMessage());
//            return false; // Or throw the exception
//        }
//    }

}