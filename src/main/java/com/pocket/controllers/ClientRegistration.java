package com.pocket.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import com.pocket.exceptions.EmptyEntryException;
import com.pocket.exceptions.InvalidEmailException;
import com.pocket.exceptions.InvalidPhoneNumberException;
import com.pocket.exceptions.UsernameAlreadyExistsException;
import com.pocket.services.UserService;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ClientRegistration {

    @FXML
    private Text registrationMessage;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;
    @FXML
    private DatePicker DateOfBirth;
    @FXML
    private TextField PhoneNumber;
    @FXML
    private TextField Email;
    @FXML
    private TextField FullName;
    @FXML
    private TextField Gender;
    @FXML
    private TextField Height;
    @FXML
    private Button back;



    @FXML
    public void initialize() {

        DatePicker DateOfBirth = new DatePicker();
        HBox hbox = new HBox(DateOfBirth);
    }

    @FXML
    public void handleRegisterAction() {
        try {
            LocalDate date = DateOfBirth.getValue();

            UserService.addClientUser(usernameField.getText(), passwordField.getText(), "Client", date, PhoneNumber.getText(),Email.getText(), true);
            registrationMessage.setText("Account created successfully!");
        } catch (UsernameAlreadyExistsException e) {
            registrationMessage.setText(e.getMessage());
        }
        catch (InvalidPhoneNumberException e) {
            registrationMessage.setText(e.getMessage());
        }
        catch (InvalidEmailException e) {
            registrationMessage.setText(e.getMessage());
        }
        catch (EmptyEntryException e) {
            registrationMessage.setText(e.getMessage());
        }


    }
    @FXML
    public void handleBackButton()
    {
        Stage stage;
        Parent root;
        try{

            stage = (Stage) back.getScene().getWindow();
            root = FXMLLoader.load(getClass().getClassLoader().getResource("SelectRole.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
            e.getCause();
        }

    }
}
