package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class MCMMORepairUseLog extends Log {

    public MCMMORepairUseLog() {

        super();
        super.setName("MCMMORepairUseLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("User", "");
        super.addArgument("Item", "");

    }

}
