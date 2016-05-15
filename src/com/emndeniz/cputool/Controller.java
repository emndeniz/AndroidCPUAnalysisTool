package com.emndeniz.cputool;

import com.emndeniz.cputool.adb.TerminalExecutor;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class Controller{

    @FXML
    private TextArea textAreaCpuData = new TextArea();

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
}
