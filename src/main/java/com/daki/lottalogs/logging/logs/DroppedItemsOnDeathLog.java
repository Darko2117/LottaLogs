package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class DroppedItemsOnDeathLog extends Log {

    public DroppedItemsOnDeathLog() {

        super();
        super.setName("DroppedItemsOnDeathLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("User", "");
        super.addArgument("Killer", "");
        super.addArgument("Items", "");
        super.addArgument("Location", "");

    }

}
