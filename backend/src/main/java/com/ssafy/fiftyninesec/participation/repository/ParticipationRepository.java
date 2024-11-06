package com.ssafy.fiftyninesec.participation.repository;


import com.ssafy.fiftyninesec.participation.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Optional<List<Participation>> findByRoomIdOrderByRankingAsc(Long roomId);
    boolean existsByRoomIdAndMemberId(Long roomId, Long memberId);
    int countByRoomId(Long roomId);
}