package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<MealTo> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println("\n ------------------------------------------------------- \n");

        mealsTo = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
    }

    public static List<MealTo> filteredByCycles(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDate = new HashMap<>();
        for (Meal meal : meals) {
            caloriesByDate.merge(meal.getDate(), meal.getCalories(), Integer::sum);
        }

        List<MealTo> mealTos = new ArrayList<>();
        for (Meal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                boolean excess = isExcess(meal, caloriesByDate, caloriesPerDay);
                MealTo mealTo = createMealTo(meal, excess);
                mealTos.add(mealTo);
            }
        }
        return mealTos;
    }

    public static List<MealTo> filteredByStreams(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesByDate = meals.stream()
                        .collect(Collectors.toMap(Meal::getDate, Meal::getCalories, Integer::sum));

        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime))
                .map(meal -> createMealTo(meal, isExcess(meal, caloriesByDate, caloriesPerDay)))
                .toList();
    }

    public static MealTo createMealTo(Meal meal, boolean excess) {
        return new MealTo(
                meal.getDateTime(),
                meal.getDescription(),
                meal.getCalories(),
                excess
        );
    }

    public static boolean isExcess(Meal meal, Map<LocalDate, Integer> caloriesByDate, int caloriesPerDay) {
        return caloriesByDate.get(meal.getDate()) > caloriesPerDay;
    }
}
