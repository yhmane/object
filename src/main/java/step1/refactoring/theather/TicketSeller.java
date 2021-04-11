package step1.refactoring.theather;

public class TicketSeller {
    private TicketOffice ticketOffice;

    public TicketSeller(TicketOffice ticketOffice) {
        this.ticketOffice = ticketOffice;
    }

    public TicketOffice getTicketOffice() {
        return ticketOffice;
    }

    public void sellTo(Audience audience) {
        Long ticketFee = audience.buy(ticketOffice.getTicket());
        ticketOffice.plusAmount(ticketFee);
    }
}
