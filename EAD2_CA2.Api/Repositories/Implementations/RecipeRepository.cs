using EAD2_CA2.Api.Data;
using EAD2_CA2.Api.Entities;
using EAD2_CA2.Api.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace EAD2_CA2.Api.Repositories.Implementations;

public class RecipeRepository : IRecipeRepository
{
    private readonly MealPlannerContext _context;

    public RecipeRepository(MealPlannerContext context)
    {
        _context = context ?? throw new ArgumentNullException(nameof(context));
    }

    public async Task<IEnumerable<Recipe>> GetAllRecipesAsync()
    {
        return await _context.Recipes.OrderBy(r => r.Name).ToListAsync();
    }

    public async Task<Recipe?> GetRecipeByIdAsync(int recipeId)
    {
        return await _context.Recipes.FirstOrDefaultAsync(r => r.RecipeId == recipeId);
    }

    public async Task<IEnumerable<Recipe>> SearchRecipesByNameOrIngredientAsync(string query)
    {
        if (string.IsNullOrWhiteSpace(query))
        {
            return await GetAllRecipesAsync();
        }

        query = query.ToLower();

        return await _context.Recipes
            .Where(r => r.Name.ToLower().Contains(query) ||
                        r.Ingredients.ToLower().Contains(query))
            .OrderBy(r => r.Name)
            .ToListAsync();
    }

    public async Task<IEnumerable<Recipe>> FilterRecipesAsync(
    string? category,
    string? cuisine,
    int? minCalories,
    int? maxCalories,
    string? ingredient,
    bool? withImage,
    bool? withVideo)
    {
        var query = _context.Recipes.AsQueryable();

        if (!string.IsNullOrWhiteSpace(category))
        {
            query = query.Where(r => r.Category.ToLower() == category.ToLower());
        }

        if (!string.IsNullOrWhiteSpace(cuisine))
        {
            query = query.Where(r => r.Cuisine.ToLower() == cuisine.ToLower());
        }

        if (minCalories.HasValue)
        {
            query = query.Where(r => r.Calories >= minCalories.Value);
        }

        if (maxCalories.HasValue)
        {
            query = query.Where(r => r.Calories <= maxCalories.Value);
        }

        if (!string.IsNullOrWhiteSpace(ingredient))
        {
            query = query.Where(r => r.Ingredients.ToLower().Contains(ingredient.ToLower()));
        }

        if (withImage == true)
        {
            query = query.Where(r => !string.IsNullOrEmpty(r.ImageUrl));
        }

        if (withVideo == true)
        {
            query = query.Where(r => !string.IsNullOrEmpty(r.VideoUrl));
        }

        return await query.ToListAsync();
    }
}