package com.emndeniz.cputool.adb;


import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by emindeniz on 14/05/16.
 * This class will work with terminal. It will get the adb data.
 */
public class TerminalExecutor {

    private Service<Void> backgroundService;
    private ArrayList<String> filterList = new ArrayList<String>();


    public ArrayList<String> getFilterList() {
        return filterList;
    }

    public void addToFilterList(String filter) {
        filterList.add(filter);
    }



    /**
     * Starts collecting cpu usage data from adb.
     */
    public void startCollectingCPUUsageFromADB() {

        backgroundService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        try {

                            BufferedReader cpuData = executeAdbFromTerminal();

                            String usageString = "";

                            String cpuUsageForEachProcess;
                            while ((cpuUsageForEachProcess = cpuData.readLine()) != null) {


                                if (isCancelled()) break; // This will break the loop when user hits stop button

                                //if(!(cpuUsageForEachProcess.contains("User")||cpuUsageForEachProcess.equals(""))) continue;
                                cpuUsageForEachProcess = modifyData(cpuUsageForEachProcess);

                                if(cpuUsageForEachProcess.equals("")) continue;

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

    /**
     * Execute the adb command.
     * @return BufferedReader
     * @throws IOException
     */
    private BufferedReader executeAdbFromTerminal() throws IOException {

        String[] adbCpuCommand = {
                "/bin/sh",
                "-c",
                "adb shell top"
        };


        // This execution will get the CPU data from adb
        Process adbProcess = Runtime.getRuntime().exec(adbCpuCommand);
        System.out.println("CPU watch starting...");

        InputStreamReader inputStreamReader = new InputStreamReader(adbProcess.getInputStream());

        return new BufferedReader(inputStreamReader);

    }

    private boolean firstData = true;
    private Integer dataCounter = 1;
    int spaceCounter =0;

    /**
     * Modify the cpu data and make it more readable.
     * @param cpuData cpuData string
     * @return modified cpu data
     */
    private String modifyData(String cpuData){
        if(firstData){
            cpuData = "--------------" + "Data " +  dataCounter.toString() + " received--------------";
            dataCounter++;
            firstData =false;
        }
        if(cpuData.equals("")){
            spaceCounter++;
            if(spaceCounter == 4){
                cpuData = "--------------" + "Data " +  dataCounter.toString() + " received--------------";
                spaceCounter = 0;
                dataCounter++;
            }
        }

        if (filterList!=null && filterList.size()>0){
            for (String list:filterList) {
                if(!cpuData.contains(list)||cpuData.equals("")){
                    return "";
                }
            }
        }

        return cpuData;
    }

    public Service<Void> getBackgroundService() {
        return backgroundService;
    }
}
