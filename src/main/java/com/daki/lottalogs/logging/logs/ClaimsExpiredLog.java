package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class ClaimsExpiredLog extends Log {

    public ClaimsExpiredLog() {

        super();
        super.setName("ClaimsExpiredLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("User", "");
        super.addArgument("LowestY", "");
        super.addArgument("Area", "");

    }

}