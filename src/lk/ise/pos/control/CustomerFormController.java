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
import lk.ise.pos.dto.CustomerDto;
import lk.ise.pos.entity.Customer;
import lk.ise.pos.enums.BoType;
import lk.ise.pos.view.tm.CustomerTM;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class CustomerFormController {
    public AnchorPane customerFormContext;
    public TextField txtSalary;
    public TextField txtAddreass;
    public TextField txtSearch;
    public TextField txtName;
    public TextField txtId;
    public TableView<CustomerTM> tbl;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colId;
    public TableColumn colSalary;
    public TableColumn colOption;
    public Button btn;

    private CustomerBO customerBO = BoFactory.getInstance().getBo(BoType.CUSTOMER);

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        loadAll("");

        tbl.getSelectionModel()
                .selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setData(newValue);
                    }
                }));
    }

    private void setData(CustomerTM newValue) {
        txtId.setText(newValue.getId());
        txtName.setText(newValue.getName());
        txtAddreass.setText(newValue.getAddress());
        txtSalary.setText(String.valueOf(newValue.getSalary()));
        btn.setText("Update Customer");
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage =(Stage) customerFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader
                .load(getClass().getResource("../view/DashboardForm.fxml"))));
    }

    public void saveCustomer(ActionEvent actionEvent) {
        CustomerDto c1 = new CustomerDto(
                txtId.getText(), txtName.getText(), txtAddreass.getText()
                , Double.parseDouble(txtSalary.getText())
        );


        if (btn.getText().equals("Save Customer")) {
            try {
                if (customerBO.saveCustomer(c1)) {
                    new Alert(Alert.AlertType.INFORMATION, "Customer Saved!").show();
                    loadAll("");
                } else {
                    new Alert(Alert.AlertType.WARNING, "Something went Wrong!").show();
                }
            }catch (Exception e){
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }

        } else {
            try {
                if (customerBO.updateCustomer(c1)) {
                    new Alert(Alert.AlertType.INFORMATION, "Customer Updated!").show();
                    loadAll("");
                } else {
                    new Alert(Alert.AlertType.WARNING, "Something went Wrong!").show();
                }
            }catch (Exception e){
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
        clearData();
    }

    private void clearData() {
        txtId.clear();
        txtName.clear();
        txtAddreass.clear();
        txtSalary.clear();
    }
    private void loadAll(String searchText){
        ObservableList<CustomerTM> tmList = FXCollections.observableArrayList();
        try {
            for (CustomerDto c : customerBO.findAllCustomers()) {
                Button btn = new Button("Delete");
                CustomerTM tm = new CustomerTM(
                        c.getId(), c.getName(), c.getAddress(), c.getSalary(), btn
                );


                btn.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?",
                            ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> type = alert.showAndWait();
                    if (type.get() == ButtonType.YES) {
                        try{
                            if (customerBO.deleteCustomer(c.getId())) {
                                new Alert(Alert.AlertType.INFORMATION, "Customer Deleted!").show();
                                loadAll("");
                            } else {
                                new Alert(Alert.AlertType.WARNING, "Something went Wrong!").show();
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                        }
                    }

                });

                tmList.add(tm);
            }
            tbl.setItems(tmList);
        }catch (Exception e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void newCustomer(ActionEvent actionEvent) {
        clearData();
        btn.setText("Save Customer");
    }
}
