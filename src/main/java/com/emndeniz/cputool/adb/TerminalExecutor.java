package com.emndeniz.cputool.adb;


import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

    private StringProperty totalCpuUsageProperty = new SimpleStringProperty(this, "totalCpuUsageProperty", "");
    private StringProperty userCpuUsageProperty = new SimpleStringProperty(this, "userCpuUsageProperty", "");
    private StringProperty systemCpuUsageProperty = new SimpleStringProperty(this, "systemCpuUsageProperty", "");

    private boolean firstData;
    private Integer dataCounter;
    int spaceCounter;

    public void resetCounters() {
        firstData = true;
        dataCounter = 1;
        spaceCounter = 0;
        setSystemCpuUsageProperty("");
        setUserCpuUsageProperty("");
        setTotalCpuUsageProperty("");
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
     * Updates total usage labels(User usage,system usage and total usage)
     * @param cpuData cpuData string
     */
    private void updateTotalUsages(String cpuData){
        String userUsage = cpuData.substring(5,cpuData.indexOf("%"));
        String systemUsage = cpuData.substring(cpuData.indexOf(",") + 9,cpuData.indexOf("%",cpuData.indexOf("%")+1));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                setSystemCpuUsageProperty("%" + systemUsage);
                setUserCpuUsageProperty("%" + userUsage);
                Integer totalUsage = Integer.parseInt(systemUsage) + Integer.parseInt(userUsage);
                setTotalCpuUsageProperty("%" + totalUsage.toString());
            }
        });
    }

    /**
     * Modify the cpu data and make it more readable.
     *
     * @param cpuData cpuData string
     * @return modified cpu data
     */
    private String modifyData(String cpuData) {
        if(cpuData.contains("User")&&cpuData.contains("System")&&cpuData.contains("%")){
          updateTotalUsages(cpuData);
        }

        String modifiedCpuData = "";
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
                } else {
                    modifiedCpuData = cpuData;
                    break;
                }

            }
        } else {
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

    public StringProperty getUserCpuUsageProperty() {
        return userCpuUsageProperty;
    }

    public void setUserCpuUsageProperty(String userCpuUsageProperty) {
        this.userCpuUsageProperty.set(userCpuUsageProperty);
    }

    public StringProperty getTotalCpuUsageProperty() {
        return totalCpuUsageProperty;
    }

    public void setTotalCpuUsageProperty(String totalCpuUsageString) {
        this.totalCpuUsageProperty.set(totalCpuUsageString);
    }
    public StringProperty getSystemCpuUsageProperty() {return systemCpuUsageProperty;}

    public void setSystemCpuUsageProperty(String systemCpuUsageProperty) {
        this.systemCpuUsageProperty.set(systemCpuUsageProperty);
    }
}
