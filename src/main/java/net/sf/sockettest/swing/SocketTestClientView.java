package net.sf.sockettest.swing;

public interface SocketTestClientView extends AskView {
    void focusOnIp();

    void focusOnPort();

    void startWaitInfo();

    void stopWaitInfo();

    void connected();

    void showConectionInfo(String messagge);

    void clearMessages();

    void focusSendField();

    void disconnected();

    boolean isHexOutput();

    void appendMessage(String message);

    void resetSend();

    String getMessages();

    String chooseFile();

    void saveText(String text, String fileName);
}
