package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class FarmLimiterLog extends Log {

    public FarmLimiterLog() {

        super();
        super.setName("FarmLimiterLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("Entity", "");
        super.addArgument("Location", "");
        super.addArgument("ClaimOwner", "");

    }

}
