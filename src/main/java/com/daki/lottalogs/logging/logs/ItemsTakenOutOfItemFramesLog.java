package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class ItemsTakenOutOfItemFramesLog extends Log {

    public ItemsTakenOutOfItemFramesLog() {

        super();
        super.setName("ItemsTakenOutOfItemFramesLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("User", "");
        super.addArgument("Item", "");
        super.addArgument("Location", "");

    }

}
