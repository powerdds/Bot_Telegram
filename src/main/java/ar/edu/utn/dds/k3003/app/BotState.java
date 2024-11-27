package ar.edu.utn.dds.k3003.app;

public class BotState {
    SubState subState = SubState.START;


    public SubState getSubState() {
        return subState;
    }
    public void setSubState(SubState subState) {
        this.subState = subState;
    }


    public void execute(String messageText, Bot bot) throws Exception {

    }

}
