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

    private boolean firstData;
    private Integer dataCounter;
    int spaceCounter;

    public void resetCounters() {
        firstData = true;
        dataCounter = 1;
        spaceCounter = 0;
    }

    /**
     * Starts collecting cpu usage data from adb.
     */
    public void startCollectingCPUUsageFromADB() {
        resetCounters();

        backgroundService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return createBackgroundTask();
            }
        };

        backgroundService.start();


    }

    private Task<Void> createBackgroundTask() {
        Task<Void> backgroundTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                try {

                    BufferedReader cpuData = executeAdbFromTerminal();
                    String cpuUsageString = "";

                    String cpuUsageForEachProcess;
                    while ((cpuUsageForEachProcess = cpuData.readLine()) != null) {


                        if (isCancelled()) break; // This will break the loop when user hits stop button

                        //if(!(cpuUsageForEachProcess.contains("User")||cpuUsageForEachProcess.equals(""))) continue;
                        cpuUsageForEachProcess = modifyData(cpuUsageForEachProcess);

                        if (cpuUsageForEachProcess.equals("")) continue;

                        System.out.println(cpuUsageForEachProcess); //Logging purposes

                        cpuUsageString += cpuUsageForEachProcess + "\n";
                        updateMessage(cpuUsageString);
                    }
                    cpuData.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        return backgroundTask;
    }

    /**
     * Execute the adb command.
     *
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

        InputStreamReader inputStreamReader = new InputStreamReader(adbProcess.getInputStream());

        return new BufferedReader(inputStreamReader);

    }


    /**
     * Modify the cpu data and make it more readable.
     *
     * @param cpuData cpuData string
     * @return modified cpu data
     */
    private String modifyData(String cpuData) {
        String modifiedCpuData="";
        if (firstData) {
            modifiedCpuData = "--------------" + "Data " + dataCounter.toString() + " received--------------";
            dataCounter++;
            firstData = false;
            return modifiedCpuData;
        }
        if (cpuData.equals("")) {
            spaceCounter++;
            if (spaceCounter == 4) {
                modifiedCpuData = "\n --------------" + "Data " + dataCounter.toString() + " received--------------";
                spaceCounter = 0;
                dataCounter++;
                return modifiedCpuData;
            }
        }

        if (filterList != null && filterList.size() > 0) {
            for (String list : filterList) {
                if (!cpuData.contains(list) || cpuData.equals("")) {
                    modifiedCpuData = "";
                }else{
                    modifiedCpuData = cpuData;
                    break;
                }

            }
        }
        else {
            modifiedCpuData = cpuData;
        }

        return modifiedCpuData;
    }

    public Service<Void> getBackgroundService() {
        return backgroundService;
    }

    public ArrayList<String> getFilterList() {
        return filterList;
    }

    public void addToFilterList(String filter) {
        filterList.add(filter);
    }

}
