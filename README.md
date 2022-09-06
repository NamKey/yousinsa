# YOUSINSA
MUSINSA 같은 온라인 편집샵 대용량 서버

## Mock-Up

[Main - No User (1 of 20)](https://ovenapp.io/project/PGw27rPWTmydr8mpmbRVAZYTZurQXuV6#7YCsA)

## 성능 개선

대용량 트래픽을 처리 하기 위한 성능 개선점들을 반영한 TPS 테스트 그래프입니다.

<div class="tps_graph" align="center">
    <img width="821" alt="image" src="https://user-images.githubusercontent.com/25685282/188317874-0f94ed55-6a9a-46a1-82c0-86d9f3091807.png">
</div>

[개선점 리스트](#-성능-개선점)

## Design

### Database
[Table 설계에 대한 Issue](https://github.com/f-lab-edu/yousinsa/issues/5)

[Table Diagram](https://dbdiagram.io/d/626c11c695e7f23c619ca37d)

<img src="https://user-images.githubusercontent.com/25685282/166242241-7685315d-fc17-4bc4-abce-a65d3a71c318.png" alt="">

### Infra

![yousinsa-network-infra_ver3](https://user-images.githubusercontent.com/25685282/186876345-929cb9c4-dc0d-4ce2-b730-752ab8927324.png)

---
## 👕 프로젝트 중점사항

프로젝트를 진행하면서 중점적으로 도입해 볼 목록을 정리했습니다.

### Common

- Version 관리 전략
- 문서화

### Spring

- Spring 기능을 충분히 활용
- Spring 내부 동작과 구조를 숙지하면서 사용
- 글로벌 서비스 기준(미구현)

### Performance

- 서버 확장성
- 대규모 트래픽을 처리에 대한 고려
- 비동기 처리를 경험해 볼 수 있도록
- 테이블 설계에 대한 고려 사항 체험

### Code Quality

- Code Convention을 준수
- OOP와 관련된 원칙들을 준수
- 테스트가 쉽도록 설계 준수
- Layer에 대한 구분 준수

### 문제 상황 Simulation

- 한정 판매로 정해진 수량의 물품만 판매 - 수량의 제한
- 주문 데이터가 많은 경우 정산에 대한 처리 시간 문제
- 이벤트 시 한번의 트래픽이 몰리는 경우
- 어쩔 수 없이 서버가 다운되는 경우에 대한 Fail-Over 테스트(미구현)
- 비동기적인 처리를 통해 처리 속도가 향상되어야 하는 문제

---

## 👖 UseCase

필요한 정책이 추가적으로 생길 수 있습니다.

### Common

- user는 `회원 가입`을 통해 Role을 획득할 수 있다.
- user는 `회원 탈퇴`를 할 수 있다.
- user는 `로그인`을 통해 서비스를 사용할 수 있다.
- user는 `로그아웃` 을 통해 서비스 사용을 종료할 수 있다.

### Buyer

- buyer는 `물품 목록 조회`를 할 수 있다.
- buyer는 `물품 구매`를 할 수 있다.

### Store Owner

- store owner는 `입점 신청` 을 진행할 수 있다.
- store owner는 입점이 완료되면 `물품 등록` 을 진행할 수 있다.

### Admin

- admin은 `입점 신청을 수락`할 수 있다.

---

## 🩳 Version Definition

- v1 : 기획 구현과 테스트를 중점으로 개발
    - Unit Test
    - Integration Test


- v2 : 리팩토링(구조 개선)
    - 확장에 유연하도록 개선
    - 관련된 Spring Module 적용


- v3 : 성능 개선(극한 상황에 대한 테스트)
    - 부하 테스트
    - 쿼리 최적화
    - MSA로 가기 위한 준비


- v4 : Monolithic to MSA(MicroService Architecture)
    - Monolithic보다 더 유연성 있는 구조와 패턴 적용(Scale-out 고려)
    - MSA와 관련된 Test
    - CQRS

---

## ⛑ 성능 개선점

- (블로그 작성중입니다!)

- [YOUSINSA Tech Doc Notion](https://www.notion.so/be-the-key/YOUSINSA-Tech-Doc-c78df8d1462a48799b25b84c6d3e1487)

- [YOUSINSA 성능 개선 Notion](https://be-the-key.notion.site/Yousinsa-369eb3085098428780e13ec16508962b)

[[#1] Server Infra 구성 문제](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#2~#3] 쿼리 문제](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#4] Database PoolSize 최적화](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#5] Scale-Up 도입](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#6] getConnection 호출 시점 미루기](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#8] CannotAquireLockException 발생 문제](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#9] Session Storage 도입](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#10] Scale-Out 도입](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#11] Cache Layer 도입](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#12] 재고 관리 Inconsistency 문제](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#13] Database I/O 문제](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#14] Redis를 활용할 방법 찾기](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#15] Redis를 사용한 재고관리](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#16] Redis Connection Pool Size 조정](https://keydo.tistory.com/category/Project/YOUSINSA)

[[#17] Redis의 Atomic Operation 보장](https://keydo.tistory.com/category/Project/YOUSINSA)

---

## 🦺 Project History

<details>
<summary>[2022.09.04] Version 3까지 진행 완료</summary>
<div markdown="1">

- [x] 대용량 트래픽을 받기 위한 부하 테스트 후 성능 개선
    - Scale-Out
    - 쿼리 최적화
    - Cache Layer 사용

</div>
</details>

<details>
<summary>[2022.06.10] Version에 대한 정의 추가</summary>
<div markdown="1">

- [x] Version(Phase)에 대한 정의 추가
    - Version 1, 2, 3, 4


- [x] v1 Schedule에 대한 부분 등록
    - https://github.com/f-lab-edu/yousinsa/milestones

</div>
</details>

<details>
<summary>[2022.04.13] 시작 ReadMe 작성</summary>
<div markdown="1">

- [x]  Mock-Up 만들기(04/10일 내로 완료 후 취합, 4/11 멘토님에게 검토)
- [x]  Naming 결정 - 마신사, 유신사
- [x]  Category - 상의, 하의, 아우터 (이 안에서도 추리기)
- [x]  우리만의 프로젝트 중점 사항 정하기

**[예시]**

       - Spring MVC 기능을 충분하고 잘 활용하기
       - Coding Convention 정하기
       - OOP와 관련된 원칙들을 준수
       - 테스트가 쉬운 코드를 작성
       - 백엔드 실무에서 발생할 수 있는 문제를 해결할 수 있도록 설계하기

- [x]  Role 정리
- [x]  문제 상황 Simulation

**[예시]**

       - 한정 판매로 정해진 수량의 물품만 판매 - 수량의 제한
       - 결제 데이터가 많은 경우 정산에 대한 처리 시간 문제
       - 이벤트 시 한번의 트래픽이 몰리는 경우
       - 결제 도중 시스템이 다운될 경우에 대한 결제에 대한 롤백 처리
       - 어쩔 수 없이 서버가 다운되는 경우에 대한 Fail-Over 테스트

</div>
</details>
