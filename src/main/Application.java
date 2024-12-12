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
import java.sql.*;

public class Application {

    private static Application instance;   // Singleton pattern

    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }
    // Main components of this application

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    private DataAdapter dataAdapterSQL;

    private DataAdapterMongo dataAdapterMongo;


    private BuyerView checkoutScreen = new BuyerView();

    private MainScreen mainScreen;

    private SupplierView supplierView = new SupplierView();

    private OrderReportView orderReportView = new OrderReportView();

    private OrderDetailView orderDetailView;

    private ProductDetailView productDetailView = new ProductDetailView();

    private User currentUser = null;

    public LoginController loginController;

    private ProductController productController;

    private OrderReportController orderReportController;

    private CheckoutController checkoutController;

    private ManageUsersController manageUsersController;

    private ManageUsersView manageUsersView;

    private CustomerEditController customerEditController;

    private CustomerEditView customerEditView;

    private CustomerBuyerView customerBuyerView;

    private CustomerBuyerController customerBuyerController;

    public LoginScreen loginScreen = new LoginScreen();

    public LoginScreen getLoginScreen() {
        return loginScreen;
    }


    public User getCurrentUser() { return currentUser; }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }


    public MainScreen getMainScreen() {
        return mainScreen;
    }

    public BuyerView getCheckoutScreen() {
        return checkoutScreen;
    }

    public SupplierView getSupplierView() {
        return supplierView;
    }

    public OrderReportView getOrderReportView() {
        return orderReportView;
    }

    public OrderDetailView getOrderDetailView() {
        return orderDetailView;
    }

    public ProductDetailView getProductDetailView() {
        return productDetailView;
    }

    public ProductController getProductController() {
        return productController;
    }

    public CheckoutController getCheckoutController() {
        return checkoutController;
    }

    private SupplierController supplierController;

    public SupplierController getSupplierController() {
        return supplierController;
    }

    public OrderReportController getOrderReportController() {
        return orderReportController;
    }

//    private AllProductController allProductController;

//    public AllProductController getAllProductController(){
//        return allProductController;
//    }

    public DataAdapter getDataAdapterSQL() {
        return dataAdapterSQL;
    }

    public DataAdapterMongo getDataAdapterMongo() {
        return dataAdapterMongo;
    }

    public void setOrderDetailView(OrderDetailView orderDetailView) {
        this.orderDetailView = orderDetailView;
    }

    public void setMainScreen(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    public void setOrderReportController(OrderReportController orderReportController) {
        this.orderReportController = orderReportController;
    }

    public void setProductController(ProductController productController) {
        this.productController = productController;
    }

    public void setCheckoutController(CheckoutController checkoutController) {
        this.checkoutController = checkoutController;
    }

    public void setSupplierController(SupplierController supplierController) {
        this.supplierController = supplierController;
    }

//    public void setAllProductController(AllProductController allProductController) {
//        this.allProductController = allProductController;
//    }

    public ManageUsersController getManageUsersController() {
        return manageUsersController;
    }

    public ManageUsersView getManageUsersView() {
        return manageUsersView;
    }

    public CustomerEditView getCustomerEditView(){
        return customerEditView;
    }

    public CustomerEditController getCustomerEditController(){
        return customerEditController;
    }

    public CustomerBuyerView getCustomerBuyerView(){
        return customerBuyerView;
    }

    public CustomerBuyerController getCustomerBuyerController(){
        return customerBuyerController;
    }

    private Application() {
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

        loginController = new LoginController(this.loginScreen, dataAdapterSQL);
    }

    public void initializeAfterLogin() { // New method
        // Initialize controllers and views AFTER successful login
        mainScreen = new MainScreen(this.dataAdapterSQL, this.dataAdapterMongo);
        if (this.getCurrentUser().getRole() != User.UserRole.Customer){
            productController = new ProductController(this.productDetailView, dataAdapterSQL);

            checkoutController = new CheckoutController(checkoutScreen, dataAdapterSQL, dataAdapterMongo);

//            allProductController = new AllProductController(productDetailView, dataAdapterSQL);
            if (this.getCurrentUser().getRole() == User.UserRole.Manager){
                orderDetailView = new OrderDetailView(this.dataAdapterMongo, this.dataAdapterSQL);
                orderReportController = new OrderReportController(orderReportView, orderDetailView, dataAdapterMongo);

                supplierController = new SupplierController(supplierView);

                manageUsersView = new ManageUsersView();

                this.manageUsersController = new ManageUsersController(manageUsersView);
            }
        }
        else if (this.getCurrentUser().getRole() == User.UserRole.Customer){
            Customer customer = dataAdapterMongo.loadCustomer(currentUser.getUserID());
            this.customerEditView = new CustomerEditView(this.mainScreen, customer, this.currentUser);
            this.customerEditController = new CustomerEditController(this.customerEditView);

            this.customerBuyerView = new CustomerBuyerView();
            this.customerBuyerController = new CustomerBuyerController(customerBuyerView);

            orderDetailView = new OrderDetailView(this.dataAdapterMongo, this.dataAdapterSQL);
            orderReportController = new OrderReportController(orderReportView, orderDetailView, dataAdapterMongo);
        }
    }


    public static void main(String[] args) {
        Application.getInstance().getLoginScreen().setVisible(true);
    }
}
