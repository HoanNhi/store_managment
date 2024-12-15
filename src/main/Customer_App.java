package main;

import adapter.*;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import controller.*;
import structure.Customer;
import structure.User;
import view.*;

import java.awt.*;
import java.sql.*;

public class Customer_App {

    private static Customer_App instance;   // Singleton pattern

    public static Customer_App getInstance() {
        if (instance == null) {
            instance = new Customer_App();
        }
        return instance;
    }
    // Database adapters
    private Connection connection;
    private DataAdapter dataAdapterSQL;
    private DataAdapterMongo dataAdapterMongo;

    // Views and controllers
    private CustomerMainScreen mainScreen;
    private CustomerLoginController loginController;
    private CustomerEditController customerEditController;
    private CustomerEditView customerEditView;
    private CustomerBuyerView customerBuyerView;
    private CustomerBuyerController customerBuyerController;
    private CustomerOrderReportView orderReportView;
    private CustomerOrderDetailView orderDetailView;
    private CustomerOrderReportController orderReportController;

    // Login screen
    private LoginScreen loginScreen = new LoginScreen();

    // Singleton user and state variables
    private User currentUser = null;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public DataAdapter getDataAdapterSQL() {
        return dataAdapterSQL;
    }

    public void setDataAdapterSQL(DataAdapter dataAdapterSQL) {
        this.dataAdapterSQL = dataAdapterSQL;
    }

    public DataAdapterMongo getDataAdapterMongo() {
        return dataAdapterMongo;
    }

    public void setDataAdapterMongo(DataAdapterMongo dataAdapterMongo) {
        this.dataAdapterMongo = dataAdapterMongo;
    }

    public CustomerMainScreen getMainScreen() {
        return mainScreen;
    }

    public void setMainScreen(CustomerMainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    public CustomerLoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(CustomerLoginController loginController) {
        this.loginController = loginController;
    }

    public CustomerEditController getCustomerEditController() {
        return customerEditController;
    }

    public void setCustomerEditController(CustomerEditController customerEditController) {
        this.customerEditController = customerEditController;
    }

    public CustomerEditView getCustomerEditView() {
        return customerEditView;
    }

    public void setCustomerEditView(CustomerEditView customerEditView) {
        this.customerEditView = customerEditView;
    }

    public CustomerBuyerView getCustomerBuyerView() {
        return customerBuyerView;
    }

    public void setCustomerBuyerView(CustomerBuyerView customerBuyerView) {
        this.customerBuyerView = customerBuyerView;
    }

    public CustomerBuyerController getCustomerBuyerController() {
        return customerBuyerController;
    }

    public void setCustomerBuyerController(CustomerBuyerController customerBuyerController) {
        this.customerBuyerController = customerBuyerController;
    }

    public void setOrderReportView(CustomerOrderReportView orderReportView) {
        this.orderReportView = orderReportView;
    }

    public CustomerOrderDetailView getOrderDetailView() {
        return orderDetailView;
    }

    public void setOrderDetailView(CustomerOrderDetailView orderDetailView) {
        this.orderDetailView = orderDetailView;
    }

    public CustomerOrderReportController getOrderReportController() {
        return orderReportController;
    }

    public void setOrderReportController(CustomerOrderReportController orderReportController) {
        this.orderReportController = orderReportController;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public LoginScreen getLoginScreen() {
        return loginScreen;
    }

    public void setLoginScreen(LoginScreen loginScreen) {
        this.loginScreen = loginScreen;
    }

    public CustomerOrderReportView getOrderReportView() {
        return orderReportView;
    }

    public CustomerBuyerView getCheckoutScreen() {
        return customerBuyerView;
    }

    public CustomerMainScreen getCustomerMainScreen() {
        return mainScreen;
    }

    private Customer_App() {
        // create SQLite database connection here!
        try {
            /**************************************SQLITE**************************************/
            Class.forName("org.sqlite.JDBC");

            String url = "jdbc:sqlite:StoreDemo/store.db";

            connection = DriverManager.getConnection(url);
            dataAdapterSQL = new DataAdapter(connection);
            /**************************************SQLITE**************************************/
            /**************************************MongoDB**************************************/

            String uri = "mongodb://localhost:27017";  // Connection string for local MongoDB
            MongoClient mongoClient = MongoClients.create(uri);
            MongoDatabase database = mongoClient.getDatabase("storeManagement"); // Connect to "storeManagement" database
            try {
                this.dataAdapterMongo = new DataAdapterMongo(database);
                System.out.println(dataAdapterMongo.loadCustomer(210114).getAddress());
            } catch (MongoException me) {
                System.err.println("Error pinging MongoDB: " + me);
            }
            /**************************************MongoDB**************************************/

        }
        catch (ClassNotFoundException ex) {
            System.out.println("SQLite is not installed. System exits with error!");
            ex.printStackTrace();
            System.exit(1);
        }

        catch (SQLException ex) {
            System.out.println("SQLite database is not ready. System exits with error!" + ex.getMessage());
            System.exit(2);
        }

        loginController = new CustomerLoginController(this.loginScreen, dataAdapterSQL);
    }

    public void initializeAfterLogin() { // New method
        // Initialize controllers and views AFTER successful login
        mainScreen = new CustomerMainScreen(this.dataAdapterSQL, this.dataAdapterMongo);
        Customer customer = dataAdapterMongo.loadCustomer(currentUser.getUserID());
        this.customerEditView = new CustomerEditView(this.mainScreen, customer, this.currentUser);
        this.customerEditController = new CustomerEditController(this.customerEditView);

        this.customerBuyerView = new CustomerBuyerView();
        this.customerBuyerController = new CustomerBuyerController(customerBuyerView);

        orderReportView = new CustomerOrderReportView();
        orderDetailView = new CustomerOrderDetailView(this.dataAdapterMongo, this.dataAdapterSQL);
        orderReportController = new CustomerOrderReportController(orderReportView, orderDetailView);
    }
    public static void main(String[] args) {
        Customer_App.getInstance().getLoginScreen().setVisible(true);
    }

    public CustomerOrderReportController getCustomerOrderReportController() {
        return orderReportController;
    }
}
