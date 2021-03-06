# 객체분해 (chapter7)

사람의 기억은 단기기억/장기기억 이 있음
일반적으로 단기기억 -> 장기기억으로 옮겨짐

> 조지밀러의 매직넘버 7
> “단기 기억 공간에 일반적으로 5 ~ 9개의 아이템을 집어 넣을 수 있다”

> 저자가 말하고 싶은 것은?
>
> “핵심은 실제로 문제를 해결하기 위해 사용하는 저장소는
> 장기 기억 저장소가 아닌 단기 기억 저장소이다”

* 하지만 단기기억 공간에는 공간의 제약이 있다
  -> 불필요한 정보를 제거하고 현재의 문제 해결에 대한 핵심 정보만 남긴다 (*추상화*)
  -> 추상화의 일반적인 방법은 문제의 크기를 줄이는 것
  -> 이처럼 큰 문제를 작은 문제로 나누는 것을 *분해* 라 한다


추상화와 분해는 인류가 창조한 가장 복잡한 문제 해결 방법
그 분야는 바로 소프트웨어이다

* 프로시저 추상화와 데이터 추상화
* 프로시저 추상화와 기능 분해
* 모듈
* 데이터 추상화와 추상 데이터 타입
*  클래스


## 프로시저 추상화와 데이터 추상화
어셈블리어? 숫자로 된 기계어 번벅 …
-> 고수준 언어, 기계적 사고를 강요하는 낮은 수준의 명령어들을 탈피를 시도
-> 인간의 눈높이에서 기계 독릭접이고 의미 있는 추상화를 제공하려는 시도

### 현대적인 프로그래밍 언어를 특징 짓는 두 가지 추상화 메카니즘
1. 프로시저 추상화
2. 데이터 추상화
   소프트웨어는 데이터를 이용해 정보를 표현하고 프로시저를 이용해 데이터를 조작
   시스템을 분해 하려면 프로시저/데이터 추상화에서 *중심*으로 할 것을 정해야 한다

* 프로시저 추상화
    * 소프트웨어가 *무엇을 해야 하는지* 추상화
    * 프로시저 중심 시스템 분해 -> *기능분해*

* 데이터 추상화
    * 소프트웨어가 *무엇을 알아야 하는지* 추상화
    * 데이터 중심 시스템 분해 -> *두가지* 방법이 존재
        * 데이터를 중심으로 타입 추상화 -> *추상 데이터 타입*
        * 데이터를 중심으로 프로시저를 추상화 -> *객체 지향*



## 프로시저 추상화와 기능 분해
* 기능 vs 데이터
    * 기능이 오랜 시간 시스템 분해의 기준으로 사용됨
    * 알고리즘 분해, 기능 분해 라고도 함
    * 기능 분해의 관점에서 추상화의 단위는 프로시저

* 프로시저
    * 반복적으로 실행되거나 거의 유사하게 실행되는 작업들을 하나의 장소에 모아 놓음
    * 로직을 추상화하고 중복을 방지하는 추상화 방법

* 전통적인 기능 분해 방법
    * 하향식 접근법
        * 시스템을 구성하는 가장 최상위 기능을 정의
        * 이 최상위 기능을 작은 단계의 하위 기능으로 분해해 나감
        * 분해의 마지막 하위 기능이 프로그래밍 언어로 구현

* 급여 관리 시스템
  [image:92115426-755F-4EEC-8041-7472E054698C-11179-00001EE1623749F2/30C91946-57CE-4848-AE57-E5B3A69AB114.png]

[image:22B40FAA-A4E9-43D3-B8C8-AC7F8CAD995E-11179-00001EE6553E90C3/6397C54A-887F-4044-A737-6A16C133E6F8.png]

[image:969F530B-4764-4F0B-9F21-0DCAEA261BB8-11179-00001EECE3359CE8/5B73ED59-C8FE-40CD-A917-23D440E530F4.png]

[image:90C75505-47C6-40C5-A7D7-9EB69D6F7A31-11179-00001EF66867DE3E/F14AE80C-C345-4F58-8328-AE63AACD4372.png]

보기에는 이상적으로 보인다


### 하향식 기능 분해의 문제점
1. 하나의 메인 함수라는 비현실적인 아이디어
   [image:F58FDD5A-A50F-412A-BF27-DED2E8BDEFC3-11179-00001FF5D7AC17B2/92D3F074-6091-4DE4-A83C-A2FCE2CBDAED.png]
    * 대부분의 시스템에서는 하나의 메인 기능이란 개념은 존재하지 않음
    * 대부분의 경우 추가되는 기능은 최초에 배포된 메임 함수의 일부가 아님
    * 새로운 기능 추가시, 매번 메인 함수를 수정
        * 큰 틀을 바꾸는 것이기 때문에 급격한 변경 가능성이 생김

2. 비즈니스 로직과 사용자 인터페이스의 결합
   [image:3EF725FA-01C2-4A12-98CA-8D6120EF13D6-11179-0000211964B74FE4/481ED4A5-ED48-481D-8389-CE1366B87C74.png]
    * 설계 초기 단계부터 입력 방법과 출력 양식을 함께 고민하도록 강요 ..
        * (사용자로부터 소득 세율 입력 받고, 급여를 계산하여 출력)
    * 문제는 사용자 인터페이스 변경은 자주 바뀌는 부분
    * 사용자 인터페이스가 바뀌지만 이 부분이 비즈니스 로직에도 영향을 주는 기이한 설계

3. 성급하게 결정된 실행 순서
    * ‘무엇을 해야 하는지’가 아니라 *’어떻게 동작 해야하는지’*에 초점을 맞춘다
    * 절차적인 순서로 진행 되기에 시간의 제약이 생긴다
    * 실행 순서나 조건, 빈복적인 제어 구조를 미리 결정해야 분해를 진행할 수 있다
        * 중앙집중 제어 스타일로 귀속
        * 모든 제어의 흐름 결정이 상위 함수에서 이뤄지게 된다
        * 따라서, 기능의 변경이 일어나게 되면 상위 함수까지 모두 수정이 발생

4. 데이터 변경으로 인한 파급효과
    * 어떤 함수가 어떤 데이터를 사용하는지 추적이 어렵다
    * 데이터가 어떤 함수에 의존하는지 파악하기 위해서는 모든 함수를 열어 화인해야 한다

* 하향식 접근법은 하나의 알고리즘 구현이나 배치 처리에는 적합


## 모듈
하향식 접근법은 위에 언급한 것처럼 많은 고통을 수반한다.
본질적인 문제를 해결하기 위해 접근을 통제하는 방법을 이용해야 한다

### 정보 은닉과 모듈
	* 퍼블릭 인터페이스를 제공하여 접근을 통제한다 -> 정보 은닉
	* 외부에 감춰야 하는 비밀에 따라 시스템을 분할하는 모듈 분할 원리
	* 모듈은 변경 가능성 있는 비밀은 내부로 감추고 쉽게 변경되지 않을 인터페이스를 외부에 제공
* 모듈
    * 서브 프로그램 이라기보다는 책임의 할당이다
    * 모듈이 감추어야 할 것
        * 복잡성
        * 변경 가능성

[image:F39C4FDA-8465-4DE3-86E6-113A51856641-11179-000024C060F2A001/04CB62AA-CB42-41C1-A5DB-A26E96B2B1C4.png]
이전 전역변수 였던 $employees, $basePays, $hourlys 등이 모듈 내부에 선언되었다

### 모듈의 장점과 한계
1. 모듈 내부의 변수가 변경 되더라도 모듈 내부에만 영향을 미친다
2. 비즈니스 로직과 사용자 인터페이스에 대한 관심사를 분리한다
3. 모듈화를 진행하여 네임스페이스 오염을 방지한다
    1. 내부변수, 함수를 이용하기 때문

모듈은 프로시저 추상화 보다는 높은 추상화 개념을 제공한다.
다만, 인스턴스의 개념을 제공하지 않기 때문에 모듈은 단지 모든 직원 정보를 가지고 있는 모듈일 뿐이다

(여기서 말하는 모듈은 우리가 아는 모듈이 아닌 프로시저의 추상화를 데이터와 함수의 단위로 묶은것 같다 ..)


## 데이터 추상화와 추상 데이터 타입
### 추상 데이터 타입
* 타입
    * 변수에 저장할 수 있는 내용물의 종류와변수에 적용될 수 있는 연산의 가짓수
    * 저장된 값에 대해 수행될 수 있는 연산의 집합을 결정

> 리스코프
> “추상 데이터 타입
> 추상 객체의 클래스를 정의한 것으로
> 추상 객체에 사용할 수 있는 오퍼레이션을 이용해 규정된다”

### 추상 데이터 타입을 정의하려면
* 타입 정의를 선언할 수 있어야 한다
* 타입의 인스턴스를 다루기 위해 오퍼레이션 집합을 정의할 수 있어야 한다
* 제공된 오퍼레이션을 통해서만 데이터를 조작할 수 있어야 한다 (퍼블릭 인터페이스)
* 타입에 대해 여러개의 인스턴스를 생성할 수 있어야 한다
  [Ruby]
  [image:F2A62E18-E3D8-4946-ADC6-AB5A03D25F1F-11179-000026E28E3F5DBE/35DEDE99-D3FF-4A56-B159-072618EFF957.png]



## 클래스
### 클래스는 추상 데이터 타입인가?
* 데이터 추상화를 기반으로 시스템을 분해하는 공통점이 있다
* 명확히는 조금 다르다
    * 클래스는 상속과 다형성을 지원한다
      상속과 다형성을 지원 -> 객체지향 프로그래밍
      지원 X -> 객체기반 프로그래밍 이라 부른다

[ 추상 데이터 타입]
[image:F5A88C53-C82E-4CCB-AC29-FDDB2D765222-11179-0000276116867FB0/11F86C11-32A8-4A22-B345-EEDAB63F825D.png]


[클래스]
[image:328F1BC4-5243-4EB0-9846-F2534DF9E4C6-11179-00002764294D44D2/DD4DBCDD-53A0-4C10-AFE7-1C50A710D0AC.png]


[image:861DC095-EC2A-48B3-AACE-37844EF70911-11179-0000274CE56A6814/37CFABE1-09F5-4C1D-BEB0-60DD4EF0AD88.png]

* 둘의 차이는 무엇일까?
    * 추상 데이터 타입은 오퍼레이션에 종속된다
    * 클래스 객체는 이러한 차이를 다형성으로 해결한다
    * 이처럼 기존 코드에 아무런 영향을 미치지 않고 새로운 객체 유형과 행위를 추가할 수 있는 객체지향의 특성을 *개방-폐쇄 원칙*이라 한다
    