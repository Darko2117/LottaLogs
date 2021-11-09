package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class CratePrizesLog extends Log {

    public CratePrizesLog() {

        super();
        super.setName("CratePrizesLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("User", "");
        super.addArgument("Items", "");
        super.addArgument("Commands", "");
        super.addArgument("Crate", "");

    }

}
