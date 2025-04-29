using Microsoft.EntityFrameworkCore;
using EAD2_CA2.Api.Data;
using EAD2_CA2.Api.Entities;
using EAD2_CA2.Api.Enums;
using EAD2_CA2.Api.Repositories.Interfaces;

namespace EAD2_CA2.Api.Repositories.Implementations;

public class MealPlanRepository : IMealPlanRepository
{
    private readonly MealPlannerContext _context;

    public MealPlanRepository(MealPlannerContext context)
    {
        _context = context ?? throw new ArgumentNullException(nameof(context));
    }

    public async Task<IEnumerable<MealPlan>> GetAllMealPlansAsync()
    {
        return await _context.MealPlans.ToListAsync();
    }

    public async Task<MealPlan?> GetMealPlanByIdAsync(int mealPlanId)
    {
        return await _context.MealPlans.FirstOrDefaultAsync(mp => mp.MealPlanId == mealPlanId);
    }

    public async Task<IEnumerable<MealPlan>> GetMealPlanByDayAsync(Day dayOfWeek)
    {
        return await _context.MealPlans
            .Where(mp => mp.DayOfWeek == dayOfWeek)
            .ToListAsync();
    }

    public async Task<MealPlan> AddMealPlanAsync(MealPlan mealPlan)
    {
        await _context.MealPlans.AddAsync(mealPlan);
        await _context.SaveChangesAsync();

        return mealPlan;
    }

    public async Task<MealPlan> EditMealPlanAsync(MealPlan mealPlan)
    {
        _context.MealPlans.Update(mealPlan);
        await _context.SaveChangesAsync();

        return mealPlan;
    }

    public async Task DeleteMealPlanAsync(int mealPlanId)
    {
        var mealPlan = await _context.MealPlans.FindAsync(mealPlanId);
        if (mealPlan != null)
        {
            _context.MealPlans.Remove(mealPlan);

            await _context.SaveChangesAsync();
        }
    }
}