# 메시지와 인터페이스 (chapter6)
---
* 객체지향적 프로그램 을지향 ->
    * 클래스가 아니라 객체에 초점 ->
        * 구체적으로 객체의 책임에 초점

중요한 것은 *책임*이 객체가 수신할 메시지의 기반이 된다는 것

---
## 협력과 메시지
* 객체는 서로 협력의 관계에 있고 메세지를 주고 받는다!! (Chapter3)
* 메시지는 객체 사이의 매개체이다
    * 즉 유일한 접근 방법은 메세지

### 클라이언트 - 서버 모델
* 두 객체 사이의 협력 관계를 설명하기 위한 전통적인 방법 *클라이언트-서버모델*
* 협력안에서 메세지 전송쪽을 클라이언트라 지칭
* 협력안에서 메세지 수신쪽을 서버라 지칭
  협력은 클라이언트가 서버의 서비스를 요청하는 단방향 상호작용

[image:CCB2AB33-2647-4E6E-8CB8-0BCFB0BA7B3D-2837-0000079F8C843890/782C98A2-EAD8-4B5D-B37F-4666DEF3ECDC.png]
* Screening은 메시지를 전송하는 클라이언트, Movie는 메시지를 수신하는 서버

[image:344707DF-D4DE-427E-A8F7-230815DF6D29-2837-000007A90198CA08/8C50EE1C-1514-498A-BAFB-C7A575ADE07F.png]
* Movie은 메시지를 전송하는 클라이언트,  DiscountPolicy는 메시지를 수신하는 서버

[image:CCC146A0-654B-4437-BA87-0A2EB1EDBA98-2837-000007B25F5B9422/2DE32167-82A1-4BB8-B1BF-0C5ED73E4A05.png]
* 이처럼 객체는 클라이언트와 서버 모두의 역할을 수행할 수 있다

### 메세지 구조
[image:ED12CCAF-77D6-4DDC-A3A3-4EC3382ACAB0-2837-000007D1C4736338/7F39BA61-D1AD-4B6A-9B5B-7DDC313EB254.png]

### 메시지와 메서드
* 메시지 전송자는 어떤 메시지를 전송할지, 메시지 수신자는 어떤 메시지를 수신할지만 알면 된다. (즉, 어떠한 클래스가 전송하는지, 어떠한 클래스가 수신하는지 몰라도 된다)

condition의 경우 PeriodCondition, SequenceCondition 인스턴스가 올 수 있다. 컴파일시점과 실행시점의 인스턴스가 다르기 때문에, 이러한 구조의 메커니즘은 두 객체 사이의 결합도를 낮춤으로써 유연하고 확장 가능한 코드를 작성할 수 있게 만든다.

### 용어 정리
[image:81403781-346E-4F7C-A9E4-089466F30FD0-2837-00000B8DEF889942/A0362F7D-9159-4AE1-BE6B-DDC240FF49CF.png]

* 메시지
    * 객체간 협력을 위해 사용하는 의사소통 메커니즘
* 오퍼레이션
    * 객체가 다른 객체에게 제공하는 추상적인 서비스
* 메서드
    * 메시지에 응답하기 위해 실행되는 코드 블록. 즉, 메서드는 오퍼레이션의 구현이다
* 퍼블릭 인터페이스
    * 객체가 협력에 참여하기 위해 외부에서 수힌할 수 있는 메시지의 묶음, 집합이다
* 시그니처
    * 시그니처는 오퍼레이션의 이름과 파라미터를 합쳐 부르는 말이다

---
## 인터페이스와 설계 품질
인터페이스 설계 품질을 위해 다음과 같은 원칙을 고려해보자
* 디미터 법칙
* 묻지 말고 시켜라
* 의도를 드러내는 인터페이스

### 디미터 법칙
> “낯선 자에게 말하지 말라” - [Larman]
> “오직 인접한 이웃하고만 말하라” - [Metz]
* 객체 내부 구조에 강하게 결합되지 않도록 협력 경로를 제한하라
* ‘오직 하나의 .(도트)를 이용하라’ 라고 요약 되기도 한다. 아래는 위반되는 코드이다.
```java
screening.getMovie().getDiscountCondition();
```

즉, 디미터 법칙은 객체가 객체 자신이 자율적 존재가 되어야 함을 강조한다

### 묻지 말고 시켜라
* 객체의 상태에 관해 묻지 말고 원하는 것을 시켜라. 이런 스타일의 메시지 작성을 장려하는 원칙이다.
* 메시지 전송자는 메시지 수신자의 상태를 기반으로 결정을 내린 후 메신지 수신자의 상태를 바꿔서는 안된다.
* 이 원칙을 따르면, 자연스럽게 정보 전문가에게 책임을 할당하게 되고 높은 응집도를 가진 클래스를 얻을수 있다.

### 의도를 드러내는 인터페이스
> 메서드를 명명하는 두가지 방법
> 첫번째, 메서드가 작업을 어떻게 수행할지 나타내도록 이름을 지어라 - [켄트백]

* 다만 이런 스타일은 좋지 않다
```java
public class PeriodCondition {
	public boolean isSatisfiedByPeriod(Screening screeing) {}
}

public class SequenceCondition {
	public boolean isSatisfiedBySequence(Screening screeing) {}
}
```

* 메서드에 대해 제대로된 커뮤니케이션이 안된다. 두가지 모두 할인 조건을 판단하는 작업을 수행하지만, 두 메서드의 이름이 다르기 때문에 내부 구현을 잘 모른다면 두 메서드가 동일한 작업을 수행한다는 사실을 알기 어렵다
* 더 큰 문제는 메서드 수준에서 캡슐화를 위반한다. 할인 여부를 판단하는 방법이 변경된다면 메서더의 이름 역시 바뀌어야 할 것이다.

> 두번째, ‘어떻게’가 아니라 ‘무엇’을 하는지를 드러내라
```java
public class PeriodCondition implements DiscountCondition {
	public boolean isSatisfied(Screening screeing) {}
}

public class SequenceCondition implements DiscountCondition {
	public boolean isSatisfied(Screening screeing) {}
}
```

* 동일한 목적을 수행한다는 것을 명시하기 위해 메서드 이름을 통일하였다
* 다만, 동일한 계층의 타입으로 묶어 주어야 한다. 인터페이스를 이용하자

이처럼, 어떻게 하느냐가 아니라 무엇을 하느냐에 초점을 맞추어 보자. 이런식으로 메서드의 이름을 통일하면 의도를 드러낼 수 있고, 인터페이스를 이용해 유연한 설계가 가능해진다.
---
## 원칙의 함정
디미터법칙, 묻지 말고 시켜라 법칙 등의 법칙이 깔끔하게 유연한 설계를 도와주지만 절대적인 것은 아니다. 잊지 말아야 할 것은 설계는 트레이드오프의 산물이라는 점이다.

### 디미터 법칙은 하나의 도트(.)를 강제하는 규칙이 아니다
```java
IntStream.of(1,2,3).filter(x -> x >10).distinct().count();
```

dot을 한줄에 여러개 사용하였다.
디미터 법칙은 결합도와 관련된 것이다. 결합도는 객체의 내부 구조가 외부로 노출되는 경우를 한정하기 때문에  디미터법칙을 위반하지 않는다.

```java
public class PeriodCondition implements DiscountCondition {
	public boolean isSatisfiedBy(Screening screening) {
		return screening.getStartTime().getDayOfWeek().equals(dayOfWeek) && startTime.compareTo(screeing.getStartTime().toLocalTime()) <= 0 && endTime.compareTo(screeing.getStartTime().toLocalTime()) >= 0;
	}
}
```

해당 로직을 묻지도 말고 시켜라 원칙을 적용해, Screening으로 옮겨 보자

```java
public class Screeing {
	public boolean isDiscountable(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
		return whenScreened.getDayOfWeek().equals(dayOfWeek) && startTime.compareTo(whenScreened.toLocalTime()) <= 0 && endTime.compareTo(whenScreened.toLocalTime()) >= 0;
	}
}

public class PeriodCondition implements DiscountCondition {
	public boolean isSatisfiedBy(Screening screening) {
		return screening. isDiscountable(dayOfWeek, startTime, endTime);
	}
}
```

묻지도 말고 시켜라 원칙을 지켰다. 그러나, Screening이 할인 조건을 떠맡게 되었다. 하지만 Screening 객체가 할인 조건을 책임 지는게 맞는 것일까? 당연히 그렇지 않다. 객체의 응집도가 낮아지게 되었다.

소프트웨어는 트레이드오프의 산물이기에 설계에 있어서 절대적인 법칙은 존재하지 않는다.

---
## 명령-쿼리 분리 원칙
명령 쿼리 분리 원칙은 퍼블릭 인터페이스에 오퍼레이션을 정의할 떄 참고할 지침을 제공한다.
* 루틴
    * 어떤 절차를 묶어 호출 가능하도록 이름을 부여한 것
    * 프로시저와 함수로 구분된다
    * 프로시저
        * 부수효과를 발생시키지만 값을 반환할 수 없다
    * 함수
        * 값을 반환할 수 있지만 부수효과를 발생시킬 수 없다

* 명령
    * 객체의 상태를 수정하는 오퍼레이션
* 쿼리
    * 객체와 관련된 정보를 반환하는 오퍼레이션

중요한 것은 어떤 오퍼레이션도 명령이거나 동시에 쿼리여서는 안된다. 따라서 명령과 쿼리를 분리하기 위해서 다음의 두가지 규칙을 준수해야 한다
1. 객체의 상태를 변경하는 명령은 반환값을 가질 수 없다
2. 객체의 정보를 반환하는 쿼리는 상태를 변경할 수 없다

아래의 예시를 살펴보자

```java
public class Event {
	public boolean isSatisFied(RecurringSchedule schedule) {
		if (...) {
			reschedule(schedule);
			return false;
		}
		return true;
	}
}
```

boolean이라는 상태값을 반환하지만, 중간에 reschedule을 호출한다, 그리고 schedule을 바꾸어 버린다 … 이 코드를 다음과 같이 분리해보자

```java
public class Event {
	public boolean isSatisFied(RecurringSchedule schedule) {
		if (...) {
			return false;
		}
		return true;
	}
}

if (!event.isSatisfied(schedule)) {
	event.reschedule(schedule);
}	
```

명령과 쿼리를 분리함으로써 내부적인 버그를 해결할 수 있다.

