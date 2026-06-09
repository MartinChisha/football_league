package com.backspacestudios.league_management.league.controller;

import com.backspacestudios.league_management.league.dto.DivisionRequest;
import com.backspacestudios.league_management.league.dto.DivisionResponse;
import com.backspacestudios.league_management.league.service.DivisionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/divisions")
@PreAuthorize("hasRole('league_admin') or hasRole('super_admin')")
public class DivisionController {

    @Autowired
    private DivisionService divisionService;

    @PostMapping("/create")
    public ResponseEntity<DivisionResponse> createDivision(@Valid @RequestBody DivisionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(divisionService.createDivision(request));
    }

    @PutMapping("/update/{divisionId}")
    public ResponseEntity<DivisionResponse> updateDivision(@PathVariable UUID divisionId,
                                                           @Valid @RequestBody DivisionRequest request) {
        return ResponseEntity.ok(divisionService.updateDivision(divisionId, request));
    }

    @GetMapping("/league/{leagueId}")
    public ResponseEntity<List<DivisionResponse>> getDivisionsByLeague(@PathVariable UUID leagueId) {
        return ResponseEntity.ok(divisionService.getDivisionsByLeague(leagueId));
    }

    @GetMapping("/id/{divisionId}")
    public ResponseEntity<DivisionResponse> getDivisionById(@PathVariable UUID divisionId) {
        return ResponseEntity.ok(divisionService.getDivisionById(divisionId));
    }
}