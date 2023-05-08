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
import lk.ise.pos.bo.custom.ItemBO;
import lk.ise.pos.dto.ItemDto;
import lk.ise.pos.entity.Item;
import lk.ise.pos.enums.BoType;
import lk.ise.pos.view.tm.ItemTM;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ItemFormController {
    public TextField txtDescription;
    public TextField txtItemCode;
    public TextField txtQtyOnHand;
    public TextField txtUnitPrice;
    public TextField txtSearch;
    public Button btn;
    public TableView<ItemTM> tbl;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colQtyOnHand;
    public TableColumn colUnitPrice;
    public TableColumn colOption;
    public AnchorPane itemFormContext;

    private ItemBO itemBO = BoFactory.getInstance().getBo(BoType.ITEM);

    public void initialize(){
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
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

    private void setData(ItemTM newValue) {
        txtItemCode.setText(newValue.getCode());
        txtDescription.setText(newValue.getDescription());
        txtQtyOnHand.setText(String.valueOf(newValue.getQtyOnHand()));
        txtUnitPrice.setText(String.valueOf(newValue.getUnitPrice()));
        btn.setText("Update Item");
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage =(Stage) itemFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader
                .load(getClass().getResource("../view/DashboardForm.fxml"))));
    }


    public void newItem(ActionEvent actionEvent) {
        clearData();
        btn.setText("Save Item");
    }

    public void saveItem(ActionEvent actionEvent) {
        ItemDto I1 = new ItemDto(
                txtItemCode.getText(), txtDescription.getText(), Integer.parseInt(txtQtyOnHand.getText())
                , Double.parseDouble(txtUnitPrice.getText())
        );


        if (btn.getText().equals("Save Item")) {
            try {
                if (itemBO.saveItem(I1)) {
                    new Alert(Alert.AlertType.INFORMATION, "Item Saved!").show();
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
                if (itemBO.updateItem(I1)) {
                    new Alert(Alert.AlertType.INFORMATION, "Item Updated!").show();
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
        txtItemCode.clear();
        txtDescription.clear();
        txtQtyOnHand.clear();
        txtUnitPrice.clear();
    }

    private void loadAll(String s) {
        ObservableList<ItemTM> tmList = FXCollections.observableArrayList();
        try {
            for(ItemDto i: itemBO.findAllItems()) {
                Button btn = new Button("Delete");
                ItemTM tm = new ItemTM(
                        i.getCode(), i.getDescription(), i.getQtyOnHand(), i.getUnitPrice(), btn
                );


                btn.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?",
                            ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> type = alert.showAndWait();
                    if (type.get() == ButtonType.YES) {
                        try{
                            if (itemBO.deleteItem(i.getCode())) {
                                new Alert(Alert.AlertType.INFORMATION, "Item Deleted!").show();
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
}
