package com.gather_club_back.gather_club_back.controller;

import com.gather_club_back.gather_club_back.model.PlaceCategoryResponse;
import com.gather_club_back.gather_club_back.service.PlaceCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/place-categories")
@RequiredArgsConstructor
@Tag(name = "Категории мест", description = "API для получения информации о категориях мест")
public class PlaceCategoryController {

    private final PlaceCategoryService placeCategoryService;

    @Operation(summary = "Получить все категории", description = "Возвращает список всех доступных категорий мест")
    @ApiResponse(responseCode = "200", description = "Список категорий успешно получен", 
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = PlaceCategoryResponse.class))))
    @GetMapping
    public ResponseEntity<List<PlaceCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(placeCategoryService.getAllCategories());
    }

    @Operation(summary = "Получить категорию по ID", description = "Возвращает информацию о категории места по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Категория успешно найдена", 
                    content = @Content(schema = @Schema(implementation = PlaceCategoryResponse.class))),
        @ApiResponse(responseCode = "404", description = "Категория не найдена")
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<PlaceCategoryResponse> getCategoryById(
            @Parameter(description = "ID категории", required = true) @PathVariable Integer categoryId) {
        return ResponseEntity.ok(placeCategoryService.getCategoryById(categoryId));
    }
}
