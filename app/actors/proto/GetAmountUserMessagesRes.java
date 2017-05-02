package actors.proto;

import models.AmountUserMessages;

public class GetAmountUserMessagesRes {
    private AmountUserMessages amountUserMessages;

    public GetAmountUserMessagesRes(AmountUserMessages amountUserMessages) {
        this.amountUserMessages = amountUserMessages;
    }

    public AmountUserMessages getAmountUserMessages() {
        return amountUserMessages;
    }

    @Override
    public String toString() {
        return "GetAmountUserMessagesRes{" +
                "amountUserMessages=" + amountUserMessages +
                '}';
    }
}
