package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.other.UtilityMethods;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class Log {

    private String name;
    private boolean enabled;
    private int daysOfLogsToKeep;
    private LinkedHashMap<String, String> arguments = new LinkedHashMap<>();

    public Log() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getDaysOfLogsToKeep() {
        return daysOfLogsToKeep;
    }

    public void setDaysOfLogsToKeep(int daysOfLogsToKeep) {
        this.daysOfLogsToKeep = daysOfLogsToKeep;
    }

    public LinkedHashMap<String, String> getArguments() {
        return arguments;
    }

    public void setArguments(LinkedHashMap<String, String> arguments) {
        this.arguments = arguments;
    }

    public void addArgument(String argumentName, String argumentValue) {
        this.arguments.put(argumentName, argumentValue);
    }

    public void addArgumentValue(String argumentValue) {
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            if (!entry.getValue().isEmpty()) continue;
            entry.setValue(argumentValue);
            break;
        }
    }

    public String getPath() {
        return File.separator + "logs" + File.separator + UtilityMethods.getDateStringYYYYMMDD() + "-" + name + ".txt";
    }

    public String getLogArgumentsString() {

        StringBuilder string = new StringBuilder();
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            string.append("|").append(entry.getKey()).append(":").append(entry.getValue());
        }
        string.append("|");

        return string.toString();

    }

}