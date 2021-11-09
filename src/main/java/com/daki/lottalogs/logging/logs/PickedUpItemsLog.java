package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class PickedUpItemsLog extends Log {

    public PickedUpItemsLog() {

        super();
        super.setName("PickedUpItemsLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("User", "");
        super.addArgument("Item", "");
        super.addArgument("Location", "");

    }

}
