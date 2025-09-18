package com.example.jackpot.adapter.out.persistence;

import com.example.jackpot.adapter.out.persistence.jpa.mapper.BetMapper;
import com.example.jackpot.adapter.out.persistence.jpa.repostiory.BetJpaRepository;
import com.example.jackpot.application.port.out.BetRepository;
import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.id.BetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BetRepositoryAdapter implements BetRepository {

    private final BetJpaRepository repository;

    @Override
    public Optional<Bet> findById(BetId id) {
        return repository.findById(id.value()).map(BetMapper::toDomain);
    }

    @Override
    public boolean existsById(BetId id) {
        return repository.existsById(id.value());
    }

    @Override
    public void save(Bet bet) {
        repository.save(BetMapper.toEntity(bet));
    }
}
