package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class ItemsDestroyedLog extends Log {

    public ItemsDestroyedLog() {

        super();
        super.setName("ItemsDestroyedLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("Item", "");
        super.addArgument("Location", "");
        super.addArgument("Cause", "");

    }

}
