package com.emndeniz.cputool;

import com.emndeniz.cputool.adb.TerminalExecutor;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller{

    @FXML
    private TextArea textAreaCpuData = new TextArea();
    @FXML
    TextField textFieldFilter = new TextField();



    private TerminalExecutor terminalExecutor = new TerminalExecutor();


    public void startCpuDataCollection(){
        terminalExecutor.startCollectingCPUUsageFromADB();
        textAreaCpuData.textProperty().bind(terminalExecutor.getBackgroundService().messageProperty()) ;
    }


    public void stopCpuDataCollection(){
        terminalExecutor.getBackgroundService().cancel();
    }

    public void exportToExcel(){

    }


    public void addFilters(){

        String filter = textFieldFilter.getText();
        terminalExecutor.addToFilterList(filter);

    }
}
