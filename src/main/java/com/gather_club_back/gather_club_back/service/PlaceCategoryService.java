package com.gather_club_back.gather_club_back.service;

import com.gather_club_back.gather_club_back.model.PlaceCategoryResponse;

import java.util.List;

public interface PlaceCategoryService {
    List<PlaceCategoryResponse> getAllCategories();
    PlaceCategoryResponse getCategoryById(Integer categoryId);
} 