/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.pos.business.BOFactory;
import lk.ijse.pos.business.BOTypes;
import lk.ijse.pos.business.custom.CustomerBO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.main.AppInitializer;
import lk.ijse.pos.util.CustomerTM;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author ranjith-suranga
 */
public class ManageCustomerFormController implements Initializable {

    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnDelete;
    @FXML
    private AnchorPane root;
    @FXML
    private JFXTextField txtCustomerId;
    @FXML
    private JFXTextField txtCustomerName;
    @FXML
    private JFXTextField txtCustomerAddress;

    @FXML
    private TableView<CustomerTM> tblCustomers;

    private CustomerBO customerBO = BOFactory.getInstance().getBO(BOTypes.CUSTOMER);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tblCustomers.getColumns().get(0).setStyle("-fx-alignment:center");
        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        btnSave.setDisable(true);
        btnDelete.setDisable(true);

        try {
            List<CustomerDTO> allCustomers = customerBO.getAllCustomers();
            for (CustomerDTO customer : allCustomers) {
                tblCustomers.getItems().add(new CustomerTM(customer.getId(), customer.getName(), customer.getAddress()));
            }
        } catch (Exception ex) {
            Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, ex);
        }

        tblCustomers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomerTM>() {
            @Override
            public void changed(ObservableValue<? extends CustomerTM> observable, CustomerTM oldValue, CustomerTM selectedCustomer) {

                if (selectedCustomer == null) {
                    // Clear Selection
                    return;
                }

                txtCustomerId.setText(selectedCustomer.getId());
                txtCustomerName.setText(selectedCustomer.getName());
                txtCustomerAddress.setText(selectedCustomer.getAddress());

                txtCustomerId.setEditable(false);

                btnSave.setDisable(false);
                btnDelete.setDisable(false);

            }
        });
    }

    @FXML
    private void navigateToHome(MouseEvent event) throws IOException {
        AppInitializer.navigateToHome(root, (Stage) this.root.getScene().getWindow());
    }

    @FXML
    private void btnSave_OnAction(ActionEvent event) {

        if (txtCustomerId.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "CustomerTM ID is empty", ButtonType.OK).showAndWait();
            txtCustomerId.requestFocus();
            return;
        } else if (txtCustomerName.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "CustomerTM Name is empty", ButtonType.OK).showAndWait();
            txtCustomerName.requestFocus();
            return;
        } else if (txtCustomerAddress.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "CustomerTM Address is empty", ButtonType.OK).showAndWait();
            txtCustomerAddress.requestFocus();
            return;
        }

        if (tblCustomers.getSelectionModel().isEmpty()) {
            // New

            ObservableList<CustomerTM> items = tblCustomers.getItems();
            for (CustomerTM customerTM : items) {
                if (customerTM.getId().equals(txtCustomerId.getText())) {
                    new Alert(Alert.AlertType.ERROR, "Duplicate CustomerTM IDs are not allowed").showAndWait();
                    txtCustomerId.requestFocus();
                    return;
                }
            }


            CustomerDTO customerDTO = new CustomerDTO(txtCustomerId.getText(), txtCustomerName.getText(), txtCustomerAddress.getText());
            try {
                customerBO.saveCustomer(customerDTO);
                new Alert(Alert.AlertType.INFORMATION, "Customer has been saved successfully", ButtonType.OK).showAndWait();

                CustomerTM customerTM = new CustomerTM(txtCustomerId.getText(), txtCustomerName.getText(), txtCustomerAddress.getText());
                tblCustomers.getItems().add(customerTM);
                tblCustomers.scrollTo(customerTM);

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Failed to save the customer, try again", ButtonType.OK).showAndWait();
                Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, e);
            }

        } else {
            // Update

            try {
                customerBO.updateCustomer(new CustomerDTO(txtCustomerId.getText(), txtCustomerName.getText(), txtCustomerAddress.getText()));

                new Alert(Alert.AlertType.INFORMATION, "Customer has been updated successfully").show();
                CustomerTM selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
                selectedCustomer.setName(txtCustomerName.getText());
                selectedCustomer.setAddress(txtCustomerAddress.getText());
               // tblCustomers.refresh();

            } catch (Exception e) {

                new Alert(Alert.AlertType.ERROR, "Failed to update the customer, try again").show();
                Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, e);
            }

        }

        reset();

    }

    @FXML
    private void btnDelete_OnAction(ActionEvent event) {
        Alert confirmMsg = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to delete this customer?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = confirmMsg.showAndWait();

        if (buttonType.get() == ButtonType.YES) {
            int selectedRow = tblCustomers.getSelectionModel().getSelectedIndex();

            try {
                customerBO.removeCustomer(txtCustomerId.getText());

                    tblCustomers.getItems().remove(tblCustomers.getSelectionModel().getSelectedItem());
                    reset();

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Failed to delete the customer, try again").show();
                Logger.getLogger("lk.ijse.pos.controller").log(Level.SEVERE, null, e);
            }

        }

    }

    @FXML
    private void btnAddNew_OnAction(ActionEvent actionEvent) {
        reset();
    }

    private void reset() {
        txtCustomerId.clear();
        txtCustomerName.clear();
        txtCustomerAddress.clear();
        txtCustomerId.requestFocus();
        txtCustomerId.setEditable(true);
        btnSave.setDisable(false);
        btnDelete.setDisable(true);
        tblCustomers.getSelectionModel().clearSelection();
    }

}
