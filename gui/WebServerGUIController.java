package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import main.RunWebServer;

import java.io.File;


public class WebServerGUIController {
    private final RunWebServer serverSwitch = new RunWebServer();

    private boolean isServerOn = false;

    @FXML
    private AnchorPane stage;

    @FXML
    private Text serverStatusLabel, serverAddressLabel, serverPortLabel;

    @FXML
    private Button serverButton, rootDirectoryButton, maintenanceButton;

    @FXML
    private CheckBox switchCheckbox;

    @FXML
    private TextField portField, webRootField, maintenanceField;

    @FXML
    public void initialize() {
        switchCheckbox.setDisable(true);
    }

    public void startServer() {
        if (!portField.getText().isBlank()) {
            serverSwitch.setPort(Integer.parseInt(portField.getText()));
        }
        Thread serverThread = new Thread(serverSwitch);
        isServerOn = !isServerOn;

        if (isServerOn) {
            this.serverOnConfiguration();
            serverThread.start();
        } else {
            this.serverOffConfiguration();
            serverSwitch.closeSocket();
        }
    }

    public void restartServerOnMaintenance(boolean maintenance) {
        serverSwitch.closeSocket();
        serverSwitch.setPort(Integer.parseInt(portField.getText()));

        if (maintenance) {
            serverSwitch.setWebRoot(maintenanceField.getText());
        } else {
            serverSwitch.setWebRoot(null);
        }

        Thread serverThread = new Thread(serverSwitch);
        serverThread.start();
    }

    public void checkMaintenanceMode() {
        if (switchCheckbox.isSelected()) {
            serverStatusLabel.setText("maintenance");
            maintenanceField.setDisable(true);
            maintenanceButton.setDisable(true);
            webRootField.setDisable(false);
            rootDirectoryButton.setDisable(false);
            restartServerOnMaintenance(true);
        } else {
            serverStatusLabel.setText("running...");
            maintenanceField.setDisable(false);
            maintenanceButton.setDisable(false);
            webRootField.setDisable(true);
            rootDirectoryButton.setDisable(true);
            restartServerOnMaintenance(false);
        }
    }

    @FXML
    public void openDirectoryPicker(ActionEvent e) {
        String buttonPressed = ((Button)e.getSource()).getId();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource File");
        directoryChooser.setInitialDirectory(
                new File("E:/Projects/College/SVV/svv-project")
        );
        File chosenDirectory = directoryChooser.showDialog(stage.getScene().getWindow());

        if (chosenDirectory == null) return;

        if (buttonPressed.equals("rootDirectoryButton")) {
            webRootField.setText(chosenDirectory.getAbsolutePath());
        } else {
            maintenanceField.setText(chosenDirectory.getAbsolutePath());
        }
    }

    public void serverOnConfiguration() {
        serverSwitch.setWebRoot(webRootField.getText());
        switchCheckbox.setDisable(false);
        webRootField.setDisable(true);
        rootDirectoryButton.setDisable(true);
        portField.setDisable(true);
        serverButton.setText("Stop server");
        serverStatusLabel.setText("running...");
        serverAddressLabel.setText(serverSwitch.getIPAddress());
        serverPortLabel.setText(String.valueOf(serverSwitch.getPort()));
    }

    public void serverOffConfiguration() {
        switchCheckbox.setDisable(true);
        portField.setDisable(false);
        webRootField.setDisable(false);
        maintenanceField.setDisable(false);
        serverButton.setText("Start server");
        serverStatusLabel.setText("not running");
        serverAddressLabel.setText("not running");
        serverPortLabel.setText("not running");
    }
}
