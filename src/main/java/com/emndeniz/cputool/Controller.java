package com.emndeniz.cputool;

import com.emndeniz.cputool.adb.TerminalExecutor;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jxl.write.WriteException;

import java.io.IOException;

/**
 * Controller class will handle user events.
 */
public class Controller {

    @FXML
    private TextArea textAreaCpuData = new TextArea();
    @FXML
    TextField textFieldFilter = new TextField();
    @FXML
    VBox filterVBox = new VBox();
    @FXML
    Label labelUserCpuUsage = new Label();
    @FXML
    Label labelTotalCpuUsage = new Label();
    @FXML
    Label labelSystemCpuUsage = new Label();


    private TerminalExecutor terminalExecutor = new TerminalExecutor();


    public void startCpuDataCollection() {
        terminalExecutor.startCollectingCPUUsageFromADB();
        textAreaCpuData.textProperty().bind(terminalExecutor.getBackgroundService().messageProperty());


        labelUserCpuUsage.textProperty().bind(terminalExecutor.getUserCpuUsageProperty());
        labelSystemCpuUsage.textProperty().bind(terminalExecutor.getSystemCpuUsageProperty());
        labelTotalCpuUsage.textProperty().bind(terminalExecutor.getTotalCpuUsageProperty());
    }


    public void stopCpuDataCollection() {
        terminalExecutor.getBackgroundService().cancel();
    }

    public void exportToExcel() {
        // Get cpu data from text area ad split each line
        String[] cpuData = textAreaCpuData.getText().split("\\r?\\n");
        try {
            // TODO hardcoded path will replace with the dynamic string after the input text added to gui.
            String path = "/Users/emindeniz/Documents/deneme1.xls";
            WriteExcel toExcel = new WriteExcel(path);
            toExcel.createAndExportToExcel(cpuData);
        } catch (IOException e) {
            System.err.println("Error occurs when creating excel file. Stacktrace : " + e.getMessage());
            e.printStackTrace();
        } catch (WriteException e) {
            System.err.println("Error occurs when creating excel file. Stacktrace : " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void addFilter() {

        String filter = textFieldFilter.getText();

        if (terminalExecutor.getFilterList().contains(filter)) return;

        terminalExecutor.addToFilterList(filter);
        textFieldFilter.clear();
        updateFilterLabels();

    }

    public void removeFilter(String filterName) {
        terminalExecutor.getFilterList().remove(filterName);
        updateFilterLabels();
    }

    public void removeAllFilters() {
        terminalExecutor.getFilterList().clear();
        updateFilterLabels();
    }


    /**
     * Clear cpu data text area
     */
    public void clearCpuData() {

        Service<Void> clearService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        updateMessage("");
                        return null;
                    }
                };
            }
        };

        if (terminalExecutor.getBackgroundService().isRunning()) return;
        //TODO For now ignore clear data when background service active

        textAreaCpuData.textProperty().bind(clearService.messageProperty());
        clearService.start();
        terminalExecutor.resetCounters();
    }

    /**
     * Update the labels on the right panel in case of add or delete.
     */
    private void updateFilterLabels() {

        filterVBox.getChildren().clear();
        filterVBox.setSpacing(5);


        for (String filter : terminalExecutor.getFilterList()) {

            HBox hBox = new HBox();
            hBox.setSpacing(5);

            Label label = new Label(filter);
            label.setPrefHeight(10);

            hBox.getChildren().add(label);
            hBox.getChildren().add(createDeleteFilterButton(filter));
            filterVBox.getChildren().add(hBox);
        }

    }

    /**
     * Creates delete button for each filter including the design.
     *
     * @param filter Filter string
     * @return delete button
     */
    private Button createDeleteFilterButton(String filter) {
        Image image = new Image(Main.class.getResource("/delete-icon.png").toExternalForm(), 15, 15, true, true);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(13);
        imageView.setFitHeight(13);

        Button deleteFilterButton = new Button();


        deleteFilterButton.setGraphic(imageView);
        deleteFilterButton.setPrefHeight(17);
        deleteFilterButton.setMaxHeight(20);
        deleteFilterButton.setMinHeight(15);
        deleteFilterButton.setPadding(new Insets(1, 2, 1, 2));
        deleteFilterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeFilter(filter);
            }
        });

        return deleteFilterButton;
    }
}
