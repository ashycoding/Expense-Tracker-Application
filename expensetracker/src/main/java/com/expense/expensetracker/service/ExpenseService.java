package com.expense.expensetracker.service;

import com.expense.expensetracker.model.Expense;
import com.expense.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.PrintWriter;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public void saveExpense(Expense expense) {
        expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public List<Expense> search(String keyword) {
        return expenseRepository.findByCategoryContainingIgnoreCase(keyword);
    }

    public List<Expense> filterByCategory(String category) {
        return expenseRepository.findByCategoryContainingIgnoreCase(category);
    }

    public List<Expense> filterByDateRange(LocalDate from, LocalDate to) {
        return expenseRepository.findByDateBetween(from, to);
    }

    public double getTotalExpense() {
        return getAllExpenses().stream().mapToDouble(Expense::getAmount).sum();
    }

    public Map<String, Double> getCategoryTotals() {
        return getAllExpenses().stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));
    }

    public Map<String, Double> getMonthlyTotals() {
        return getAllExpenses().stream()
                .filter(e -> e.getDate() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        TreeMap::new,
                        Collectors.summingDouble(Expense::getAmount)
                ));
    }

    public Expense getMostRecentExpense() {
        return getAllExpenses().stream()
                .filter(e -> e.getDate() != null)
                .max(Comparator.comparing(Expense::getDate))
                .orElse(null);
    }

    public Expense getHighestExpense() {
        return getAllExpenses().stream()
                .max(Comparator.comparing(Expense::getAmount))
                .orElse(null);
    }

    public String getMostFrequentCategory() {
        return getAllExpenses().stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    public List<Expense> getByMonth(String month) {
        return getAllExpenses().stream()
                .filter(e -> e.getDate() != null &&
                        e.getDate().getMonth().toString().equalsIgnoreCase(month))
                .toList();
    }

    // ✅ Export to CSV
    public void exportToCSV(PrintWriter writer) {
        List<Expense> expenses = getAllExpenses();
        writer.println("Description,Amount,Category,Date");
        for (Expense e : expenses) {
            writer.printf("%s,%.2f,%s,%s%n",
                    e.getDescription().replace(",", " "),
                    e.getAmount(),
                    e.getCategory(),
                    e.getDate());
        }
    }
}
