package lk.ise.pos.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ise.pos.bo.BoFactory;
import lk.ise.pos.bo.custom.CustomerBO;
import lk.ise.pos.bo.custom.ItemBO;
import lk.ise.pos.bo.custom.OrderBO;
import lk.ise.pos.db.Database;
import lk.ise.pos.dto.CustomerDto;
import lk.ise.pos.dto.ItemDto;
import lk.ise.pos.dto.OrderDetailsDto;
import lk.ise.pos.dto.OrderDto;
import lk.ise.pos.entity.Customer;
import lk.ise.pos.entity.Item;
import lk.ise.pos.entity.Order;
import lk.ise.pos.entity.OrderDetails;
import lk.ise.pos.enums.BoType;
import lk.ise.pos.view.tm.CartTm;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class PlaceOrderFormController {
    public TextField txtCustomerName;
    public ComboBox<String> cmbCustomerId;
    public TextField txtAddress;
    public TextField txtSalary;
    public ComboBox<String> cmbItemCode;
    public TextField txtDescription;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public TextField txtRequestQty;
    public TableColumn colItem;
    public TableView<CartTm> tblCart;
    public TableColumn colDescription;
    public TableColumn colUnitPrice;
    public TableColumn colQty;
    public TableColumn colTotal;
    public TableColumn colOption;
    public Label lblTotal;
    public AnchorPane context;
    public Label lblOrderId;

    private CustomerBO customerBo = BoFactory.getInstance().getBo(BoType.CUSTOMER);
    private ItemBO itemBo = BoFactory.getInstance().getBo(BoType.ITEM);
    private OrderBO orderBO = BoFactory.getInstance().getBo(BoType.ORDER);


    public void initialize() throws SQLException, ClassNotFoundException {
        colItem.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

        loadCustomerIds();
        loadItemCode();
        loadOrderId();

        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null){
                try {
                    setCustomerData(newValue);
                } catch (ClassNotFoundException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null){
                try {
                    setItemData(newValue);
                } catch (ClassNotFoundException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void loadItemCode() {
        try {
            ObservableList<String> obList = FXCollections.observableArrayList(
                    itemBo.loadItemCodes()
            );
            cmbItemCode.setItems(obList);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void setCustomerData(String id) throws SQLException, ClassNotFoundException {
        try {
            CustomerDto customer = customerBo.findCustomer(id);
            if (customer!=null){
                txtCustomerName.setText(customer.getName());
                txtAddress.setText(customer.getAddress());
                txtSalary.setText(String.valueOf(customer.getSalary()));
            }else{
                new Alert(Alert.AlertType.WARNING,"Not Found").show();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    private void loadOrderId(){
        try {
            lblOrderId.setText( orderBO.generateOrderId());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void setItemData(String code) throws SQLException, ClassNotFoundException {
        try {
            ItemDto item = itemBo.findItem(code);
            if (item!=null){
                txtDescription.setText(item.getDescription());
                txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
                txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
            }else{
                new Alert(Alert.AlertType.WARNING,"Not Found").show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void loadCustomerIds() {
        try {
            ObservableList<String> obList = FXCollections.observableArrayList(
                    customerBo.loadCustomerIds()
            );
            cmbCustomerId.setItems(obList);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    ObservableList<CartTm> tmList= FXCollections.observableArrayList();

    public void addToCartOnAction(ActionEvent actionEvent) {
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtRequestQty.getText());
        double total=unitPrice*qty;
        if (isExists(cmbItemCode.getValue())){
            for (CartTm t:tmList
            ) {
                if (t.getCode().equals(cmbItemCode.getValue())){
                    t.setQty(t.getQty()+qty);
                    t.setTotal(t.getTotal()+total);
                    tblCart.refresh();
                }
            }
            // update
        }else{
            Button btn= new Button("Delete");
            CartTm tm = new CartTm(cmbItemCode.getValue(),
                    txtDescription.getText(),unitPrice,qty,total,btn);
            btn.setOnAction(event ->{
                tmList.remove(tm);
                tblCart.refresh();
                calculateTotal();
                    });
            tmList.add(tm);
        }
        clear();
        calculateTotal();
        tblCart.setItems(tmList);
    }

    private void calculateTotal() {
        double total = 0;
        for (CartTm tm : tmList) {
            total += tm.getTotal();
        }
        lblTotal.setText(String.valueOf(total));
    }

    private boolean isExists(String code){
        Optional<CartTm> selectedCartTm =
                tmList.stream().filter(e -> e.getCode().equals(code)).findFirst();
        return selectedCartTm.isPresent();
    }
    private void clear(){
        cmbItemCode.setValue(null);
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtRequestQty.clear();
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage =(Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader
                .load(getClass().getResource("../view/DashboardForm.fxml"))));
    }

    public void saveOrder(ActionEvent actionEvent) {
        ArrayList<OrderDetailsDto> products = new ArrayList<>();
        for (CartTm tm : tmList) {
            products.add(new OrderDetailsDto(tm.getCode(),lblOrderId.getText(), tm.getUnitPrice(), tm.getQty()));
            manageQty(tm.getCode(), tm.getQty());
        }
        OrderDto orderDto = new OrderDto(lblOrderId.getText(),
                cmbCustomerId.getValue(), new Date(),
                Double.parseDouble(lblTotal.getText()));
        try {
            boolean isSaved = orderBO.saveOrder(orderDto,products);
            if(isSaved){
                new Alert(Alert.AlertType.CONFIRMATION,"Order Completed").show();
                tmList.clear();
                tblCart.refresh();
                lblTotal.setText(String.valueOf(0));
                loadOrderId();
            }else {
                new Alert(Alert.AlertType.ERROR,"Try Again!").show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void manageQty(String code, int qty) {
        for (Item i : Database.items) {
            if (i.getCode().equals(code)){
                i.setQtyOnHand(i.getQtyOnHand()-qty);
                return;
            }
        }
    }
}
