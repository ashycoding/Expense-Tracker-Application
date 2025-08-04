package com.expense.expensetracker.controller;

import com.expense.expensetracker.model.Expense;
import com.expense.expensetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/")
    public String viewHome(Model model) {
        List<Expense> expenses = expenseService.getAllExpenses();
        model.addAttribute("expenses", expenses);
        model.addAttribute("total", expenseService.getTotalExpense());
        return "index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "form";
    }

    @PostMapping("/save")
    public String saveExpense(@ModelAttribute("expense") Expense expense) {
        expenseService.saveExpense(expense);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Expense expense = expenseService.getExpenseById(id);
        model.addAttribute("expense", expense);
        return "form";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable("id") Long id) {
        expenseService.deleteExpense(id);
        return "redirect:/";
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<Expense> results = expenseService.search(keyword);
        model.addAttribute("expenses", results);
        model.addAttribute("total", results.stream().mapToDouble(Expense::getAmount).sum());
        return "index";
    }

    @GetMapping("/filter")
    public String filterByCategory(@RequestParam String category, Model model) {
        List<Expense> results = expenseService.filterByCategory(category);
        model.addAttribute("expenses", results);
        model.addAttribute("total", results.stream().mapToDouble(Expense::getAmount).sum());
        return "index";
    }
    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.csv");
        expenseService.exportToCSV(response.getWriter());
    }


    @GetMapping("/date-filter")
    public String filterByDateRange(
            @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
            Model model) {

        List<Expense> results = expenseService.filterByDateRange(from, to);
        model.addAttribute("expenses", results);
        model.addAttribute("total", results.stream().mapToDouble(Expense::getAmount).sum());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        double total = expenseService.getTotalExpense();
        Map<String, Double> categoryTotals = expenseService.getCategoryTotals();
        Map<String, Double> monthlyTotals = expenseService.getMonthlyTotals();

        Expense mostRecent = expenseService.getMostRecentExpense();
        Expense highest = expenseService.getHighestExpense();
        String mostFrequentCategory = expenseService.getMostFrequentCategory();

        model.addAttribute("total", total);
        model.addAttribute("categoryTotals", categoryTotals);
        model.addAttribute("monthlyTotals", monthlyTotals);
        model.addAttribute("mostRecent", mostRecent);
        model.addAttribute("highest", highest);
        model.addAttribute("mostFrequentCategory", mostFrequentCategory);

        return "dashboard";
    }
}
