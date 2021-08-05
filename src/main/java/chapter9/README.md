# 9장 유연한 설계
유연한 설계를 위한 다양한 방법을 알아보자. 여기서는 다음과 같이 알아볼 예정이다.

* 개방-폐쇄 원칙
* 생성 사용 분리
* 의존성 주입
* 의존성 역전 원칙

---
## 개방-폐쇄 원칙
> 로버트 마틴
> “소프트웨어 개채(클래스, 모듈, 함수 등등)는 확장에 열려 있어야 하고,
> 수정에 대해서는 닫혀 있어야 한다”

키워드는 *확장*과 *수정*이다. 이 키워드를 애플리케이션 관점에서 바라보자
* 확장에 대해 열려 있다
    * 요구사항이 변경될 때, 변경에 맞게 새로운 동작을 추가해 기능을 확장 할 수 있다
* 수정에 닫혀 있다
    * 기존의 코드를 수정하지 않고도 애플리케이션의 동작을 추가/변경 할 수 있다

이렇듯, 개방-폐쇄 원칙은 기존의 코드를 수정하지 않고 애플리케이션의 기능을 확장할 수 있는 설계라 할 수 있다.
개방-폐쇄에 대해 좀더 알고 싶다면 uncle bob의 블로그를 참조해보자
[Clean Coder Blog](https://blog.cleancoder.com/uncle-bob/2014/05/12/TheOpenClosedPrinciple.html)


### 컴파일 의존성을 고정 시키고 런타임 의존성을 변경하라
개방-폐쇄 원칙은 사실 런타임,컴파일타임 의존성에 관한 이야기이다
[image:64372520-D1F8-4E5C-9C76-29C153331B5D-2772-000007195C2DCFD2/A3911862-C800-447F-A974-555B4918B22B.png]
* 런타임 의존성 : 실행시 협락에 참여하는 객체들 사이의 관계이다
* 컴파일타임 의존성 : 코드에서 드러나는 클래스들 사이의 관계이다

이제 의존성을 위에서 언급한 확장과 수정에 대해서 빗대어 알아보자
* 기능 추가 : Amount, Percent 이외에 Overlapped, None 등의 정책이 추가 가능하다
* 수정 : 해당 정책 클래스 내에서만 이루어진다

컴파일타임 의존성을 유지지하며 런타임 의존성을 변경할 경우, 코드의 유연한 설계가 가능해진다

### 추상화가 핵심이다
개방-폐쇄 원칙의 핵심은 *추상화에 의존하는 것*이다

추상화란 핵심적인 부분만 남기고 불필요한 부분을 생략하는 기법이다.
추상화 과정을 거치면 문맥이 바뀌더라도 변하지 않는 부분만 남게 되고,
문맥에 따라 변하는 부분은 생략된다.

아래 코드를 살펴보자
```java
public abstract class DiscountPolicy {
	public Money calculateDiscountAmount(Screening screening) {
		for (DiscountCondition each: conditions) {
			if (each.isSatisfiedBy(screening) {
				return getDiscountAmount(screening);
			}
		}
	}
	abstract protected Money getDiscountAmount(Screening screening);
}
```

문맥이 바뀌더라도 할인여부를 판단하는 로직은 바뀌지 않는다. 변하지 않는 부분을 고정하고 변하는 부분을 생략하는 추상화 매커니즘이 개방-폐쇄 원칙의 기반이 된다

---
## 생성 사용 분리
[image:608C967F-779E-4180-90E8-A9AF2588000A-2772-0000095EEEE7EFCD/2C97389F-E76E-470B-9BCF-E1C75390BEA7.png]

Movie가 오직 DiscountPolicy라는 추상화에만 의존하기 위해서는 Movie 내부에서 구체 클래스에 의존해서는 안된다. 다음 코드를 살펴보자

```java
public class Movie {
	...
	private DiscountPolicy discountPolicy;
	public Movie(...) {
		...
		this.discountPolicy = new AmountDiscountPolicy(...);
	}
}
```

Movie 내부 생성자에서 AmountDiscountPolicy를 직접 생성하기 때문에 다른 할인 정책을 적용하는 것이 어렵다. 이러한 코드는 확장이나 수정에 유연하게 대처하는 것이 어렵기 때문에 위에서 알아본 *개방-폐쇄* 원칙을 위반한다

유연하고 재사용 가능한 코드를 사용하려면 *생성과 사용을 분리* 해야 한다.
```java
public class Client {
	public Money getAvatarFree() {
		Movie avatar = new Movie(..., new AmountDiscountPolicy(...));
		return avatar.getFee();
	}
}
```
위와 같이 컨텍스트에 대한 결정권을 Client(Movie외부)로 옮김으로써 유연한 설계가 가능해졌다

### Factory 추가하기
생성 책임을 Movie -> Client로 옮김으로써 Movie의 특정 컨텍스트에 묶이는 것을 해결하였다. 하지만, Movie를 사용하는 Client에서도 특정 컨텍스트에 묶이질 않길 바란다면 어떻게 해결해야 할까?

아래의 코드를 살펴보자

```java
public class Factory {
	public Movie createAvatarMovie() {
		return new Movie(..., new AmountDiscountPolicy(...));
	}
}

public class Client {
	private Factory factory;
	public Client(Factory factory) {
		this.factory = factory;
	}
	public Money getAvatarFree() {
		Movie avatar = 	factory.createAvatarMovie();
		return avatar.getFee();
	}
}
```

이 문제는 Movie에서 해결한 방법과 마찬가지로 결정권자를 옮김으로 해결할 수 있다
이처럼 생성과 사용을 분리하기 위해 객체 생성에 특화된 객체를 Factory라고 한다

### 순수한 가공물에게 책임 할당하기
앞서 책임을 할당할 때, 정보 전문가에게 할당하라는 원칙을 배웠습니다. 하지만, 방금 언급한 Factory는 Information Expert와는 거리가 먼 모델이라는 것을 알수 있습니다.

> 크레이그 라만
> “시스템을 객체로 분해하는 데는 크게 두가지 방식이 존재한다.
> 첫번째는 표현적 분해이고, 다른 하나는 행위적 분해이다”

이처럼 표현적 분해는 도메인에 존재하는 사물 또는 개념을 표현하는 객체들을 이용해 시스템을 분해하는 것을 말합니다. 하지만, 모든 책임을 도메인 객체에게 할당할 수 없는 경우가 생깁니다. 이럴 경우에는 Factory 처럼 가상의 기계적 개념들이 필요한데, 이처럼 창조되는 도메인과 무관한 인공적인 객체를 *PURE FABRICATION(순수한 가공물)* 이라고 부릅니다. 이것을 행위적 분해라고 가르킵니다.

정리하자면, 일차적으로 도메인 모델에 책임을 할당하는 것을 고려하고, 그것이 여의치 않다면 대안으로 삼을 수 있는 것이 FACTORY와 같은 PURE FABRICATION 입니다.

---
## 의존성 주입
```java
public class Movie {
	...
	private DiscountPolicy discountPolicy;
	public Movie(...) {
	}
}
```

위의 코드를 살펴보자. 생성과 사용을 분리하며 Movie에는 오로지 인스턴스를 사용하는 책임만 남게 되었다. 이처럼 사용하는 객체가 아닌 외부의 독립적인 객체가 인스턴스를 생성 후 이를 전달해서 의존성을 해결하는 방법을 *의존성 주입* 이라고 부른다.

의존성을 해결하기 위해 다음과 같은 3가지 메카니즘을 이용한다. 8장에서도 나온 내용이기에 간략히 정리한다.
* 생성자 주입
```java
Movie avatar = new Movie(..., new AmountDiscountPolicy(...));
```
* setter 주입
```java
avatar.setDiscountPolicy(new AmountDiscountPolicy(...));
```
* 메서드 주입
```java
avatar.calcalateDiscountAmount(screening, new AmountDiscountPolicy(...));
```

가장 좋은 방법은 생성자와 수정자를 같이 이용하는 것이다. 시스템의 안전성을 보장해주고 필요에 따라 의존성을 변경할 수 있기 때문에 유연성을 향상시킬 수 있다

### 숨겨진 의존성은 나쁘다
의존성 주입 이외에도 의존성을 해결할 수 있는 다양한 방법이 존재한다. 그 중에서 가장 널리 사용되는 대표적인 방법은 *SERVICE LOCATOR* 패턴이다

외부에서 객체에게 의존성을 전달하는 의존성 주입과 달리 SERVICE LOCATOR의 경우 객체가 직접 SERVICE LOCATOR에게 의존성을 해결해줄 것을 요청한다

```java
public class Movie {
	private DiscountPolicy discountPolicy;
	public Movie(...) {
		this.discountPolicy = ServiceLocator.discountPolicy();
	}
}

public class ServiceLocator {
	private DiscountPolicy discountPolicyl
	private static ServiceLocator soleInstance = new ServiceLocator();
	public static void provide(DiscountPolicy discountPolicy) {
		soleInstance.discountPolicy = discountPolicy;
	}
}
```

이제 SERVICE LOCATOR 패턴을 이용해 인스턴스를 생성해보자
```java
ServiceLocator.provide(new AmountDiscountPolicy(...));
Movie avatar = new Movie(...);
ServiceLocator.provide(new PercentDiscountPolicy(...));
Movie avatar1 = new Movie(...);
```

위와 같이 쉽게 의존성 문제를 해결할 수 있다. 다만 의존성이 숨겨져 있다는 치명적인 단점이 있다. 아래의 코드를 살펴보자

```java
Movie avatar = new Movie(...);
```

위의 코드는 NPE 문제가 발생할 것이다. 그 이유는 ServiceLocator로 DiscountPolicy를 주입시켜주지 않았기 때문이다.

결론적으로, SERVICE LOCATOR 패턴을 사용하지 말라는 것은 아니다. 다만, 숨겨진 의존성보다 명시적인 의존성을 먼저 고려하는 것이 시스템의 안정을 위해 좋다는 것이다.

---
## 의존성 역전 원칙
### 추상화와 의존성 역전
구체클래스에 의존하는 Movie 클래스를 살펴보자. 결합도가 높아지고 재사용성이 낮아진 것을 확인할 수 있다.

```java
public class Movie {
	private AmountDiscountPolicy discountPolicy;
}
```

객체 사이의 협력이 존재할 때, 그 협력의 본질을 담고 있는 것은 상위 수준의 정책이다.
여기서의 본질은 영화의 가격을 계산하는 것이다. 할인 금액을 계산하는 것은 협력의 본질이 아니다.

그러나 상위 수준의 클래스가 하위 수준의 클래스에 의존하면 하위 수준의 변경에 의해 상위 수준 클래스가 영향을 받게 된다. 즉, 하위 수준의 AmountDiscountPolicy를 PercentCountPolicy로 변경해서 상위 수준의 Movie가 영향을 받아서는 안된다.

[image:E008E3EA-7DA1-498E-B6BD-A693802A7712-2772-0000137FFB7A80C2/BFF4C7E2-05B0-474E-BEEC-112F2229B43C.png]

이 경우 해결할 수 있는 방법이 위에서 알아본 추상화 기법이다.
위의 언급한 내용들을 정리를 해보면
1. 상위 수준의 모듈은 하위 수준의 모듈에 의존해서는 안된다. 둘다 모두 추상화에 의존해야 한다
2. 추상화는 구체적인 사항에 의존해서는 안된다. 구체적인 사항은 추상화에 의존해야 한다.
   이를 *의존성 역전 원칙* 이라 한다

---
## 결론
우리는 런타임의존성, 캄파일타임 의존성을 배우며 유연하고 재사용 가능한 설계에 대해 알아보았다. 하지만 이러한 설계가 항상 옳고 좋은 것은 아니다.
설계의 미덕은 단순함과 명확함이지만 이러한 설계는 복잡성을 높이기 때문이다.

따러서, 필자(조영호님)는 아래와 같은 융통성을 제시한다

* 미래에 일어나지 않을 변경에 대비하여 꼭 복잡한 설계를 할 필요는 없다
* 단순하고 명확한 해법이 만족스럽다면 유연성을 제거해도 된다
* 의존성이 생기는 이유는 객체의 협력과 책임이 있기 때문이다
* 객체 생성에 초점을 맞추기 보다 객체의 역할과 책임의 균형을 맞추는 것에 초점을 맞추자
