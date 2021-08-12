# 12장 다형성

---
## 01. 다형성
> 다형성(Polymorphism)
> ‘많은’ - poly + ‘형태’ - ‘morph’의 합성어로 많은 형태를 가질 수 있는 능력을 의미

‘Computer Science’ 에서는 다형성을 하나의 추상 인터페이스에 대해 코드를 작성하고 이 추상 인터페이스에 대해 서로 다른 구현을 연결할 수 있는 능력으로 정의한다.

객체지향 프로그래밍에서는 4개의 다형성으로 정의 한다.
* 오버로딩 다형성
    * 하나의 클래스 안에 동일한 이름의 메서드가 존재하는 경우
* 강제 다형성
    * 자동적인 타입 변환 방식
* 매개변수 다형성
    * 제네릭 프로그맹과 관련이 깊다
* 포함 다형성
    * 메시지가 동일하더라도 수신한 객체의 타입에 따라 실제 수행되는 행동이 달라진다
    * 서브타입 다형성이라고도 부른다

---
## 02. 상속의 양면성
객체지향 프로그램을 작성하기 위해서는 항상 데이터와 행동이라는 두가지 관점을 함께 고려해야 한다. 상속의 경우도 마찬가지이다.

* 상속을 이용하면 부모 클래스에 정의한 모든 데이터를 자식 클래스의 인스터에 포함시킬 수 있다 -> 데이터 관점의 상속
* 데이터 뿐만 아니라 메서드도 포함 시킬 수 있다 -> 행동 관점의 상속

> 언뜻 보면 상속은 부모 클래스에서 정의한 것을 자동적으로 공유하고 재사용 할 수 있는 메커니즘으로 보이지만, 이 관점은 상속을 오해한 것

* 상속의 목적은 코드 재사용이 아니다
* 상속은 프로그램을 구성하는 개념들 기반으로 다형성을 가능하게 하는 타입 계층을 구축하기 위한 것
* 타입 계층에 대한 고민 없이 상속을 이용하면 유지보수가 어려워 진다

### 상속을 사용한 강의 평가
* 상속의 메카니즘을 이해하기 위해 예제를 살펴보자
    * 수강생들의 성적을 계산하는 프로그램

Lecture.java
```java
public class Lecture {
    private int pass; // 이수여부 판단할 기준 점수
    private String title; // 과목명
    private List<Integer> scores = new ArrayList<>(); // 학생들의 성적 리스트

    public Lecture(String title, int pass, List<Integer> scores) {
        this.title = title;
        this.pass = pass;
        this.scores = scores;
    }

    public double average() {
        return scores.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    public List<Integer> getScores() {
        return Collections.unmodifiableList(scores);
    }

    public String evaluate() {
        return String.format("Pass:%d Fail:%d", passCount(), failCount());
    }

    private long passCount() {
        return scores.stream().filter(score -> score >= pass).count();
    }

    private long failCount() {
        return scores.size() - passCount();
    }
}
```

이수 기준 70점, 5명의 대한 설정 통계
```java
Lecture lecture = new Lecture("객체지향 프로그래밍", 70, Arrays.asList(81, 95, 75, 50,45));
String evaluration = lecture.evaluate(); // 결과 => "Pass:3, Fail:2"
```

GradeLecture.java Lecture의 출력 결과에 등급별 통계를 추가
```java
public class GradeLecture extends Lecture {
    private List<Grade> grades;

    public GradeLecture(String name, int pass, List<Grade> grades, List<Integer> scores) {
        super(name, pass, scores);
        this.grades = grades;
    }

    @Override
    public String evaluate() {
        return super.evaluate() + ", " + gradesStatistics();
    }

    private String gradesStatistics() {
        return grades.stream().map(grade -> format(grade)).collect(joining(" "));
    }

    private String format(Grade grade) {
        return String.format("%s:%d", grade.getName(), gradeCount(grade));
    }

    private long gradeCount(Grade grade) {
        return getScores().stream().filter(grade::include).count();
    }

    public double average(String gradeName) {
        return grades.stream()
                .filter(each -> each.isName(gradeName))
                .findFirst()
                .map(this::gradeAverage)
                .orElse(0d);
    }

    private double gradeAverage(Grade grade) {
        return getScores().stream()
                .filter(grade::include)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }
}
```

Grade.java 등급의 이름과 각등긥 범위를 정의
```java
public class Grade {
    private String name; // 등급
    private int upper,lower; // 상한선, 하한선

    private Grade(String name, int upper, int lower) {
        this.name = name;
        this.upper = upper;
        this.lower = lower;
    }

    public String getName() {
        return name;
    }

    public boolean isName(String name) {
        return this.name.equals(name);
    }

    public boolean include(int score) {
        return score >= lower && score <= upper;
    }
}
```

GradeLecture 클래스의  evaluate 메서드를 봐보자. 부모의 evaluate를 재정의하여 사용하고 있다. 이처럼 동일한 시그니처의 메서드를 재정의해서 부모 클래스의 구현을 새로운 구현으로 대체하는 것을 메서드 오버라이딩이라 한다

Lecture와 GradeLecture의 average를 살펴보자. 메서드 이름은 같지만 시그니처가 다르다. 둘은 대체되지 않으며 호출하는 방법이 다르므로 공존하게 된다. 이처럼 메서드 이름은 동일하지만 시그니처는 다른 메서드를 메서드 오버로딩이라 한다

### 데이터 관점의 상속
위에서 구현한 클래스를 인스턴스로 생성해보자
```java
Lecture lecture = new Lecture("객체지향 프로그래밍", 70, Arrays.asList(81, 95, 75, 50,45));
```

```java
Lecture lecture = new GradeLecture("객체지향 프로그래밍",
	70, Arrays.asList(	new Grade("A", 100, 95),
						new Grade("B", 94, 80),
						new Grade("C", 79, 70),
						new Grade("D", 69, 50),
						new Grade("F", 49, 0)),
		Arrays.asList(81, 95, 75, 50, 45));
```

아래 사진과 같이 데이의 관점에서 표현할 수 있다
[image:0DE15C96-F407-43EA-8E58-C97C290F8B63-34831-00002C4CCAD60EBD/33249717-F2E0-42CC-B381-13976C3D59DB.png]
즉, 자식 클래스의 인스턴스는 자동으로 부모 클래스에서 정의한 모든 인스턴스 변수를 내부에 포함하게 된다

### 행동 관점의 상속
행동 관점의 상속은 부모 클래스가 정의한 일부 메서드를 자식 클래스의 메서드로 포함시키는 것을 의미한다. 부모 클래스의 모든 퍼블릭 메서드는 자식 클래스의 퍼블릭 인터페이스에 포함된다.

---
## 03. 업캐스팅과 동적 바인딩
### 같은 메시지, 다른 메서드
성적 계산 프로그램에 각 교수별로 강의에 대한 성적 통계 기능을 추가해 본다

Professor.java 통계를 계산
```java
public class Professor {
    private String name;
    private Lecture lecture;

    public Professor(String name, Lecture lecture) {
        this.name = name;
        this.lecture = lecture;
    }

    public String compileStatistics() {
        return String.format("[%s] %s - Avg: %.1f", name,
                lecture.evaluate(), lecture.average());
    }
}
```

```java
Professore professor = new Profeesor("다익스트라", new Lecture(...));
Professore professor = new Profeesor("다익스트라", new GradeLecture(...));
```

Professor의 생성자 인자 타입은 Lecture로 선언돼 있지만 Lecture, GradeLecture 모두 올수가 있다. 이처럼 코드 안에서 선언된 참조 타입과 무관하게 실제로 메시지를 수신하는 객체의 타입에 따라 실행되는 메서드가 달라지는 것은 업캐스팅과 동적 바인딩 메카니즘이 작용하기 때문이다

* 부모 클래스 타입으로 선언된 변수에서 자식 클래스의 인스턴스를 할당하는 것을 업캐스팅이라 한다
* 선언된 변수 타입이 아니라도 메시지를 수신하는 객체의 타입에 따라 실행되는 메서드가 결정된다. 이것은 컴파일 시점이 아니라 실행시점에 결정하기 때문인데, 이를 동적 바인딩이라 한다

### 업캐스팅
상속을 이용하면 부모 클래스의 퍼블릭 인터페이스가 자식 클래스의 퍼빌릭 인터페이스에 합쳐지기 때문에 메시지를 자식 클래스의 인스턴스에게 전송할 수 있다.

```java
Lecture lecture = new GradeLecture(...);
```

반대의 경우를 다운캐스팅(downcasting)이라 한다
```java
Lecture lecture = new GradeLecture(...);
GradeLecture gradeLecture = (GradeLecture) lecture;
```

### 동적 바인딩
컴파일 시점이 아닌 메서드를 런타임에 결정하는 방식을 동적바인딩, 지연바인딩 이라 한다. (앞 chapter에 나와 있는 추상화와 의존성을 같이 봐두면 좋다)

---
## 04. 동적 메서드 탐색과 다형성
객체지향 시스템은 다음과 같은 규칙으로 실행할 메서드를 선택한다
1. 먼저, 자신을 생성한 클래스에 적합한 메서드가 있는지 검사
2. 없을 경우 부모 클래스에서 메스드를 탐색, 있을때까지 상속 계층을 따라 올라간다
3. 최상위 계층까지 탐색 하여 없을 경우 예외 발생

### 자동적인 메시지 위임
상속을 이용할 경우, 메시지 수신을 처리할 대상을 자동으로 찾게 된다. 즉, 명시적으로 코드를 작성할 필요가 없고, 상속 계층에 따라 부모 클래스에게 위임 된다.  이런 관점에서 상속 계층을 정의하는 것은 메서드 탐색 경로를 정의하는 것과 동일하다.

#### 메서드 오버라이딩
```java
Lecture lecture = new GradeLecture(...);
lecture.evalute();
```

[image:BB07AC4D-6FE1-4E31-ACC9-C4876CDED8F6-2512-000002D22113ABE1/B7FCF9B2-F2B1-47B9-97AF-A5A908456A38.png]

위의 코드를 실행해보자. Lecture.evaluate -> 는 GradeLecture.evaluate에 재정의 되어 있다. 실행시, self 참조에 의해 자기 자신의 클래스를 먼저 탐색하게 된다. 따라서, GradeLecture.evaluate()가 실행되게 되고, 오버라이딩된 부모 클래스의 메서드를 감추게 한다.

동적메서드 탐색이 자식 클래스에서 부모 클래스 방향으로 진행된다는 것을 기억하면 오버라이딩의 결과는 당연하다.

#### 메서드 오버로딩
```java
Lecture lecture = new GradeLecture(...);
lecture.avaerage();
```

[image:DC929578-5E25-4C0A-8A15-DE2501DB628C-2512-000003C86C1A93FA/3B86E3E0-5772-4119-BE9C-F290FAFF6BAE.png]

위의 코드를 실행해보자. 오버라이딩과 마찬가지로 selft 참조를 하게 된다. GradeLecture에는 시그니처가 다르기에 부모 클래스에서 해당 시그니처를 찾게 된다.
이처럼 시그니처가 다르기 때문에 동일한 이름의 메서드가 공존하는 경우를 오버로딩이라 부른다.

### 동적인 문맥
```java
lecture.evaluate()
```
> 위의 메시지 전송만으로 어떤 클래스의 메서드가 실행될지 알 수 없다는 것을 이해하였다. 여기서 중요한 것은 메시지를 수신한 객체가 무엇이냐에 따라 메서드 탐색을 위한 문맥이 동적으로 바뀐다는 것이다. 그리고 이 동적인 문맥을 결정하는 것은 바로 메시지를 수신한 객체를 가리키는 self 참조이다

### 이해할 수 없는 메시지
동적 메서드 탐색을 통해, 메시지를 수신하면 부모 클래스까지 탐색하여 찾게된다. 만약, 최상위 계층까지 탐색후 원하는 메시지가 없다면 어떻게 될까?

#### 정적 타입 언어와 이해할 수 없는 메시지
정적 타입 언어에서는 상속 계층 전체를 탐색후 메시지를 처리할 메서드를 발견하지 못하면 컴파일 에러를 발생시킨다
```java
Lecture lecture = new GradeLecture(...);
lecture.unknownMessage(); // 컴파일 에러!!
```

#### 동적 타입 언어와 이해할 수 없는 메시지
동적 타입 언어도 자식으로 부터 부모 방향으로 메서드를 탐색한다. 차이점이라면 컴파일단계가 존재하지 않아 실제 코드를 실행해보기 전엔 메시지 처리 가능 여부를 알 수 없다는 점이다.

### super
Self 참조의 가장 큰 특징은 동적이라는 점이다. Self 참조는 메시지를 수신한 객체의 클래스에 따라 메서드 탐색을 위한 문맥을 실행 시점에 결정한다. self의 이런 특성과 대비해서 언급할 만한 가치가 있는 것이 super 참조이다.

자식 클래스에서 부모 클래스의 구현을 재사용해야 하는 경우가 있다. 이럴 경우, 부모 클래스의 변수나 메서드에 접근하기 위해 super 참조라는 내부 변수를 제공한다.

```java
public class GradeLecture extends Lecture {
	@Override
	public String evaluate() {
		return super.evalute() + ", " + gradesStatistics();
	}
}
```

또한, super 참조를 통해 실행하고자 하는 메서드가 반드시 부모 클래스에 위치하지 않아도 되는 유연성을 제공한다. 그 메서드가 조상 클래스 어딘가에 있기만 하면 성공적으로 탐색이 가능하다


---
## 참조
* 조영호님의 오브젝트 12장 “다형성”
