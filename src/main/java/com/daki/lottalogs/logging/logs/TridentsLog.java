package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class TridentsLog extends Log {

    public TridentsLog() {

        super();
        super.setName("TridentsLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("Player", "");
        super.addArgument("Trident", "");
        super.addArgument("Location", "");
        super.addArgument("Action", "");
        super.addArgument("Target", "");

    }

}
