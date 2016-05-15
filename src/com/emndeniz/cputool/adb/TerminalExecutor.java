package com.emndeniz.cputool.adb;


import javafx.concurrent.Service;
import javafx.concurrent.Task;

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
    private Process adbProcess;
    private String cpuUsageForEachProcess;

    public Service<Void> getBackgroundService() {
        return backgroundService;
    }

    private Service<Void> backgroundService;

    /**
     * Starts collecting cpu usage data from adb.
     */
    public void startCollectingCPUUsageFromADB() {


        String[] adbCpuCommand = {
                "/bin/sh",
                "-c",
                "adb shell top"
        };

        backgroundService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        try {

                            // This execution will get the CPU data from adb
                            adbProcess = Runtime.getRuntime().exec(adbCpuCommand);
                            System.out.println("CPU watch starting...");

                            cpuData = new BufferedReader(new InputStreamReader(adbProcess.getInputStream()));

                            String usageString = "";
                            int spaceCounter =0;
                            Integer dataCounter = new Integer(1);
                            boolean firstData = true;
                            while ((cpuUsageForEachProcess = cpuData.readLine()) != null) {

                                //if(!(cpuUsageForEachProcess.contains("User")||cpuUsageForEachProcess.equals(""))) continue;
                                if (isCancelled()) break; // This will break the loop when user hits stop button
                                // Every line is belongs to a process
                                if(firstData){
                                    cpuUsageForEachProcess = "--------" + "Data " +  dataCounter.toString() + " received--------";
                                    dataCounter++;
                                    firstData =false;
                                }
                                if(cpuUsageForEachProcess.equals("")){
                                    spaceCounter++;

                                    if(spaceCounter == 4){
                                        cpuUsageForEachProcess = "--------" + "Data " +  dataCounter.toString() + " received--------";
                                        spaceCounter = 0;
                                        dataCounter++;

                                    }else{
                                        continue;
                                    }

                                }

                                System.out.println(cpuUsageForEachProcess);

                                usageString += cpuUsageForEachProcess + "\n";
                                updateMessage(usageString);
                            }
                            cpuData.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
            }
        };

        backgroundService.start();

        System.out.println("Continue..");

    }


}
