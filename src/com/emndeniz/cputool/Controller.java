package com.emndeniz.cputool;

import com.emndeniz.cputool.adb.TerminalExecutor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Controller {

    @FXML
    private TextArea textAreaCpuData = new TextArea();
    @FXML
    TextField textFieldFilter = new TextField();
    @FXML
    VBox filterVBox = new VBox();


    private TerminalExecutor terminalExecutor = new TerminalExecutor();


    public void startCpuDataCollection() {
        terminalExecutor.startCollectingCPUUsageFromADB();
        textAreaCpuData.textProperty().bind(terminalExecutor.getBackgroundService().messageProperty());
    }


    public void stopCpuDataCollection() {
        terminalExecutor.getBackgroundService().cancel();
    }

    public void exportToExcel() {

    }


    public void addFilters() {

        String filter = textFieldFilter.getText();
        terminalExecutor.addToFilterList(filter);
        updateFilterLabels();

    }

    /**
     * Update the labels on the right panel in case of add or delete.
     */
    private void updateFilterLabels() {

        filterVBox.getChildren().clear();
        filterVBox.setSpacing(5);


        for (String filter : terminalExecutor.getFilterList()) {

            Label label = new Label(filter);
            filterVBox.getChildren().add(label);
        }

    }
}
