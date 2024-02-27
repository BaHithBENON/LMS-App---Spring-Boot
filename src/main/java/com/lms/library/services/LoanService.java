package com.lms.library.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.library.models.ModelLoan;
import com.lms.library.repositories.LoanRepository;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    public List<ModelLoan> findAll() {
        return loanRepository.findAll();
    }

    public ModelLoan save(ModelLoan loan) {
        return loanRepository.save(loan);
    }

    public ModelLoan findById(Long id) {
        return loanRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        loanRepository.deleteById(id);
    }
}
