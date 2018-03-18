package ch.uzh.zempt.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents a module which is created during
 * the parsing of the reactor summary.
 */
public class Module {
    private String name;
    private String status;
    private List<String> lines;

    public Module(String name, String status) {
        this.name = name;
        this.status = status;
        this.lines = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<String> getLines() {
        return lines;
    }

    public String getStatus() {
        return status;
    }

    public void clean() {
        this.removeEndingInfoLines();
    }

    /*
     * Helper function to remove Endling info lines in the log lines.
     */
    private void removeEndingInfoLines() {
        if (this.getLines().size() > 0) {
            String lastLine = this.getLines().get(this.getLines().size() - 1);
            while (lastLine.contains("[INFO] -------------------------") || lastLine.length() == 0) {
                this.getLines().remove(lastLine);
                lastLine = this.getLines().get(this.getLines().size() - 1);
            }
        }
    }

}
