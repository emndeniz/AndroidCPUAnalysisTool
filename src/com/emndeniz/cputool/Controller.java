package com.emndeniz.cputool;

import com.emndeniz.cputool.adb.TerminalExecutor;

public class Controller {

    private TerminalExecutor terminalExecutor = new TerminalExecutor();

    public void startCpuDataCollection(){
        terminalExecutor.startCollectingCPUUsageFromADB();

    }

    public void stopCpuDataCollection(){
        terminalExecutor.getTerminalThread().stop();
    }

    public void exportToExcel(){

    }

}