package net.sf.sockettest.swing;

public interface SocketTestServerView extends AskView {
    String chooseFile();

    boolean isHexInput();

    void focusOnPort();

    void focusOnIp();

    void connected();

    void stopWaitInfo();

    void startWaitInfo();

    void appendMessage(String msg);

    void stopped();

    void showConnectionInfo(String ip);

    void socketSet(boolean set);

    void resetSendField();

    boolean isHexOutput();
}
