package com.daki.lottalogs.logs;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.Methods;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Log {

    private String name = this.getClass().getSimpleName();
    private boolean enabled = true;
    private int daysOfLogsToKeep = 365;
    private List<String> blacklistedStrings = new ArrayList<>();
    private LinkedHashMap<String, String> arguments = new LinkedHashMap<>();

    public Log(String[] argumentKeys, String[] argumentValues) {

        if (argumentKeys.length == 0) {
            throw new IllegalArgumentException(name + " doesn't have argument keys. Not initializing it, fix this immediately!");
        }

        if (argumentValues.length == 0) {
            for (int i = 0; i < argumentKeys.length; i++) {
                arguments.put(argumentKeys[i], "");
            }
            return;
        }

        if (argumentKeys.length != argumentValues.length) {
            throw new IllegalArgumentException("Number of argument keys and values doesn't match for " + name + ". Not initializing it, fix this immediately! If one of the values isn't needed pass an empty string.");
        }

        for (int i = 0; i < argumentKeys.length; i++) {
            arguments.put(argumentKeys[i], argumentValues[i]);
        }

    }

    public String getPath() {

        return File.separator + "logs" + File.separator + Methods.getDateStringYYYYMMDD() + "-" + name + ".txt";

    }

    public String getStringToWrite() {

        if (arguments.entrySet().iterator().next().getValue().isEmpty()) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder("|");
        for (Map.Entry<String, String> entry : arguments.entrySet()) {

            String argumentKey = entry.getKey();
            String argumentValue = entry.getValue();

            argumentValue = argumentValue.replaceAll("\n", " ");
            argumentValue = argumentValue.replaceAll("\\|", " ");

            stringBuilder.append(argumentKey).append(":").append(argumentValue).append("|");

        }

        stringBuilder.append("\n");

        String string = stringBuilder.toString();

        {
            String stringLowercased = string.toLowerCase();
            for (String blacklistedString : Logging.getCachedLogs().get(name).getBlacklistedStrings()) {
                if (stringLowercased.contains(blacklistedString)) {
                    return "";
                }
            }
        }

        return string;

    }

}
