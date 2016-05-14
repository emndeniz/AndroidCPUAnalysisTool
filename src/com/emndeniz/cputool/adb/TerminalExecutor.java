package com.emndeniz.cputool.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by emindeniz on 14/05/16.
 * This class will work with terminal. It will get the adb data.
 */
public class TerminalExecutor {
    // This cpuData object will held the output text which came from adb
    private BufferedReader cpuData;

    public Process getAdbProcess() {
        return adbProcess;
    }

    private Process adbProcess;

    private String cpuUsageForEachProcess;

    public Thread getTerminalThread() {
        return terminalThread;
    }

    private Thread terminalThread;


    /**
     * Starts collecting cpu usage data from adb.
     */
    public void startCollectingCPUUsageFromADB() {


        String[] adbCpuCommand = {
                "/bin/sh",
                "-c",
                "adb shell top"
        };

        terminalThread = new Thread(){
            @Override
            public void run() {

                try {

                    // This execution will get the CPU data from adb
                    adbProcess = Runtime.getRuntime().exec(adbCpuCommand);
                    System.out.println("CPU watch starting...");

                    cpuData = new BufferedReader(new InputStreamReader(adbProcess.getInputStream()));

                    // DataTransferInterface dataTransferInterface  = MainWindow.getMainWindow();




                    while ((cpuUsageForEachProcess = cpuData.readLine()) != null) {
                        // Every line is belongs to a process
                        System.out.println(cpuUsageForEachProcess);

                    }
                    cpuData.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        terminalThread.start();
        System.out.println("Continue..");

    }


}
