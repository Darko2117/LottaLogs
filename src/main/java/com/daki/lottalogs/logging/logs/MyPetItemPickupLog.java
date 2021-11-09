package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class MyPetItemPickupLog extends Log {

    public MyPetItemPickupLog() {

        super();
        super.setName("MyPetItemPickupLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("Pet", "");
        super.addArgument("Owner", "");
        super.addArgument("Item", "");
        super.addArgument("Location", "");

    }

}
