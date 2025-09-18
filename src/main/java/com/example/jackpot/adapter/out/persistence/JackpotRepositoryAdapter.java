package com.example.jackpot.adapter.out.persistence;

import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotEntity;
import com.example.jackpot.adapter.out.persistence.jpa.mapper.JackpotMapper;
import com.example.jackpot.adapter.out.persistence.jpa.repostiory.JackpotJpaRepository;
import com.example.jackpot.application.out.JackpotRepository;
import com.example.jackpot.domain.exception.JackpotNotFoundException;
import com.example.jackpot.domain.model.Jackpot;
import com.example.jackpot.domain.model.id.JackpotId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JackpotRepositoryAdapter implements JackpotRepository {

    private final JackpotJpaRepository repository;

    @Override
    public Optional<Jackpot> findById(JackpotId id) {
        return repository.findById(id.value()).map(JackpotMapper::toDomain);
    }

    public void save(Jackpot jackpot) {
        JackpotEntity entity = repository.findById(jackpot.jackpotId().value())
                .orElseThrow(() -> new JackpotNotFoundException("Jackpot not found: %s".formatted(jackpot.jackpotId())));

        repository.save(JackpotMapper.toEntity(jackpot, entity.getVersion()));
    }
}
