package me.ste.stevesseries.corebase.config;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import me.ste.stevesseries.corebase.util.Utilities;
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

    public String getAsString(Map<String, String> context) {
        return getAsString(context, ($) -> true);
    }

    public String getAsString(Map<String, String> context, Function<String, Boolean> bindingFilter) {
        Preconditions.checkState(section.isString(path), "Value not string");
        Bindings bindings = Utilities.scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.clear();
        if (bindingFilter.apply("section")) {
            bindings.put("section", section);
        }
        if (bindingFilter.apply("path")) {
            bindings.put("path", path);
        }
        bindings.put("env", context);
        Utilities.scriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        return section.getString(path) == null ? "" : handleString(section.getString(path));
    }

    public List<String> getAsStringList(Map<String, String> context) {
        return getAsStringList(context, ($) -> true);
    }

    public List<String> getAsStringList(Map<String, String> context, Function<String, Boolean> bindingFilter) {
        Preconditions.checkState(section.isList("path"), "Value not list");
        Bindings bindings = Utilities.scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.clear();
        if (bindingFilter.apply("section")) {
            bindings.put("section", section);
        }
        if (bindingFilter.apply("path")) {
            bindings.put("path", path);
        }
        bindings.put("env", context);
        Utilities.scriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        List<String> list = new ArrayList<>();
        for (String s : section.getStringList(path)) {
            list.add(handleString(s));
        }
        return list;
    }

    private String handleString(String str) {
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
                            evalResult = Utilities.scriptEngine.eval(str.substring(beginIndex, i)).toString();
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

    // =======================================
    // NH (No Handling) methods

    public int getAsInt() {
        Preconditions.checkState(section.isInt(path), "Value not integer");
        return section.getInt(path);
    }

    public List<Integer> getAsIntList() {
        Preconditions.checkState(section.isList(path), "Value not list");
        return section.getIntegerList(path);
    }

    public long getAsLong() {
        Preconditions.checkState(section.isLong(path), "Value not long");
        return section.getLong(path);
    }

    public List<Long> getAsLongList() {
        Preconditions.checkState(section.isList(path), "Value not list");
        return section.getLongList(path);
    }

    public double getAsDouble() {
        Preconditions.checkState(section.isDouble(path), "Value not double");
        return section.getDouble(path);
    }

    public List<Double> getAsDoubleList() {
        Preconditions.checkState(section.isList(path), "Value not list");
        return section.getDoubleList(path);
    }
}