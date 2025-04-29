using EAD2_CA2.Api.Entities;

namespace EAD2_CA2.Api.Repositories.Interfaces;

public interface IMealPlanRepository
{
    Task<IEnumerable<MealPlan>> GetAllMealPlansAsync();
    Task<MealPlan?> GetMealPlanByIdAsync(int mealPlanId);
    Task<IEnumerable<MealPlan>> GetMealPlanByDayAsync(Enums.Day dayofWeek);
    Task<MealPlan> AddMealPlanAsync(MealPlan mealPlan);
    Task<MealPlan> EditMealPlanAsync(MealPlan mealPlan);
    Task DeleteMealPlanAsync(int mealPlanId);
}