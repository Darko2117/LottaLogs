package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class CommandsWithLocationLog extends Log {

    public CommandsWithLocationLog() {

        super();
        super.setName("CommandsWithLocationLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("User", "");
        super.addArgument("Command", "");
        super.addArgument("Location", "");

    }

}
