package com.pranta.MealManagement.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pranta.MealManagement.Dtos.ExpenseDto;
import com.pranta.MealManagement.Entity.Expense;
import com.pranta.MealManagement.Entity.Member;
import com.pranta.MealManagement.Entity.Mess;
import com.pranta.MealManagement.Repository.ExpenseRepository;
import com.pranta.MealManagement.Repository.MemberRepository;
import com.pranta.MealManagement.Repository.MessRepository;

@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MessRepository messRepository; 
    
    public ExpenseDto addExpense(ExpenseDto expenseDto) {
        // Get the member who is adding the expense
        Member addedBy = memberRepository.findById(expenseDto.getAddedById())
                .orElseThrow(() -> new RuntimeException("Member not Found"));

        // Get the mess
        Mess mess = messRepository.findById(expenseDto.getMessId())
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        // Create expense entity
        Expense expense = new Expense();
        expense.setDescription(expenseDto.getDescription());
        expense.setAmount(expenseDto.getAmount());
        expense.setDate(LocalDate.now());
        expense.setCategory(expenseDto.getCategory());
        expense.setAddedBy(addedBy);
        expense.setMess(mess);
        
        // Save and return
        Expense saveExpense = expenseRepository.save(expense);
        return convertToDto(saveExpense);
    }

    public List<ExpenseDto> getExpensesByMessAndDateRange(Long messId, LocalDate startDate, LocalDate endDate) {
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        return expenseRepository.findByMessAndDateBetweenOrderByDateDesc(mess, startDate, endDate)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalExpensesByMessAndDateRange(Long messId, LocalDate startDate, LocalDate endDate) {
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        BigDecimal total = expenseRepository.getTotalExpensesByMessBetweenDates(mess, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<ExpenseDto> getExpenseByMessAndCategory(Long messId, Expense.ExpenseCategory expenseCategory) {
        Mess mess = messRepository.findById(messId)
                .orElseThrow(() -> new RuntimeException("Mess not found"));

        return expenseRepository.findByMessAndCategory(mess, expenseCategory)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    } 

    public ExpenseDto updateExpense(Long id, ExpenseDto expenseDto) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        expense.setAmount(expenseDto.getAmount());
        expense.setCategory(expenseDto.getCategory());
        expense.setDescription(expenseDto.getDescription());
        // Don't update date - keep original creation date
        
        Expense updateExpense = expenseRepository.save(expense);
        return convertToDto(updateExpense);
    }

    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Expense not Found"));

        expenseRepository.delete(expense);
    }

    private ExpenseDto convertToDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        dto.setCategory(expense.getCategory());
        dto.setAddedById(expense.getAddedBy().getId());
        dto.setAddedByName(expense.getAddedBy().getName());
        dto.setMessId(expense.getMess().getId()); 
        return dto;
    }
}