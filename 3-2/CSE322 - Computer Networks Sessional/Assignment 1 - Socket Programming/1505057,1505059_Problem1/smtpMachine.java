import java.io.IOException;

public class smtpMachine {
    smtpStates beginState;
    smtpStates heloState;
    smtpStates mailFromState;
    smtpStates rcptToState;
    smtpStates dataState;

    smtpStates currentState;

    public smtpMachine(){
        beginState = new beginState(this);
        heloState = new heloState(this);
        mailFromState = new mailFromState(this);
        rcptToState = new rcptToState(this);
        currentState = beginState;
    }

    void helo() throws IOException {
        currentState.helo();
    }

    void mailfrom() throws IOException {
        currentState.mailFrom();
    }

    void rcptTo() throws IOException {
        currentState.rcptTo();
    }

    void data() throws IOException {
        currentState.data();
    }

    public smtpStates getBeginState() {
        return beginState;
    }

    public void setBeginState(smtpStates beginState) {
        this.beginState = beginState;
    }

    public smtpStates getHeloState() {
        return heloState;
    }

    public void setHeloState(smtpStates heloState) {
        this.heloState = heloState;
    }

    public smtpStates getMailFromState() {
        return mailFromState;
    }

    public void setMailFromState(smtpStates mailFromState) {
        this.mailFromState = mailFromState;
    }

    public smtpStates getRcptToState() {
        return rcptToState;
    }

    public void setRcptToState(smtpStates rcptToState) {
        this.rcptToState = rcptToState;
    }

    public smtpStates getDataState() {
        return dataState;
    }

    public void setDataState(smtpStates dataState) {
        this.dataState = dataState;
    }

    public smtpStates getCurrentState() {
        return currentState;
    }

    public void setCurrentState(smtpStates currentState) {
        this.currentState = currentState;
    }
}
