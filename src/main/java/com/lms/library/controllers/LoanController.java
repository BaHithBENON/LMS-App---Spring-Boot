package com.lms.library.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.library.models.ModelLoan;
import com.lms.library.services.LoanService;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @GetMapping
    public List<ModelLoan> getAllLoans() {
        return loanService.findAll();
    }

    @PostMapping
    public ModelLoan createLoan(@RequestBody ModelLoan loan) {
        return loanService.save(loan);
    }

    @GetMapping("/{id}")
    public ModelLoan getLoanById(@PathVariable Long id) {
        return loanService.findById(id);
    }

    @PutMapping("/{id}")
    public ModelLoan updateLoan(@PathVariable Long id, @RequestBody ModelLoan loanDetails) {
        // Implémentez la logique de mise à jour  ici
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteLoan(@PathVariable Long id) {
        loanService.deleteById(id);
    }
}
