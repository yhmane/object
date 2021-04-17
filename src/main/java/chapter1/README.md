## 티켓판매 어플리케이션 (origin > theater)
### 요구사항
* 추첨을 통해 선정된 관람객에게 공연을 무료로 관람할 수 있는 초대장을 발송

### 클래스
* Invitation (초대장)
* Ticket (공연 티켓)
* Bag (소지품 보관함)
* Audience (관람객)
* TicketOffice (매표소)
* TicketSeller (판매원)
* Theater (극장)

### 무엇이 문제일까??
> 로버틴 마틴에 따르면 모든 모듈은 제대로 실행되어야 하고, 변경이 용이해야 하며, 이해하기 쉬워야 한다

코드는 제대로 동작하지만, 변경 용이성과 읽는 사람의 의사소통의 목적은 만족하지 못한다.
* Theater > enter 메서드
    * Audience와 TicketSeller 의존해 있다
    * Audience는 Bag, TicketSeller는 TicketOffice까지 의존 되어 있기에 변경에 취약한 문제가 있다
    * 즉 높은 결합도를 가지고 있다

---
## 리팩토링 (refactoring > theater)
### 어떻게 해결할 것인가?
* Theater가 Audience와 TicketSeller에 관해 세세한 부분까지 알 필요가 없다
* Audience와 TicketSeller를 자율적인 존재로 만들어야 한다
* Audience와 TicketSeller의 내부 구현을 외부에 노출하지 않고 스스로 책임지게 만들었다

수정전 (Theater)
```java
public void enter(Audience audience) {
    if (audience.getBag().hasInvitation()) {
        Ticket ticket = ticketSeller.getTicketOffice().getTicket();
        audience.getBag().setTicket(ticket);
    } else {
        Ticket ticket = ticketSeller.getTicketOffice().getTicket();
        audience.getBag().minusAmount(ticket.getFee());
        ticketSeller.getTicketOffice().plusAmount(ticket.getFee());
        audience.getBag().setTicket(ticket);
    }
}
```
수정후 (Theater, TicketSeller, Audience)
```java
public void enter(Audience audience) {
    ticketSeller.sellTo(audience);
}

public void sellTo(Audience audience) {
    Long ticketFee = audience.buy(ticketOffice.getTicket());
    ticketOffice.plusAmount(ticketFee); 
}

public Long buy(Ticket ticket) {
    if (bag.hasInvitation()) {
        bag.setTicket(ticket);
        return 0L;
    } else {
        bag.setTicket(ticket);
        bag.minusAmount(ticket.getFee());
        return ticket.getFee();
    }
}
```
### 무엇이 개선됐는가?
> 로버틴 마틴에 따르면 모든 모듈은 제대로 실행되어야 하고, 변경이 용이해야 하며, 이해하기 쉬워야 한다

위의 원칙을 다시 봐보자. 정상적으로 동작하고, 코드가 직관적으로 변했으며, Audience와 TicketSeller가 변경에 용이해졌다
* 책임의 이동
  * Theater에게 많은 책임이 부여 되었다. TicketSeller, TicketOffice, Audience, Bag (책임 분배 필요)
  * Theater가 몰라도 되는 세부사항 Audience와 TickerSeller 내부로 감추자 (캡슐화)  
  * 이러한 책임을 각 객체에 적절하게 분산시킴으로 절차적인 프로그래밍을 객체지향적으로 바꿀 수 있었다 (자율성을 높이고 응집도를 낮춤)
  
  
## 조금더 개선해보자 (think > theather)
수정전 (TicketSeller, Audience)
```java
public void sellTo(Audience audience) {
    Long ticketFee = audience.buy(ticketOffice.getTicket());
    ticketOffice.plusAmount(ticketFee); 
}

public Long buy(Ticket ticket) {
    if (bag.hasInvitation()) {
        bag.setTicket(ticket);
        return 0L;
    } else {
        bag.setTicket(ticket);
        bag.minusAmount(ticket.getFee());
        return ticket.getFee();
    }
}
```
수정후 (TicketSeller, Audience, TickerOffice, Bag)
```java
public void sellTo(Audience audience) {
    ticketOffice.sellTicketTo(audience);
}

public Long buy(Ticket ticket) {
    return bag.hold(ticket);
}

public void sellTicketTo(Audience audience) {
    Long ticketFee = audience.buy(getTicket());
    plusAmount(ticketFee);
}

public Long hold(Ticket ticket) {
    if (hasInvitation()) {
        setTicket(ticket);
        return 0L;
    } else {
        setTicket(ticket);
        minusAmount(ticket.getFee());
        return ticket.getFee();
    }
}
```
* TicketOffice와 Bag의 자율성을 확보했다
  * 하지만, TicketOffice와 Audience에 의존성이 추가 되었다
  * 기존 Audience는 TicketSeller에만 의존했는데, TicketSeller와 TicketOffice 모두에 의존하기 때문에 결합도가 높아져 버렸다
  * trade-off의 순간이 온 것이다
  
설계는 모든 사람을 만족시킬 수 없다. 훌륭한 설계는 적절한 트레이드오프의 결과물이라는 사실을 명심하자

---
* 캡슐화
* 책임의 분배
* 자율성

  