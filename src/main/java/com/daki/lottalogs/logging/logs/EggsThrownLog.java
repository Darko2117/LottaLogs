package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class EggsThrownLog extends Log {

    public EggsThrownLog() {

        super();
        super.setName("EggsThrownLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("User", "");
        super.addArgument("Location", "");
        super.addArgument("ClaimOwner", "");

    }

}