package com.example.dotheG.service;

import com.example.dotheG.dto.step.StepSummaryResponseDto;
import com.example.dotheG.model.Member;
import com.example.dotheG.model.MemberInfo;
import com.example.dotheG.model.Step;
import com.example.dotheG.repository.MemberInfoRepository;
import com.example.dotheG.repository.MemberRepository;
import com.example.dotheG.repository.StepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StepService {

    private final StepRepository stepRepository;
    private final MemberRepository memberRepository;
    private final MemberInfoRepository memberInfoRepository;

    // 기존유저용 (12시 지나면 step객체 생성)
    @Transactional
    public void createStepForAllUsers() {
        // FIXME 실제유저 데이터베이스에서 호출하는것으로 변경 필요
        // FIXME 현재 null로 작성되어있는 회원전체목록 불러오기 변경 필요
        List<Member> members = null;
        for (Member member : members) {
            Optional<Step> existingStep = stepRepository.findByUserIdAndStepDate(member, LocalDate.now());
            if (existingStep.isEmpty()) {
                createStep(member);
            }
        }

    }

    // 신규유저용(회원가입시 객체생성)
    // TODO 회원가입 로직에 step객체 생성하는 로직 추가
    public void createStep(Member member) {
        Step step = new Step(member, LocalDate.now());
        stepRepository.save(step);
    }

    // TODO 기능추가구현
    // 기본걸음수 반환 필요한지?

    // 걸음수 업데이트
    @Transactional
    public int updateStep(Long userId, int walkingCount) {
        // FIXME Authentication.getUserName()으로 user정보 받아오기
        Member member = memberRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("유저를 찾을수없습니다."));

        // step객체 업데이트
        Optional<Step> stepOptional = stepRepository.findByUserIdAndStepDate(member, LocalDate.now());
        Step step = stepOptional.orElseThrow(()-> new IllegalStateException("업데이트할 step객체가 없습니다."));
        step.updateStepCount(walkingCount);
        stepRepository.save(step);

        // TODO 반환을 해줄까?
        return step.getStepCount();
    }

    // 만보기 요약 보고서 반환
    // TODO month통계는 뺴기
    public StepSummaryResponseDto getStepSummary(Long userId) {
        // FIXME Authentication.getUserName()으로 user정보 받아오기
        Member member = memberRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("유저를 찾을수없습니다."));

        // FIXME 매번 조회할때마다 search하는게 비효율적일거같은데, erd추가하는거 고민해보기
        int todaySteps = getTodayStepCount(member);
        int weeklySteps = getWeekStepCount(member, LocalDate.now());
        //int monthlySteps = getMonthStepCount(member, LocalDate.now());
        int totalSteps = getTotalStepCount(member);
        double carbonReduction = getCarbonReduction(weeklySteps);

        StepSummaryResponseDto responseDto = new StepSummaryResponseDto(
                todaySteps,
                weeklySteps,
                //monthlySteps,
                totalSteps,
                carbonReduction
        );

        return responseDto;
    }

    // 만보기 목표달성시 리워드 지급
    @Transactional
    public void getReward(Long userId) {
        // FIXME Authentication.getUserName()으로 user정보 받아오기
        Member member = memberRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("유저를 찾을수없습니다."));


        // 목표달성 검증
        Optional<Step> stepOptional = stepRepository.findByUserIdAndStepDate(member, LocalDate.now());
        Step step = stepOptional.orElseThrow(()-> new IllegalStateException("step객체를 찾을수없습니다."));

        if (step.getStepCount() >= 7000) {
            // 리워드 지급
            MemberInfo memberInfo = memberInfoRepository.findByUserId(member);
            memberInfo.addReward(5);
            memberInfoRepository.save(memberInfo);
        }
    }

    // 오늘 걸음수
    private int getTodayStepCount(Member member) {
        LocalDate today = LocalDate.now();
        Optional<Step> stepOptional = stepRepository.findByUserIdAndStepDate(member, today);
        Step step = stepOptional.orElseThrow(()-> new IllegalStateException("step객체가 없습니다."));
        return step.getStepCount();
    }

    // 월요일 부터 해당날짜까지 시작일 계산
    private LocalDate getStartOfWeek(LocalDate date) {
        // 오늘이 월요일 -> 오늘 데이터만 반환
        if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
            return date;
        }

        // 오늘이 수요일 -> 월요일 ~ 수요일 데이터 반환
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }

    // 주간 집계
    private int getWeekStepCount(Member member, LocalDate date) {
        LocalDate startOfWeek = getStartOfWeek(date); // 월요일부터
        return stepRepository.findWeeklyStepsByMember(member, startOfWeek, date); // 현재요일까지
    }

    // 월간 집계
    // TODO 제거
    private int getMonthStepCount(Member member, LocalDate date) {
        LocalDate startOfMonth = date.withDayOfMonth(1); // 이번달 1일부터
        return stepRepository.findMonthlyStepsByMember(member, startOfMonth, date); // 현재날짜까지
    }

    // 누적 집계
    private int getTotalStepCount(Member member) {
        return stepRepository.findTotalStepsByMember(member);
    }

    // 탄소배출량 계산
    private double getCarbonReduction(int weeklyStepCount) {
        // TODO 버스, 전철, 차량 폭이 너무큰데 어떤거할지?
        double temp = weeklyStepCount * 0.75 / 1000;    // km로 환산
        double carbonReductionCar = temp * 0.192;       // 가솔린차량
        double carbonReductionBus = temp * 0.105;       // 버스
        double carbonReductionSubway = temp * 0.052;    // 전철

        return carbonReductionBus;
    }

}
