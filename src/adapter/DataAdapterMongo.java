package adapter;
import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import com.mongodb.MongoException;

import structure.*;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DataAdapterMongo implements DataAdapterInterface{

    MongoDatabase database;
    public DataAdapterMongo(MongoDatabase database) {
        this.database = database;
    }

    public Customer loadCustomer(int userID) {
        try {
            MongoCollection<Document> customersCollection = database.getCollection("Customer");

            // Find the customer document
            Document filter = new Document("userID", userID); // Filter by customerID
            Document customerDoc = customersCollection.find(filter).first();

            if (customerDoc != null) {
                Document paymentInfo = (Document) customerDoc.get("credit_card_payment");
                //Get payment info
                String cardNumber = paymentInfo.getString("cardNumber");
                String csv = paymentInfo.getString("csv");
                String expiryYear = paymentInfo.getString("expiry_year");
                String expiryMonth = paymentInfo.getString("expiry_month");
                String cardType = paymentInfo.getString("card_type");

                Customer customer = new Customer();
                customer.setCustomerID(customerDoc.getInteger("customerID"));
                customer.setUserID(customerDoc.getInteger("userID"));
                customer.setFirstName(customerDoc.getString("firstName"));
                customer.setLastName(customerDoc.getString("lastName"));
                customer.setPhone(customerDoc.getString("phone"));
                customer.setEmail(customerDoc.getString("email"));
                customer.setAddress(customerDoc.getString("address"));
                customer.setCard(cardNumber, csv, expiryYear, expiryMonth, cardType);

                return customer;
            } else {
                System.out.println("No customer found");
                return null; // Customer not found
            }

        } catch (Exception e) {
            System.err.println("Error loading customer: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateCustomer(Customer customer) {
        try{
            MongoCollection<Document> customersCollection = database.getCollection("Customer");

            // Create filter to find the customer by userID
            Bson filter = new Document("userID", customer.getUserID());

            // Create update document (using $set to update fields)
            Bson updates = new Document("$set", new Document()
                    .append("firstName", customer.getFirstName())
                    .append("lastName", customer.getLastName())
                    .append("phone", customer.getPhone())
                    .append("email", customer.getEmail())
                    .append("address", customer.getAddress())
                    .append("credit_card_payment", new Document()
                    .append("cardNumber", customer.getCardNumber())
                    .append("csv", customer.getCsv())
                    .append("expiry_year", customer.getExpiryYear())
                    .append("expiry_month", customer.getExpiryMonth())
                    .append("card_type", customer.getCardType())
                    )
            );

            // Update the customer document
            customersCollection.updateOne(filter, updates);

            return true;

        } catch (MongoException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
            return false; // Or throw an exception
        }
    }

    public boolean createCustomer(Customer customer) {
        try {
            MongoCollection<Document> customersCollection = database.getCollection("Customer");

            // Create a Document from the Customer object
            Document customerDoc = new Document("customerID", customer.getCustomerID())
                    .append("userID", customer.getUserID())
                    .append("firstName", customer.getFirstName())
                    .append("lastName", customer.getLastName())
                    .append("phone", customer.getPhone())
                    .append("email", customer.getEmail())
                    .append("address", customer.getAddress())
                    .append("credit_card_payment", new Document("cardNumber", customer.getCardNumber()) // Assuming Customer has card details
                            .append("csv", customer.getCsv())
                            .append("expiry_year", customer.getExpiryYear())
                            .append("expiry_month", customer.getExpiryMonth())
                            .append("card_type", customer.getCardType())
                    );

            customersCollection.insertOne(customerDoc); // Insert the document
            return true; // Indicate success

        } catch (MongoException e) {
            System.err.println("Error creating customer: " + e.getMessage());
            // ... other error handling (e.g., check for duplicate key) ...
            return false; // Or throw an exception
        }
    }

    @Override
    public boolean saveCustomer(Customer customer) {
        return false;
    }

    @Override
    public boolean deleteCustomer(int customerID) {
        return false;
    }

//    public boolean createOrder(Order order) {
//            MongoCollection<Document> ordersCollection = database.getCollection("Order");
//
//            // Get the next orderID (auto-increment)
//            int nextOrderID = getNextOrderID(ordersCollection);
//            order.setOrderID(nextOrderID); // Set the orderID in the Order object
//
//
//            // Create the main order document
//            Document orderDoc = new Document("orderID", nextOrderID)
//                    .append("customerID", order.getCustomerID())
//                    .append("orderDate", order.getDate());
//
//
//            Document itemsDoc = new Document(); // Create the "Item" document
//            List<OrderItem> orderItems = order.getLines(); // Assuming Order has a getItems() method
//            for (int i = 0; i < orderItems.size(); i++) {
//                OrderItem item = orderItems.get(i);
//                Document itemDoc = new Document("productID", item.getProductID())
//                        .append("productName", item.getProductName())
//                        .append("Quantity", item.getQuantity())
//                        .append("unitPrice", item.getUnitCost())
//                        .append("totalPrice", item.getUnitCost()*item.getQuantity());
//                itemsDoc.append(String.valueOf(i + 1), itemDoc);  // Use 1-based index as key
//            }
//            orderDoc.append("Item", itemsDoc);
//
//            orderDoc.append("totalItems", order.getLines().size());
//            orderDoc.append("totalPrice", order.getTotalCost());
//
//
//            ordersCollection.insertOne(orderDoc);
//            return true;
//    }

    public Shipper loadShipper(int shipperID) {
        try {
            MongoCollection<Document> shippersCollection = database.getCollection("Shipper");
            Document filter = new Document("shipperID", shipperID);
            Document shipperDoc = shippersCollection.find(filter).first();

            if (shipperDoc != null) {
                Shipper shipper = new Shipper();
                shipper.setShipperID(shipperDoc.getInteger("shipperID"));
                shipper.setShipperName(shipperDoc.getString("companyName")); // Assuming "companyName" in MongoDB
                shipper.setPricePerKM(shipperDoc.getDouble("pricePerKM"));
                return shipper;
            } else {
                return null; // Shipper not found
            }

        } catch (MongoException e) {
            System.err.println("Error loading shipper: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Shipper> loadAllShippers() {
        List<Shipper> shippers = new ArrayList<>();
        try {
            MongoCollection<Document> shippersCollection = database.getCollection("Shipper");
            FindIterable<Document> documents = shippersCollection.find(); // Find all documents

            for (Document shipperDoc : documents) {
                Shipper shipper = new Shipper();
                shipper.setShipperID(shipperDoc.getInteger("shipperID"));
                shipper.setShipperName(shipperDoc.getString("companyName"));
                shipper.setPricePerKM(shipperDoc.getDouble("pricePerKM"));
                shippers.add(shipper);
            }

        } catch (MongoException e) {
            System.err.println("Error loading all shippers: " + e.getMessage());
            e.printStackTrace();
            // Handle the error as needed (e.g., return null or an empty list)
        }
        return shippers;
    }

    public boolean deleteShipper(int shipperID) {
        try {
            MongoCollection<Document> shippersCollection = database.getCollection("Shipper");
            Document filter = new Document("shipperID", shipperID);
            shippersCollection.deleteOne(filter);  // Delete the matching document
            return true; // Indicate success


        } catch (MongoException e) {
            System.err.println("Error deleting shipper: " + e.getMessage());
            e.printStackTrace();
            return false;  // Or throw an exception
        }
    }

    public boolean saveOrder(Order order) {
        try {
            MongoCollection<Document> ordersCollection = database.getCollection("Order");

            // Get the next orderID (using countDocuments as you requested)
            int nextOrderID = getNextOrderID(ordersCollection);
            order.setOrderID(nextOrderID);

            // Create the main order document
            Document orderDoc = new Document("orderID", nextOrderID)
                    .append("customerID", order.getCustomerID())
                    .append("orderDate", order.getDate()); // Using Timestamp directly

            // Add items
            Document itemsDoc = new Document();
            List<OrderItem> orderItems = order.getLines(); // Using getLines() from Order
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);
                Document itemDoc = new Document("productID", item.getProductID())
                        .append("productName", item.getProductName())
                        .append("Quantity", item.getQuantity())
                        .append("unitPrice", item.getUnitCost()) // Using getUnitCost() from OrderItem
                        .append("totalPrice", item.getCost());   // Using getCost() from OrderItem
                itemsDoc.append(String.valueOf(i + 1), itemDoc);
            }
            orderDoc.append("Item", itemsDoc);

            orderDoc.append("totalItems", orderItems.size()); // Calculate totalItems
            orderDoc.append("totalPrice", order.getTotalCost());
            orderDoc.append("address", order.getAddress()); // Assuming Order has getAddress()
            orderDoc.append("shipper", order.getShipperName());  // Assuming Order has getShipper()

            ordersCollection.insertOne(orderDoc);
            return true;

        } catch (MongoException e) {
            return false;
        }
    }

    public boolean deleteOrder(int orderID) {
        try {
            MongoCollection<Document> ordersCollection = database.getCollection("Order");
            Document filter = new Document("orderID", orderID);
            ordersCollection.deleteOne(filter);
            return true;

        } catch (MongoException e) {
            System.err.println("Error deleting order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private int getNextOrderID(MongoCollection<Document> ordersCollection) {
        try {
            long count = ordersCollection.countDocuments(); // Count all documents in the collection
            return (int) count + 1; // Cast to int (assuming orderID is an int)

        } catch (MongoException e) {
            System.err.println("Error getting next order ID: " + e.getMessage());
            // ... handle the error as needed (e.g., return a default value, throw an exception) ...
            return -1; // Or some other error indicator
        }
    }

    public List<Order> loadAllOrders() {
        List<Order> orders = new ArrayList<>();

        try {
            MongoCollection<Document> ordersCollection = database.getCollection("Order");

            FindIterable<Document> documents = ordersCollection.find(); // Find all documents

            for (Document orderDoc : documents) {
                Order order = new Order();
                order.setOrderID(orderDoc.getInteger("orderID"));
                order.setCustomerID(orderDoc.getInteger("customerID"));
                order.setDate(new Timestamp(orderDoc.getDate("orderDate").getTime())); // Convert Date to Timestamp

                // Load items
                Document itemsDoc = (Document) orderDoc.get("Item");
                if (itemsDoc != null) {  // Check if "Item" document exists
                    for (String key : itemsDoc.keySet()) {  // Iterate through item keys ("1", "2", etc.)
                        Document itemDoc = (Document) itemsDoc.get(key);
                        OrderItem orderItem = new OrderItem();
                        orderItem.setProductID(itemDoc.getInteger("productID"));
                        orderItem.setProductName(itemDoc.getString("productName"));
                        orderItem.setQuantity(itemDoc.getInteger("Quantity"));
                        orderItem.setUnitCost(itemDoc.getDouble("unitPrice"));
                        orderItem.setCost(itemDoc.getDouble("totalPrice"));
                        order.addLine(orderItem);
                    }
                }

                order.setTotalCost(orderDoc.getDouble("totalPrice"));
                order.setAddress(orderDoc.getString("address"));
                order.setShipperName(orderDoc.getString("shipper")); // Assuming Order has setShipper

                orders.add(order);
            }

        } catch (MongoException e) {
            System.err.println("Error loading all orders: " + e.getMessage());
            e.printStackTrace();
            // Handle the error as needed (e.g., return null or an empty list)
        }

        return orders;
    }

    @Override
    public Product loadProduct(int id) {
        return null;
    }

    @Override
    public List<Product> loadAllProducts() {
        return List.of();
    }

    @Override
    public String[][] loadAllProductsData() {
        return new String[0][];
    }

    @Override
    public boolean saveProduct(Product product) {
        return false;
    }

    @Override
    public boolean deleteProduct(int productID) {
        return false;
    }

    public Order loadOrder(int orderID) {
        try {
            MongoCollection<Document> ordersCollection = database.getCollection("Order");

            Document filter = new Document("orderID", orderID);
            Document orderDoc = ordersCollection.find(filter).first(); // Find the matching document

            if (orderDoc != null) {
                Order order = new Order();
                order.setOrderID(orderDoc.getInteger("orderID"));
                order.setCustomerID(orderDoc.getInteger("customerID"));
                order.setDate(new Timestamp(orderDoc.getDate("orderDate").getTime()));

                // Load items (similar to loadAllOrders)
                Document itemsDoc = (Document) orderDoc.get("Item");
                if (itemsDoc != null) {  // Check if "Item" document exists
                    for (String key : itemsDoc.keySet()) {  // Iterate through item keys ("1", "2", etc.)
                        Document itemDoc = (Document) itemsDoc.get(key);
                        OrderItem orderItem = new OrderItem();
                        orderItem.setProductID(itemDoc.getInteger("productID"));
                        orderItem.setProductName(itemDoc.getString("productName"));
                        orderItem.setQuantity(itemDoc.getInteger("Quantity"));
                        orderItem.setUnitCost(itemDoc.getDouble("unitPrice"));
                        orderItem.setCost(itemDoc.getDouble("totalPrice"));
                        order.addLine(orderItem);
                    }
                }


                order.setTotalCost(orderDoc.getDouble("totalPrice"));
                order.setAddress(orderDoc.getString("address"));
                order.setShipperName(orderDoc.getString("shipper"));

                return order;
            } else {
                return null; // Order not found
            }

        } catch (MongoException e) {
            System.err.println("Error loading order: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Order> loadOrdersByDateRange(Date startDate, Date endDate) {
        List<Order> orders = new ArrayList<>();

        try{
            MongoCollection<Document> ordersCollection = database.getCollection("Order");

            // Create the date range filter
            Bson filter = new Document("orderDate", new Document("$gte", startDate).append("$lte", endDate));

            FindIterable<Document> documents = ordersCollection.find(filter);

            for (Document orderDoc : documents) {
                Order order = new Order();
                order.setOrderID(orderDoc.getInteger("orderID"));
                order.setCustomerID(orderDoc.getInteger("customerID"));
                order.setDate(new Timestamp(orderDoc.getDate("orderDate").getTime())); // Convert Date to Timestamp

                // Load items
                Document itemsDoc = (Document) orderDoc.get("Item");
                if (itemsDoc != null) {  // Check if "Item" document exists
                    for (String key : itemsDoc.keySet()) {  // Iterate through item keys ("1", "2", etc.)
                        Document itemDoc = (Document) itemsDoc.get(key);
                        OrderItem orderItem = new OrderItem();
                        orderItem.setProductID(itemDoc.getInteger("productID"));
                        orderItem.setProductName(itemDoc.getString("productName"));
                        orderItem.setQuantity(itemDoc.getInteger("Quantity"));
                        orderItem.setUnitCost(itemDoc.getDouble("unitPrice"));
                        orderItem.setCost(itemDoc.getDouble("totalPrice"));
                        order.addLine(orderItem);
                    }
                }

                order.setTotalCost(orderDoc.getDouble("totalPrice"));
                order.setAddress(orderDoc.getString("address"));
                order.setShipperName(orderDoc.getString("shipper")); // Assuming Order has setShipper

                orders.add(order);
            }

        } catch (MongoException e) {
            System.err.println("Error loading all orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public User loadUser(String username, String password) {
        return null;
    }

    @Override
    public String[][] loadAllUsersData() throws SQLException {
        return new String[0][];
    }

    @Override
    public boolean addUser(User user) {
        return false;
    }

    @Override
    public boolean updateUser(User user) {
        return false;
    }

    @Override
    public boolean deleteUser(int employeeID) {
        return false;
    }

    @Override
    public Supplier loadSupplier(int supplierID) {
        return null;
    }

    @Override
    public boolean saveSupplier(Supplier supplier) {
        return false;
    }

    @Override
    public boolean deleteSupplier(int supplierID) {
        return false;
    }

}