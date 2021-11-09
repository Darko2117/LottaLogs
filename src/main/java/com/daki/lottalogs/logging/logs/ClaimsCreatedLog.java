package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class ClaimsCreatedLog extends Log {

    public ClaimsCreatedLog() {

        super();
        super.setName("ClaimsCreatedLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("User", "");
        super.addArgument("LowestY", "");
        super.addArgument("Area", "");

    }

}