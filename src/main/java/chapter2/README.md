# 영화 예매 시스템
### 요구사항
사용자는 영화 예매 시스템을 이용해 쉽고 빠르게 영화를 예매할 수 있다

### 용어
사용자가 실제로 예매하는 대상은 영화가 아니라 상영
* 영화
    * 제목, 상영시간, 가격정보 등
* 상영
    * 실제로 관객이 영화를 관람하는 사건. 상영일자, 시간, 시간 등
* 할인액을 결정하는 두가지 규칙
    * 할인 조건 - 가격의 할인 여부를 결정
        * 순서조건 - 매일 몇번째 상영 영화
        * 기간조건 - 요일, 시작~종료시간
    * 할인 정책
        * 비율 - 일정 비율 (%)
        * 금액 - 일정 금액

### 클래스
* Moive (영화)
* Screening (상영)
* DiscountPolicy (할인 정책)
* AmountDiscountPolicy (금액 할인 정책)
* PercentDiscountPolicy (비율 할인 정책)
* DiscountCondition (할인 조건)
* SequenceCondition (순번 조건)
* PeriodCondition (기간 조건)
* Reservation (예매)
    
### 객체지향 프로그래밍
대부분의 사람들은 클래스를 결정한 후에 클래스에 어떤 속성과 메서드가 필요한지 고민한다.<br/>
안타깝게도 이것은 객체 지향의 본질과는 거리가 멀다. 진정한 객체지향의 패러다임은 클래스가 아닌 객체의 초점을 맞추는 것이다.

* 어떤 클래스가 필요할지를 고민하기 전에 어떤 객체들이 필요한지 고민하라
* 객체를 독립적인 존재가 아니라 기능을 구현하기 위해 협력하는 공동체의 일원으로 접근하라

### 클래스 구현하기
> 외부에서의 간섭을 최소화하여 객체를 자율적인 존재로 만든다 (캡슐화 - 객체의 상태는 숨기고 행동만 외부로 공개)
* 객체는 상태값과 행위로 이루어 진다
* 외부에서 객체의 속성에 직접 접근하는 것을 막는다
* 적절한 행위 값을 제공하여 내부 상태값을 변경 할 수 있도록 한다
* 적절한 접근 제어자를 제공한다 (public, protected, private)
---
* 1장에서는 돈과 관련된 상태를 long amount로 표현하였다
    * 2장에서는 Money로 표현한다
    * 하나의 인스턴스 변수만 포함하더라도 개념을 명시적으로 표현하려면 클래스로 도메인을 구현해라
    * 조금더 직관적으로 의미를 전달할 수 있다
    * 금액과 관련된 로직이 서로 다른 곳에 중복되어 구현하는 것을 막을 수 있다

### 상속과 다형성
> 컴파일 시간 의존성과 실행 시간 의존성 -> Movie와 DiscountPolicy는 의존 관계
```java
// 금액 할인정책에 의존
Movie avatar = new Movie("아바타", Duration.ofMinutes(120), Money.wons(10000), new AmountDiscountPolicy(Money.wons(800), ...));
// 비율 할인정책에 의존
Movie avatar = new Movie("아바타", Duration.ofMinutes(120), Money.wons(10000), new PercentDiscountPolicy(0,1, ...));

public class Movie {
    public Money calculateMovieFee(Screening screening) {
        return fee.minus(discountPolicy.calculateDiscountAmount(screening));
    }
}
```
* 코드의 의존성과 실행 시점의 의존성이 서로 다를 수 있다.
    * 코드의 의존성과 실행 시점의 의존성이 다르면 코드를 이해하기 어려워 질 수 있다
    * 반면, 코드는 유연해지고 확장 가능성이 생기는 트레이드오프의 산물
    * Movie.calculateMovieFee()는 코드레벨에서 어떠한 DiscountPolicy에 의해 calculateDiscountAmount() 실행될지 몰라도 된다
    
#### 상속
> 상속은 객체지향에서 코드를 재사용하기 위해 가장 널리 사용되는 방법
* 기존 클래스가 가지고 있는 모든 속성과 행동을 새로운 클래스에 전이
* 부모 클래스의 구현을 공유하면서도 행동이 다른 자식 클래스에 대하여 구현이 용이  
* DiscountPolicy
    * AmountDiscountPolicy
    * PercentDiscountPolicy
* 단점
    * 상속을 하게 되면 캡슐화가 위반될 수 있다
    * 객체 설계가 유연하지 않게 된다

단점을 극복하기 위해 다음과 같이 인스턴스 변수를 이용해 실행 시점에 할인 정책을 변경할 수 있도록 코드를 추가해주자
```java
public class Moive {
    private DiscountPolicy discountPolicy;
    
    public void chanseDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }
}

Movie avatar = new Movie("아바타", Duration.ofMinutes(120), Money.wons(10000), new AmountDiscountPolicy(Money.wons(800), ...));
avatar.chanseDiscountPolicy(new PercentDiscountPolicy(0,1, ...));
```