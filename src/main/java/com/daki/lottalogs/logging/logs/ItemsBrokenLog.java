package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class ItemsBrokenLog extends Log {

    public ItemsBrokenLog() {

        super();
        super.setName("ItemsBrokenLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("User", "");
        super.addArgument("Item", "");
        super.addArgument("Location", "");

    }

}
