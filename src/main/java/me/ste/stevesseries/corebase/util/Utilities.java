package me.ste.stevesseries.corebase.util;

import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Utilities {

    public static ScriptEngine scriptEngine;
    public static Logger logger;

    private Utilities() {} // utility class, no constructor

    public static void init(Logger logger) {
        Utilities.logger = logger;
        Utilities.scriptEngine = new ScriptEngineManager(null).getEngineByName("JavaScript");
        if (scriptEngine == null) {
            logger.severe("No JavaScript engine is available, JavaScript support disabled.");
        }
    }
}
