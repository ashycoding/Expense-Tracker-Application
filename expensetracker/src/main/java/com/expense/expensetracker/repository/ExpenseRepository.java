package com.expense.expensetracker.repository;

import com.expense.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCategoryContainingIgnoreCase(String category);

    List<Expense> findByDateBetween(LocalDate start, LocalDate end);
}
