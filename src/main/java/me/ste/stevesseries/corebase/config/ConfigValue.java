package me.ste.stevesseries.corebase.config;

import com.google.common.base.Preconditions;
import me.ste.stevesseries.corebase.CoreBase;
import org.bukkit.configuration.ConfigurationSection;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.util.Map;

public class ConfigValue {
    private ConfigurationSection section;
    private String path;

    public ConfigValue(ConfigurationSection section, String path) {
        this.section = section;
        this.path = path;
    }

    public String getValue(Map<String, String> context) {
        Preconditions.checkState(section.isString(path), "Config value must be a string");
        Bindings bindings = CoreBase.getPlugin(CoreBase.class).scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.clear();
        bindings.put("section", section);
        bindings.put("path", path);
        bindings.put("env", context);
        CoreBase.getPlugin(CoreBase.class).scriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        String str = section.getString(path);
        StringBuilder result = new StringBuilder();
        boolean dollarSign = false;
        int bracketsOpen = 0;
        int beginIndex = -1;
        int lastEndIndex = 0;
        char[] chars = str.toCharArray();
        for(int i = 0; i < chars.length; i++) { // i is for index
            char c = chars[i];
            if(dollarSign) {
                if(c == '{') {
                    if(beginIndex == -1) {
                        beginIndex = i + 1;
                    }
                    bracketsOpen++;
                } else if(c == '}') {
                    if(bracketsOpen > 0) {
                        bracketsOpen--;
                    }
                    if(bracketsOpen <= 0) {
                        String evalResult = "";
                        try {
                            evalResult = CoreBase.getPlugin(CoreBase.class).scriptEngine.eval(str.substring(beginIndex, i)).toString();
                        } catch (ScriptException e) {
                            e.printStackTrace();
                        }
                        result.append(str, lastEndIndex, beginIndex - 2);
                        result.append(evalResult);
                        lastEndIndex = i + 1;
                        dollarSign = false;
                        beginIndex = -1;
                        bracketsOpen = 0;
                    }
                } else {
                    dollarSign = false;
                }
            } else {
                if(c == '$') {
                    dollarSign = true;
                }
            }
        }
        result.append(str.substring(lastEndIndex));
        return result.toString();
    }
}