package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class DroppedItemsLog extends Log {

    public DroppedItemsLog() {

        super();
        super.setName("DroppedItemsLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("User", "");
        super.addArgument("Item", "");
        super.addArgument("Location", "");

    }

}
