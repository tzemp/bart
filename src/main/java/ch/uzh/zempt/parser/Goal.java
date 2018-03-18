package ch.uzh.zempt.parser;

import java.util.ArrayList;
import java.util.List;

public class Goal {
    private List<String> lines;
    private String plugin;
    private String version;
    private String name;
    private String status;

    public Goal(String plugin, String version, String name) {
        this.plugin = plugin;
        this.version = version;
        this.name = name;
        this.lines = new ArrayList<>();
        // we assume a goal execution was successfull unless otherwise proven
        this.status = "SUCCESS";
    }

    public List<String> getLines() {
        return lines;
    }

    public String getPlugin() {
        return plugin;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public void addLine(String line) {
        this.getLines().add(line);
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
