package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import lk.ijse.pos.business.BOFactory;
import lk.ijse.pos.business.BOTypes;
import lk.ijse.pos.business.custom.CustomerBO;
import lk.ijse.pos.business.custom.ItemBO;
import lk.ijse.pos.business.custom.OrderBO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailDTO;
import lk.ijse.pos.main.AppInitializer;
import lk.ijse.pos.util.ItemTM;
import lk.ijse.pos.util.OrderDetailTM;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlaceOrderFormController {

    public AnchorPane root;
    public Label lblOrderID;
    public Label lblCustomerName;
    public Label lblOrderDate;
    public JFXComboBox<String> cmbCustomerId;
    public JFXComboBox<String> cmbItemCode;
    public Label lblDescription;
    public Label lblUnitPrice;
    public Label lblQtyOnHand;
    public JFXTextField txtQty;
    public Label lblTotal;
    public TableView<OrderDetailTM> tblOrderDetails;
    public JFXButton btnAdd;

    private ArrayList<ItemTM> tmpItems = new ArrayList<>();
    private int orderId = 0;

    private OrderBO orderBO = BOFactory.getInstance().getBO(BOTypes.ORDER);
    private CustomerBO customerBO = BOFactory.getInstance().getBO(BOTypes.CUSTOMER);
    private ItemBO itemBO = BOFactory.getInstance().getBO(BOTypes.ITEM);

    public void initialize() {

        clear();
        lblOrderDate.setText(LocalDate.now().toString());

        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));

        double lastColWidth = tblOrderDetails.getColumns().get(5).getWidth();
        tblOrderDetails.getColumns().remove(5);
        TableColumn<OrderDetailTM, OrderDetailTM> lastCol = new TableColumn<>();

        lastCol.setCellFactory(new Callback<TableColumn<OrderDetailTM, OrderDetailTM>, TableCell<OrderDetailTM, OrderDetailTM>>() {
            @Override
            public TableCell<OrderDetailTM, OrderDetailTM> call(TableColumn<OrderDetailTM, OrderDetailTM> param) {
                return new TableCell<OrderDetailTM, OrderDetailTM>() {

                    JFXButton btnDelete = new JFXButton("Delete");

                    TableCell cell = this;

                    @Override
                    protected void updateItem(OrderDetailTM item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            setGraphic(btnDelete);
                        //    tblOrderDetails.refresh();
                        }

                        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                int index = ((TableRow) cell.getParent()).getIndex();
                                String code = tblOrderDetails.getItems().get(index).getCode();
                                int qty = tblOrderDetails.getItems().get(index).getQty();
                                tblOrderDetails.getItems().remove(index);

                                for (ItemTM tmpItem : tmpItems) {
                                    if (tmpItem.getCode().equals(code)) {
                                        tmpItem.setQtyOnHand(tmpItem.getQtyOnHand() + qty);
                                        break;
                                    }
                                }

                                calculateTotal();
                            }
                        });
                    }
                };
            }
        });
        lastCol.setPrefWidth(lastColWidth);
        tblOrderDetails.getColumns().add(lastCol);

        // Loading all customers Ids
        try {
            List<CustomerDTO> allCustomers = customerBO.getAllCustomers();
            for (CustomerDTO customer : allCustomers) {
                cmbCustomerId.getItems().add(customer.getId());
            }
        } catch (Exception ex) {
            Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, ex);
        }

        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String customerId) {
                try {
                    if (customerId == null) return;
                    CustomerDTO customer = customerBO.getCustomerById(customerId);
                    lblCustomerName.setText(customer.getName());
                } catch (Exception ex) {
                    Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, ex);
                }
            }
        });

        // Loading all item codes
        try {
            List<ItemDTO> allItems = itemBO.getAllItems();
            for (ItemDTO item : allItems) {

                String itemCode = item.getCode();
                String description = item.getDescription();
                int qtyOnHand = item.getQtyOnHand();
                double unitPrice = item.getUnitPrice();

                ItemTM itemTM = new ItemTM(itemCode, description, qtyOnHand, unitPrice);
                tmpItems.add(itemTM);

                cmbItemCode.getItems().add(itemCode);
            }
        } catch (Exception ex) {
            Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, ex);
        }

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String itemCode) {

                lblDescription.setText("");
                lblQtyOnHand.setText("");
                lblUnitPrice.setText("");

                for (ItemTM tmpItem : tmpItems) {
                    if (tmpItem.getCode().equals(itemCode)) {
                        lblDescription.setText(tmpItem.getDescription());
                        lblQtyOnHand.setText(tmpItem.getQtyOnHand() + "");
                        lblUnitPrice.setText(tmpItem.getUnitPrice() + "");
                        break;
                    }
                }

            }
        });

        try {

            orderId = orderBO.generateOrderId();
            lblOrderID.setText("OD" + orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tblOrderDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OrderDetailTM>() {
            @Override
            public void changed(ObservableValue<? extends OrderDetailTM> observable, OrderDetailTM oldValue, OrderDetailTM orderDetail) {

                for (ItemTM tmpItem : tmpItems) {
                    if (tmpItem.getCode().equals(orderDetail.getCode())) {
                        tmpItem.setQtyOnHand(tmpItem.getQtyOnHand() + orderDetail.getQty());
                        break;
                    }
                }

                cmbItemCode.getSelectionModel().select(orderDetail.getCode());
                cmbItemCode.setDisable(true);
                btnAdd.setText("Update");
                txtQty.setText(orderDetail.getQty() + "");
                txtQty.requestFocus();

            }
        });

    }

    public void navigateToHome(MouseEvent mouseEvent) throws IOException {
        AppInitializer.navigateToHome(root, (Stage) this.root.getScene().getWindow());
    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {

        // Validation #1

        if (cmbItemCode.getSelectionModel().getSelectedIndex() == -1) {
            new Alert(Alert.AlertType.ERROR, "Item should be selected before adding").show();
            cmbItemCode.requestFocus();
            return;
        }

        int qty = 0;

        // Validation #2

        try {
            qty = Integer.parseInt(txtQty.getText());
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Invalid Qty").show();
            txtQty.requestFocus();
            return;
        }

        int qtyOnHand = Integer.parseInt(lblQtyOnHand.getText());

        // Validation #3

        if (qty < 1 || qty > qtyOnHand) {
            new Alert(Alert.AlertType.ERROR, "Invalid Qty").show();
            txtQty.requestFocus();
            return;
        }

        ObservableList<OrderDetailTM> items = tblOrderDetails.getItems();


        for (OrderDetailTM item : items) {
            if (item.getCode().equals(cmbItemCode.getValue())) {

                if (btnAdd.getText().equals("Update")) {
                    btnAdd.setText("Add");
                    cmbItemCode.setDisable(false);
                    item.setQty(qty);
                } else {
                    item.setQty(item.getQty() + qty);
                }
                updateQty(item.getCode(), qty);
              //  tblOrderDetails.refresh();
                calculateTotal();
                txtQty.clear();
                cmbItemCode.getSelectionModel().select(-1);
                cmbItemCode.requestFocus();
                return;
            }
        }

        OrderDetailTM orderDetailTM = new OrderDetailTM(cmbItemCode.getValue(), lblDescription.getText(),
                qty, Double.parseDouble(lblUnitPrice.getText()));

        updateQty(orderDetailTM.getCode(), orderDetailTM.getQty());
        tblOrderDetails.getItems().add(orderDetailTM);

        calculateTotal();
        txtQty.clear();
        cmbItemCode.getSelectionModel().select(-1);
        cmbItemCode.requestFocus();

    }

    public void cmbItemCode_OnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            txtQty.requestFocus();
        }
    }

    public void btnPlaceOrder_OnAction(ActionEvent actionEvent) {

        // # Validation

        if (cmbCustomerId.getSelectionModel().getSelectedIndex() == -1) {
            new Alert(Alert.AlertType.ERROR, "Please select a customer to proceed").show();
            cmbCustomerId.requestFocus();
            return;
        }

        if (tblOrderDetails.getItems().size() == 0) {
            new Alert(Alert.AlertType.ERROR, "There should be at least one order detail to proceed").show();
            return;
        }

        List<OrderDetailDTO> alOrderDetails = new ArrayList<>();
        for (OrderDetailTM item : tblOrderDetails.getItems()) {
            OrderDetailDTO orderDetailDTO = new OrderDetailDTO(orderId,
                    item.getCode(),
                    item.getQty(),
                    item.getUnitPrice());
            alOrderDetails.add(orderDetailDTO);
        }

        OrderDTO orderDTO = new OrderDTO(orderId, LocalDate.now(), cmbCustomerId.getValue(), alOrderDetails);

        try {
            orderBO.placeOrder(orderDTO);
            new Alert(Alert.AlertType.INFORMATION, "Order has been saved successfully").show();
            btnNewOrder_OnAction(null);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to save the order").show();
            Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, e);
        }

    }

    public void btnNewOrder_OnAction(ActionEvent actionEvent) {
        clear();
        cmbCustomerId.getSelectionModel().select(-1);
        tblOrderDetails.getItems().removeAll(tblOrderDetails.getItems());
        initialize();
    }

    public void clear() {
        lblCustomerName.setText("");
        lblDescription.setText("");
        lblQtyOnHand.setText("");
        lblUnitPrice.setText("");
        lblTotal.setText("");
        txtQty.setText("");
        cmbItemCode.getSelectionModel().select(-1);
    }

    public void cmbCustomerID_OnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            cmbItemCode.requestFocus();
        }
    }

    public void txtQty_OnAction(ActionEvent actionEvent) {
        btnAdd_OnAction(actionEvent);
    }

    private void calculateTotal() {
        ObservableList<OrderDetailTM> items = tblOrderDetails.getItems();
        double total = 0;
        for (OrderDetailTM item : items) {
            total += item.getTotal();
        }

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        String formattedTotal = nf.format(total);

        lblTotal.setText(formattedTotal + "");
    }

    private void updateQty(String itemCode, int qty) {
        for (ItemTM tmpItem : tmpItems) {
            if (tmpItem.getCode().equals(itemCode)) {
                int currentQty = tmpItem.getQtyOnHand() - qty;
                tmpItem.setQtyOnHand(currentQty);
                lblQtyOnHand.setText(currentQty + "");
                break;
            }
        }
    }
}
