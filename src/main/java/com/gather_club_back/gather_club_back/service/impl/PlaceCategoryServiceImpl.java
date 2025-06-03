package com.gather_club_back.gather_club_back.service.impl;

import com.gather_club_back.gather_club_back.entity.PlaceCategory;
import com.gather_club_back.gather_club_back.mapper.PlaceCategoryMapper;
import com.gather_club_back.gather_club_back.model.PlaceCategoryResponse;
import com.gather_club_back.gather_club_back.repository.PlaceCategoryRepository;
import com.gather_club_back.gather_club_back.service.PlaceCategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceCategoryServiceImpl implements PlaceCategoryService {

    private final PlaceCategoryRepository placeCategoryRepository;
    private final PlaceCategoryMapper placeCategoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PlaceCategoryResponse> getAllCategories() {
        return placeCategoryRepository.findAll().stream()
                .map(placeCategoryMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlaceCategoryResponse getCategoryById(Integer categoryId) {
        PlaceCategory category = placeCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Категория с ID " + categoryId + " не найдена"));
        return placeCategoryMapper.toModel(category);
    }
} 