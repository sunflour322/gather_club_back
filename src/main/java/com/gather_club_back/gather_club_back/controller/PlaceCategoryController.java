package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.PlaceCategoryResponse;
import com.gather_club_back.gather_club_back.service.PlaceCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/place-categories")
@RequiredArgsConstructor
public class PlaceCategoryController {

    private final PlaceCategoryService placeCategoryService;

    @GetMapping
    public ResponseEntity<List<PlaceCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(placeCategoryService.getAllCategories());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<PlaceCategoryResponse> getCategoryById(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(placeCategoryService.getCategoryById(categoryId));
    }
} 