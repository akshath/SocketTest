package net.sf.sockettest.swing;

public interface AskView {
    boolean confirm(String title, String message, int option);

    void error(String error);

    void error(String error, String heading);
}
