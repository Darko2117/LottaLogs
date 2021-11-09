package com.daki.lottalogs.logging.logs;

import com.daki.lottalogs.logging.Logging;

public class MinecartsDestroyedLog extends Log {

    public MinecartsDestroyedLog() {

        super();
        super.setName("MinecartsDestroyedLog");
        super.setEnabled(true);
        super.setDaysOfLogsToKeep(Logging.defaultDaysOfLogsToKeep);
        super.addArgument("Time", "");
        super.addArgument("Attacker", "");
        super.addArgument("Location", "");
        super.addArgument("ClaimOwner", "");

    }

}
